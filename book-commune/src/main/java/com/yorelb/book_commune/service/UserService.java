package com.yorelb.book_commune.service;

import com.yorelb.book_commune.model.Book;
import com.yorelb.book_commune.model.BorrowRecord;
import com.yorelb.book_commune.model.BorrowStatus;
import com.yorelb.book_commune.model.User;
import com.yorelb.book_commune.repository.BookRepository;
import com.yorelb.book_commune.repository.BorrowRecordRepository;
import com.yorelb.book_commune.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final BorrowRecordRepository borrowRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(BorrowRecordRepository borrowRepository,
                       BookRepository bookRepository,
                       UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.borrowRepository = borrowRepository;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    //Add a user to the db
    public User addUser(User user) {
        //Encrypt before save
        String plainTextPassword = user.getPassword();
        String encryptedPassword = passwordEncoder.encode(plainTextPassword);
        user.setPassword(encryptedPassword);

        return userRepository.save(user);
    }

    //Edit user details
    public User updateUser (Long id, User updatedUser) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Can't update: User no found"));

        if (updatedUser.getUsername() != null && !updatedUser.getUsername().isEmpty()) {
            //Checking if the username is taken, if so, does it belong to current user?
            Optional<User> userWithSame = userRepository.findByUsername(updatedUser.getUsername());
            if (userWithSame.isPresent() && !userWithSame.get().getId().equals(id)) {
                throw new IllegalArgumentException("That username is already taken. Please choose another one.");
            }

            existingUser.setUsername(updatedUser.getUsername());
        }
        if (updatedUser.getAddress() != null && !updatedUser.getAddress().isEmpty()) {
            existingUser.setAddress(updatedUser.getAddress());
        }
        if (updatedUser.getBio() != null && !updatedUser.getBio().isEmpty()) {
            existingUser.setBio(updatedUser.getBio());
        }
        if (updatedUser.getProfileImageUrl() != null && !updatedUser.getProfileImageUrl().isEmpty()) {
            existingUser.setProfileImageUrl(updatedUser.getProfileImageUrl());
        }

        return userRepository.save(existingUser);
    }

    //Delete a user
    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new IllegalArgumentException("This user does not exist.");
        }
        userRepository.deleteById(userId);
    }

    //Find a user by id
    public User findUser(Long userId) {
        User foundUser =  userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<BorrowRecord> borrowedList = borrowRepository.findAllByBorrowerIdAndStatusNot(userId, BorrowStatus.REJECTED);
        foundUser.setBorrowedBooks(borrowedList.size());
        foundUser.setLentBooks(allLentByUser(userId));
        return foundUser;
    }

    //Find all users
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    // a user trying to log in
    public User verifyLogin(String username, String rawPassword) {
        User userToLogin = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Invalid username or password."));

        if (!passwordEncoder.matches(rawPassword, userToLogin.getPassword())) {
            throw new IllegalArgumentException("Invalid username or password.");
        }

        return userToLogin;
    }

    // Count occurrences of each book a user owns in the borrow record table
    private int allLentByUser(Long userId) {
        return borrowRepository.countByBookOwnerIdAndStatusNot(userId,BorrowStatus.REJECTED );
    }

    // Change user's email
    public User changeEmail(Long id, String currentEmail, String newEmail, String rawPassword) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!existingUser.getEmail().equalsIgnoreCase(currentEmail)) {
            throw new IllegalArgumentException("The current email you entered is incorrect.");
        }
        if (!passwordEncoder.matches(rawPassword, existingUser.getPassword())) {
            throw new IllegalArgumentException("Incorrect password.");
        }

        Optional<User> userWithSameEmail = Optional.ofNullable(userRepository.findByEmail(newEmail));

        if (userWithSameEmail.isPresent() && !userWithSameEmail.get().getId().equals(id)) {
            throw new IllegalArgumentException("That email address is already in use.");
        }

        existingUser.setEmail(newEmail);
        return userRepository.save(existingUser);
    }

    // Change password
    public User changePassword(Long id, String currentPassword, String newPassword) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!passwordEncoder.matches(currentPassword, existingUser.getPassword())) {
            throw new IllegalArgumentException("The current password you entered is incorrect.");
        }

        String encryptedNewPassword = passwordEncoder.encode(newPassword);
        existingUser.setPassword(encryptedNewPassword);

        return userRepository.save(existingUser);
    }
}
