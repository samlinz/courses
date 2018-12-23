package sec.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sec.domain.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {
    
}
