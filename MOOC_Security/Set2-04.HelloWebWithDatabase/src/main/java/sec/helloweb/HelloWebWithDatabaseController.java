package sec.helloweb;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HelloWebWithDatabaseController {

    @Autowired
    private HelloMessageRepository helloMessageRepository;

    @RequestMapping("/")
    @ResponseBody
    public String home() {
        final long msgCount = helloMessageRepository.count();

        if (msgCount > 0) {
            final long randIndex = new Random().nextInt((int) msgCount) + 1;
            final HelloMessage msg = helloMessageRepository.getOne(randIndex);
            return msg.getContent();
        } else {
            return "No message";
        }
    }
}
