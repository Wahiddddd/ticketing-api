package com.bootcamp.ticketing_api.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tickets")
@Data
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "event_category_id")
    private EventCategory eventCategory;

    @Column(unique = true)
    private String ticketCode;

    private String seatNumber;

    @Enumerated(EnumType.STRING)
    private TicketStatus status; // AVAILABLE, SOLD, BROKEN, REFUNDED

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user; // Akan terisi jika status SOLD

    public enum TicketStatus {
        AVAILABLE, SOLD, BROKEN, REFUNDED
    }
}
