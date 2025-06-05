-- V1__init.sql

-- 1) Tabela bazowa użytkowników
CREATE TABLE app_user (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL,
    role VARCHAR(20) NOT NULL
);

-- 2) Tabela lekarzy (extends User)
CREATE TABLE doctor (
    id INTEGER PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    specialization VARCHAR(100),
    email VARCHAR(100) UNIQUE,
    phone VARCHAR(20),
    CONSTRAINT fk_doctor_user FOREIGN KEY (id) REFERENCES app_user(id) ON DELETE CASCADE
);

-- 3) Tabela pacjentów (extends User)
CREATE TABLE patient (
    id INTEGER PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(100) UNIQUE,
    phone VARCHAR(20),
    CONSTRAINT fk_patient_user FOREIGN KEY (id) REFERENCES app_user(id) ON DELETE CASCADE
);

-- 4) Tabela wizyt (Appointment)
CREATE TABLE appointment (
    id SERIAL PRIMARY KEY,
    appointment_time TIMESTAMP NOT NULL,
    doctor_id INTEGER NOT NULL,
    patient_id INTEGER NOT NULL,
    status VARCHAR(20),
    CONSTRAINT fk_appointment_doctor FOREIGN KEY (doctor_id) REFERENCES doctor(id) ON DELETE CASCADE,
    CONSTRAINT fk_appointment_patient FOREIGN KEY (patient_id) REFERENCES patient(id) ON DELETE CASCADE
);
