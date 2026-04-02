package com.example.Event.Management.Platform.repository;

import com.example.Event.Management.Platform.model.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {
    Optional<Location> findByStreetNameNumberCityZipCode(String s, Integer number, String city, String s1);
}
