package com.rangel.financeapi.controller;

import com.rangel.financeapi.dto.SummaryResponseDTO;
import com.rangel.financeapi.dto.TransactionRequestDTO;
import com.rangel.financeapi.dto.TransactionResponseDTO;
import com.rangel.financeapi.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping
    public ResponseEntity<TransactionResponseDTO> create(
            @RequestBody TransactionRequestDTO dto,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.status(201)
                .body(transactionService.createTransaction(dto, userDetails.getUsername()));
    }

    @GetMapping
    public ResponseEntity<List<TransactionResponseDTO>> getAll(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(transactionService.getTransactionsByUser(userDetails.getUsername()));
    }

    @GetMapping("/summary")
    public ResponseEntity<SummaryResponseDTO> getSummary(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(transactionService.getSummary(userDetails.getUsername()));
    }

    @GetMapping("/by-category")
    public ResponseEntity<List<TransactionResponseDTO>> getByCategory(
            @RequestParam Long categoryId,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(
                transactionService.getTransactionsByCategory(userDetails.getUsername(), categoryId)
        );
    }
}
