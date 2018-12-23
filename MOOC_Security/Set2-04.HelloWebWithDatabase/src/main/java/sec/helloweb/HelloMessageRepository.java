package sec.helloweb;

import org.springframework.data.jpa.repository.JpaRepository;

public interface HelloMessageRepository extends JpaRepository<HelloMessage, Long> {

}
