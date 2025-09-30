package dk.rossen.dbdemo.model;

import java.math.BigDecimal;

public record AccountResponse(
        Long customerId,
        String accountNumber,
        String accountName,
        BigDecimal balance
) {
}
