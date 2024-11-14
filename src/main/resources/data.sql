CREATE TABLE measurement (
   id BIGINT NOT NULL auto_increment,
   sensor_id VARCHAR(50) NOT NULL,
   co2 INT NOT NULL,
   measurement_timestamp TIMESTAMP NOT NULL,
   PRIMARY KEY (id)
);

CREATE TABLE alert (
   id BIGINT NOT NULL auto_increment,
   sensor_id VARCHAR(50) NOT NULL,
   start_timestamp TIMESTAMP NOT NULL,
   end_timestamp TIMESTAMP,
   PRIMARY KEY (id)
);

create UNIQUE INDEX MEASUREMENT_IDX on measurement(sensor_id, measurement_timestamp);
create UNIQUE INDEX ALERT_IDX on alert(sensor_id, start_timestamp);

CREATE SEQUENCE MEASUREMENT_SEQ;
CREATE SEQUENCE ALERT_SEQ;

--INSERT INTO measurement (id, sensor_id, co2, measurement_timestamp) VALUES (NEXTVAL('MEASUREMENT_SEQ'), 'sensor_1', 1800, '2024-11-13 12:00:00');
--INSERT INTO measurement (id, sensor_id, co2, measurement_timestamp) VALUES (NEXTVAL('MEASUREMENT_SEQ'), 'sensor_1', 1900, '2024-11-13 12:00:01');

--INSERT INTO alert (id, sensor_id, start_timestamp, end_timestamp) VALUES (NEXTVAL('ALERT_SEQ'), 'sensor_1', '2024-11-13 12:00:00', '2999-12-31 23:59:59');
