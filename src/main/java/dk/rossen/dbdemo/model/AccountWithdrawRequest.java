package dk.rossen.dbdemo.model;

import java.math.BigDecimal;

public record AccountWithdrawRequest(
        String debtorAccountNumber,
        String creditorAccountNumber,
        BigDecimal amount,
        String postingText
) {
}
