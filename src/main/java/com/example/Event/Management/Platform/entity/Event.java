package com.example.Event.Management.Platform.entity;

import com.example.Event.Management.Platform.enums.EventCategory;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "events")
@Data
@AllArgsConstructor
@NoArgsConstructor
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
