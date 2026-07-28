package com.yorelb.book_commune.controller;
import com.yorelb.book_commune.model.Book;
import com.yorelb.book_commune.service.BookService;
import com.yorelb.book_commune.service.BorrowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookController {


    private final BookService bookService;

    @Autowired
    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    // Add new book
    @PostMapping
    public ResponseEntity<?> addBook(@RequestBody Book book) {
        try {
            Book savedBook = bookService.addBook(book);
            return ResponseEntity.ok(savedBook);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to add book: " + e.getMessage());
        }
    }

    // list of all available books
    @GetMapping
    public ResponseEntity<List<Book>> getAllBooks() {
        List<Book> allBooks = bookService.findAllAvailableBooks();
        return ResponseEntity.ok(allBooks);
    }

    // a specific book's details by its ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getBookById(@PathVariable Long id) {
        try {
            Book foundBook = bookService.findBook(id);
            return ResponseEntity.ok(foundBook);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // all books owned by a specific user
    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<?> getBooksByOwner(@PathVariable Long ownerId) {
        try {
            List<Book> foundBooks = bookService.findAllBy(ownerId);
            return ResponseEntity.ok(foundBooks);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Edit a book's details
    @PutMapping("/{id}")
    public ResponseEntity<?> updateBook(@PathVariable Long id, @RequestBody Book updatedBook) {
        try {
            Book savedBook = bookService.updateBook(id, updatedBook);
            return ResponseEntity.ok(savedBook);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Delete a book
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBook(@PathVariable Long id) {
        try {
            bookService.deleteBook(id);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}