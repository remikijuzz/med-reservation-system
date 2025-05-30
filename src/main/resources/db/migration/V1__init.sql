-- V1__init.sql: podstawowe tabele dla systemu rezerwacji wizyt lekarskich

CREATE TABLE patient (
  id              BIGSERIAL PRIMARY KEY,
  first_name      VARCHAR(100) NOT NULL,
  last_name       VARCHAR(100) NOT NULL,
  email           VARCHAR(150) UNIQUE NOT NULL,
  phone           VARCHAR(20),
  created_at      TIMESTAMP     DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE doctor (
  id              BIGSERIAL PRIMARY KEY,
  first_name      VARCHAR(100) NOT NULL,
  last_name       VARCHAR(100) NOT NULL,
  specialty       VARCHAR(100),
  email           VARCHAR(150) UNIQUE,
  phone           VARCHAR(20),
  created_at      TIMESTAMP     DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE appointment (
  id              BIGSERIAL PRIMARY KEY,
  appointment_time TIMESTAMP     NOT NULL,
  status          VARCHAR(50)   NOT NULL,
  patient_id      BIGINT        NOT NULL REFERENCES patient(id) ON DELETE CASCADE,
  doctor_id       BIGINT        NOT NULL REFERENCES doctor(id)  ON DELETE CASCADE,
  created_at      TIMESTAMP     DEFAULT CURRENT_TIMESTAMP
);

-- indeksy dla szybszych zapytań
CREATE INDEX idx_appointment_time ON appointment(appointment_time);
CREATE INDEX idx_patient_email     ON patient(email);
CREATE INDEX idx_doctor_specialty  ON doctor(specialty);
