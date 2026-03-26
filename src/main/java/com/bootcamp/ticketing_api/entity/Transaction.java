package com.bootcamp.ticketing_api.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@Data
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "ticket_id")
    private Ticket ticket;

    private Double amountPaid;

    @Enumerated(EnumType.STRING)
    private TransactionType transactionType; // PURCHASE, REFUND

    private LocalDateTime createdAt = LocalDateTime.now();

    public enum TransactionType {
        PURCHASE, REFUND
    }
}
