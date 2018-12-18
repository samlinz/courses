package sec.notebook;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class NotebookController {
    public static final int MAX_NOTES = 10;
    private List<String> _notes = new ArrayList<>();

    @RequestMapping("/")
    public String home(Model model, @RequestParam(required = false) String note) {
        if (note != null && !note.isEmpty()) {
            _notes.add(note);
            while (_notes.size() > MAX_NOTES)
                _notes.remove(0);
        }

        model.addAttribute("notes", _notes);

        return "index";
    }
}
