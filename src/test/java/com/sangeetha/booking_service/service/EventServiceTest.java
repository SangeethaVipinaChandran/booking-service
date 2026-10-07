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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private EventService eventService;

    @Test
    void createEvent_setsTicketsRemainingToTotalTickets() {
        EventRequest request = new EventRequest();
        request.setName("Concert");
        request.setDate(LocalDateTime.now().plusDays(10));
        request.setTotalTickets(100);

        when(eventRepository.save(any(Event.class))).thenAnswer(invocation -> {
            Event saved = invocation.getArgument(0);
            saved.setId(1L);
            return saved;
        });

        EventResponse response = eventService.createEvent(request);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getTotalTickets()).isEqualTo(100);
        assertThat(response.getTicketsRemaining()).isEqualTo(100);
    }

    @Test
    void getEventById_whenEventMissing_throwsNotFound() {
        when(eventRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> eventService.getEventById(99L))
                .isInstanceOf(EventNotFoundException.class);
    }

    @Test
    void bookTickets_withEnoughTickets_decrementsRemainingAndSavesBooking() {
        Event event = event(1L, 10, LocalDateTime.now().plusDays(5));
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> {
            Booking saved = invocation.getArgument(0);
            saved.setId(1L);
            return saved;
        });

        BookingResponse response = eventService.bookTickets(1L, bookingRequest(3));

        assertThat(event.getTicketsRemaining()).isEqualTo(7);
        assertThat(response.getBookingId()).isEqualTo(1L);
        assertThat(response.getEventId()).isEqualTo(1L);
        assertThat(response.getNumberOfTickets()).isEqualTo(3);
        verify(eventRepository).save(event);
    }

    @Test
    void bookTickets_forExactlyTheRemainingTickets_succeeds() {
        Event event = event(1L, 4, LocalDateTime.now().plusDays(5));
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> invocation.getArgument(0));

        eventService.bookTickets(1L, bookingRequest(4));

        assertThat(event.getTicketsRemaining()).isZero();
    }

    @Test
    void bookTickets_withTooManyTickets_throwsAndSavesNothing() {
        Event event = event(1L, 2, LocalDateTime.now().plusDays(5));
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));

        assertThatThrownBy(() -> eventService.bookTickets(1L, bookingRequest(5)))
                .isInstanceOf(InsufficientTicketsException.class);

        assertThat(event.getTicketsRemaining()).isEqualTo(2);
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void bookTickets_forPastEvent_throwsEventAlreadyPassed() {
        Event event = event(1L, 10, LocalDateTime.now().minusDays(1));
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));

        assertThatThrownBy(() -> eventService.bookTickets(1L, bookingRequest(1)))
                .isInstanceOf(EventAlreadyPassedException.class);

        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void bookTickets_forMissingEvent_throwsNotFound() {
        when(eventRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> eventService.bookTickets(99L, bookingRequest(1)))
                .isInstanceOf(EventNotFoundException.class);
    }

    private Event event(long id, int remaining, LocalDateTime date) {
        Event event = new Event();
        event.setId(id);
        event.setName("Concert");
        event.setDate(date);
        event.setTotalTickets(10);
        event.setTicketsRemaining(remaining);
        return event;
    }

    private BookingRequest bookingRequest(int tickets) {
        BookingRequest request = new BookingRequest();
        request.setCustomerName("Alice");
        request.setNumberOfTickets(tickets);
        return request;
    }
}