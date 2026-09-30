package com.foodrescue.controller;

import com.foodrescue.security.SessionUser;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/** Makes "currentUser" available in every HTML page (used by the top menu). */
@ControllerAdvice
public class GlobalModelAdvice {

    @ModelAttribute("currentUser")
    public SessionUser currentUser(HttpServletRequest request) {
        return SessionUser.from(request.getSession(false));
    }
}
