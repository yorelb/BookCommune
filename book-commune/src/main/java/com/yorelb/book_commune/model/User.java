package com.yorelb.book_commune.model;
import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;
    private String address;

    // can own many books
    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL)
    private List<Book> ownedBooks;

    // can have many borrow transactions
    @OneToMany(mappedBy = "borrower", cascade = CascadeType.ALL)
    private List<BorrowRecord> borrowRecords;

    public User() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName (String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail (String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress (String address) {
        this.address = address;
    }
}