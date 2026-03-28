package com.example.Event.Management.Platform.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "users")
@Data
@SuperBuilder
@NoArgsConstructor
@PrimaryKeyJoinColumn(name = "user_id")
public class User extends Person{

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Registration> registration;
}

