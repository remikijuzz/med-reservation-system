-- src/main/resources/db/migration/V1__init.sql

-- 1. Tabela users z kolumną dtype i notification_channel
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    dtype VARCHAR(31) NOT NULL,
    notification_channel VARCHAR(20) NOT NULL DEFAULT 'EMAIL'
);

-- 2. Tabela user_roles
CREATE TABLE IF NOT EXISTS user_roles (
    user_id BIGINT NOT NULL,
    role VARCHAR(50) NOT NULL,
    CONSTRAINT fk_user_roles_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE
);

-- 3. Tabela doctors
CREATE TABLE IF NOT EXISTS doctors (
    id BIGINT PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    specialization VARCHAR(255) NOT NULL,
    phone_number VARCHAR(25) NOT NULL,
    CONSTRAINT fk_doctors_user
        FOREIGN KEY (id)
        REFERENCES users(id)
        ON DELETE CASCADE
);

-- 4. Tabela patients
CREATE TABLE IF NOT EXISTS patients (
    id BIGINT PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    phone_number VARCHAR(25) NOT NULL,
    CONSTRAINT fk_patients_user
        FOREIGN KEY (id)
        REFERENCES users(id)
        ON DELETE CASCADE
);

-- 5. Tabela appointments
CREATE TABLE IF NOT EXISTS appointments (
    id BIGSERIAL PRIMARY KEY,
    appointment_date_time TIMESTAMP NOT NULL,
    patient_id BIGINT NOT NULL,
    doctor_id BIGINT NOT NULL,
    description TEXT,
    CONSTRAINT fk_appointment_patient
        FOREIGN KEY (patient_id)
        REFERENCES patients(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_appointment_doctor
        FOREIGN KEY (doctor_id)
        REFERENCES doctors(id)
        ON DELETE CASCADE
);
