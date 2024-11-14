package com.example.repository;

import com.example.dto.MetricsDto;
import com.example.repository.entity.Measurement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

public interface MeasurementRepository extends JpaRepository<Measurement, Long> {

    @Query("SELECT new com.example.dto.MetricsDto(MAX(m.co2) as maxLast30Days, AVG(m.co2) as avgLast30Days) FROM Measurement m WHERE m.sensorId = :sensorId and m.measurementTimestamp >= :from")
    MetricsDto retrieveMetrics(@Param("sensorId") String sensorId, @Param("from") Timestamp from);

    @Query("SELECT m FROM Measurement m WHERE m.sensorId = :sensorId ORDER BY m.measurementTimestamp DESC LIMIT 3")
    List<Measurement> retrieveLast3MeasurementsBySensorId(@Param("sensorId") String sensorId);

    @Query("SELECT m FROM Measurement m WHERE m.sensorId = :sensorId ORDER BY m.measurementTimestamp DESC LIMIT 1")
    Optional<Measurement> retrieveTheLastMeasurementBySensorId(@Param("sensorId") String sensorId);

}