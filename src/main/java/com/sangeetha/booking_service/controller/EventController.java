package com.sangeetha.booking_service.controller;

import com.sangeetha.booking_service.dto.EventRequest;
import com.sangeetha.booking_service.dto.EventResponse;
import com.sangeetha.booking_service.service.EventService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/events")
public class EventController {
    private final EventService eventService;
    public EventController(EventService eventService)
    {
        this.eventService = eventService;
    }

    @PostMapping
    public EventResponse createEvent( @Valid @RequestBody EventRequest request)
    {
        return eventService.createEvent(request);
    }

    @GetMapping
    public List<EventResponse> getAllEvents()
    {
        return eventService.getAllEvents();
    }

    @GetMapping("/{id}")
    public EventResponse getEventById(@PathVariable Long id)
    {
        return eventService.getEventById(id);
    }

}
