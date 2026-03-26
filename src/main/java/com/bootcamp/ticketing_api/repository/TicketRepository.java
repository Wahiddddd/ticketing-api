package com.bootcamp.ticketing_api.repository;

import com.bootcamp.ticketing_api.entity.Ticket;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    Optional<Ticket> findByTicketCode(String ticketCode);

    // Mencari tiket tersedia dengan ID terkecil (Flowchart Image 11)
    Optional<Ticket> findFirstByEventCategoryIdAndStatusOrderByIdAsc(Long categoryId, Ticket.TicketStatus status);

    Optional<Ticket> findByEventCategory_IdAndSeatNumberAndStatus(Long categoryId, String seatNumber, Ticket.TicketStatus status);

    // Menampilkan tiket milik user (Flowchart Image 12)
    Page<Ticket> findAllByUserIdAndStatusNot(String userId, Ticket.TicketStatus status, Pageable pageable);

    // Menghitung tiket terjual untuk update kuota (Flowchart Image 7)
    long countByEventCategoryIdAndStatus(Long categoryId, Ticket.TicketStatus status);

    // Cek apakah ada tiket dengan status tertentu di sebuah event (Flowchart Image
    // 8)
    boolean existsByEventCategory_Event_IdAndStatus(Long eventId, Ticket.TicketStatus status);
}
