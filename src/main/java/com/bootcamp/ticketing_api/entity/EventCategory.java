package com.bootcamp.ticketing_api.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "event_categories")
@Data
public class EventCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;

    private String categoryName; // Contoh: VIP, Reguler

    @Column(unique = true)
    private String categoryCode;

    private Double price;
    private Integer totalCapacity;
    private Integer remainingCapacity;
}
