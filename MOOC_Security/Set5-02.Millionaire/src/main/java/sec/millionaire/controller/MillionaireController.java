package sec.millionaire.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import sec.millionaire.domain.AnswerOption;
import sec.millionaire.domain.UserDetails;
import sec.millionaire.repository.AnswerOptionRepository;
import sec.millionaire.repository.UserDetailsRepository;

import javax.servlet.http.HttpSession;
import java.util.HashSet;
import java.util.List;

@Controller
public class MillionaireController {

    @Autowired
    private HttpSession session;

    @Autowired
    private AnswerOptionRepository answerOptionRepository;

    @Autowired
    private UserDetailsRepository userDetailsRepository;

    @RequestMapping("*")
    public String main() {
        return "redirect:/topics";
    }

    @RequestMapping("/incorrect")
    public String incorrect() {
        return "incorrect";
    }

    @RequestMapping("/finish")
    public String finish() {

        if (!isFinished()) return "redirect:/topics";
        return "finish";
    }

    @RequestMapping(value = "/details", method = RequestMethod.POST)
    public String postData(@ModelAttribute UserDetails details) {
        if (!isFinished()) return "redirect:/topics";
        userDetailsRepository.save(details);
        return "thanks";
    }

    private boolean isFinished() {
        // Check if user has completed all topics.
        HashSet<Long> answers = (HashSet<Long>) session.getAttribute("answers");
        if (answers == null) {
            return false;
        }

        List<AnswerOption> all = answerOptionRepository.findAll();
        boolean allCorrect = true;
        for (AnswerOption a : all) {
            if (a.getCorrect() && !answers.contains(a.getId())) {
                allCorrect = false;
                break;
            }
        }

        // Do not allow access if all questions are not answered.
        if (!allCorrect) {
            return false;
        }

        return true;
    }
}
