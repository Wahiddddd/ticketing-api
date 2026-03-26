package com.bootcamp.ticketing_api.service;

import com.bootcamp.ticketing_api.DTO.TicketResponse;
import com.bootcamp.ticketing_api.entity.Ticket;
import com.bootcamp.ticketing_api.entity.Transaction;
import com.bootcamp.ticketing_api.entity.User;
import com.bootcamp.ticketing_api.exception.BusinessException;
import com.bootcamp.ticketing_api.exception.ResourceNotFoundException;
import com.bootcamp.ticketing_api.repository.TicketRepository;
import com.bootcamp.ticketing_api.repository.TransactionRepository;
import com.bootcamp.ticketing_api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@lombok.extern.slf4j.Slf4j
public class TicketService {
    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final com.bootcamp.ticketing_api.repository.EventCategoryRepository categoryRepository;

    public Page<TicketResponse> getUserTickets(String username, Pageable pageable) {
        log.info("Fetching tickets for user: {}", username);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User tidak ditemukan"));

        // Hanya menampilkan tiket yang statusnya SOLD, BROKEN, atau REFUNDED (bukan
        // AVAILABLE)
        return ticketRepository.findAllByUserIdAndStatusNot(user.getId(), Ticket.TicketStatus.AVAILABLE, pageable)
                .map(this::convertToResponse);
    }

    @Transactional
    public void cancelAndRefundTicket(Long ticketId, String username) {
        log.info("Processing user refund request for Ticket ID: {} by user: {}", ticketId, username);
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Tiket tidak ditemukan"));

        // Validasi kepemilikan
        if (!ticket.getUser().getUsername().equals(username))
            throw new BusinessException("Akses ditolak");

        // 1. Kembalikan Saldo ke User (Flowchart Image 12)
        User user = ticket.getUser();
        Double refundAmount = ticket.getEventCategory().getPrice();
        user.setBalance(user.getBalance() + refundAmount);
        userRepository.save(user);

        // 2. Ubah Status Tiket menjadi REFUNDED (Bukan AVAILABLE) agar history tetap
        // ada
        ticket.setStatus(Ticket.TicketStatus.REFUNDED);
        // ticket.setUser(null); Dihapus agar user tetap bisa melihat tiket ini
        ticketRepository.save(ticket);

        // 3. Generate Tiket Pengganti berstatus AVAILABLE agar kuota kembali
        Ticket replacementTicket = new Ticket();
        replacementTicket.setEventCategory(ticket.getEventCategory());
        replacementTicket.setTicketCode(ticket.getEventCategory().getCategoryCode() + "-REP-" + System.nanoTime());
        replacementTicket.setSeatNumber(ticket.getSeatNumber());
        replacementTicket.setStatus(Ticket.TicketStatus.AVAILABLE);
        ticketRepository.save(replacementTicket);

        // 4. Update Stok di EventCategory
        categoryRepository.increaseStock(ticket.getEventCategory().getId());

        // 3. Catat Transaksi Refund
        Transaction refundTx = new Transaction();
        refundTx.setUser(user);
        refundTx.setTicket(ticket);
        refundTx.setAmountPaid(refundAmount);
        refundTx.setTransactionType(Transaction.TransactionType.REFUND);
        transactionRepository.save(refundTx);
    }

    @Transactional
    public void disableTicket(String ticketCode) {
        log.info("Admin disabling ticket: {}", ticketCode);
        Ticket ticket = ticketRepository.findByTicketCode(ticketCode)
                .orElseThrow(() -> {
                    log.error("Ticket code not found for disable: {}", ticketCode);
                    return new ResourceNotFoundException("Tiket tidak ditemukan");
                });

        // Jika SOLD, tetap bisa di-disable (misal: kursi patah mendadak)
        // Admin wajib memindahkan kursi user setelah ini
        ticket.setStatus(Ticket.TicketStatus.BROKEN);
        ticketRepository.save(ticket);
    }

