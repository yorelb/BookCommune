package com.yorelb.book_commune.model;
import jakarta.persistence.*;
import java.util.List;
import java.util.Locale;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Transient
    private int borrowedBooks;

    private String forename;
    private String surname;
    private String address;
    private String bio;
    private Role role = Role.COMMUNITY_MEMBER;
    // Using link "simulates" storing image elsewhere and using link to access
    private String profileImageUrl;

    // Relationships

    // can own many books
    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL)
    private List<Book> ownedBooks;

    // can have many borrow transactions
    @OneToMany(mappedBy = "borrower", cascade = CascadeType.ALL)
    private List<BorrowRecord> borrowRecords;

    // Getters and Setters

    public User() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) { this.id = id; }

    public String getName() {
        return forename + " " + surname;
    }

    public void setForename (String forename) { this.forename = forename; }

    public void setSurname (String surname) { this.surname = surname; }

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

    public String getUsername() {return username;}

    public void setUsername(String username) {this.username = username;}

    public String getPassword() {return password;}

    public void setPassword(String password) {this.password = password;}

    public void setRole(Role role) {this.role = role;}

    public Role getRole() {return role;}

    public void setBio(String bio) {this.bio = bio;}

    public String getBio() {return bio;}

    public String getProfileImageUrl() { return profileImageUrl; }

    public void setProfileImageUrl(String profileImageUrl) { this.profileImageUrl = profileImageUrl; }

    public int getBorrowedBooks() {return borrowedBooks;}

    public void setBorrowedBooks(int borrowedBooks) {this.borrowedBooks = borrowedBooks;}

    private String roleToString (Role role) {
        String roleInString = " ";
        String [] roleInStringBits = role.toString().split("_");
        for (int n = 0; n < roleInStringBits.length; n++) {
            roleInStringBits[n] = roleInStringBits[n].substring(0,1).toUpperCase() + roleInStringBits[n].substring(1).toLowerCase();
            roleInString = roleInString + " " + roleInStringBits[n];
        }
        return roleInString.trim();
    }

    public String getRoleName() {
        return roleToString(this.role);
    }


}