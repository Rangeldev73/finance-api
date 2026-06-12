package com.rangel.financeapi.service;

import com.rangel.financeapi.dto.UserRequestDTO;
import com.rangel.financeapi.dto.UserResponseDTO;
import com.rangel.financeapi.model.User;
import com.rangel.financeapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
   private final UserRepository userRepository;

    public UserResponseDTO createUser(UserRequestDTO dto){
        User user = User.builder()
                .name(dto.getName())
                .email(dto.getEmail())
                .password(dto.getPassword())
                .build();

        User savedUser = userRepository.save(user);

        return UserResponseDTO.builder()
                .id(savedUser.getId())
                .name(savedUser.getName())
                .email(savedUser.getEmail())
                .createdAt(savedUser.getCreatedAt())
                .build();
    }
}
