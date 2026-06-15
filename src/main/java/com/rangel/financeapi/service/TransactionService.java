package com.rangel.financeapi.service;

import com.rangel.financeapi.dto.TransactionRequestDTO;
import com.rangel.financeapi.dto.TransactionResponseDTO;
import com.rangel.financeapi.model.Category;
import com.rangel.financeapi.model.Transaction;
import com.rangel.financeapi.model.User;
import com.rangel.financeapi.repository.CategoryRepository;
import com.rangel.financeapi.repository.TransactionRepository;
import com.rangel.financeapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    public TransactionResponseDTO createTransaction(TransactionRequestDTO dto, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Category category = null;
        if (dto.getCategoryId() != null) {
            category = categoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Category not found"));
        }

        Transaction transaction = Transaction.builder()
                .description(dto.getDescription())
                .amount(dto.getAmount())
                .type(dto.getType())
                .user(user)
                .category(category)
                .build();

        Transaction saved = transactionRepository.save(transaction);

        return TransactionResponseDTO.builder()
                .id(saved.getId())
                .description(saved.getDescription())
                .amount(saved.getAmount())
                .type(saved.getType())
                .createdAt(saved.getCreatedAt())
                .categoryId(saved.getCategory() != null ? saved.getCategory().getId() : null)
                .build();
    }

    public List<TransactionResponseDTO> getTransactionsByUser(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return transactionRepository.findByUserId(user.getId())
                .stream()
                .map(t -> TransactionResponseDTO.builder()
                        .id(t.getId())
                        .description(t.getDescription())
                        .amount(t.getAmount())
                        .type(t.getType())
                        .createdAt(t.getCreatedAt())
                        .categoryId(t.getCategory() != null ? t.getCategory().getId() : null)
                        .build())
                .toList();
    }
}
