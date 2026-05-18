package com.example.Event.Management.Platform.service.serviceImpl;

import com.example.Event.Management.Platform.model.dto.RegisterRequest;
import com.example.Event.Management.Platform.model.dto.UserResponseDto;
import com.example.Event.Management.Platform.model.dto.UserUpdateDto;
import com.example.Event.Management.Platform.model.entity.User;
import com.example.Event.Management.Platform.model.enums.Role;
import com.example.Event.Management.Platform.model.exceptions.UserExceptions;
import com.example.Event.Management.Platform.repository.UserRepository;
import com.example.Event.Management.Platform.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponseDto register(RegisterRequest request) {
        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new UserExceptions.EmailAlreadyExistsException(request.email());
        }

        User user = new User();
        user.setName(request.username());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(Role.ROLE_USER);

        return toDto(userRepository.save(user));
    }

    @Override
    public UserResponseDto getMe(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(()-> new UsernameNotFoundException("User not found"));

        return toDto(user);
    }

    @Override
    public UserResponseDto updateMe(String email, UserUpdateDto update) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(()-> new UsernameNotFoundException("User not found"));

        if (update.username() != null) user.setName(update.username());
        if (update.password() != null) user.setPassword(passwordEncoder.encode(update.password()));

        return toDto(userRepository.save(user));
    }

    @Override
    public void deleteMe(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(()-> new UsernameNotFoundException("User not found"));

        userRepository.delete(user);
    }

    @Override
    public List<UserResponseDto> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public UserResponseDto getUserById(Long id) {
        return toDto(userRepository.findById(id)
                .orElseThrow(()-> new  UserExceptions.NotFoundException(id)));
    }

    @Override
    public void deleteUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(()-> new  UserExceptions.NotFoundException(id));

        userRepository.delete(user);
    }

    @Override
    public UserResponseDto changeRole(Long id, Role role) {
        User user = userRepository.findById(id)
                .orElseThrow(()-> new  UserExceptions.NotFoundException(id));

        user.setRole(role);
        return toDto(userRepository.save(user));
    }
}
