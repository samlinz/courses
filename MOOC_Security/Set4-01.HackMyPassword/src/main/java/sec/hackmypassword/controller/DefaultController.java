package sec.hackmypassword.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class DefaultController {

    @RequestMapping("*")
    @ResponseBody
    public String defaultResponseBody() {
        return "Correct!";
    }
}
