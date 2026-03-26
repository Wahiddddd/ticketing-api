package com.bootcamp.ticketing_api.repository;

import com.bootcamp.ticketing_api.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    // Laporan per event (Summary)
    @Query("SELECT SUM(CASE WHEN t.transactionType = 'PURCHASE' THEN t.amountPaid ELSE -t.amountPaid END) " +
           "FROM Transaction t WHERE t.ticket.eventCategory.event.id = :eventId")
    Double getTotalRevenueByEvent(@Param("eventId") Long eventId);

    // Laporan Global berdasarkan periode tanggal
    @Query("SELECT t FROM Transaction t WHERE t.createdAt BETWEEN :startDate AND :endDate")
    List<Transaction> findAllByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}
