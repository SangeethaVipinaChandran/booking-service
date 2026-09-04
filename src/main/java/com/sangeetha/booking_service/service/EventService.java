package com.sangeetha.booking_service.service;

import com.sangeetha.booking_service.dto.EventRequest;
import com.sangeetha.booking_service.dto.EventResponse;
import com.sangeetha.booking_service.exception.EventNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class EventService {
    private final Map<Long, EventResponse> events = new ConcurrentHashMap<>();
    private final AtomicLong idCounter = new AtomicLong();

    public EventResponse createEvent(EventRequest request) {
        long id = idCounter.incrementAndGet();
        EventResponse event = new EventResponse();
        event.setId(id);
        event.setName(request.getName());
        event.setDate(request.getDate());
        event.setTotalTickets(request.getTotalTickets());
        event.setTicketsRemaining(request.getTotalTickets());
        events.put(id, event);
        return event;
    }
    public List<EventResponse> getAllEvents() {
        return new ArrayList<>(events.values());
    }

    public EventResponse getEventById(Long id) {
        EventResponse event = events.get(id);
        if (event == null)
        {
            throw new EventNotFoundException(id);
        }
            return event;
        }
}
