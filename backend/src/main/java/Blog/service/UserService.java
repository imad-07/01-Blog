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
    public void registerUser(User user) {
        if (userRepository.existsByUsernameIgnoreCase(user.getUsername())) {
            throw new Blog.exception.ConflictException("Username already exists");
        }
        if (!validators.ValidatePassword(user.getUsername())) {
            throw new Blog.exception.BadRequestException("Invalid username");
        }
        if (!validators.ValidatePassword(user.getPassword())) {
            throw new Blog.exception.BadRequestException("Invalid password");
        }
        if (!validators.ValidateAvatar(user.getAvatar())) {
            user.setAvatar("default.png");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(Role);
        user.setStatus(true);
        userRepository.save(user);
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
                        user != null && followservice.isFollowing(user, u),
                        u.getRole()))
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
                .filter(u -> !"ADMIN".equals(u.getRole()))
                .map(u -> new Author(u.getId(), u.getAvatar(), u.getUsername(), u.isStatus(), u.getRole()))
                .collect(Collectors.toList());
        return authors;

    }

    public int togglestatus(long id) {
        return userRepository.updatestatusById(id);
    }

    public Boolean DeleteUser(long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return true;
        }
        return false;
    }

}
