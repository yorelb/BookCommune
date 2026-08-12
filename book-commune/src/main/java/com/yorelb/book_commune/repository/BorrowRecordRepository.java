package com.yorelb.book_commune.repository;
import com.yorelb.book_commune.model.Book;
import com.yorelb.book_commune.model.BorrowRecord;
import com.yorelb.book_commune.model.BorrowStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BorrowRecordRepository extends JpaRepository<BorrowRecord, Long> {
    List<BorrowRecord> findByBorrowerId(Long borrowerId);
    List<BorrowRecord> findByStatus(BorrowStatus status);
    List<BorrowRecord> findAllByBorrowerIdAndStatusNot(Long borrowerId, BorrowStatus status);

}

