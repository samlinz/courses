package sec.millionaire.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sec.millionaire.domain.UserDetails;

public interface UserDetailsRepository extends JpaRepository<UserDetails, Long> {
    UserDetails findByName(String name);
}
