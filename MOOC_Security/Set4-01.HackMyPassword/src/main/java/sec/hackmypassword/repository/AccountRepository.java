package sec.hackmypassword.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sec.hackmypassword.domain.Account;

public interface AccountRepository extends JpaRepository<Account, Long> {

    Account findByUsername(String username);
}
