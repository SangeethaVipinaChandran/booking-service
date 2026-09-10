package com.sangeetha.booking_service.service;

import com.sangeetha.booking_service.dto.BookingRequest;
import com.sangeetha.booking_service.dto.BookingResponse;
import com.sangeetha.booking_service.dto.EventRequest;
import com.sangeetha.booking_service.dto.EventResponse;
import com.sangeetha.booking_service.entity.Booking;
import com.sangeetha.booking_service.entity.Event;
import com.sangeetha.booking_service.exception.EventAlreadyPassedException;
import com.sangeetha.booking_service.exception.EventNotFoundException;
import com.sangeetha.booking_service.exception.InsufficientTicketsException;
import com.sangeetha.booking_service.repository.BookingRepository;
import com.sangeetha.booking_service.repository.EventRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EventService {
    private final EventRepository eventRepository;
    private final BookingRepository bookingRepository;

    public EventService(EventRepository eventRepository, BookingRepository bookingRepository) {
        this.eventRepository = eventRepository;
        this.bookingRepository = bookingRepository;
    }

    public EventResponse createEvent(EventRequest request) {
        Event event = new Event();
        event.setName(request.getName());
        event.setDate(request.getDate());
        event.setTotalTickets(request.getTotalTickets());
        event.setTicketsRemaining(request.getTotalTickets());
        Event saved = eventRepository.save(event);
        return toEventResponse(saved);
    }

    public List<EventResponse> getAllEvents() {
        return eventRepository.findAll().stream().map(this::toEventResponse).toList();
    }

    public EventResponse getEventById(Long id) {
        Event event = findEventEntity(id);
        return toEventResponse(event);
    }

    public BookingResponse bookTickets(Long eventId, BookingRequest request) {
        Event event = findEventEntity(eventId);
        if (event.getDate().isBefore(LocalDateTime.now())) {
            throw new EventAlreadyPassedException(eventId);
        }
        if (request.getNumberOfTickets() > event.getTicketsRemaining()) {
            throw new InsufficientTicketsException(eventId, request.getNumberOfTickets(), event.getTicketsRemaining());
        }
        event.setTicketsRemaining(event.getTicketsRemaining() - request.getNumberOfTickets());
        eventRepository.save(event);
        Booking booking = new Booking();
        booking.setCustomerName(request.getCustomerName());
        booking.setNumberOfTickets(request.getNumberOfTickets());
        booking.setEvent(event);
        Booking savedBooking = bookingRepository.save(booking);
        return toBookingResponse(savedBooking);
    }

    private Event findEventEntity(Long id) {
        return eventRepository.findById(id).orElseThrow(() -> new EventNotFoundException(id));
    }

    private EventResponse toEventResponse(Event event) {
        EventResponse response = new EventResponse();
        response.setId(event.getId());
        response.setName(event.getName());
        response.setDate(event.getDate());
        response.setTotalTickets(event.getTotalTickets());
        response.setTicketsRemaining(event.getTicketsRemaining());
        return response;
    }

    private BookingResponse toBookingResponse(Booking booking) {
        BookingResponse response = new BookingResponse();
        response.setBookingId(booking.getId());
        response.setEventId(booking.getEvent().getId());
        response.setCustomerName(booking.getCustomerName());
        response.setNumberOfTickets(booking.getNumberOfTickets());
        return response;
    }
}