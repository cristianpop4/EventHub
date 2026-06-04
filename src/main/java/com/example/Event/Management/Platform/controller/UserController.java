package com.example.Event.Management.Platform.controller;

import com.example.Event.Management.Platform.model.dto.*;
import com.example.Event.Management.Platform.model.enums.Role;
import com.example.Event.Management.Platform.security.JwtService;
import com.example.Event.Management.Platform.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String token = jwtService.generateToken(userDetails);

        return ResponseEntity.ok(new AuthResponse(
                token,
                userDetails.getUsername(),
                userDetails.getAuthorities().iterator().next().getAuthority()
        ));
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponseDto> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(userService.register(request));
    }

    @GetMapping("/me")
    public UserResponseDto me(Principal principal) {
        return userService.getMe(principal.getName());
    }

    @PutMapping("/me")
    public UserResponseDto updateMe(Principal principal, @Valid @RequestBody UserUpdateDto update) {
        return userService.updateMe(principal.getName(), update);
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteMe(Principal principal) {
        userService.deleteMe(principal.getName());
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public List<UserResponseDto> getAllUsers(){
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public UserResponseDto getUserById(@PathVariable Long id){
        return userService.getUserById(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUserById(@PathVariable Long id){
        userService.deleteUserById(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/role")
    public UserResponseDto changeRole(@PathVariable Long id, @RequestParam Role role){
        return userService.changeRole(id, role);
    }
}
