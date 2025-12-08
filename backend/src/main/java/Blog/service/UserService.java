package Blog.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import Blog.helpers.validators;
import Blog.model.Author;
import Blog.model.Errors;
import Blog.model.User;
import Blog.repository.UserRepository;

@Service
public class UserService {

    public final UserRepository userRepository;
    final FollowService followservice;
    private final PasswordEncoder passwordEncoder;
    private final String Role = "USER";

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder,
            @Lazy FollowService followservice) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.followservice = followservice;
    }

    // Register a new user
    public Errors.Register_Error registerUser(User user) {
        if (userRepository.existsByUsername(user.getUsername()) || !validators.ValidatePassword(user.getUsername())) {
            return Errors.Register_Error.UsernameError;
        }
        if (!validators.ValidatePassword(user.getPassword())) {
            return Errors.Register_Error.PasswordError;
        }
        if (!validators.ValidateAvatar(user.getAvatar())) {
            user.setAvatar("default.png");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(Role);
        user.setStatus(true);
        userRepository.save(user);
        return Errors.Register_Error.Success;
    }

    // Get user by ID
    public Optional<User> getUserById(long id) {
        return userRepository.findById(id);
    }

    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    // Get all users
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Total users
    public long countUsers() {
        return userRepository.count();
    }

    public long countActiveUsers() {
        return userRepository.countByStatusTrue();
    }

    public long countSuspendedUsers() {
        return userRepository.countByStatusFalse();
    }

    public List<User> getLatestUsers() {
        return userRepository.findTop3ByOrderByCreatedAtDesc();
    }

    public long getAdminid() {
        return userRepository.findAdminUserId();
    }

    public List<Author> getUsers(long startid, String username) {
        if (startid == 1) {
            return new ArrayList<>();
        }
        if (startid == 0) {
            User u = userRepository.findTopByOrderByIdDesc();
            if (u == null) {
                return new ArrayList<>();
            }
            startid = u.getId() + 1;
        }
        User user = userRepository.findByUsername(username).orElse(null);
        List<User> usrs = userRepository.findTop20ByIdLessThanOrderByIdDesc(startid);
        List<Author> authors = usrs.stream()
                .filter(u -> !u.getUsername().equals(username))
                .map(u -> new Author(
                        u.getId(),
                        u.getAvatar(),
                        u.getUsername(),
                        user != null && followservice.isFollowing(user, u)))
                .collect(Collectors.toList());

        return authors;
    }

    public List<Author> getAdminUsers(long startid) {
        if (startid == 1) {
            return new ArrayList<>();
        }
        if (startid == 0) {
            User u = userRepository.findTopByOrderByIdDesc();
            if (u == null) {
                return new ArrayList<>();
            }
            startid = u.getId() + 1;
        }
        List<User> usrs = userRepository.findTop20ByIdLessThanOrderByIdDesc(startid);
        List<Author> authors = usrs.stream()
                .map(u -> new Author(u.getId(), u.getAvatar(), u.getUsername(), u.isStatus()))
                .collect(Collectors.toList());
        return authors;

    }

    public int togglestatus(long id) {
        return userRepository.updatestatusById(id);
    }

    public Boolean DeleteUser(long postid) {
        return userRepository.deleteUserById(postid) == 1;
    }

}
