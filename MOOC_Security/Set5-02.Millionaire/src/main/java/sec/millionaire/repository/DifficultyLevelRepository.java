package sec.millionaire.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import sec.millionaire.domain.DifficultyLevel;
import sec.millionaire.domain.Topic;

public interface DifficultyLevelRepository extends JpaRepository<DifficultyLevel, Long> {
    List<DifficultyLevel> findByTopic(Topic topic);
    DifficultyLevel findByTopicAndLevel(Topic topic, Long level);
}
