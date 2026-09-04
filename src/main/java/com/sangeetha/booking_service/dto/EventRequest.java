package com.sangeetha.booking_service.dto;


import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;

public class EventRequest {
    @NotBlank(message = "Event name is required")
    private String name;
    @Future(message = "Event date must be in the future")
    private LocalDateTime date;
    @Positive(message = "Total tickets must be greater than zero")
    private int totalTickets;

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTotalTickets() {
        return totalTickets;
    }

    public void setTotalTickets(int totalTickets) {
        this.totalTickets = totalTickets;
    }
}
