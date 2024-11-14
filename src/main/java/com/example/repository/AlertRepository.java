package com.example.repository;

import com.example.repository.entity.Alert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Timestamp;
import java.util.Optional;

public interface AlertRepository extends JpaRepository<Alert, Long> {

    @Query("Select a FROM Alert a WHERE a.sensorId = :sensorId and :now BETWEEN a.startTimestamp and a.endTimestamp")
    Optional<Alert> retrieveCurrentAlert(@Param("sensorId") String sensorId, @Param("now") Timestamp now);


}