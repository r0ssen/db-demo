package dk.rossen.dbdemo.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record AccountPosting(
        LocalDateTime time,
        String debtorAccountNumber,
        String creditorAccountNumber,
        BigDecimal amount,
        String postingText,
        BigDecimal balance
) {
}
