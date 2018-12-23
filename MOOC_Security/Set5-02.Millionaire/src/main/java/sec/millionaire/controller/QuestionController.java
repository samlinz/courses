package sec.millionaire.controller;

import java.util.Collections;
import java.util.List;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import sec.millionaire.domain.DifficultyLevel;
import sec.millionaire.domain.Question;
import sec.millionaire.domain.Topic;
import sec.millionaire.repository.DifficultyLevelRepository;
import sec.millionaire.repository.QuestionRepository;
import sec.millionaire.repository.TopicRepository;

@Controller
public class QuestionController {

    @Autowired
    private HttpSession session;

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private DifficultyLevelRepository difficultyLevelRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @RequestMapping("/topics/{topicId}/levels/{levelId}/questions")
    public String getQuestionsForLevelAndTopic(Model model, @PathVariable Long topicId, @PathVariable Long levelId) {
        if (!topicId.equals(session.getAttribute("topic"))) {
            return "redirect:/topics/" + session.getAttribute("topic");
        }

        if (!levelId.equals(session.getAttribute("level"))) {
            return "redirect:/topics/" + topicId + "/levels/" + session.getAttribute("level");
        }

        Topic topic = topicRepository.findOne(topicId);
        DifficultyLevel difficultyLevel = difficultyLevelRepository.findByTopicAndLevel(topic, levelId);

        List<Question> questions = difficultyLevel.getQuestions();
        Collections.shuffle(questions);
        Question question = questions.get(0);

        session.setAttribute("question", question.getId());

        return "redirect:/topics/" + topicId + "/levels/" + levelId + "/questions/" + question.getId();
    }

    @RequestMapping("/topics/{topicId}/levels/{levelId}/questions/{questionId}")
    public String getQuestion(Model model, @PathVariable Long topicId, @PathVariable Long levelId, @PathVariable Long questionId) {
        if (!topicId.equals(session.getAttribute("topic"))) {
            return "redirect:/topics/" + session.getAttribute("topic");
        }

        if (!levelId.equals(session.getAttribute("level"))) {
            return "redirect:/topics/" + topicId + "/levels/" + session.getAttribute("level");
        }

        if (!questionId.equals(session.getAttribute("question"))) {
            return "redirect:/topics/" + topicId + "/levels/" + levelId + "/questions/" + session.getAttribute("question");
        }

        model.addAttribute("question", questionRepository.findOne(questionId));

        return "question";
    }
}
