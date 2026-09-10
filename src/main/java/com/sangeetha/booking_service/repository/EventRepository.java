package com.sangeetha.booking_service.repository;

import com.sangeetha.booking_service.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, Long> {
}