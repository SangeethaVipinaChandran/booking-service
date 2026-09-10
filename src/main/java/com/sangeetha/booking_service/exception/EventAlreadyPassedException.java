package com.sangeetha.booking_service.exception;

public class EventAlreadyPassedException extends RuntimeException {
    public EventAlreadyPassedException(Long eventId) {
        super("Cannot book event " + eventId + " — the event date has already passed");
    }
}
