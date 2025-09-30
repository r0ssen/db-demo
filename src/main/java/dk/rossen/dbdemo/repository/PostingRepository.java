package dk.rossen.dbdemo.repository;

import dk.rossen.dbdemo.repository.entity.Posting;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PostingRepository extends JpaRepository<Posting, Long> {
    Optional<List<Posting>> findPostingsByDebtorAccountNumber(String debtorAccountNumber);
}
