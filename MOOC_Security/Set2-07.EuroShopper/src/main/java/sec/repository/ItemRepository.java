package sec.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sec.domain.Item;

public interface ItemRepository extends JpaRepository<Item, Long> {

}
