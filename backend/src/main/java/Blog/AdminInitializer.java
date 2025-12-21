package Blog;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import Blog.model.User;
import Blog.repository.UserRepository;

@Component
public class AdminInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (!userRepository.existsByUsernameIgnoreCase("admin")) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setAvatar("admin.jpg");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setStatus(true);
            admin.setRole("ADMIN");
            userRepository.save(admin);
            System.out.println("Admin account created: username=admin, password=admin");
        } else {
            System.out.println("Admin account already exists.");
        }
    }
}
