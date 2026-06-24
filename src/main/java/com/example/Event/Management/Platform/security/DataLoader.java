package com.example.Event.Management.Platform.security;

import com.example.Event.Management.Platform.model.entity.User;
import com.example.Event.Management.Platform.model.enums.Role;
import com.example.Event.Management.Platform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class DataLoader {
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;

    @Value("${app.admin.email}")
    private String adminEmail;
    @Value("${app.admin.password}")
    private String adminPassword;

    @Value("${app.organizer.email}")
    private String organizerEmail;
    @Value("${app.organizer.password}")
    private String organizerPassword;

    @Value("${app.user.email}")
    private String userEmail;
    @Value("${app.user.password}")
    private String userPassword;

    @Bean
    CommandLineRunner init() {
        return args -> {

            if (userRepository.findByEmail("admin@eventub.com").isEmpty()){
                User admin = new User();
                admin.setName("Admin");
                admin.setEmail(adminEmail);
                admin.setPassword(encoder.encode(adminPassword));
                admin.setRole(Role.ROLE_ADMIN);
                userRepository.save(admin);
            }

            if (userRepository.findByEmail("organizer@eventhub.com").isEmpty()) {
                User organizer = new User();
                organizer.setName("Organizer");
                organizer.setEmail(organizerEmail);
                organizer.setPassword(encoder.encode(organizerPassword));
                organizer.setRole(Role.ROLE_ORGANIZER);
                userRepository.save(organizer);
            }

            if (userRepository.findByEmail("user@eventhub.com").isEmpty()) {
                User user = new User();
                user.setName("User");
                user.setEmail(userEmail);
                user.setPassword(encoder.encode(userPassword));
                user.setRole(Role.ROLE_USER);
                userRepository.save(user);
            }
        };
    }
}
