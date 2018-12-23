package sec.millionaire.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sec.millionaire.domain.AnswerOption;

public interface AnswerOptionRepository extends JpaRepository<AnswerOption, Long> {
}
