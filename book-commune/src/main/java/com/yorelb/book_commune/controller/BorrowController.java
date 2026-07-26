package com.yorelb.book_commune.controller;

import com.yorelb.book_commune.model.BorrowRecord;
import com.yorelb.book_commune.service.BorrowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/borrow")
public class BorrowController {

    private final BorrowService borrowService;

    @Autowired
    public BorrowController(BorrowService borrowService) {
        this.borrowService = borrowService;
    }

    // Request to borrow a book
    @PostMapping("/request")
    public ResponseEntity<?> requestBorrow(@RequestParam Long bookId, @RequestParam Long borrowerId) {
        try {
            BorrowRecord record = borrowService.borrowBook(bookId, borrowerId);
            return ResponseEntity.ok(record);
        } catch (IllegalArgumentException e) {
            // If they try to borrow their own
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Owner approves borrow request
    @PostMapping("/{recordId}/approve")
    public ResponseEntity<?> approveBorrow(@PathVariable Long recordId) {
        try {
            BorrowRecord record = borrowService.approveBorrowRequest(recordId);
            return ResponseEntity.ok(record);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}