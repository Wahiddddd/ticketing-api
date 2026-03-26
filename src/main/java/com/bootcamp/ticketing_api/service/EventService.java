package com.bootcamp.ticketing_api.service;

import com.bootcamp.ticketing_api.DTO.EventSimpleResponse;
import com.bootcamp.ticketing_api.DTO.EventDetailResponse;
import com.bootcamp.ticketing_api.DTO.CategoryResponse;
import com.bootcamp.ticketing_api.entity.Event;
import com.bootcamp.ticketing_api.entity.EventCategory;
import com.bootcamp.ticketing_api.repository.EventCategoryRepository;
import com.bootcamp.ticketing_api.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EventService {
        private final EventRepository eventRepository;
        private final EventCategoryRepository categoryRepository;

        public Page<EventSimpleResponse> getAllEvents(
                        String search,
                        Event.EventStatus status,
                        java.time.LocalDateTime startDate,
                        java.time.LocalDateTime endDate,
                        Pageable pageable) {
                // Untuk halaman depan, filter default adalah UPCOMING jika tidak ditentukan
                Event.EventStatus searchStatus = (status != null) ? status : Event.EventStatus.UPCOMING;
                return eventRepository.findWithFilters(search, searchStatus, startDate, endDate, pageable)
                                .map(this::convertToSimpleResponse);
        }

        private EventSimpleResponse convertToSimpleResponse(Event event) {
                // Logika mencari harga terendah untuk ditampilkan di halaman awal
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
                                .status(event.getStatus().toString())
                                .build();
        }

        public EventDetailResponse getEventDetail(Long id) {
                Event event = eventRepository.findById(id).orElseThrow();

                java.util.List<CategoryResponse> categories = categoryRepository.findAllByEventId(event.getId())
                                .stream()
                                .map(cat -> CategoryResponse.builder()
                                                .id(cat.getId())
                                                .categoryName(cat.getCategoryName())
                                                .categoryCode(cat.getCategoryCode())
                                                .price(cat.getPrice())
                                                .remainingCapacity(cat.getRemainingCapacity())
                                                .build())
                                .toList();

                return EventDetailResponse.builder()
                                .id(event.getId())
                                .name(event.getName())
                                .description(event.getDescription())
                                .location(event.getLocation())
                                .eventDate(event.getEventDate())
                                .status(event.getStatus() != null ? event.getStatus().name() : null)
                                .categories(categories)
                                .build();
        }
}
