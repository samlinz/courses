package sec.megaupload.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sec.megaupload.domain.Account;

public interface AccountRepository extends JpaRepository<Account, Long> {

    Account findByUsername(String username);
}
