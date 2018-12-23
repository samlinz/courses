package sec.webshop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sec.webshop.domain.Item;

public interface ItemRepository extends JpaRepository<Item, Long> {

}
