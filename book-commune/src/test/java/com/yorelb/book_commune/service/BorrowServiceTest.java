package com.yorelb.book_commune.service;

import com.yorelb.book_commune.model.Book;
import com.yorelb.book_commune.model.BorrowRecord;
import com.yorelb.book_commune.model.BorrowStatus;
import com.yorelb.book_commune.model.User;
import com.yorelb.book_commune.repository.BookRepository;
import com.yorelb.book_commune.repository.BorrowRecordRepository;
import com.yorelb.book_commune.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BorrowServiceTest {

    @Mock
    private BorrowRecordRepository borrowRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private BorrowService borrowService;

    private User owner;
    private User borrower;
    private Book book;
    private BorrowRecord record;

    @BeforeEach
    void setUp() {
        owner = new User();
        owner.setId(1L);

        borrower = new User();
        borrower.setId(2L);

        book = new Book();
        book.setId(10L);
        book.setOwner(owner);
        book.setAvailability(true);

        record = new BorrowRecord();
        record.setId(100L);
        record.setBook(book);
        record.setBorrower(borrower);
        record.setStatus(BorrowStatus.PENDING);
    }

    @Test
    void testBorrowBook_Success() {
        when(bookRepository.findById(10L)).thenReturn(Optional.of(book));
        when(userRepository.findById(2L)).thenReturn(Optional.of(borrower));
        when(borrowRepository.existsByBookIdAndBorrowerIdAndStatusIn(
                eq(10L), eq(2L), anyList())).thenReturn(false);

        when(borrowRepository.save(any(BorrowRecord.class))).thenAnswer(invocation -> invocation.getArgument(0));

        BorrowRecord newRecord = borrowService.borrowBook(10L, 2L);

        assertEquals(BorrowStatus.PENDING, newRecord.getStatus());
        assertEquals(book, newRecord.getBook());
        assertEquals(borrower, newRecord.getBorrower());
        assertEquals(LocalDate.now(), newRecord.getBorrowDate());
        assertEquals(LocalDate.now().plusDays(14), newRecord.getDueDate());

        verify(borrowRepository, times(1)).save(any(BorrowRecord.class));
    }

    @Test
    void testBorrowBook_FailsIfBookOrUserNotFound() {
        when(bookRepository.findById(99L)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> borrowService.borrowBook(99L, 2L));
        assertEquals("Book or User not found!", exception.getMessage());
    }

    @Test
    void testBorrowBook_FailsIfBorrowingOwnBook() {
        when(bookRepository.findById(10L)).thenReturn(Optional.of(book));
        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> borrowService.borrowBook(10L, 1L));
        assertEquals("You cannot borrow your own book.", exception.getMessage());
    }

    @Test
    void testBorrowBook_FailsIfAlreadyRequested() {
        when(bookRepository.findById(10L)).thenReturn(Optional.of(book));
        when(userRepository.findById(2L)).thenReturn(Optional.of(borrower));

        when(borrowRepository.existsByBookIdAndBorrowerIdAndStatusIn(
                eq(10L), eq(2L), anyList())).thenReturn(true);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> borrowService.borrowBook(10L, 2L));
        assertEquals("You have already requested this book.", exception.getMessage());
    }

    @Test
    void testApproveBorrowRequest_Success() {
        when(borrowRepository.findById(100L)).thenReturn(Optional.of(record));
        when(bookRepository.save(any(Book.class))).thenReturn(book);
        when(borrowRepository.save(any(BorrowRecord.class))).thenReturn(record);

        BorrowRecord approvedRecord = borrowService.approveBorrowRequest(100L);

        assertEquals(BorrowStatus.ACTIVE, approvedRecord.getStatus());
        assertFalse(approvedRecord.getBook().getAvailability());

        verify(bookRepository, times(1)).save(book);
        verify(borrowRepository, times(1)).save(record);
    }

    @Test
    void testApproveBorrowRequest_NotFound() {
        when(borrowRepository.findById(99L)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> borrowService.approveBorrowRequest(99L));
        assertEquals("Transaction not found.", exception.getMessage());
    }

    @Test
    void testDenyBorrowRequest_Success() {
        when(borrowRepository.findById(100L)).thenReturn(Optional.of(record));
        when(borrowRepository.save(any(BorrowRecord.class))).thenReturn(record);

        BorrowRecord deniedRecord = borrowService.denyBorrowRequest(100L);

        assertEquals(BorrowStatus.REJECTED, deniedRecord.getStatus());
        verify(borrowRepository, times(1)).save(record);
        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    void testGetOutgoingRequests() {
        when(borrowRepository.findByBorrowerId(2L)).thenReturn(List.of(record));

        List<BorrowRecord> records = borrowService.getOutgoingRequests(2L);

        assertEquals(1, records.size());
        assertEquals(100L, records.get(0).getId());
    }

    @Test
    void testGetIncomingRequests() {
        when(borrowRepository.findByBookOwnerId(1L)).thenReturn(List.of(record));

        List<BorrowRecord> records = borrowService.getIncomingRequests(1L);

        assertEquals(1, records.size());
        assertEquals(100L, records.get(0).getId());
    }
}
