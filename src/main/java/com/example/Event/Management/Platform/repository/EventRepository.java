package com.example.Event.Management.Platform.repository;

import com.example.Event.Management.Platform.model.dto.EventResponseDto;
import com.example.Event.Management.Platform.model.dto.EventSearchDto;
import com.example.Event.Management.Platform.model.entity.Event;
import com.example.Event.Management.Platform.model.enums.EventCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    @Query(
            "SELECT e FROM Event e WHERE" +
                    "(:city IS NULL OR e.location.city = :city) AND " +
                    "(:category IS NULL OR e.eventCategory = :category) AND" +
                    "(:date IS NULL OR e.date >= :date) AND " +
                    "e.date > :now"
    )
    List<Event> searchEvents(
            @Param("city") String city,
            @Param("category") EventCategory category,
            @Param("date") LocalDateTime date,
            @Param("now") LocalDateTime now
            );
}
