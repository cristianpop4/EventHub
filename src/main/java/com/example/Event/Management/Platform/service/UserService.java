package com.example.Event.Management.Platform.service;

import com.example.Event.Management.Platform.model.dto.RegisterRequest;
import com.example.Event.Management.Platform.model.dto.UserResponseDto;
import com.example.Event.Management.Platform.model.dto.UserUpdateDto;
import com.example.Event.Management.Platform.model.entity.User;
import com.example.Event.Management.Platform.model.enums.Role;

import java.util.List;

public interface UserService {
    UserResponseDto register(RegisterRequest request);
    UserResponseDto getMe(String email);
    UserResponseDto updateMe(String email, UserUpdateDto update);
    void deleteMe(String email);
    List<UserResponseDto> getAllUsers();
    UserResponseDto getUserById(Long id);
    void deleteUserById(Long id);
    UserResponseDto changeRole(Long id, Role role);


    default UserResponseDto toDto(User user){
        return new UserResponseDto(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPassword()
        );
    }
}
