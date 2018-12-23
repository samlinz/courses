package sec.millionaire.domain;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
public class Question extends AbstractPersistable<Long> {

    @Column(length = 1000)
    private String text;
    @OneToMany
    private List<AnswerOption> answerOptions = new ArrayList<>();

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text.replaceAll("\n", "<br/>");
    }

    public List<AnswerOption> getAnswerOptions() {
        return answerOptions;
    }

    public void setAnswerOptions(List<AnswerOption> answerOptions) {
        this.answerOptions = answerOptions;
    }

}
