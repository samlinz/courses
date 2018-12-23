package sec.millionaire.controller;

import java.util.List;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import sec.millionaire.domain.DifficultyLevel;
import sec.millionaire.domain.Topic;
import sec.millionaire.repository.DifficultyLevelRepository;
import sec.millionaire.repository.TopicRepository;

@Controller
public class LevelController {

    @Autowired
    private HttpSession session;

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private DifficultyLevelRepository difficultyLevelRepository;

    @RequestMapping("/topics/{topicId}/levels/{levelId}")
    public String getTopicLevel(Model model, @PathVariable Long topicId, @PathVariable Long levelId) {
        if (!topicId.equals(session.getAttribute("topic"))) {
            return "redirect:/topics/" + session.getAttribute("topic");
        }

        if (!levelId.equals(session.getAttribute("level"))) {
            return "redirect:/topics/" + topicId + "/levels/" + session.getAttribute("level");
        }

        Topic topic = topicRepository.findOne(topicId);

        model.addAttribute("topic", topic);

        List<DifficultyLevel> difficultyLevels = difficultyLevelRepository.findByTopic(topic);
        model.addAttribute("levels", difficultyLevels);

        Long max = difficultyLevels.stream().map(d -> d.getLevel()).max(Long::compareTo).get();
        if (levelId > max) {
            return "redirect:/finish";
        }

        return "levels";
    }

}
