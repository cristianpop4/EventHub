package com.example.Event.Management.Platform.security;

import com.example.Event.Management.Platform.model.entity.User;
import com.example.Event.Management.Platform.model.enums.Role;
import com.example.Event.Management.Platform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class DataLoader {
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;

    @Bean
    CommandLineRunner init() {
        return args -> {

            if (userRepository.findByEmail("admin@eventub.com").isEmpty()){
                User admin = new User();
                admin.setName("admin");
                admin.setEmail("admin@eventub.com");
                admin.setPassword(encoder.encode("admin123"));
                admin.setRole(Role.ROLE_ADMIN);
                userRepository.save(admin);
            }

            if (userRepository.findByEmail("organizer@eventhub.com").isEmpty()) {
                User organizer = new User();
                organizer.setName("Organizer");
                organizer.setEmail("organizer@eventhub.com");
                organizer.setPassword(encoder.encode("organizer123"));
                organizer.setRole(Role.ROLE_ORGANIZER);
                userRepository.save(organizer);
            }

            if (userRepository.findByEmail("user@eventhub.com").isEmpty()) {
                User user = new User();
                user.setName("User");
                user.setEmail("user@eventhub.com");
                user.setPassword(encoder.encode("user123"));
                user.setRole(Role.ROLE_USER);
                userRepository.save(user);
            }
        };
    }
}
