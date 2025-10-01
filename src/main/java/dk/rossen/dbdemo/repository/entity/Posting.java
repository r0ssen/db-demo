package dk.rossen.dbdemo.repository.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "posting")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Posting {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Size(min = 10, max = 14, message = "Account number has to be between 10 and 14 characters")
    @Column(name = "debtor_account_no", nullable = false)
    private String debtorAccountNumber;

    @Size(min = 10, max = 14, message = "Account number has to be between 10 and 14 characters")
    @Column(name = "creditor_account_no", nullable = false)
    private String creditorAccountNumber;

    @Column(name = "amount", precision = 15, scale = 2)
    private BigDecimal amount;

    @Column(name = "posting_text")
    private String postingText;

    @Column(name = "balance", nullable = false)
    private BigDecimal balance;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @PrePersist
    private void prePersist() {
        this.createdAt = LocalDateTime.now();
        if (StringUtils.isEmpty(this.postingText)) {
            this.postingText = "Transaction";
        }
    }
}
