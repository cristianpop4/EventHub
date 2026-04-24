package com.example.Event.Management.Platform.repository;

import com.example.Event.Management.Platform.model.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    @Query(
            value = "SELECT e.* FROM events e " +
                    "JOIN locations l ON l.id = e.location_id " +
                    "WHERE (:name IS NULL OR LOWER(e.name) LIKE %:name%) AND " +
                    "(:city IS NULL OR LOWER(l.city) LIKE %:city%) AND " +
                    "(:category IS NULL OR e.event_category = :category) AND " +
                    "(:date IS NULL OR e.date >= CAST(:date AS timestamp)) AND " +
                    "e.date > CAST(:now AS timestamp)",
            nativeQuery = true
    )
    List<Event> searchEvents(
            @Param("name") String name,
            @Param("city") String city,
            @Param("category") String category,
            @Param("date") String date,
            @Param("now") String now
            );
}
