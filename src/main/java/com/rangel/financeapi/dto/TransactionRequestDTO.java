package com.rangel.financeapi.dto;

import com.rangel.financeapi.model.Type;
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
    private String description;
    private BigDecimal amount;
    private Type type;
}
