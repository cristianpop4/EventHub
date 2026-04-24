package com.example.Event.Management.Platform.repository;

import com.example.Event.Management.Platform.model.entity.Booking;
import com.example.Event.Management.Platform.model.enums.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByUserId(Long userId);

    List<Booking> findAllByEventId(Long eventId);

    List<Booking> findAllByUserIdAndStatus(Long userId, BookingStatus status);

    List<Booking> findAllByStatus(BookingStatus status);
}
