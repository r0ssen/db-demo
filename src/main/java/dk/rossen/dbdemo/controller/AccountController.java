package dk.rossen.dbdemo.controller;

import dk.rossen.dbdemo.model.*;
import dk.rossen.dbdemo.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/accounts")
@RequiredArgsConstructor
public class AccountController {
    private final AccountService accountService;

    @PostMapping
    public AccountResponse createAccount(@RequestBody @Valid NewAccountRequest newAccountRequest) {
        return accountService.createAccount(newAccountRequest);
    }

    @GetMapping("/{account-number}")
    public AccountResponse getAccount(@PathVariable(name = "account-number") String accountNumber) {
        return accountService.getAccountByAccountNumber(accountNumber);
    }

    @PutMapping("/{account-number}/deposit")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deposit(@RequestBody @Valid AccountDepositRequest accountDepositRequest, @PathVariable(name = "account-number") String accountNumber) {
        validateAccountMatch(accountNumber, accountDepositRequest.accountNumber());
        accountService.deposit(accountDepositRequest);
    }

    @PutMapping(path = "/{account-number}/withdraw")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void withdraw(@RequestBody @Valid AccountWithdrawRequest accountWithdrawRequest, @PathVariable(name = "account-number") String accountNumber) {
        validateAccountMatch(accountNumber, accountWithdrawRequest.debtorAccountNumber());
        accountService.withdraw(accountWithdrawRequest);
    }

    @GetMapping(path = "/{account-number}/postings")
    public List<AccountPosting> getPostings(@PathVariable(name = "account-number") String accountNumber) {
        return accountService.getPostings(accountNumber);
    }

    private void validateAccountMatch(String pathAccountId, String bodyAccountId) {
        if (!pathAccountId.equalsIgnoreCase(bodyAccountId)) {
            throw new IllegalArgumentException("Account ID in path must match account number in body");
        }
    }
}
