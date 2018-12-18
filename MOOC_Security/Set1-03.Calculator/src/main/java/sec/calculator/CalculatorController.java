package sec.calculator;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class CalculatorController {
    @RequestMapping("/add")
    @ResponseBody
    public String add(@RequestParam int first, @RequestParam int second) {
        return String.valueOf(first + second);
    }

    @RequestMapping("/multiply")
    @ResponseBody
    public String multiply(@RequestParam int first, @RequestParam int second) {
        return String.valueOf(first * second);
    }
}
