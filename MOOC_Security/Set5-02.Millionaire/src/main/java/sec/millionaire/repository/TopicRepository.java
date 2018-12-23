package sec.millionaire.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sec.millionaire.domain.Topic;

public interface TopicRepository extends JpaRepository<Topic, Long> {

}
