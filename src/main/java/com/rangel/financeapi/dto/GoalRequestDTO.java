package com.rangel.financeapi.dto;

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
public class GoalRequestDTO {
    @NotBlank
    @Size(max=255)
    private String name;

    @NotNull
    @Positive
    private BigDecimal limitAmount;

    @NotNull
    private Long categoryId;

    @NotNull
    @Positive
    private int month;

    @NotNull
    @Positive
    private int year;
}
