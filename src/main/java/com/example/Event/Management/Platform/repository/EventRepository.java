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
                    "WHERE (:name IS NULL OR LOWER(e.name) LIKE CONCAT('%', LOWER(CAST(:name AS text)), '%')) AND " +
                    "(:city IS NULL OR LOWER(l.city) LIKE CONCAT('%', LOWER(CAST(:city AS text)), '%')) AND " +
                    "(:category IS NULL OR e.event_category = CAST(:category AS text)) AND " +
                    "(:date IS NULL OR e.date >= TO_TIMESTAMP(CAST(:date AS text), 'YYYY-MM-DD HH24:MI:SS')) AND " +
                    "e.date > TO_TIMESTAMP(:now, 'YYYY-MM-DD HH24:MI:SS')",
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
