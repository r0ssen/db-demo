package dk.rossen.dbdemo.service;

import dk.rossen.dbdemo.exception.AccountAlreadyExistsException;
import dk.rossen.dbdemo.exception.AccountNotFoundException;
import dk.rossen.dbdemo.model.*;
import dk.rossen.dbdemo.repository.AccountsRepository;
import dk.rossen.dbdemo.repository.PostingRepository;
import dk.rossen.dbdemo.repository.entity.Account;
import dk.rossen.dbdemo.repository.entity.Customer;
import dk.rossen.dbdemo.repository.entity.Posting;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.retry.RetryException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountsRepository accountsRepository;
    private final CustomerService customerService;
    private final PostingRepository postingRepository;
    private final Random random = new Random();

    @Transactional
    public AccountResponse createAccount(NewAccountRequest newAccountRequest) {
        Customer customer = customerService.getCustomer(newAccountRequest.customerNumber());

        String accountNumber = generateAccountNumber();

       Account account = accountsRepository.save(
                Account.builder()
                        .accountNumber(accountNumber)
                        .accountName(newAccountRequest.accountName())
                        .customer(customer)
                        .build()
        );

        return new AccountResponse(
                account.getCustomer().getCustomerNumber(),
                account.getAccountNumber(),
                account.getAccountName(),
                account.getBalance(),
                account.getCreatedAt()
        );
    }

    private String generateAccountNumber() {
        String accountNumber = "0216" + StringUtils.leftPad(String.valueOf(random.nextInt(1000000000)), 10, "0");
        checkAccountNumber(accountNumber);
        return accountNumber;
    }

    public AccountResponse getAccountByAccountNumber(String accountNumber) {
        Optional<Account> optionalAccount = accountsRepository.findByAccountNumber(accountNumber);
        Account account = optionalAccount.orElseThrow();
        return new AccountResponse(
                account.getCustomer().getCustomerNumber(),
                account.getAccountNumber(),
                account.getAccountName(),
                account.getBalance(),
                account.getCreatedAt()
        );
    }

    @Transactional
    public void deposit(AccountDepositRequest accountDepositRequest) {
        if (accountDepositRequest.amount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Invalid amount");
        }

        Account account = getAccount(accountDepositRequest.accountNumber());

        BigDecimal newBalance = account.getBalance().add(accountDepositRequest.amount());
        accountsRepository.updateBalance(accountDepositRequest.accountNumber(), newBalance);

        postingRepository.save(
                Posting.builder()
                        .amount(accountDepositRequest.amount())
                        .debtorAccountNumber(account.getAccountNumber())
                        .creditorAccountNumber(account.getAccountNumber())
                        .postingText(String.format("Deposit %s", accountDepositRequest.amount()))
                        .balance(newBalance)
                        .account(account)
                        .build()
        );
    }

    @Transactional
    public void withdraw(AccountWithdrawRequest accountWithdrawRequest) {
        if (accountWithdrawRequest.amount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Invalid amount");
        }

        Account account = getAccount(accountWithdrawRequest.debtorAccountNumber());

        BigDecimal newBalance = account.getBalance().subtract(accountWithdrawRequest.amount());
        if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Insufficient funds");
        }
        accountsRepository.updateBalance(account.getAccountNumber(), newBalance);

        postingRepository.save(
                Posting.builder()
                        .amount(accountWithdrawRequest.amount())
                        .debtorAccountNumber(accountWithdrawRequest.debtorAccountNumber())
                        .creditorAccountNumber(accountWithdrawRequest.creditorAccountNumber())
                        .postingText(Optional.ofNullable(accountWithdrawRequest.postingText()).isPresent() ? accountWithdrawRequest.postingText() : String.format("Transfer %s from %s to %s", accountWithdrawRequest.amount(), accountWithdrawRequest.debtorAccountNumber(), accountWithdrawRequest.creditorAccountNumber()))
                        .balance(newBalance)
                        .account(account)
                        .build()
        );
    }

    public List<AccountPosting> getPostings(String accountNumber) {
        AccountResponse accountResponse = getAccountByAccountNumber(accountNumber);
        Optional<List<Posting>> postingEntities = postingRepository.findPostingsByDebtorAccountNumber(accountResponse.accountNumber());
        return postingEntities
                .map(entities ->
                        entities.stream()
                                .map(p -> new AccountPosting(p.getCreatedAt(), p.getDebtorAccountNumber(), p.getCreditorAccountNumber(), p.getAmount(), p.getPostingText(), p.getBalance()))
                                .toList())
                .orElse(Collections.emptyList());
    }

    private Account getAccount(String accountNumber) {
        return accountsRepository.findByAccountNumber(accountNumber).orElseThrow(AccountNotFoundException::new);
    }

    @Retryable(
            retryFor = AccountAlreadyExistsException.class,
            maxAttempts = 5,
            backoff = @Backoff(delay = 500)
    )
    private void checkAccountNumber(String accountNumber) {
        if (accountsRepository.findByAccountNumber(accountNumber).isPresent()) {
            throw new AccountAlreadyExistsException();
        }
    }

    @Recover
    private void recover(AccountAlreadyExistsException exception) {
        throw new RetryException(exception.getMessage(), exception);
    }
}
