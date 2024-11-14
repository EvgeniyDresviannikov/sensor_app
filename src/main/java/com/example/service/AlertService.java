package com.example.service;

import com.example.repository.AlertRepository;
import com.example.repository.MeasurementRepository;
import com.example.repository.entity.Alert;
import com.example.repository.entity.Measurement;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AlertService {

    static final Timestamp MAX_TIMESTAMP = Timestamp.valueOf("2999-12-31 23:59:59");

    private final AlertRepository alertRepository;
    private final MeasurementRepository measurementRepository;

    public void processAlertStatus(String sensorId, Timestamp now) {

        Optional<Alert> currentAlert = alertRepository.retrieveCurrentAlert(sensorId, now);
        List<Measurement> lastMeasurements = measurementRepository.retrieveLast3MeasurementsBySensorId(sensorId);

        if (currentAlert.isPresent() && allOk(lastMeasurements)) {
            currentAlert.get().setEndTimestamp(now);
        } else if (currentAlert.isEmpty() && allNotOk(lastMeasurements)) {
            alertRepository.save(Alert.builder()
                    .sensorId(sensorId)
                    .startTimestamp(now)
                    .endTimestamp(MAX_TIMESTAMP)
                    .build()
            );
        }
    }

    public boolean checkAlert(String sensorId, Timestamp now) {
        return alertRepository.retrieveCurrentAlert(sensorId, now).isPresent();
    }


    private boolean allOk(List<Measurement> measurements) {
        return measurements.stream().allMatch(i -> i.getCo2() < 2000);
    }

    private boolean allNotOk(List<Measurement> measurements) {
        return measurements.stream().allMatch(i -> i.getCo2() >= 2000);
    }

}
