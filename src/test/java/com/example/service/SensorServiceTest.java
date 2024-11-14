package com.example.service;

import com.example.dto.MeasurementDto;
import com.example.dto.MetricsDto;
import com.example.dto.SensorStatus;
import com.example.dto.SensorStatusDto;
import com.example.mapper.MeasurementMapper;
import com.example.repository.MeasurementRepository;
import com.example.repository.entity.Measurement;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;

@RunWith(MockitoJUnitRunner.class)
class SensorServiceTest {

    private static final String SENSOR_UUID = "e7c086d3-6131-4cb7-886f-f8a88b62d2c0";
    private static final LocalDateTime MEASUREMENT_TIME = LocalDateTime.now();
    private static final Timestamp MEASUREMENT_TIMESTAMP = Timestamp.valueOf(MEASUREMENT_TIME);

    private static final Timestamp TIMESTAMP_NOW = MEASUREMENT_TIMESTAMP;

    @Mock
    MeasurementMapper mapper;

    @Mock
    MeasurementRepository measurementRepository;

    @Mock
    AlertService alertService;

    private SensorService sensorService;

    @BeforeEach
    void before() {
        MockitoAnnotations.initMocks(this);

        sensorService = new SensorService(mapper, measurementRepository, alertService);
    }


    @Test
    void collect() {
        // before
        MeasurementDto measurementDto = MeasurementDto.builder().time(MEASUREMENT_TIME).co2(2000).build();
        Measurement measurement = Measurement.builder().measurementTimestamp(MEASUREMENT_TIMESTAMP).co2(2000).sensorId(SENSOR_UUID).build();

        Mockito.when(mapper.toMeasurement(measurementDto, SENSOR_UUID)).thenReturn(measurement);

        // when
        sensorService.collect(measurementDto, SENSOR_UUID);

        // then
        Mockito.verify(mapper).toMeasurement(measurementDto, SENSOR_UUID);
        Mockito.verify(measurementRepository).save(measurement);
        Mockito.verify(alertService).processAlertStatus(SENSOR_UUID, MEASUREMENT_TIMESTAMP);

    }

    @Test
    void getStatus_shouldReturnOK() {
        // before
        Measurement measurementOk = Measurement.builder().co2(1999).build();
        Mockito.when(alertService.checkAlert(SENSOR_UUID, TIMESTAMP_NOW)).thenReturn(false);
        Mockito.when(measurementRepository.retrieveTheLastMeasurementBySensorId(SENSOR_UUID)).thenReturn(Optional.of(measurementOk));

        // when
        SensorStatusDto result = sensorService.getStatus(SENSOR_UUID, TIMESTAMP_NOW);

        // then
        Assertions.assertEquals(SensorStatus.OK, result.getStatus());
    }

    @Test
    void getStatus_shouldReturnWARN() {
        // before
        Measurement measurementNotOk = Measurement.builder().co2(2000).build();
        Mockito.when(alertService.checkAlert(SENSOR_UUID, TIMESTAMP_NOW)).thenReturn(false);
        Mockito.when(measurementRepository.retrieveTheLastMeasurementBySensorId(SENSOR_UUID)).thenReturn(Optional.of(measurementNotOk));

        // when
        SensorStatusDto result = sensorService.getStatus(SENSOR_UUID, TIMESTAMP_NOW);

        // then
        Assertions.assertEquals(SensorStatus.WARN, result.getStatus());
    }

    @Test
    void getStatus_shouldReturnALERT() {
        // before
        Mockito.when(alertService.checkAlert(SENSOR_UUID, TIMESTAMP_NOW)).thenReturn(true);

        // when
        SensorStatusDto result = sensorService.getStatus(SENSOR_UUID, TIMESTAMP_NOW);

        // then
        Mockito.verifyNoInteractions(measurementRepository);
        Assertions.assertEquals(SensorStatus.ALERT, result.getStatus());
    }

    @Test
    void getMetrics() {

        // before
        MetricsDto metricsFromDB = MetricsDto.builder().avgLast30Days(1000.0).maxLast30Days(2005).build();
        Mockito.when(measurementRepository.retrieveMetrics(SENSOR_UUID, TIMESTAMP_NOW)).thenReturn(metricsFromDB);

        // when
        MetricsDto result = sensorService.getMetrics(SENSOR_UUID, TIMESTAMP_NOW);

        // then
        Mockito.verify(measurementRepository).retrieveMetrics(SENSOR_UUID, TIMESTAMP_NOW);
        Assertions.assertEquals(metricsFromDB, result);
    }
}