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
@SequenceGenerator(name = "alert_seq", sequenceName = "alert_SEQ", allocationSize = 1)
public class Alert {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "alert_seq")
    private Long id;
    private String sensorId;
    private Timestamp startTimestamp;
    private Timestamp endTimestamp;
}
