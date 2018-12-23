package sec.millionaire.controller;

import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import sec.millionaire.domain.AnswerOption;
import sec.millionaire.repository.AnswerOptionRepository;

import java.util.HashSet;
import java.util.List;

@Controller
public class AnswerController {

    @Autowired
    private HttpSession session;

    @Autowired
    private AnswerOptionRepository answerOptionRepository;

    @RequestMapping("/topics/{topicId}/levels/{levelId}/questions/{questionId}/answers/{answerId}")
    public String answerQuestion(RedirectAttributes redirectAttributes, @PathVariable Long topicId, @PathVariable Long levelId, @PathVariable Long questionId, @PathVariable Long answerId) {
        if (!topicId.equals(session.getAttribute("topic"))) {
            return "redirect:/topics/" + session.getAttribute("topic");
        }

        if (!levelId.equals(session.getAttribute("level"))) {
            return "redirect:/topics/" + topicId + "/levels/" + session.getAttribute("level");
        }

        if (!questionId.equals(session.getAttribute("question"))) {
            return "redirect:/topics/" + topicId + "/levels/" + levelId + "/questions/" + session.getAttribute("question");
        }

        AnswerOption answerOption = answerOptionRepository.findOne(answerId);
        if (answerOption.getCorrect()) {

            HashSet<Long> answers = (HashSet<Long>) session.getAttribute("answers");
            if (answers == null) {
                answers = new HashSet<>();
            }
            answers.add(answerOption.getId());
            session.setAttribute("answers", answers);

            redirectAttributes.addFlashAttribute("feedback", "Correct!");
            session.setAttribute("level", (Long) session.getAttribute("level") + 1);
            return "redirect:/topics/" + topicId + "/levels/" + session.getAttribute("level");
        } else {
            session.removeAttribute("level");
            session.removeAttribute("question");

            return "redirect:/incorrect";
        }
    }
}
