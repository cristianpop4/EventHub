package com.example.Event.Management.Platform;

import com.example.Event.Management.Platform.model.dto.UserRequestDto;
import com.example.Event.Management.Platform.model.dto.UserResponseDto;
import com.example.Event.Management.Platform.model.entity.User;
import com.example.Event.Management.Platform.repository.UserRepository;
import com.example.Event.Management.Platform.service.serviceImpl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void createUser_success(){

        UserRequestDto request = new UserRequestDto("user", "user@gmail.com", "user123");

        User user = new User();
        user.setId(1L);
        user.setEmail(request.email());
        user.setUsername(request.username());
        user.setPassword(request.password());

        when(userRepository.findByEmail(request.email()))
                .thenReturn(Optional.empty());

        when(userRepository.save(any(User.class)))
                .thenReturn(user);

        UserResponseDto response = userService.createUser(request);

        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals("user", response.username());
        assertEquals("user@gmail.com", response.email());
        assertEquals("user123", response.password());

        verify(userRepository).save(any(User.class));
    }

    @Test
    void createUser_failed_userWithEmailAlreadyExists(){
        UserRequestDto request = new UserRequestDto("user", "user@gmail.com", "user123");

        User user = new User();
        user.setId(1L);
        user.setEmail(request.email());
        user.setUsername(request.username());
        user.setPassword(request.password());

        when(userRepository.findByEmail(request.email()))
                .thenReturn(Optional.of(user));

        RuntimeException exception = assertThrows(
                RuntimeException.class, ()-> userService.createUser(request));

        verify(userRepository, never()).save(any());
    }
}
