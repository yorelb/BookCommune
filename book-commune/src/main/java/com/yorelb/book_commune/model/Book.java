package com.yorelb.book_commune.model;
import jakarta.persistence.*;

@Entity
@Table(name = "books")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String author;
    private String isbn;
    private String description;

    @Enumerated(EnumType.STRING)
    private Condition condition;

    // Many books can belong to one owner
    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    public Book() {}

    // Imagine blah blah blah...
}
