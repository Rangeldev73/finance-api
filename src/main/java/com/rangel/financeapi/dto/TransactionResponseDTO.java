package com.rangel.financeapi.dto;

import com.rangel.financeapi.model.Type;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResponseDTO {
    private Long id;
    private String description;
    private BigDecimal amount;
    private Type type;
    private Long categoryId;
    private String categoryName;
    private LocalDateTime createdAt;
}