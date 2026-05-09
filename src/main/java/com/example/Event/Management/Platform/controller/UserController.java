package com.example.Event.Management.Platform.controller;

import com.example.Event.Management.Platform.model.dto.RegisterRequest;
import com.example.Event.Management.Platform.model.dto.UserResponseDto;
import com.example.Event.Management.Platform.model.entity.User;
import com.example.Event.Management.Platform.model.enums.Role;
import com.example.Event.Management.Platform.model.exceptions.UserExceptions;
import com.example.Event.Management.Platform.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ResponseEntity<UserResponseDto> register(@Valid @RequestBody RegisterRequest request){
        if (userRepository.findByEmail(request.email()).isPresent()) {
            return ResponseEntity.badRequest().build();
        }

        User user = new User();
        user.setName(request.username());
        user.setEmail(request.email());
        user.setPassword(request.password());
        user.setRole(Role.ROLE_USER);

        User saved = userRepository.save(user);

        return ResponseEntity.ok(
                new UserResponseDto(
                        saved.getId(),
                        saved.getName(),
                        saved.getEmail(),
                        saved.getRole().name()
                )
        );
    }

    @GetMapping("/me")
    public UserResponseDto me(Principal principal){
        User user = userRepository.findByEmail(principal.getName()).orElseThrow();

        return new UserResponseDto(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole().name()
        );
    }
}
