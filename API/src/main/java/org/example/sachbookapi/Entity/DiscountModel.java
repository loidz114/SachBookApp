package org.example.sachbookapi.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Getter
@Setter
@Table(name = "discounts")
public class DiscountModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(unique = true)
    private String code; // SALE10, BOOK20...

    private String description;

    @Min(0)
    @Max(100)
    private Double discountPercent; // Nếu dùng giảm theo %

    @Min(0)
    private Double discountAmount; // Nếu dùng giảm cố định

    @Temporal(TemporalType.DATE)
    private Date startDate;

    @Temporal(TemporalType.DATE)
    private Date endDate;
}