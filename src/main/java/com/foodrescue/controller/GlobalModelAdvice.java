package com.foodrescue.controller;

import com.foodrescue.security.SessionUser;
<<<<<<< HEAD
import com.foodrescue.service.NotificationService;
=======
>>>>>>> b3947b32b8c1031c4bab383c7ff83f179a94df59
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

<<<<<<< HEAD
/** Makes "currentUser" and the unread notification count available on every page (used by the top menu). */
@ControllerAdvice
public class GlobalModelAdvice {

    private final NotificationService notificationService;

    public GlobalModelAdvice(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

=======
//This class acts as a global helper—it automatically runs before any page loads,
// checks if a user is logged in, and attaches currentUser to the view.
@ControllerAdvice
public class GlobalModelAdvice {

>>>>>>> b3947b32b8c1031c4bab383c7ff83f179a94df59
    @ModelAttribute("currentUser")
    public SessionUser currentUser(HttpServletRequest request) {
        return SessionUser.from(request.getSession(false));
    }
<<<<<<< HEAD

    @ModelAttribute("unreadNotifications")
    public long unreadNotifications(HttpServletRequest request) {
        SessionUser user = SessionUser.from(request.getSession(false));
        return user == null ? 0 : notificationService.unreadCountFor(user.getId());
    }
=======
>>>>>>> b3947b32b8c1031c4bab383c7ff83f179a94df59
}
