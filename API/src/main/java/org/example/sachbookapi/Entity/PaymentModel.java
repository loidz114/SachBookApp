package org.example.sachbookapi.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Getter
@Setter
@Table(name = "payments")
public class PaymentModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "order_id", nullable = false)
    private OrderModel order;

    @NotBlank
    private String paymentMethod;

    private String transactionId;

    @NotBlank
    private String status; // PENDING, COMPLETED, FAILED

    @Temporal(TemporalType.TIMESTAMP)
    private Date paymentDate = new Date();
}