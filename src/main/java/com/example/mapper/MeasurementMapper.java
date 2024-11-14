package com.example.mapper;

import com.example.dto.MeasurementDto;
import com.example.repository.entity.Measurement;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;

@Service
public class MeasurementMapper {

    public Measurement toMeasurement(MeasurementDto measurementDto, String sensorId) {

        return Measurement.builder()
                .sensorId(sensorId)
                .co2(measurementDto.getCo2())
                .measurementTimestamp(Timestamp.valueOf(measurementDto.getTime()))
                .build();
    }

    public MeasurementDto toMeasurementFto(Measurement measurement) {

        return MeasurementDto.builder()
                .co2(measurement.getCo2())
                .time(measurement.getMeasurementTimestamp().toLocalDateTime())
                .build();
    }
}
