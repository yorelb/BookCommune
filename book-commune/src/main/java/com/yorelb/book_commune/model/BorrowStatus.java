package com.yorelb.book_commune.model;

public enum BorrowStatus {
    PENDING,
    ACTIVE,
    RETURNED,
    OVERDUE,
    REJECTED
}
//NOTE: The rejected should be for if a user rejects to lend the book to another user