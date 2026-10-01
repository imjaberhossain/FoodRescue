package com.foodrescue.security;

import com.foodrescue.model.User;
import jakarta.servlet.http.HttpSession;

import java.io.Serializable;

/** Small object stored in the login session. It never contains the password. */
public class SessionUser implements Serializable {

    public static final String KEY = "loggedInUser";

    private final Long id;
    private final String fullName;
    private final User.Role role;
    private final boolean admin;

    public SessionUser(Long id, String fullName, User.Role role, boolean admin) {
        this.id = id;
        this.fullName = fullName;
        this.role = role;
        this.admin = admin;
    }

    /** Returns the logged-in user, or null if nobody is logged in. */
    public static SessionUser from(HttpSession session) {
        if (session == null) return null;
        Object value = session.getAttribute(KEY);
        return value instanceof SessionUser ? (SessionUser) value : null;
    }

    public Long getId() { return id; }
    public String getFullName() { return fullName; }
    public User.Role getRole() { return role; }
    public boolean isProvider() { return role == User.Role.PROVIDER; }
    public boolean isNgo() { return role == User.Role.NGO; }
    public boolean isVolunteer() { return role == User.Role.VOLUNTEER; }
    /** True for anyone who requests food (NGO or Volunteer), as opposed to a Provider. */
    public boolean isClaimant() { return role != User.Role.PROVIDER; }
    /** True only for accounts an admin manually flagged with is_admin = TRUE in the database. */
    public boolean isAdmin() { return admin; }
}
