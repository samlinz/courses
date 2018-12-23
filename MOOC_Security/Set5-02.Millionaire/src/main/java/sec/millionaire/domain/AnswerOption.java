package sec.millionaire.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
public class AnswerOption extends AbstractPersistable<Long> {

    @Column(length = 500)
    private String text;

    private Boolean correct;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Boolean getCorrect() {
        return correct;
    }

    public void setCorrect(Boolean correct) {
        this.correct = correct;
    }

}
