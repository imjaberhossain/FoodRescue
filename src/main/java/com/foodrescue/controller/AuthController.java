package com.foodrescue.controller;

import com.foodrescue.dto.RegisterForm;
import com.foodrescue.model.User;
import com.foodrescue.security.SessionUser;
import com.foodrescue.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // ---------- Login ----------

    @GetMapping("/login")
    public String loginPage(HttpSession session) {
        if (SessionUser.from(session) != null) return "redirect:/dashboard";
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String email,
                        @RequestParam String password,
                        HttpServletRequest request,
                        Model model) {
        Optional<User> found = authService.authenticate(email, password);
        if (found.isEmpty()) {
            model.addAttribute("error", "Email or password is wrong. Please try again.");
            model.addAttribute("email", email);
            return "login";
        }

        User user = found.get();
        HttpSession session = request.getSession(true);
        request.changeSessionId(); // new session id after login (safer)
        session.setAttribute(SessionUser.KEY,
                new SessionUser(user.getId(), user.getFullName(), user.getRole(), user.isAdmin()));
        return "redirect:/dashboard";
    }

    // ---------- Sign up ----------

    @GetMapping("/register")
    public String registerPage(HttpSession session, Model model) {
        if (SessionUser.from(session) != null) return "redirect:/dashboard";
        if (!model.containsAttribute("registerForm")) {
            model.addAttribute("registerForm", new RegisterForm());
        }
        return "register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("registerForm") RegisterForm form,
                           BindingResult bindingResult,
                           Model model,
                           RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) return "register";

        try {
            authService.register(form);
        } catch (IllegalArgumentException e) {
            // Covers: duplicate email, missing NID/document for NGO or Volunteer, missing org name.
            model.addAttribute("error", e.getMessage());
            return "register";
        } catch (DataIntegrityViolationException e) {
            model.addAttribute("error", "This email is already registered. Try logging in.");
            return "register";
        }

        redirectAttributes.addFlashAttribute("success",
                form.getRole() == User.Role.PROVIDER
                        ? "Your account is ready. Please log in."
                        : "Your account is ready. Your verification is pending review - you can still log in and browse.");
        return "redirect:/login";
    }

    // ---------- Logout ----------

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}