    @Transactional
    public void moveSeat(String ticketCode, String newSeatNumber) {
        log.info("Admin moving ticket {} to new seat {}", ticketCode, newSeatNumber);
        Ticket currentTicket = ticketRepository.findByTicketCode(ticketCode)
                .orElseThrow(() -> {
                    log.error("Ticket code not found for move: {}", ticketCode);
                    return new ResourceNotFoundException("Tiket tidak ditemukan");
                });

        // Cari apakah ada tiket AVAILABLE dengan nomor kursi baru tersebut di kategori yang sama
        Ticket targetTicket = ticketRepository.findByEventCategory_IdAndSeatNumberAndStatus(
                currentTicket.getEventCategory().getId(),
                newSeatNumber,
                Ticket.TicketStatus.AVAILABLE
        ).orElseThrow(() -> new BusinessException("Kursi tujuan tidak tersedia atau tidak ditemukan di kategori ini"));

        // Swap nomor kursi antara ticket lama dan ticket target
        // Ini lebih aman daripada cuma update string karena menjaga konsistensi resource
        String oldSeat = currentTicket.getSeatNumber();
        currentTicket.setSeatNumber(newSeatNumber);
        targetTicket.setSeatNumber(oldSeat);

        ticketRepository.save(currentTicket);
        ticketRepository.save(targetTicket);
    }

    @Transactional
    public void adminRefundTicket(String ticketCode) {
        Ticket ticket = ticketRepository.findByTicketCode(ticketCode)
                .orElseThrow(() -> new RuntimeException("Tiket tidak ditemukan"));

        if (ticket.getUser() == null || ticket.getStatus() != Ticket.TicketStatus.SOLD) {
            throw new RuntimeException("Tiket belum di-booking atau dibayar oleh User.");
        }

        User user = ticket.getUser();
        Double refundAmount = ticket.getEventCategory().getPrice();
        user.setBalance(user.getBalance() + refundAmount);
        userRepository.save(user);

        // Ubah Status Tiket menjadi REFUNDED (Bukan AVAILABLE) agar history tetap ada
        ticket.setStatus(Ticket.TicketStatus.REFUNDED);
        // ticket.setUser(null); Dihapus agar user tetap bisa melihat tiket ini
        ticketRepository.save(ticket);

        // Generate Tiket Pengganti berstatus AVAILABLE agar kuota kembali
        Ticket replacementTicket = new Ticket();
        replacementTicket.setEventCategory(ticket.getEventCategory());
        replacementTicket.setTicketCode(ticket.getEventCategory().getCategoryCode() + "-REP-" + System.nanoTime());
        replacementTicket.setSeatNumber(ticket.getSeatNumber());
        replacementTicket.setStatus(Ticket.TicketStatus.AVAILABLE);
        ticketRepository.save(replacementTicket);

        // Update Stok di EventCategory
        categoryRepository.increaseStock(ticket.getEventCategory().getId());

        Transaction refundTx = new Transaction();
        refundTx.setUser(user);
        refundTx.setTicket(ticket);
        refundTx.setAmountPaid(refundAmount);
        refundTx.setTransactionType(Transaction.TransactionType.REFUND);
        transactionRepository.save(refundTx);
    }

    private TicketResponse convertToResponse(Ticket ticket) {
        return TicketResponse.builder()
                .id(ticket.getId())
                .ticketCode(ticket.getTicketCode())
                .eventName(ticket.getEventCategory() != null && ticket.getEventCategory().getEvent() != null
                        ? ticket.getEventCategory().getEvent().getName()
                        : null)
                .categoryName(ticket.getEventCategory() != null ? ticket.getEventCategory().getCategoryName() : null)
                .seatNumber(ticket.getSeatNumber())
                .eventDate(ticket.getEventCategory() != null && ticket.getEventCategory().getEvent() != null
                        ? ticket.getEventCategory().getEvent().getEventDate()
                        : null)
                .location(ticket.getEventCategory() != null && ticket.getEventCategory().getEvent() != null
                        ? ticket.getEventCategory().getEvent().getLocation()
                        : null)
                .status(ticket.getStatus() != null ? ticket.getStatus().name() : null)
                .build();
    }
}
