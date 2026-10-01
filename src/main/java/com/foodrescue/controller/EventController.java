package com.foodrescue.controller;

import com.foodrescue.dto.EventForm;
import com.foodrescue.security.SessionUser;
import com.foodrescue.service.EventService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/** NGO/Volunteer upcoming events - so providers know in advance where food will be needed. */
@Controller
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping("/events")
    public String browseEvents(@RequestParam(defaultValue = "") String keyword,
                               HttpSession session, Model model) {
        SessionUser user = SessionUser.from(session);
        if (user == null) return "redirect:/login";

        model.addAttribute("keyword", keyword);
        model.addAttribute("upcomingEvents", eventService.browseUpcoming(keyword));
        if (user.isClaimant()) {
            model.addAttribute("myEvents", eventService.eventsFor(user.getId()));
            if (!model.containsAttribute("eventForm")) {
                model.addAttribute("eventForm", new EventForm());
            }
        }
        return "events";
    }

    @PostMapping("/events")
    public String createEvent(@Valid @ModelAttribute("eventForm") EventForm form,
                              BindingResult bindingResult,
                              HttpSession session,
                              Model model,
                              RedirectAttributes redirectAttributes) {
        SessionUser user = SessionUser.from(session);
        if (user == null) return "redirect:/login";

        if (bindingResult.hasErrors()) {
            model.addAttribute("upcomingEvents", eventService.browseUpcoming(""));
            model.addAttribute("myEvents", eventService.eventsFor(user.getId()));
            return "events";
        }
        try {
            eventService.createEvent(user.getId(), form);
            redirectAttributes.addFlashAttribute("success", "Event posted. Nearby providers have been notified.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/events";
    }

    @PostMapping("/events/{id}/delete")
    public String deleteEvent(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        SessionUser user = SessionUser.from(session);
        if (user == null) return "redirect:/login";
        try {
            eventService.deleteEvent(id, user.getId());
        } catch (IllegalArgumentException | IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/events";
    }
}
