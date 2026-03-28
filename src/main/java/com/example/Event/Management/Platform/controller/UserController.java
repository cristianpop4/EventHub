package com.example.Event.Management.Platform.controller;

import com.example.Event.Management.Platform.dto.UserRequestDto;
import com.example.Event.Management.Platform.dto.UserResponseDto;
import com.example.Event.Management.Platform.dto.UserUpdateDto;
import com.example.Event.Management.Platform.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService service;

    @PostMapping
    public UserResponseDto createUser(@RequestBody UserRequestDto dto) {
        return service.createUser(dto);
    }

    @GetMapping("{id}")
    public UserResponseDto getUserById(@PathVariable Long id) {
        return service.getUserById(id);
    }

    @GetMapping
    public List<UserResponseDto> getAllUsers(){
        return service.getAllUsers();
    }

    @PutMapping("{id}")
    public UserResponseDto update(@PathVariable Long id, @RequestBody UserUpdateDto dto){
        return service.update(id, dto);
    }

    @DeleteMapping("{id}")
    public void deleteUserById (@PathVariable Long id){
        service.deleteUserById(id);
    }

}
