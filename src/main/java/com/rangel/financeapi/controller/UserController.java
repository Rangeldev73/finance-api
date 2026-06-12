package com.rangel.financeapi.controller;

import com.rangel.financeapi.dto.UserRequestDTO;
import com.rangel.financeapi.dto.UserResponseDTO;
import com.rangel.financeapi.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserResponseDTO> createUser(@RequestBody UserRequestDTO dto){
        UserResponseDTO response = userService.createUser(dto);
        return ResponseEntity.status(201).body(response);
    }
}
