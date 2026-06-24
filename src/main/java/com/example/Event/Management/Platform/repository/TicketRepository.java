package com.example.Event.Management.Platform.repository;

import com.example.Event.Management.Platform.model.entity.Ticket;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    List<Ticket> findByEventId(Long eventId);

    @Query("SELECT COALESCE(SUM(t.availableQuantity), 0) FROM Ticket t WHERE t.event.id = :eventId")
    Integer getNumberOfTicketsAlreadyAllocated(@Param("eventId") Long eventId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT t FROM Ticket t WHERE t.id = :id")
    Optional<Ticket> findByIdWithLock(@Param("id") Long id);
}
