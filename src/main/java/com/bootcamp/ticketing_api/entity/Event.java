package com.bootcamp.ticketing_api.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "events")
@Data
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String location;

    @Column(columnDefinition = "TEXT")
    private String description;

    private LocalDateTime eventDate;

    @Enumerated(EnumType.STRING)
    private EventStatus status; // UPCOMING, ONGOING, FINISHED, CANCELLED

    private Boolean isActive = true;

    @ManyToOne
    @JoinColumn(name = "admin_id")
    private User admin;

    public enum EventStatus {
        UPCOMING, ONGOING, FINISHED, CANCELLED
    }
}
