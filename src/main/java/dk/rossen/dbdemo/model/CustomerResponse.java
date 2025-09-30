package dk.rossen.dbdemo.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

public record CustomerResponse(
        Long id,
        String customerNumber,
        String firstName,
        String lastName,
        @JsonInclude(JsonInclude.Include.NON_NULL) List<AccountResponse> accountResponseList
) {
}
