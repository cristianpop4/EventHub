package com.example.Event.Management.Platform.service.serviceImpl;

import com.example.Event.Management.Platform.model.dto.UserRequestDto;
import com.example.Event.Management.Platform.model.dto.UserResponseDto;
import com.example.Event.Management.Platform.model.dto.UserUpdateDto;
import com.example.Event.Management.Platform.model.entity.User;
import com.example.Event.Management.Platform.model.exceptions.UserExceptions;
import com.example.Event.Management.Platform.repository.UserRepository;
import com.example.Event.Management.Platform.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository repository;

    @Override
    public UserResponseDto createUser(UserRequestDto request){
        repository.findByEmail(request.email())
                .ifPresent(user -> {
                    throw new UserExceptions.EmailAlreadyExistsException(request.email());
                });

        User user = User.builder()
                .username(request.username())
                .email(request.email())
                .password(request.password())
                .build();

        return toDto(repository.save(user));
    }

    @Override
    public UserResponseDto getUserById (Long id){
         return toDto(
                 repository.findById(id)
                         .orElseThrow(()-> new RuntimeException("User not found"))
         );
    }

    @Override
    public List<UserResponseDto> getAllUsers(){
        return repository.findAll()
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public UserResponseDto update(Long id, UserUpdateDto dto){
        User user = repository.findById(id)
                .orElseThrow(()-> new RuntimeException("User not found"));

        user.setUsername(dto.username());
        user.setEmail(dto.email());
        user.setPassword(dto.password());

        return toDto(repository.save(user));
    }

    @Override
    public void deleteUserById (Long id){
        User user = repository.findById(id)
                        .orElseThrow(()-> new RuntimeException("User not found"));

        repository.delete(user);
    }
}
