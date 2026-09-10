package com.sangeetha.booking_service.exception;

public class InsufficientTicketsException extends RuntimeException {
    public InsufficientTicketsException(Long eventId, int requested, int available) {
        super("Cannot book " + requested + " ticket(s) for event " + eventId + " — only " + available + " remaining");
    }
}
