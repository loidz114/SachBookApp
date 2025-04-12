package org.example.sachbookapi.Controller;

import org.example.sachbookapi.Entity.BookModel;
import org.example.sachbookapi.Service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
@CrossOrigin(origins = "*") //
public class BookController {

    @Autowired
    private BookService bookService;

    // 1. Lấy tất cả sách
    @GetMapping
    public List<BookModel> getAllBooks() {
        return bookService.getAllBooks();
    }

    // 2. Lấy sách theo ID
    @GetMapping("/{id}")
    public BookModel getBookById(@PathVariable Long id) {
        return bookService.getBookById(id);
    }

    // 3. Thêm sách
    @PostMapping
    public BookModel createBook(@RequestBody BookModel book) {
        return bookService.saveBook(book);
    }

    // 4. Cập nhật sách
    @PutMapping("/{id}")
    public BookModel updateBook(@PathVariable Long id, @RequestBody BookModel book) {
        book.setId(id);
        return bookService.updateBook(book);
    }

    // 5. Xoá sách
    @DeleteMapping("/{id}")
    public void deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
    }

    // 6. Tìm theo tên sách
    @GetMapping("/search/title")
    public List<BookModel> getBooksByTitle(@RequestParam String title) {
        return bookService.getBooksByTitle(title);
    }

    // 7. Tìm theo tác giả
    @GetMapping("/search/author")
    public List<BookModel> getBooksByAuthor(@RequestParam String author) {
        return bookService.getBooksByAuthor(author);
    }

    // 8. Tìm theo khoảng giá
    @GetMapping("/search/price")
    public List<BookModel> getBooksByPriceRange(@RequestParam double min, @RequestParam double max) {
        return bookService.getBooksByPriceRange(min, max);
    }

    // 9. Tìm theo thể loại
    @GetMapping("/search/category")
    public List<BookModel> getBooksByCategory(@RequestParam String category) {
        return bookService.getBooksByCategory(category);
    }
}