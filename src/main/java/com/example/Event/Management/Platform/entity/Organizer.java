package com.example.Event.Management.Platform.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "event_organizers")
@Data
@SuperBuilder
@NoArgsConstructor
@PrimaryKeyJoinColumn(name = "organizer_id")
public class Organizer extends Person{

    @OneToMany(mappedBy = "organizer", cascade = CascadeType.ALL)
    private List<Event> event = new ArrayList<>();
}
