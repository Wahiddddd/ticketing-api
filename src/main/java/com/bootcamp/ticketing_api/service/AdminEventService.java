package com.bootcamp.ticketing_api.service;

import com.bootcamp.ticketing_api.DTO.*;
import com.bootcamp.ticketing_api.entity.*;
import com.bootcamp.ticketing_api.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bootcamp.ticketing_api.exception.BusinessException;
import com.bootcamp.ticketing_api.exception.ResourceNotFoundException;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
@lombok.extern.slf4j.Slf4j
public class AdminEventService {
    private final EventRepository eventRepository;
    private final EventCategoryRepository categoryRepository;
    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;

    public org.springframework.data.domain.Page<EventSimpleResponse> getAllEvents(
            String search,
            Event.EventStatus status,
            LocalDateTime startDate,
            LocalDateTime endDate,
            org.springframework.data.domain.Pageable pageable) {
        return eventRepository.findWithFilters(search, status, startDate, endDate, pageable)
                .map(this::convertToSimpleResponse);
    }

    private EventSimpleResponse convertToSimpleResponse(Event event) {
        // Logika mencari harga terendah
        Double minPrice = categoryRepository.findAllByEventId(event.getId())
                .stream()
                .mapToDouble(EventCategory::getPrice)
                .min()
                .orElse(0.0);

        return EventSimpleResponse.builder()
                .id(event.getId())
                .name(event.getName())
                .location(event.getLocation())
                .eventDate(event.getEventDate())
                .startingPrice(minPrice)
                .status(event.getStatus() != null ? event.getStatus().toString() : null)
                .build();
    }

    @Transactional
    public void saveEvent(EventCreateRequest request, String adminUsername) {
        log.info("Admin {} is creating a new event: {}", adminUsername, request.getName());
        // 1. Validasi Tanggal (Flowchart Image 6)
        if (request.getEventDate().isBefore(LocalDateTime.now())) {
            log.warn("Event creation failed: Invalid date {}", request.getEventDate());
            throw new BusinessException("Tanggal tidak valid");
        }

        // Global Unique
        if (eventRepository.existsByNameAndLocationAndEventDate(request.getName(), request.getLocation(),
                request.getEventDate())) {
            throw new BusinessException("Event sudah terdaftar");
        }

        // Local Unique
        Set<String> categoryCodes = new HashSet<>();
        for (CategoryCreateRequest cat : request.getCategories()) {
            if (!categoryCodes.add(cat.getCode())) {
                throw new BusinessException("Kode Kategori tidak boleh sama");
            }
        }

        // 2. Simpan Event
        Event event = new Event();
        event.setName(request.getName());
        event.setLocation(request.getLocation());
        event.setDescription(request.getDescription());
        event.setEventDate(request.getEventDate());
        event.setStatus(Event.EventStatus.UPCOMING);

        // Tambah admin_id dari user yang login
        User admin = userRepository.findByUsername(adminUsername)
                .orElseThrow(() -> new RuntimeException("Admin tidak ditemukan"));
        event.setAdmin(admin);

        eventRepository.save(event);

        // 3. Simpan Kategori & Generate Tiket Otomatis (Flowchart Image 6)
        request.getCategories().forEach(catReq -> {
            EventCategory category = new EventCategory();
            category.setEvent(event);
            category.setCategoryName(catReq.getName());
            category.setCategoryCode(catReq.getCode());
            category.setPrice(catReq.getPrice());
            category.setTotalCapacity(catReq.getQuota());
            category.setRemainingCapacity(catReq.getQuota());
            categoryRepository.save(category);

            // Generate Tiket sejumlah kuota
            for (int i = 1; i <= catReq.getQuota(); i++) {
                Ticket ticket = new Ticket();
                ticket.setEventCategory(category);
                ticket.setTicketCode(catReq.getCode() + "-" + System.nanoTime());
                ticket.setSeatNumber(catReq.getName() + "-" + i);
                ticket.setStatus(Ticket.TicketStatus.AVAILABLE);
                ticketRepository.save(ticket);
            }
        });
    }

