package dk.rossen.dbdemo.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record NewCustomerRequest(
        @NotBlank(message = "Customer number is required")
        @Size(min = 10, max = 20, message = "Customer number has to be between 10 and 20 characters")
        String customerNumber,

        @NotBlank(message = "First name is required")
        @Size(min = 2, max = 30, message = "First name has to been between 2 and 30 characters")
        String firstName,

        @NotBlank(message = "Last name is required")
        @Size(min = 2, max = 30, message = "Last name has to been between 2 and 30 characters")
        String lastName
) {
}
