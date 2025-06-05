-- V1__init.sql

-- 1) Tabela użytkowników (zgodna z @Table(name="app_user") w User.java)
CREATE TABLE app_user (
  id SERIAL PRIMARY KEY,
  username VARCHAR(50) UNIQUE NOT NULL,
  password VARCHAR(100) NOT NULL,
  role VARCHAR(20) NOT NULL
);

-- 2) Tabela powiązań user → role (jedna kolumna user_id wskazuje na app_user.id)
CREATE TABLE user_role (
  user_id INTEGER NOT NULL REFERENCES app_user(id) ON DELETE CASCADE,
  role VARCHAR(255) NOT NULL
);

-- 3) Tabela lekarzy (encja Doctor powinna być @Table(name="doctor") lub domyślnie doctor)
CREATE TABLE doctor (
  id SERIAL PRIMARY KEY,
  name VARCHAR(100) NOT NULL,
  specialization VARCHAR(100) NOT NULL
);

-- 4) Tabela pacjentów (encja Patient → @Table(name="patient") lub domyślnie patient)
CREATE TABLE patient (
  id SERIAL PRIMARY KEY,
  name VARCHAR(100) NOT NULL,
  email VARCHAR(100) UNIQUE NOT NULL
);

-- 5) Tabela wizyt (encja Appointment → @Table(name="appointment") lub domyślnie appointment)
CREATE TABLE appointment (
  id SERIAL PRIMARY KEY,
  date TIMESTAMP NOT NULL,
  doctor_id INTEGER NOT NULL REFERENCES doctor(id) ON DELETE CASCADE,
  patient_id INTEGER NOT NULL REFERENCES patient(id) ON DELETE CASCADE
);
