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
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> register(@RequestBody UserRequestDTO dto) {
        return ResponseEntity.status(201).body(userService.createUser(dto));
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody UserRequestDTO dto) {
        return ResponseEntity.ok(userService.login(dto));
    }
}
