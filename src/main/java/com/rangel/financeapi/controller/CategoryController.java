package com.rangel.financeapi.controller;

import com.rangel.financeapi.dto.CategoryRequestDTO;
import com.rangel.financeapi.dto.CategoryResponseDTO;
import com.rangel.financeapi.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<CategoryResponseDTO> create(
        @Valid @RequestBody CategoryRequestDTO dto,
        @AuthenticationPrincipal UserDetails userDetails){
            return ResponseEntity.status(201)
                    .body(categoryService.createCategory(dto, userDetails.getUsername()));
    }

    @GetMapping
    public ResponseEntity<List<CategoryResponseDTO>> getAll(
            @AuthenticationPrincipal UserDetails userDetails) {
            return ResponseEntity.ok(categoryService.getCategoriesByUser(userDetails.getUsername()));
        }
}
