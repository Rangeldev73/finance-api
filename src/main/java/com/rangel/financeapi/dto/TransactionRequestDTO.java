package com.rangel.financeapi.dto;

import com.rangel.financeapi.model.Type;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionRequestDTO {
    @NotBlank
    @Size(max=255)
    private String description;

    @NotNull
    @Positive
    private BigDecimal amount;

    @NotNull
    private Type type;

    @NotNull
    private Long categoryId;
}
