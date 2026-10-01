package com.foodrescue.controller;

import com.foodrescue.security.SessionUser;
import com.foodrescue.service.NotificationService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping("/notifications")
    public String list(HttpSession session, Model model) {
        SessionUser user = SessionUser.from(session);
        if (user == null) return "redirect:/login";
        model.addAttribute("notifications", notificationService.allFor(user.getId()));
        return "notifications";
    }

    @PostMapping("/notifications/mark-read")
    public String markRead(HttpSession session) {
        SessionUser user = SessionUser.from(session);
        if (user == null) return "redirect:/login";
        notificationService.markAllRead(user.getId());
        return "redirect:/notifications";
    }
}
