package sec.oldiesbutgoodies;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

// can be launched using 
// mvn jetty:run
@Controller
public class OldiesButGoodiesController {

    @RequestMapping("/")
    public String defaultShowIndex() {
        return "index";
    }

}
