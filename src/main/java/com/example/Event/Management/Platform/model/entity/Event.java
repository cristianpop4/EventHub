package com.example.Event.Management.Platform.model.entity;

import com.example.Event.Management.Platform.model.enums.EventCategory;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "events")
@Data
@Builder
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;

    @Enumerated(EnumType.STRING)
    private EventCategory eventCategory;

    @ManyToOne
    @JoinColumn(name = "location_id")
    private Location location;

    private LocalDateTime date;
    private Integer maxParticipants;

    @ManyToOne
    @JoinColumn(name = "organizer_id")
    private Organizer organizer;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL)
    private List<Registration> registration;
}
