package com.example.service;

import com.example.dto.MeasurementDto;
import com.example.dto.MetricsDto;
import com.example.dto.SensorStatus;
import com.example.dto.SensorStatusDto;
import com.example.exception.DBUniqueConstraintViolationException;
import com.example.exception.NotFoundException;
import com.example.mapper.MeasurementMapper;
import com.example.repository.MeasurementRepository;
import com.example.repository.entity.Measurement;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;

@Service
@RequiredArgsConstructor
public class SensorService {

    private final MeasurementMapper mapper;
    private final MeasurementRepository measurementRepository;
    private final AlertService alertService;

    @Transactional
    public void collect(MeasurementDto measurementDto, String sensorId) {
        Measurement measurement = mapper.toMeasurement(measurementDto, sensorId);
        measurementRepository.save(measurement);

        // manage alert table
        alertService.processAlertStatus(measurement.getSensorId(), measurement.getMeasurementTimestamp());
    }

    public SensorStatusDto getStatus(String sensorId, Timestamp now) {
        SensorStatus status = SensorStatus.OK;

        if (alertService.checkAlert(sensorId, now)) {
            status = SensorStatus.ALERT;
        } else {
            Measurement lastMeasurement = measurementRepository.retrieveTheLastMeasurementBySensorId(sensorId)
                    .orElseThrow(() -> new NotFoundException("sensor not found"));
            if (lastMeasurement.getCo2() >= 2000) {
                status = SensorStatus.WARN;
            }
        }

        return SensorStatusDto.builder().status(status).build();
    }

    public MetricsDto getMetrics(String sensorId, Timestamp from) {
        MetricsDto metricsDto = measurementRepository.retrieveMetrics(sensorId, from);
        if (metricsDto.getAvgLast30Days() == null) {
            throw new NotFoundException("no data for last 30 days");
        }
        return metricsDto;
    }
}
