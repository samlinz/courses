package sec.webshop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sec.webshop.domain.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {
    
}
