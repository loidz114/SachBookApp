package org.example.sachbookapi.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;

import java.util.Date;

@Entity
@Getter
@Setter
@Table(name = "books")
public class BookModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Title cannot be blank")
    @Column(nullable = false)
    private String title;

    @NotBlank(message = "Author cannot be blank")
    @Column(nullable = false)
    private String author;

    @Lob
    private String description;

    @Positive(message = "Price must be positive")
    @Column(nullable = false)
    private Double price;

    @Min(value = 0, message = "Quantity must be >= 0")
    @Column(nullable = false)
    private Integer quantity;

    @URL(message = "Image URL is invalid")
    private String imageUrl;

    @ManyToOne(optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private CategoryModel category;

    @ManyToOne
    @JoinColumn(name = "discount_id") // nullable mặc định là true
    private DiscountModel discount;

    @PastOrPresent(message = "Created date must be in the past or present")
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false, updatable = false)
    private Date createdAt;

    @NotNull(message = "Availability status must be set")
    @Column(nullable = false)
    private Boolean isAvailable;

    // Gán tự động thời điểm tạo sách
    @PrePersist
    protected void onCreate() {
        this.createdAt = new Date();
    }
}