package com.rangel.financeapi.service;

import com.rangel.financeapi.dto.CategoryRequestDTO;
import com.rangel.financeapi.dto.CategoryResponseDTO;
import com.rangel.financeapi.dto.TransactionResponseDTO;
import com.rangel.financeapi.model.Category;
import com.rangel.financeapi.model.Transaction;
import com.rangel.financeapi.model.User;
import com.rangel.financeapi.repository.CategoryRepository;
import com.rangel.financeapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    public CategoryResponseDTO createCategory(CategoryRequestDTO dto, String userEmail){
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Category category = Category.builder()
                .name(dto.getName())
                .user(user)
                .color(dto.getColor())
                .build();

        Category saved = categoryRepository.save(category);

        return CategoryResponseDTO.builder()
                .id(saved.getId())
                .name(saved.getName())
                .color(saved.getColor())
                .createdAt(saved.getCreatedAt())
                .build();
    }

    public List<CategoryResponseDTO> getCategoriesByUser(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return categoryRepository.findByUserId(user.getId())
                .stream()
                .map(c -> CategoryResponseDTO.builder()
                        .id(c.getId())
                        .name(c.getName())
                        .color(c.getColor())
                        .createdAt(c.getCreatedAt())
                        .build())
                .toList();
    }

    public CategoryResponseDTO getCategoryById(Long id, String userEmail){
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        if (!category.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Category not found");
        }
        return CategoryResponseDTO.builder()
                .id(category.getId())
                .name(category.getName())
                .color(category.getColor())
                .createdAt(category.getCreatedAt())
                .build();
    }
}
