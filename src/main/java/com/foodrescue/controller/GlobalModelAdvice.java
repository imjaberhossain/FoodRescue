package com.foodrescue.controller;

import com.foodrescue.security.SessionUser;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

//This class acts as a global helper—it automatically runs before any page loads,
// checks if a user is logged in, and attaches currentUser to the view.
@ControllerAdvice
public class GlobalModelAdvice {

    @ModelAttribute("currentUser")
    public SessionUser currentUser(HttpServletRequest request) {
        return SessionUser.from(request.getSession(false));
    }
}