    @Transactional
    public void deleteEvent(Long id) { // rename from softDeleteEvent map to deleteEvent
        log.info("Admin request to delete event ID: {}", id);
        Event event = eventRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Event tidak ditemukan"));

        // Cek apakah ada tiket sold
        boolean hasSoldTickets = ticketRepository.existsByEventCategory_Event_IdAndStatus(id, Ticket.TicketStatus.SOLD);

        if (hasSoldTickets) {
            // Sesuai Flowchart Image 8: is_active = false & status = CANCELLED
            event.setIsActive(false);
            event.setStatus(Event.EventStatus.CANCELLED);
            eventRepository.save(event);
        } else {
            // Hard Delete
            eventRepository.delete(event);
        }
    }

    @Transactional
    public void updateEvent(Long id, EventUpdateRequest request) {
        log.info("Admin request to update event ID: {}", id);
        // Validasi Tanggal
        if (request.getEventDate().isBefore(LocalDateTime.now())) {
            log.warn("Event update failed: Invalid date {}", request.getEventDate());
            throw new BusinessException("Error: Tanggal sudah lewat");
        }

        Event event = eventRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Event tidak ditemukan"));

        // Validasi Duplikat Event Lain
        boolean isChanged = !event.getEventDate().equals(request.getEventDate()) ||
                !event.getName().equals(request.getName()) ||
                !event.getLocation().equals(request.getLocation());

        if (isChanged && eventRepository.existsByNameAndLocationAndEventDate(request.getName(), request.getLocation(),
                request.getEventDate())) {
            throw new RuntimeException("Data Duplikat");
        }

        // Validasi kategori kuota baru >= tiket laku
        if (request.getCategories() != null) {
            for (CategoryCreateRequest catReq : request.getCategories()) {
                EventCategory existingCat = categoryRepository.findByEventIdAndCategoryCode(event.getId(),
                        catReq.getCode());
                if (existingCat != null) {
                    long soldTickets = ticketRepository.countByEventCategoryIdAndStatus(existingCat.getId(),
                            Ticket.TicketStatus.SOLD);
                    if (catReq.getQuota() < soldTickets) {
                        throw new RuntimeException("Kuota tidak boleh lebih kecil dari tiket laku");
                    }
                }
            }
        }

        // Simpan Data Update Event
        event.setName(request.getName());
        event.setLocation(request.getLocation());
        event.setDescription(request.getDescription());
        event.setEventDate(request.getEventDate());
        eventRepository.save(event);

        // Update Kategori & Generate Tiket
        if (request.getCategories() != null) {
            for (CategoryCreateRequest catReq : request.getCategories()) {
                EventCategory existingCat = categoryRepository.findByEventIdAndCategoryCode(event.getId(),
                        catReq.getCode());
                if (existingCat != null) {
                    existingCat.setCategoryName(catReq.getName());
                    existingCat.setPrice(catReq.getPrice());

                    if (catReq.getQuota() > existingCat.getTotalCapacity()) {
                        int additional = catReq.getQuota() - existingCat.getTotalCapacity();
                        for (int i = 1; i <= additional; i++) {
                            Ticket ticket = new Ticket();
                            ticket.setEventCategory(existingCat);
                            ticket.setTicketCode(existingCat.getCategoryCode() + "-New-" + System.nanoTime());
                            ticket.setSeatNumber(existingCat.getCategoryName() + "-New-" + i);
                            ticket.setStatus(Ticket.TicketStatus.AVAILABLE);
                            ticketRepository.save(ticket);
                        }
                    }
                    existingCat.setTotalCapacity(catReq.getQuota());
                    existingCat.setRemainingCapacity(
                            existingCat.getRemainingCapacity() + (catReq.getQuota() - existingCat.getTotalCapacity()));
                    categoryRepository.save(existingCat);
                }
            }
        }
    }
}
