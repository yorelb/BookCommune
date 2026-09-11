package com.yorelb.book_commune.service;

import com.yorelb.book_commune.model.Book;
import com.yorelb.book_commune.repository.BookRepository;
import com.yorelb.book_commune.repository.BorrowRecordRepository;
import com.yorelb.book_commune.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BorrowRecordRepository borrowRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private BookService bookService;

    private Book testBook;

    @BeforeEach
    void setUp() {
        testBook = new Book();
        testBook.setId(1L);
        testBook.setTitle("1984");
        testBook.setAuthor("George Orwell");
        testBook.setDescription("Dystopian novel");
        testBook.setAvailability(true);
    }

    @Test
    void testFindAllBooks() {
        when(bookRepository.findAll()).thenReturn(List.of(testBook));
        List<Book> books = bookService.findAllBooks();
        assertEquals(1, books.size());
        assertEquals("1984", books.get(0).getTitle());
    }

    @Test
    void testFindAllAvailableBooks() {
        when(bookRepository.findByAvailableTrue()).thenReturn(List.of(testBook));
        List<Book> availableBooks = bookService.findAllAvailableBooks();
        assertEquals(1, availableBooks.size());
        assertTrue(availableBooks.get(0).getAvailability());
    }

    @Test
    void testFindBook_Success() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));
        Book foundBook = bookService.findBook(1L);
        assertEquals("1984", foundBook.getTitle());
    }

    @Test
    void testFindBook_NotFound() {
        when(bookRepository.findById(99L)).thenReturn(Optional.empty());
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> bookService.findBook(99L));
        assertEquals("Book not found.", exception.getMessage());
    }

    @Test
    void testFindAllByOwnerId() {
        when(bookRepository.findByOwnerId(5L)).thenReturn(List.of(testBook));
        List<Book> ownerBooks = bookService.findAllBy(5L);
        assertEquals(1, ownerBooks.size());
    }

    @Test
    void testAddBook_ForcesAvailabilityToTrue() {
        Book newBook = new Book();
        newBook.setTitle("The Hobbit");
        newBook.setAvailability(false);

        when(bookRepository.save(any(Book.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Book savedBook = bookService.addBook(newBook);

        assertTrue(savedBook.getAvailability());
        verify(bookRepository, times(1)).save(newBook);
    }

    @Test
    void testUpdateBook_Success() {
        Book updateInfo = new Book();
        updateInfo.setTitle("1984 - Revised");
        updateInfo.setAuthor("G. Orwell");
        updateInfo.setDescription("Updated description");

        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));
        when(bookRepository.save(any(Book.class))).thenReturn(testBook);

        Book updatedBook = bookService.updateBook(1L, updateInfo);

        assertEquals("1984 - Revised", updatedBook.getTitle());
        assertEquals("G. Orwell", updatedBook.getAuthor());
        assertEquals("Updated description", updatedBook.getDescription());
    }

    @Test
    void testUpdateBook_NotFound() {
        Book updateInfo = new Book();
        when(bookRepository.findById(99L)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> bookService.updateBook(99L, updateInfo));
        assertEquals("Can't update: Book not found.", exception.getMessage());
    }

    @Test
    void testDeleteBook_Success() {
        when(bookRepository.existsById(1L)).thenReturn(true);
        bookService.deleteBook(1L);

        verify(bookRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteBook_NotFound() {
        when(bookRepository.existsById(99L)).thenReturn(false);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> bookService.deleteBook(99L));
        assertEquals("Cannot delete: Book not found.", exception.getMessage());

        verify(bookRepository, never()).deleteById(anyLong());
    }
}