package com.example.Event.Management.Platform.unitTests;

import com.example.Event.Management.Platform.model.dto.RegisterRequest;
import com.example.Event.Management.Platform.model.dto.UserResponseDto;
import com.example.Event.Management.Platform.model.dto.UserUpdateDto;
import com.example.Event.Management.Platform.model.entity.User;
import com.example.Event.Management.Platform.model.enums.Role;
import com.example.Event.Management.Platform.model.exceptions.UserExceptions;
import com.example.Event.Management.Platform.repository.UserRepository;
import com.example.Event.Management.Platform.service.notification.MailService;
import com.example.Event.Management.Platform.service.serviceImpl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class UserServiceTests {
    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private MailService mailService;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private RegisterRequest registerRequest;
    private UserUpdateDto userUpdateDto;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest(
                "Cristian",
                "cristian@example.com",
                "Password12!"
        );

        user = new User(
                1L,
                "Cristian",
                "cristian@example.com",
                "encodedPassword",
                Role.ROLE_USER
        );

        userUpdateDto = new UserUpdateDto(
                "CristianUpdated",
                "NewPassword12!"
        );
    }

    @Test
    void register_ShouldCreateUser_WhenEmailNotTaken() {
        when(userRepository.findByEmail(registerRequest.email()))
                .thenReturn(Optional.empty());

        when(passwordEncoder.encode(registerRequest.password()))
                .thenReturn("encodedPassword");

        when(userRepository.save(any(User.class)))
                .thenReturn(user);

        UserResponseDto response = userService.register(registerRequest);

        assertNotNull(response);
        assertEquals(user.getName(), response.username());
        assertEquals(user.getEmail(), response.email());

        verify(userRepository).findByEmail(registerRequest.email());
        verify(passwordEncoder).encode(registerRequest.password());
        verify(userRepository).save(argThat(saved ->
                saved.getName().equals(registerRequest.username()) &&
                        saved.getEmail().equals(registerRequest.email()) &&
                        saved.getPassword().equals("encodedPassword") &&
                        saved.getRole() == Role.ROLE_USER
        ));
        verify(mailService).sendWelcomeEmail(user.getEmail(), user.getName());
    }

    @Test
    void register_ShouldThrow_WhenEmailAlreadyExists() {
        when(userRepository.findByEmail(registerRequest.email()))
                .thenReturn(Optional.of(user));

        assertThrows(UserExceptions.EmailAlreadyExistsException.class,
                () -> userService.register(registerRequest));

        verify(userRepository, never()).save(any());
        verifyNoInteractions(mailService);
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    void getMe_ShouldReturnUser_WhenFound() {
        when(userRepository.findByEmail(user.getEmail()))
                .thenReturn(Optional.of(user));

        UserResponseDto response = userService.getMe(user.getEmail());

        assertNotNull(response);
        assertEquals(user.getEmail(), response.email());
        assertEquals(user.getName(), response.username());
    }

    @Test
    void getMe_ShouldThrow_WhenUserNotFound() {
        when(userRepository.findByEmail("missing@example.com"))
                .thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> userService.getMe("missing@example.com"));
    }

    @Test
    void updateMe_ShouldUpdateNameAndPassword_WhenBothProvided() {
        when(userRepository.findByEmail(user.getEmail()))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.encode(userUpdateDto.password()))
                .thenReturn("newEncodedPassword");

        when(userRepository.save(any(User.class)))
                .thenReturn(user);

        UserResponseDto response = userService.updateMe(user.getEmail(), userUpdateDto);

        assertNotNull(response);

        verify(userRepository).save(argThat(saved ->
                saved.getName().equals(userUpdateDto.username()) &&
                        saved.getPassword().equals("newEncodedPassword")
        ));
    }

    @Test
    void updateMe_ShouldOnlyUpdateName_WhenPasswordIsNull() {
        UserUpdateDto nameOnlyUpdate = new UserUpdateDto("CristianUpdated", null);

        when(userRepository.findByEmail(user.getEmail()))
                .thenReturn(Optional.of(user));

        when(userRepository.save(any(User.class)))
                .thenReturn(user);

        userService.updateMe(user.getEmail(), nameOnlyUpdate);

        verify(passwordEncoder, never()).encode(any());
        verify(userRepository).save(argThat(saved ->
                saved.getName().equals("CristianUpdated") &&
                        saved.getPassword().equals("encodedPassword") // unchanged from fixture
        ));
    }

    @Test
    void updateMe_ShouldOnlyUpdatePassword_WhenUsernameIsNull() {
        UserUpdateDto passwordOnlyUpdate = new UserUpdateDto(null, "NewPassword12!");

        when(userRepository.findByEmail(user.getEmail()))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.encode(passwordOnlyUpdate.password()))
                .thenReturn("newEncodedPassword");

        when(userRepository.save(any(User.class)))
                .thenReturn(user);

        userService.updateMe(user.getEmail(), passwordOnlyUpdate);

        verify(userRepository).save(argThat(saved ->
                saved.getName().equals("Cristian") && // unchanged from fixture
                        saved.getPassword().equals("newEncodedPassword")
        ));
    }

    @Test
    void updateMe_ShouldThrow_WhenUserNotFound() {
        when(userRepository.findByEmail("missing@example.com"))
                .thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> userService.updateMe("missing@example.com", userUpdateDto));

        verify(userRepository, never()).save(any());
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    void deleteMe_ShouldDeleteUser_WhenFound() {
        when(userRepository.findByEmail(user.getEmail()))
                .thenReturn(Optional.of(user));

        userService.deleteMe(user.getEmail());

        verify(userRepository).delete(user);
    }

    @Test
    void deleteMe_ShouldThrow_WhenUserNotFound() {
        when(userRepository.findByEmail("missing@example.com"))
                .thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> userService.deleteMe("missing@example.com"));

        verify(userRepository, never()).delete(any());
    }

    @Test
    void getAllUsers_ShouldReturnListOfUsers() {
        when(userRepository.findAll())
                .thenReturn(List.of(user));

        List<UserResponseDto> result = userService.getAllUsers();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(user.getEmail(), result.get(0).email());
    }

    @Test
    void getAllUsers_ShouldReturnEmptyList_WhenNoUsersExist() {
        when(userRepository.findAll())
                .thenReturn(List.of());

        List<UserResponseDto> result = userService.getAllUsers();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getUserById_ShouldReturnUser_WhenFound() {
        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        UserResponseDto response = userService.getUserById(1L);

        assertNotNull(response);
        assertEquals(user.getEmail(), response.email());
    }

    @Test
    void getUserById_ShouldThrow_WhenUserNotFound() {
        when(userRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(UserExceptions.NotFoundException.class,
                () -> userService.getUserById(99L));
    }

    @Test
    void deleteUserById_ShouldDeleteUser_WhenFound() {
        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        userService.deleteUserById(1L);

        verify(userRepository).delete(user);
    }

    @Test
    void deleteUserById_ShouldThrow_WhenUserNotFound() {
        when(userRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(UserExceptions.NotFoundException.class,
                () -> userService.deleteUserById(99L));

        verify(userRepository, never()).delete(any());
    }

    @Test
    void changeRole_ShouldUpdateRoleAndSendMail_WhenUserFound() {
        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        when(userRepository.save(any(User.class)))
                .thenReturn(user);

        UserResponseDto response = userService.changeRole(1L, Role.ROLE_ADMIN);

        assertNotNull(response);

        verify(userRepository).save(argThat(saved ->
                saved.getRole() == Role.ROLE_ADMIN
        ));
        verify(mailService).sendRoleChangeEmail(user.getEmail(), user.getName(), Role.ROLE_ADMIN);
    }

    @Test
    void changeRole_ShouldThrow_WhenUserNotFound() {
        when(userRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(UserExceptions.NotFoundException.class,
                () -> userService.changeRole(99L, Role.ROLE_ADMIN));

        verify(userRepository, never()).save(any());
        verifyNoInteractions(mailService);
    }
}
