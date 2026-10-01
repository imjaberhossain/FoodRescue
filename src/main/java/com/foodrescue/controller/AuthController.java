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

    // Dependency injection for authentication & user management service
    private final AuthService authService;

    // Constructor injection
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // ---------- Login ----------

    /**
     * Renders the login page (/login).
     * Redirects to the dashboard if the user is already authenticated.
     */
    @GetMapping("/login")
    public String loginPage(HttpSession session) {
        if (SessionUser.from(session) != null) return "redirect:/dashboard";
        return "login"; // Renders login.html
    }

    /**
     * Processes login form submissions.
     * Authenticates credentials, creates a secure session, and redirects to dashboard.
     */
    @PostMapping("/login")
    public String login(@RequestParam String email,
                        @RequestParam String password,
                        HttpServletRequest request,
                        Model model) {
        // Authenticate credentials against database
        Optional<User> found = authService.authenticate(email, password);
        if (found.isEmpty()) {
            model.addAttribute("error", "Email or password is wrong. Please try again.");
            model.addAttribute("email", email);
            return "login";
        }

        // Create new session and change session ID to prevent Session Fixation attacks
        User user = found.get();
        HttpSession session = request.getSession(true);
        request.changeSessionId();

        // Store user authentication details in session
        session.setAttribute(SessionUser.KEY,
                new SessionUser(user.getId(), user.getFullName(), user.getRole(), user.isAdmin()));
        return "redirect:/dashboard";
    }

    // ---------- Sign up ----------

    /**
     * Renders the registration page (/register).
     * Redirects to the dashboard if the user is already logged in.
     */
    @GetMapping("/register")
    public String registerPage(HttpSession session, Model model) {
        if (SessionUser.from(session) != null) return "redirect:/dashboard";
        if (!model.containsAttribute("registerForm")) {
            model.addAttribute("registerForm", new RegisterForm());
        }
        return "register"; // Renders register.html
    }

    /**
     * Processes new user account creation.
     * Handles role-specific validation (NID/documents/Org names) and duplicate email checks.
     */
    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("registerForm") RegisterForm form,
                           BindingResult bindingResult,
                           Model model,
                           RedirectAttributes redirectAttributes) {
        // Return to registration form if validation annotations fail
        if (bindingResult.hasErrors()) return "register";

        try {
            authService.register(form);
        } catch (IllegalArgumentException e) {
            // Catches validation exceptions (e.g., missing NID/Org details, duplicate email)
            model.addAttribute("error", e.getMessage());
            return "register";
        } catch (DataIntegrityViolationException e) {
            // Catches database constraint violations for existing email addresses
            model.addAttribute("error", "This email is already registered. Try logging in.");
            return "register";
        }

        // Success message custom notification based on user role (Provider vs NGO/Volunteer)
        redirectAttributes.addFlashAttribute("success",
                form.getRole() == User.Role.PROVIDER
                        ? "Your account is ready. Please log in."
                        : "Your account is ready. Your verification is pending review - you can still log in and browse.");
        return "redirect:/login";
    }

    // ---------- Logout ----------

    /**
     * Invalidates the user session to perform logout and redirects to home.
     */
    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate(); // Clear session data
        return "redirect:/"; // Redirect to homepage
    }
}