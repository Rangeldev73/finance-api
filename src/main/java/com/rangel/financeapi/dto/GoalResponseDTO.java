package com.rangel.financeapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoalResponseDTO {
    private Long id;
    private String name;
    private BigDecimal limitAmount;
    private BigDecimal currentAmount;
    private boolean exceeded;
    private int month;
    private int year;
    private Long categoryId;
}
