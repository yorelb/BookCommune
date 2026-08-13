package com.yorelb.book_commune.service;
import com.yorelb.book_commune.model.Book;
import com.yorelb.book_commune.model.BorrowRecord;
import com.yorelb.book_commune.model.BorrowStatus;
import com.yorelb.book_commune.model.User;
import com.yorelb.book_commune.repository.BookRepository;
import com.yorelb.book_commune.repository.BorrowRecordRepository;
import com.yorelb.book_commune.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class BorrowService {

    private final BorrowRecordRepository borrowRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    // To avoid field injection
    @Autowired
    public BorrowService(BorrowRecordRepository borrowRepository,
                         BookRepository bookRepository,
                         UserRepository userRepository) {
        this.borrowRepository = borrowRepository;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
    }

    // Borrowing a book. This is assuming user has clicked on the book
    public BorrowRecord borrowBook(Long bookId, Long borrowerId) {

        // Look for the book and user from the database
        Optional<Book> bookOpt = bookRepository.findById(bookId);
        Optional<User> userOpt = userRepository.findById(borrowerId);

        if (bookOpt.isEmpty() || userOpt.isEmpty()) {
            throw new IllegalArgumentException("Book or User not found!");
        }

        Book book = bookOpt.get();
        User borrower = userOpt.get();

        // Cant borrow own book
        if (book.getOwner().getId().equals(borrowerId)) {
            throw new IllegalArgumentException("You cannot borrow your own book.");
        }

        // Make the transaction
        BorrowRecord record = new BorrowRecord();
        record.setBook(book);
        record.setBorrower(borrower);
        record.setBorrowDate(LocalDate.now());
        record.setDueDate(LocalDate.now().plusDays(14)); //TODO: Set a constant for borrow periods
        record.setStatus(BorrowStatus.PENDING); //For pending, it switches to active once owner approves

        return borrowRepository.save(record);
    }

    //Approving a borrow request. Assuming user has clicked approve
    public BorrowRecord approveBorrowRequest(Long recordId) {
        Optional<BorrowRecord> recordOpt = borrowRepository.findById(recordId);

        if (recordOpt.isEmpty()) {
            throw new IllegalArgumentException("Transaction not found.");
        }

        BorrowRecord record = recordOpt.get();
        record.setStatus(BorrowStatus.ACTIVE); //Active once a user accepts to lend
        Book borrowedBook = record.getBook();
        borrowedBook.setAvailability(false);
        bookRepository.save(borrowedBook);
        return borrowRepository.save(record);
    }

}
//TODO: Try write tests for these