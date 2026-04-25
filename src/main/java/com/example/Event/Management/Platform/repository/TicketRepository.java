package com.example.Event.Management.Platform.repository;

import com.example.Event.Management.Platform.model.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    List<Ticket> findByEventId(Long eventId);

    @Query("SELECT COALESCE(SUM(t.availableQuantity), 0) FROM Ticket t WHERE t.event.id = :eventId")
    Integer getNumberOfTicketsAlreadyAllocated(@Param("eventId") Long eventId);
}
