package com.example.service;

import com.example.repository.AlertRepository;
import com.example.repository.MeasurementRepository;
import com.example.repository.entity.Alert;
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
import java.util.List;
import java.util.Optional;

import static com.example.service.AlertService.MAX_TIMESTAMP;

@RunWith(MockitoJUnitRunner.class)
class AlertServiceTest {

    private static final String SENSOR_UUID = "e7c086d3-6131-4cb7-886f-f8a88b62d2c0";
    private static final LocalDateTime LOCAL_DATE_TIME_NOW = LocalDateTime.now();
    private static final Timestamp TIMESTAMP_NOW = Timestamp.valueOf(LOCAL_DATE_TIME_NOW);
    private static final Timestamp TIMESTAMP_START_TIME = Timestamp.valueOf(LOCAL_DATE_TIME_NOW.minusMinutes(1L));

    @Mock
    AlertRepository alertRepository;

    @Mock
    MeasurementRepository measurementRepository;

    private AlertService alertService;

    @BeforeEach
    void before() {
        MockitoAnnotations.initMocks(this);

        alertService = new AlertService(alertRepository, measurementRepository);
    }


    @Test
    void processAlertStatus_should_close_current_alert() {

        // before
        Alert alertFromDb = Alert.builder()
                .id(1L)
                .sensorId(SENSOR_UUID)
                .startTimestamp(TIMESTAMP_START_TIME)
                .endTimestamp(MAX_TIMESTAMP)
                .build();

        Mockito.when(alertRepository.retrieveCurrentAlert(SENSOR_UUID, TIMESTAMP_NOW)).thenReturn(Optional.of(alertFromDb));

        // last 3 measurements lower 2000
        Mockito.when(measurementRepository.retrieveLast3MeasurementsBySensorId(SENSOR_UUID)).thenReturn(
                List.of(Measurement.builder().co2(1999).build(),
                        Measurement.builder().co2(1997).build(),
                        Measurement.builder().co2(1998).build()
                )
        );

        // when
        alertService.processAlertStatus(SENSOR_UUID, TIMESTAMP_NOW);

        // then
        Assertions.assertEquals(TIMESTAMP_NOW, alertFromDb.getEndTimestamp());
    }

    @Test
    void processAlertStatus_should_add_new_alert() {

        // before
        Mockito.when(alertRepository.retrieveCurrentAlert(SENSOR_UUID, TIMESTAMP_NOW)).thenReturn(Optional.empty());

        // all last 3 measurements higher or equal 2000
        Mockito.when(measurementRepository.retrieveLast3MeasurementsBySensorId(SENSOR_UUID)).thenReturn(
                List.of(Measurement.builder().co2(2000).build(),
                        Measurement.builder().co2(2001).build(),
                        Measurement.builder().co2(2002).build()
                )
        );

        // when
        alertService.processAlertStatus(SENSOR_UUID, TIMESTAMP_NOW);

        // then
        Mockito.verify(alertRepository).save(
                Alert.builder()
                        .sensorId(SENSOR_UUID)
                        .startTimestamp(TIMESTAMP_NOW)
                        .endTimestamp(MAX_TIMESTAMP)
                        .build()
        );

    }

    @Test
    void processAlertStatus_should_not_add_new_alert() {

        // before
        Mockito.when(alertRepository.retrieveCurrentAlert(SENSOR_UUID, TIMESTAMP_NOW)).thenReturn(Optional.empty());

        // not all last 3 measurements higher or equal 2000
        Mockito.when(measurementRepository.retrieveLast3MeasurementsBySensorId(SENSOR_UUID)).thenReturn(
                List.of(Measurement.builder().co2(2000).build(),
                        Measurement.builder().co2(1999).build(),
                        Measurement.builder().co2(2002).build()
                )
        );

        // when
        alertService.processAlertStatus(SENSOR_UUID, TIMESTAMP_NOW);

        // then
        Mockito.verify(alertRepository).retrieveCurrentAlert(SENSOR_UUID, TIMESTAMP_NOW);
        Mockito.verifyNoMoreInteractions(alertRepository);

    }

    @Test
    void checkAlert_should_return_true() {
        // before
        Mockito.when(alertRepository.retrieveCurrentAlert(SENSOR_UUID, TIMESTAMP_NOW))
                .thenReturn(Optional.of(Alert.builder().build()));

        // when
        Assertions.assertTrue(alertService.checkAlert(SENSOR_UUID, TIMESTAMP_NOW));

    }

    @Test
    void checkAlert_should_return_false() {
        // before
        Mockito.when(alertRepository.retrieveCurrentAlert(SENSOR_UUID, TIMESTAMP_NOW))
                .thenReturn(Optional.empty());

        // when
        Assertions.assertFalse(alertService.checkAlert(SENSOR_UUID, TIMESTAMP_NOW));

    }
}