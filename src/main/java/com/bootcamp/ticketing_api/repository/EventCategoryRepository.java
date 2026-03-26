package com.bootcamp.ticketing_api.repository;

import com.bootcamp.ticketing_api.entity.EventCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventCategoryRepository extends JpaRepository<EventCategory, Long> {
    List<EventCategory> findAllByEventId(Long eventId);

    // Get specific category by Event ID and Code
    EventCategory findByEventIdAndCategoryCode(Long eventId, String categoryCode);

    // Cek duplikasi kode kategori dalam satu event (Flowchart Image 6)
    boolean existsByEventIdAndCategoryCode(Long eventId, String categoryCode);

    // Mengurangi stok secara aman (Atomic Update)
    @Modifying
    @Query("UPDATE EventCategory c SET c.remainingCapacity = c.remainingCapacity - 1 " +
            "WHERE c.id = :categoryId AND c.remainingCapacity > 0")
    int decreaseStock(@Param("categoryId") Long categoryId);

    // Mengembalikan stok secara aman (Atomic Update)
    @Modifying
    @Query("UPDATE EventCategory c SET c.remainingCapacity = c.remainingCapacity + 1 " +
            "WHERE c.id = :categoryId")
    void increaseStock(@Param("categoryId") Long categoryId);
}
