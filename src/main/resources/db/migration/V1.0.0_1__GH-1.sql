CREATE TABLE bookings
(
    clinic_id   VARCHAR(250) NOT NULL,
    service_id  VARCHAR(250) NOT NULL,
    start_time  TIMESTAMP    NOT NULL,
    customer_id VARCHAR(250) NOT NULL,
    end_time    TIMESTAMP    NULL,
    PRIMARY KEY (clinic_id, service_id, start_time)
);