package com.foodrescue.controller;

import com.foodrescue.security.SessionUser;
import com.foodrescue.service.NotificationService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/** Makes "currentUser" and the unread notification count available on every page (used by the top menu). */
@ControllerAdvice
public class GlobalModelAdvice {

    private final NotificationService notificationService;

    public GlobalModelAdvice(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @ModelAttribute("currentUser")
    public SessionUser currentUser(HttpServletRequest request) {
        return SessionUser.from(request.getSession(false));
    }

    @ModelAttribute("unreadNotifications")
    public long unreadNotifications(HttpServletRequest request) {
        SessionUser user = SessionUser.from(request.getSession(false));
        return user == null ? 0 : notificationService.unreadCountFor(user.getId());
    }
}
