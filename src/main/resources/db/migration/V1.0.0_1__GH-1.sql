CREATE TABLE bookings
(
    clinic_id   VARCHAR(250) NOT NULL,
    service_id  VARCHAR(250) NOT NULL,
    date        DATE         NOT NULL,
    start_time  TIME         NOT NULL,
    customer_id VARCHAR(250) NOT NULL,
    PRIMARY KEY (clinic_id, service_id, date, start_time)
);