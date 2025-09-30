package dk.rossen.dbdemo.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record AccountDepositRequest(
        @NotBlank
        String accountNumber,

        @Positive
        BigDecimal amount
) {
}
