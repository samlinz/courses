package sec.millionaire.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sec.millionaire.domain.Question;

public interface QuestionRepository extends JpaRepository<Question, Long> {
}
