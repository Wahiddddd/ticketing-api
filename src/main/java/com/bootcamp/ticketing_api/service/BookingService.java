package com.bootcamp.ticketing_api.service;

import com.bootcamp.ticketing_api.DTO.BookingResponse;
import com.bootcamp.ticketing_api.entity.EventCategory;
import com.bootcamp.ticketing_api.entity.Ticket;
import com.bootcamp.ticketing_api.entity.Transaction;
import com.bootcamp.ticketing_api.entity.User;
import com.bootcamp.ticketing_api.repository.EventCategoryRepository;
import com.bootcamp.ticketing_api.repository.TicketRepository;
import com.bootcamp.ticketing_api.repository.TransactionRepository;
import com.bootcamp.ticketing_api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import com.bootcamp.ticketing_api.exception.BusinessException;
import com.bootcamp.ticketing_api.exception.ResourceNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@lombok.extern.slf4j.Slf4j
public class BookingService {
    private final UserRepository userRepository;
    private final EventCategoryRepository categoryRepository;
    private final TicketRepository ticketRepository;
    private final TransactionRepository transactionRepository;

    @Transactional
    public BookingResponse buyTicket(String username, Long categoryId) {
        log.info("User {} is attempting to buy a ticket for category ID: {}", username, categoryId);
        // 1. Cek & Potong Stok secara Atomik (Database Level)
        int updatedRows = categoryRepository.decreaseStock(categoryId);
        if (updatedRows == 0) {
            log.warn("Booking failed for category {}: Out of stock", categoryId);
            throw new BusinessException("Tiket habis!");
        }

        // 2. Ambil data kategori & tiket tersedia (ID Terkecil)
        EventCategory category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Kategori tidak ditemukan"));
        Ticket ticket = ticketRepository.findFirstByEventCategoryIdAndStatusOrderByIdAsc(
                        categoryId, Ticket.TicketStatus.AVAILABLE)
                .orElseThrow(() -> {
                    log.error("Sistem Error: Tiket tidak ditemukan untuk kategori {}", categoryId);
                    return new BusinessException("Sistem Error: Tiket tidak ditemukan");
                });

        // 3. Cek & Potong Saldo (Database Level)
        int balanceUpdated = userRepository.updateBalance(username, category.getPrice());
        if (balanceUpdated == 0) {
            log.warn("Booking failed for user {}: Insufficient balance", username);
            throw new BusinessException("Saldo tidak cukup!");
        }

        // 4. Update Status Tiket & User ID
        User user = userRepository.findByUsername(username).get();
        ticket.setUser(user);
        ticket.setStatus(Ticket.TicketStatus.SOLD);
        ticketRepository.save(ticket);

        // 5. Simpan Riwayat Transaksi untuk Laporan
        Transaction transaction = new Transaction();
        transaction.setUser(user);
        transaction.setTicket(ticket);
        transaction.setAmountPaid(category.getPrice());
        transaction.setTransactionType(Transaction.TransactionType.PURCHASE);
        transactionRepository.save(transaction);

        return BookingResponse.builder()
                .ticketCode(ticket.getTicketCode())
                .seatNumber(ticket.getSeatNumber())
                .eventName(category.getEvent().getName())
                .amountPaid(category.getPrice())
                .build();
    }
}
