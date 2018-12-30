package sec.project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import sec.project.domain.User;
import sec.project.repository.SQLiteRepository;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.sql.SQLException;

@Controller
public class MainController {

    @Autowired
    private SQLiteRepository repository;

    @RequestMapping(value = "/signup", method = RequestMethod.GET)
    public String register() {
        return "register";
    }

    @RequestMapping(value = "/signup", method = RequestMethod.POST)
    public String registerPost(@RequestParam String name, @RequestParam String pwd, HttpServletResponse response) throws SQLException {
        if (name == null || name.isEmpty() || pwd == null || pwd.isEmpty()) {
            response.setStatus(400);
            return "Empty fields";
        }

        User existingUser = repository.getUser(name);
        if (existingUser != null) {
            response.setStatus(400);
            return "User exists already";
        }

        repository.addUser(name, pwd);

        return "redirect:/";
    }

    @RequestMapping(value = "/notes", method = RequestMethod.GET)
    public String notes(@CookieValue(value = "session", defaultValue = "") String token, Model model) throws SQLException {
        User user = getUserIfLoggedIn(token);
        if (user == null) {
            return "redirect:/";
        }

        // Fetch all notes and add to model.
        model.addAttribute("notes", repository.getNotes());

        return "notes";
    }

    @RequestMapping(value = "/notes", method = RequestMethod.POST)
    public String notes(@CookieValue(value = "session", defaultValue = "") String token
            , @RequestParam String message
            , HttpServletResponse response) throws SQLException {
        User user = getUserIfLoggedIn(token);
        if (user == null) {
            response.setStatus(403);
            return "forbidden";
        }

        repository.addNote(user.name, message);

        return "redirect:/notes";
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String main(@CookieValue(value = "session", defaultValue = "") String token) throws SQLException {
        User user = getUserIfLoggedIn(token);
        if (user != null) {
            return "redirect:/notes";
        }

        return "signin";
    }

    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public String logout(@CookieValue(value = "session", defaultValue = "") String token, HttpServletResponse response) throws SQLException {
        User user = getUserIfLoggedIn(token);
        if (user != null) {
            repository.removeSessionForUser(user.name);
            response.addCookie(new Cookie("session", null));
        }

        return "redirect:/";
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String login(@RequestParam String name, @RequestParam String pwd, HttpServletResponse response) throws SQLException {
        boolean success = false;

        User user = repository.getUser(name);
        if (user != null) {
            if (user.pwd.equals(pwd)) {
                // The provided password matches the stored one; login is successful.
                success = true;
            }
        }

        // If something wasn't right, forbid the login.
        if (!success) {
            response.setStatus(403);
            return "forbidden";
        } else {
            // Set session cookie and redirect to main page.
            String session = repository.newSessionForUser(name);
            response.addCookie(new Cookie("session", session));
            return "redirect:/";
        }
    }

    // Error page.
    @RequestMapping(value = "/error", method = RequestMethod.GET)
    public String error() {
        return "error";
    }

    private User getUserIfLoggedIn(String token) throws SQLException {
        String usernameForSession = repository.getUsernameForSession(token);
        if (usernameForSession != null) {
            return repository.getUser(usernameForSession);
        }

        return null;
    }
}
