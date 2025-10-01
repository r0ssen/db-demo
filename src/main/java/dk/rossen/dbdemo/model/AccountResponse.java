package dk.rossen.dbdemo.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record AccountResponse(
        String customerNumber,
        String accountNumber,
        String accountName,
        BigDecimal balance,
        LocalDateTime createdAt
) {
}
