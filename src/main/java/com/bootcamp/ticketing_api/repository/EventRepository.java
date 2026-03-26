package com.bootcamp.ticketing_api.repository;

import com.bootcamp.ticketing_api.entity.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    @org.springframework.data.jpa.repository.Query("SELECT e FROM Event e WHERE e.isActive = true " +
            "AND (:status IS NULL OR e.status = :status) " +
            "AND (:startDate IS NULL OR e.eventDate >= :startDate) " +
            "AND (:endDate IS NULL OR e.eventDate <= :endDate) " +
            "AND (:searchTerm IS NULL OR (CAST(e.id AS string) LIKE %:searchTerm% OR LOWER(e.name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))))")
    Page<Event> findWithFilters(
            @org.springframework.data.repository.query.Param("searchTerm") String searchTerm,
            @org.springframework.data.repository.query.Param("status") Event.EventStatus status,
            @org.springframework.data.repository.query.Param("startDate") java.time.LocalDateTime startDate,
            @org.springframework.data.repository.query.Param("endDate") java.time.LocalDateTime endDate,
            Pageable pageable);

    // Tampilan halaman awal (hanya yang aktif dan UPCOMING) tanpa filter search
    Page<Event> findAllByIsActiveTrueAndStatus(Event.EventStatus status, Pageable pageable);

    // List semua untuk Admin
    Page<Event> findAllByIsActiveTrue(Pageable pageable);

    // Validasi duplikat Global
    boolean existsByNameAndLocationAndEventDate(String name, String location, java.time.LocalDateTime eventDate);
}