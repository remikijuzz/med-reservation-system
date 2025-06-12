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

-- *************************************************************************
-- PRZYKŁADOWE DANE
-- *************************************************************************

-- 1) Wszyscy użytkownicy: user1, admin, dwóch doktorów, dwóch pacjentów
INSERT INTO users (username, email, password, dtype, notification_channel)
VALUES
  ('user1',    'user1@example.com',    'pass1',      'User',    'EMAIL'),
  ('admin',    'admin@example.com',    'adminpass',  'User',    'EMAIL'),
  ('doctor1',  'doctor1@example.com',  'pass2',      'Doctor',  'EMAIL'),
  ('doctor2',  'doctor2@example.com',  'pass3',      'Doctor',  'SMS'),
  ('patient1', 'patient1@example.com', 'pass4',      'Patient', 'EMAIL'),
  ('patient2', 'patient2@example.com', 'pass5',      'Patient', 'SMS');

-- 2) Role dla wszystkich Userów
INSERT INTO user_roles (user_id, role)
SELECT id, 'ROLE_USER' FROM users
  WHERE username IN ('user1','admin','doctor1','doctor2','patient1','patient2');

-- 3) Dodatkowe role
INSERT INTO user_roles (user_id, role)
SELECT id, 'ROLE_ADMIN'   FROM users WHERE username = 'admin';
INSERT INTO user_roles (user_id, role)
SELECT id, 'ROLE_DOCTOR'  FROM users WHERE username IN ('doctor1','doctor2');
INSERT INTO user_roles (user_id, role)
SELECT id, 'ROLE_PATIENT' FROM users WHERE username IN ('patient1','patient2');

-- 4) Szczegóły lekarzy
INSERT INTO doctors (id, first_name, last_name, specialization, phone_number)
SELECT id, 'John', 'Doe', 'General',   '+48111111111' FROM users WHERE username = 'doctor1';
INSERT INTO doctors (id, first_name, last_name, specialization, phone_number)
SELECT id, 'Jane', 'Roe', 'Neurology', '+48222222222' FROM users WHERE username = 'doctor2';

-- 5) Szczegóły pacjentów
INSERT INTO patients (id, first_name, last_name, phone_number)
SELECT id, 'Alice', 'Wonder', '+48333333333' FROM users WHERE username = 'patient1';
INSERT INTO patients (id, first_name, last_name, phone_number)
SELECT id, 'Bob',   'Builder', '+48444444444' FROM users WHERE username = 'patient2';

-- 6) Przykładowe wizyty (2 dla patient1, 2 dla patient2)
INSERT INTO appointments (appointment_date_time, patient_id, doctor_id, description)
VALUES
  ('2025-06-15 09:00:00',
     (SELECT id FROM users WHERE username='patient1'),
     (SELECT id FROM users WHERE username='doctor1'),
     'Konsultacja ogólna'),
  ('2025-06-16 10:30:00',
     (SELECT id FROM users WHERE username='patient1'),
     (SELECT id FROM users WHERE username='doctor2'),
     'Kontrola wyników'),
  ('2025-06-17 11:00:00',
     (SELECT id FROM users WHERE username='patient2'),
     (SELECT id FROM users WHERE username='doctor1'),
     'Badanie profilaktyczne'),
  ('2025-06-18 14:00:00',
     (SELECT id FROM users WHERE username='patient2'),
     (SELECT id FROM users WHERE username='doctor2'),
     'Konsultacja specjalistyczna');
