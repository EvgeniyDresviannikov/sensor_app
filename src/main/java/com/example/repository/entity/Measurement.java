package com.example.repository.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@SequenceGenerator(name = "measurement_seq", sequenceName = "measurement_SEQ", allocationSize = 1)
public class Measurement {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "measurement_seq")
    private Long id;
    private String sensorId;
    private Integer co2;
    private Timestamp measurementTimestamp;

}
