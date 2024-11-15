package com.example.controller;

import com.example.dto.MeasurementDto;
import com.example.repository.MeasurementRepository;
import com.example.repository.entity.Measurement;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SensorControllerTest {

    private static final String SENSOR_UUID = "e7c086d3-6131-4cb7-886f-f8a88b62d2c0";
    private static final LocalDateTime MEASUREMENT_TIME = LocalDateTime.now();
    private static final Timestamp MEASUREMENT_TIMESTAMP = Timestamp.valueOf(MEASUREMENT_TIME);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MeasurementRepository measurementRepository;

    @BeforeEach
    void cleanMeasurements() {
        measurementRepository.deleteAll();
    }

    @Test
    void collect() throws Exception {

        // before
        MeasurementDto body = MeasurementDto.builder()
                .co2(1000)
                .time(LocalDateTime.parse("2019-02-01T18:55:47+00:00", DateTimeFormatter.ISO_DATE_TIME))
                .build();

        String json = objectMapper.writeValueAsString(body);

        // when then
        mockMvc.perform(post("/api/v1/sensors/" + SENSOR_UUID + "/measurements")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .characterEncoding("utf-8"))
                .andExpect(status().isOk())
                .andReturn();

    }

    @Test
    void getStatus() throws Exception {
        // before
        measurementRepository.save(Measurement.builder()
                .sensorId(SENSOR_UUID)
                .measurementTimestamp(MEASUREMENT_TIMESTAMP)
                .co2(1999)
                .build()
        );

        // when then
        mockMvc.perform(get("/api/v1/sensors/" + SENSOR_UUID))
                .andExpect(status().isOk())
                .andExpect(content().json("{'status':'OK'}"));

    }

    @Test
    void getMetrics() throws Exception {
        // before
        measurementRepository.save(Measurement.builder()
                .sensorId(SENSOR_UUID)
                .measurementTimestamp(MEASUREMENT_TIMESTAMP)
                .co2(1999)
                .build()
        );

        // when then
        mockMvc.perform(get("/api/v1/sensors/" + SENSOR_UUID + "/metrics"))
                .andExpect(status().isOk())
                .andExpect(content().json("{'maxLast30Days':1999,'avgLast30Days':1999.0}"));
    }
}