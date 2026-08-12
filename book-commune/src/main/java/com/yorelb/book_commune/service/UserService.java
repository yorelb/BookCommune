package com.yorelb.book_commune.service;

import com.yorelb.book_commune.model.Book;
import com.yorelb.book_commune.model.BorrowRecord;
import com.yorelb.book_commune.model.User;
import com.yorelb.book_commune.repository.BookRepository;
import com.yorelb.book_commune.repository.BorrowRecordRepository;
import com.yorelb.book_commune.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

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
        Optional<User> foundUser =  userRepository.findById(userId);
        if (foundUser.isEmpty()) {
            throw new IllegalArgumentException("Cannot find this user.");
        }
        return foundUser.get();
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
}
