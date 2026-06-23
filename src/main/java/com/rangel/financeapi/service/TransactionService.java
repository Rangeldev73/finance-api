package com.rangel.financeapi.service;

import com.rangel.financeapi.dto.SummaryResponseDTO;
import com.rangel.financeapi.dto.TransactionRequestDTO;
import com.rangel.financeapi.dto.TransactionResponseDTO;
import com.rangel.financeapi.model.Category;
import com.rangel.financeapi.model.Transaction;
import com.rangel.financeapi.model.Type;
import com.rangel.financeapi.model.User;
import com.rangel.financeapi.repository.CategoryRepository;
import com.rangel.financeapi.repository.TransactionRepository;
import com.rangel.financeapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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

    public SummaryResponseDTO getSummary(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        BigDecimal totalIncome = transactionRepository.sumByUserAndType(user.getId(), Type.INCOME);
        BigDecimal totalExpenses = transactionRepository.sumByUserAndType(user.getId(), Type.EXPENSES);
        BigDecimal balance = totalIncome.subtract(totalExpenses);

        return SummaryResponseDTO.builder()
                .totalIncome(totalIncome)
                .totalExpenses(totalExpenses)
                .balance(balance)
                .build();
    }

    public List<TransactionResponseDTO> getTransactionsByCategory(String userEmail, Long categoryId) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return transactionRepository.findByUserIdAndCategoryId(user.getId(), categoryId)
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

    public TransactionResponseDTO getTransactionById(Long id, String userEmail){
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));
        if (!transaction.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Transaction not found");
        }
        return TransactionResponseDTO.builder()
                .id(transaction.getId())
                .description(transaction.getDescription())
                .amount(transaction.getAmount())
                .type(transaction.getType())
                .createdAt(transaction.getCreatedAt())
                .categoryId(transaction.getCategory() != null ? transaction.getCategory().getId() : null)
                .build();
    }

    public void deleteTransaction(Long id, String userEmail){
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));
        if (!transaction.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Transaction not found");
        }
        transactionRepository.delete(transaction);
    }

    public TransactionResponseDTO updateTransaction(Long id, TransactionRequestDTO dto, String userEmail){
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));
        if (!transaction.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Transaction not found");
        }
        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));
        transaction.setCategory(category);
        transaction.setDescription(dto.getDescription());
        transaction.setAmount(dto.getAmount());
        transaction.setType(dto.getType());
        transactionRepository.save(transaction);
        return TransactionResponseDTO.builder()
                .id(transaction.getId())
                .description(transaction.getDescription())
                .categoryId(transaction.getCategory().getId())
                .amount(transaction.getAmount())
                .type(transaction.getType())
                .createdAt(transaction.getCreatedAt())
                .build();
    }

    public List<TransactionResponseDTO> filterByPeriod(String userEmail, LocalDate startDate, LocalDate endDate){
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);
        List<Transaction> transactions = transactionRepository.findByUserAndCreatedAtBetween(user, startDateTime, endDateTime);
        return transactions
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

    public Page<TransactionResponseDTO> getTransactionsPaged(String userEmail, int page, int size){
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Pageable pageable = PageRequest.of(page, size);
        Page<Transaction> transactions = transactionRepository.findByUserId(user.getId(), pageable);
        return transactions.map(t -> TransactionResponseDTO.builder()
                        .id(t.getId())
                        .description(t.getDescription())
                        .amount(t.getAmount())
                        .type(t.getType())
                        .createdAt(t.getCreatedAt())
                        .categoryId(t.getCategory() != null ? t.getCategory().getId() : null)
                        .build());
    }
}
