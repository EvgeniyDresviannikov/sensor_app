package com.example.controller;

import com.example.dto.MeasurementDto;
import com.example.dto.MetricsDto;
import com.example.dto.SensorStatusDto;
import com.example.service.SensorService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;


@RestController
@RequestMapping("/api/v1/sensors")
@AllArgsConstructor
public class SensorController {

    private final SensorService sensorService;

    @PostMapping(value = "/{uuid}/measurements" )
    public ResponseEntity<Void> collect(@PathVariable("uuid") String sensorId,
                                        @RequestBody MeasurementDto measurementDto) {

        sensorService.collect(measurementDto, sensorId);

        return ResponseEntity.status(HttpStatus.OK).build();

    }

    @GetMapping(value = "/{uuid}")
    public ResponseEntity<SensorStatusDto> getStatus(@PathVariable("uuid") String sensorId) {
        return ResponseEntity.status(HttpStatus.OK).body(
                sensorService.getStatus(sensorId, Timestamp.valueOf(LocalDateTime.now()))
        );
    }

    @GetMapping(value = "/{uuid}/metrics")
    public ResponseEntity<MetricsDto> getMetrics(@PathVariable("uuid") String sensorId) {
        Timestamp from = Timestamp.valueOf(LocalDateTime.now().minusDays(30));
        return ResponseEntity.status(HttpStatus.OK).body(sensorService.getMetrics(sensorId, from));
    }

}

