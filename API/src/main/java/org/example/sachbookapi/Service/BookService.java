package org.example.sachbookapi.Service;

import org.example.sachbookapi.Entity.BookModel;
import org.example.sachbookapi.Repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookService {
    @Autowired
    private BookRepository bookRepository;

    // 1. Lấy tất cả sách
    public List<BookModel> getAllBooks() {
        return bookRepository.findAll();
    }

    // 2. Lấy sách theo ID
    public BookModel getBookById(Long id) {
        return bookRepository.findById(id).orElse(null);
    }

    // 3. Tìm theo tên sách (chứa keyword)
    public List<BookModel> getBooksByTitle(String title) {
        return bookRepository.findByTitleContainingIgnoreCase(title);
    }

    // 4. Tìm theo tên tác giả
    public List<BookModel> getBooksByAuthor(String author) {
        return bookRepository.findByAuthorContainingIgnoreCase(author);
    }

    // 5. Tìm theo khoảng giá
    public List<BookModel> getBooksByPriceRange(double min, double max) {
        return bookRepository.findByPriceBetween(min, max);
    }

    // 6. Tìm theo loại (nếu có category trong entity)
    public List<BookModel> getBooksByCategory(String category) {
        return bookRepository.findByCategoryIgnoreCase(category);
    }

    // 7. Thêm sách mới
    public BookModel saveBook(BookModel book) {
        return bookRepository.save(book);
    }

    // 8. Cập nhật sách (giống như thêm nhưng cần ID hợp lệ)
    public BookModel updateBook(BookModel book) {
        return bookRepository.save(book);
    }

    // 9. Xóa sách
    public void deleteBook(Long id) {
        bookRepository.deleteById(id);
    }
}
