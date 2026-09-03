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

    // Fetch requests made by the user
    @GetMapping("/outgoing/{userId}")
    public ResponseEntity<?> getOutgoingRequests(@PathVariable Long userId) {
        try {
            return ResponseEntity.ok(borrowService.getOutgoingRequests(userId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Fetch requests made to the user for their books
    @GetMapping("/incoming/{userId}")
    public ResponseEntity<?> getIncomingRequests(@PathVariable Long userId) {
        try {
            return ResponseEntity.ok(borrowService.getIncomingRequests(userId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Owner denies borrow request
    @PostMapping("/{recordId}/deny")
    public ResponseEntity<?> denyBorrow(@PathVariable Long recordId) {
        try {
            BorrowRecord record = borrowService.denyBorrowRequest(recordId);
            return ResponseEntity.ok(record);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}