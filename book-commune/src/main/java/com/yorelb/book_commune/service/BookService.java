package com.yorelb.book_commune.service;

import com.yorelb.book_commune.model.Book;
import com.yorelb.book_commune.model.BorrowRecord;
import com.yorelb.book_commune.model.BorrowStatus;
import com.yorelb.book_commune.repository.BookRepository;
import com.yorelb.book_commune.repository.BorrowRecordRepository;
import com.yorelb.book_commune.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class BookService {

    private final BorrowRecordRepository borrowRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    @Autowired
    public BookService(BorrowRecordRepository borrowRepository,
                         BookRepository bookRepository,
                         UserRepository userRepository) {
        this.borrowRepository = borrowRepository;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
    }

    public List<Book> findAllAvailableBooks() {
        return bookRepository.findByAvailableTrue();
    }

    public Book findBook (Long id) {
        Optional<Book> foundBook = bookRepository.findById(id);
        if (foundBook.isEmpty()) {
            throw new IllegalArgumentException("Book not found.");
        }
        return foundBook.get();
    }

    public List<Book> findAllBy(Long ownerId) {
        return bookRepository.findByOwnerId(ownerId);
    }

    public void deleteBook(Long bookId) {
        if (!bookRepository.existsById(bookId)) {
            throw new IllegalArgumentException("Cannot delete: Book not found.");
        }
        bookRepository.deleteById(bookId);
    }

    public Book addBook(Book newBook) {
        newBook.setAvailability(true);
        return bookRepository.save(newBook);
    }

    public Book updateBook(Long id, Book updatedBook) {
        Book existingBook = bookRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Can't update: Book not found."));
        existingBook.setTitle(updatedBook.getTitle());
        existingBook.setAuthor(updatedBook.getAuthor());
        existingBook.setDescription(updatedBook.getDescription());
        return bookRepository.save(existingBook);
    }
}
