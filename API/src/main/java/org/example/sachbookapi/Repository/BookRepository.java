package org.example.sachbookapi.Repository;

import org.example.sachbookapi.Entity.BookModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookRepository extends JpaRepository<BookModel, Long> {
    List<BookModel> findByTitleContainingIgnoreCase(String title);
    List<BookModel> findByAuthorContainingIgnoreCase(String author);
    List<BookModel> findByPriceBetween(double min, double max);
    List<BookModel> findByCategoryIgnoreCase(String category);
}