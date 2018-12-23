package sec.csrf.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sec.csrf.domain.Account;

public interface AccountRepository extends JpaRepository<Account, Long> {

    Account findByUsername(String username);
}
