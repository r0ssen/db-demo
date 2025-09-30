package dk.rossen.dbdemo.model;

import jakarta.validation.constraints.Size;

public record NewAccountRequest(
        @Size(min = 10, max = 14, message = "Account number has to be between 10 and 14 characters")
        String customerNumber,
        String accountName
) {
}
