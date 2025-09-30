package dk.rossen.dbdemo.repository;

import dk.rossen.dbdemo.repository.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.Optional;

public interface AccountsRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByAccountNumber(String accountNumber);

    @Modifying
    @Query(value = "UPDATE account SET balance = :balance WHERE account_no = :debtorAccountNumber", nativeQuery = true)
    void updateBalance(@Param("debtorAccountNumber") String accountNumber, @Param("balance") BigDecimal balance);
}
