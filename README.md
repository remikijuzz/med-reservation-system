# Medical Reservation System

## Opis działania projektu

Medical Reservation System to aplikacja webowa typu REST API umożliwiająca:

* **Rejestrację i logowanie** użytkowników o rolach USER, DOCTOR, PATIENT i ADMIN.  
* **Zarządzanie wizytami medycznymi**: tworzenie, przeglądanie, aktualizacja i usuwanie wizyt.  
* **Powiadomienia** o zaplanowanych, zmienionych lub odwołanych wizytach, wysyłane e-mailem lub SMS (wzorzec Strategy).

### 1. Wymagania konieczne

**1.1. Role-Based Access Control**  
Aplikacja definiuje cztery role: `ROLE_USER`, `ROLE_ADMIN`, `ROLE_DOCTOR` i `ROLE_PATIENT`.  
Autoryzację HTTP skonfigurowano w klasie `SecurityConfig`:

- **Publiczne**: wszystkie ścieżki `/api/auth/**` oraz Swagger UI (`/swagger-ui.html`, `/v3/api-docs/**`).  
- **ADMIN only**: `POST`, `PUT`, `DELETE` na `/api/doctors/**` i `/api/patients/**`.  
- **Authenticated** (`USER`, `ADMIN`, `DOCTOR`, `PATIENT`): `GET /api/doctors/**`, `GET /api/patients/**` oraz wszystkie operacje na `/api/appointments/**`.  

Uwierzytelnianie odbywa się przez JWT, a role są ładowane w `CustomUserDetailsService` i umieszczane w tokenie.

**1.2. Polimorfizm i wzorzec projektowy**  
Do wysyłania powiadomień zastosowano wzorzec **Strategy**:

- Interfejs `NotificationService` z metodą `sendNotification(User, String)`.  
- Dwie implementacje:
  - `EmailNotificationService` (`@Service("EMAIL")`)  
  - `SmsNotificationService`   (`@Service("SMS")`)  
- Wstrzyknięto `Map<String, NotificationService>` w `AppointmentService`, aby według pola `notificationChannel` użytkownika wybierać odpowiedni serwis.  

To rozwiązanie wykorzystuje **polimorfizm** (różne klasy pod jednym interfejsem) i spełnia zasadę **Open/Closed** – w przyszłości wystarczy dodać kolejną klasę `@Service("PUSH")`, by wspierać nowy kanał.

---

### 2. Warstwa prezentacji (Controllers)

* Klasy w pakiecie `org.example.medreservationsystem.controller` odpowiadają za odbiór żądań HTTP:
  * **AuthController**: endpointy `/api/auth/**` do rejestracji i logowania (generowanie JWT).
  * **DoctorController**, **PatientController**, **AppointmentController**: CRUD dla obiektów Doctor, Patient, Appointment.  
* Każdy controller zwraca odpowiednie kody HTTP (`201 Created`, `200 OK`, `404 Not Found`, `204 No Content`).  
* Walidacja wejścia (`@Valid`, `@NotBlank`, `@NotNull`) zapewnia podstawową kontrolę danych.

### 3. Warstwa logiki biznesowej (Services)

* Pakiet `org.example.medreservationsystem.service` implementuje kluczowe operacje:
  * **AuthService**: rejestracja użytkowników, enkodowanie haseł, generowanie tokenów JWT.  
  * **AppointmentService**: tworzenie i zarządzanie wizytami; po zapisaniu wizyty wysyła powiadomienia (polimorfizm NotificationService).  
  * **DoctorService** i **PatientService**: operacje CRUD na encjach Doctor i Patient.  
  * **JwtService** i **CustomUserDetailsService**: obsługa tokenów JWT i integracja ze Spring Security.  
* Zasada **Single Responsibility**: każdy serwis ma jasno wyodrębniony zakres odpowiedzialności.

### 4. Warstwa dostępu do danych (Repositories)

* Pakiet `org.example.medreservationsystem.repository` używa Spring Data JPA:
  * **UserRepository**: wyszukiwanie po loginie, sprawdzanie unikalności username/email.  
  * **DoctorRepository**, **PatientRepository**, **AppointmentRepository**: dedykowane metody finderów (`findByDoctorId`, `findByPatientId`).  
* Mapowanie obiektowo-relacyjne realizowane przez Hibernate i adnotacje JPA.

### 5. Konfiguracje i zabezpieczenia

* Pakiet `org.example.medreservationsystem.config`:
  * **SecurityConfig**: Spring Security z JWT, Role-Based Access Control, CORS i CSRF wyłączone.  
  * **OpenApiConfig**: konfiguracja Springdoc OpenAPI (Swagger UI) zabezpieczona `bearerAuth`.  
* **JwtAuthenticationFilter** (pakiet `security`): parsowanie tokenu, ustawienie kontekstu bezpieczeństwa.

### 6. Powiadomienia (Notification)

* Wzorzec **Strategy**: interfejs `NotificationService` z implementacjami `EmailNotificationService` i `SmsNotificationService`.  
* W `AppointmentService` wybór odpowiedniej usługi na podstawie preferencji użytkownika (`notificationChannel`).

### 7. Migracje bazy danych i uruchomienie

* **Flyway**: migracje SQL w `src/main/resources/db/migration/V1__init.sql` tworzą tabele `users`, `user_roles`, `doctors`, `patients`, `appointments`.  
* Baza PostgreSQL zarządzana przez Docker Compose (`docker-compose.yml`).

### 8. Swagger UI (Demo)

Poniżej krok po kroku, jak przygotować środowisko demo i przetestować aplikację w Swagger UI, wliczając ręczne wstawienie przykładowych danych SQL oraz rejestrację/logowanie użytkowników.

#### A. Wstawienie przykładowych danych (SQL)

````sql
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

```sql
````

#### B. Rejestracja, logowanie i dostęp do endpointów dla administratora. 

1. **Rejestracja administratora (ROLE\_ADMIN):**

   * AuthController → `POST /api/auth/register-admin`
   * Kliknij *Try it out* i wklej:

     ```json
     {
       "username": "admin1",
       "email": "admin1@example.com",
       "password": "adminpass",
       "notificationChannel": "EMAIL"
     }
     ```
   * Kliknij *Execute* → `201 Created`.

2. **Logowanie administratora:**

   * AuthController → `POST /api/auth/login`
   * Kliknij *Try it out* i wklej:

     ```json
     {
       "username": "admin1",
       "password": "adminpass"
     }
     ```
   * Kliknij *Execute* → otrzymasz:

     ```json
     {
       "token": "<JWT_TOKEN_ADMIN>"
     }
     ```

3. **Ustawienie tokena administratora:**

   * Kliknij **Authorize** i wpisz:

     ```text
     <JWT_TOKEN_ADMIN>
     ```

4. **Dostępne endpointy dla administratora (ROLE_ADMIN):**

   * **CRUD lekarzy:**
     * `GET    /api/doctors`           – pobiera listę wszystkich lekarzy  
     * `POST   /api/doctors`           – tworzy nowego lekarza  
     * `PUT    /api/doctors/{id}`      – aktualizuje dane lekarza o podanym `id`  
     * `DELETE /api/doctors/{id}`      – usuwa lekarza o podanym `id`  

   * **CRUD pacjentów:**
     * `GET    /api/patients`          – pobiera listę wszystkich pacjentów  
     * `POST   /api/patients`          – tworzy nowego pacjenta  
     * `PUT    /api/patients/{id}`     – aktualizuje dane pacjenta o podanym `id`  
     * `DELETE /api/patients/{id}`     – usuwa pacjenta o podanym `id`  


#### C. Rejestracja pacjenta, logowanie i dodawanie wizyty

1. **Rejestracja nowego pacjenta (ROLE_PATIENT)**  
   * AuthController → `POST /api/auth/register/patient?firstName={firstName}&lastName={lastName}&phoneNumber={phone}`  
   * Kliknij **Try it out**, ustaw query parameters:  
     - `firstName` = `Jan`  
     - `lastName` = `Kowalski`  
     - `phoneNumber` = `+48123123123`  
   * W sekcji *Request body* wklej:
     ```json
     {
       "username": "patient3",
       "email":    "patient3@example.com",
       "password": "patientpass",
       "notificationChannel": "EMAIL"
     }
     ```
   * Kliknij **Execute** → otrzymasz `201 Created` i obiekt `Patient` z polem `id`.

2. **Logowanie pacjenta**  
   * AuthController → `POST /api/auth/login`  
   * Kliknij **Try it out** i wklej:
     ```json
     {
       "username": "patient3",
       "password": "patientpass"
     }
     ```
   * Kliknij **Execute** → w odpowiedzi:
     ```json
     {
       "token": "<JWT_TOKEN_PATIENT>"
     }
     ```

3. **Ustawienie tokena pacjenta w Swagger UI**  
   * Kliknij **Authorize** (ikonka kłódki) w prawym górnym rogu.  
   * W polu **Value** wpisz:
     ```
     <JWT_TOKEN_PATIENT>
     ```
   * Kliknij **Authorize** → teraz wszystkie żądania będą wysyłane jako zalogowany pacjent.

4. **Dodanie nowej wizyty**  
   * AppointmentController → `POST /api/appointments`  
   * Kliknij **Try it out**, w *Request body* wklej (podmieniaj `patientId` na ID z rejestracji, `doctorId` na dowolnego lekarza z bazy, np. `1` lub `2`):
     ```json
     {
       "patientId":          3,
       "doctorId":           1,
       "appointmentDateTime":"2025-06-20T10:00:00",
       "description":        "Konsultacja kontrolna"
     }
     ```
   * Kliknij **Execute** → otrzymasz `201 Created` i obiekt `Appointment`.  
   * W zakładce **Responses** możesz zobaczyć cały JSON wizyty, w tym wygenerowane `id`, datę, pacjenta i lekarza.

5. **Pozostałe endpointy pacjenta**  
   * `GET  /api/appointments/patient/{patientId}` – lista wizyt pacjenta  
   * `PUT  /api/appointments/{id}`              – aktualizacja wizyty  
   * `DELETE /api/appointments/{id}`             – usunięcie wizyty  

#### D. Rejestracja lekarza, logowanie i przeglądanie wizyt

1. **Rejestracja nowego lekarza (ROLE_DOCTOR)**  
   * AuthController → `POST /api/auth/register/doctor?firstName={firstName}&lastName={lastName}&specialization={spec}&phoneNumber={phone}`  
   * Kliknij **Try it out**, ustaw query parameters:  
     - `firstName` = `Anna`  
     - `lastName` = `Nowak`  
     - `specialization` = `Pediatrics`  
     - `phoneNumber` = `+48987654321`  
   * W sekcji *Request body* wklej:
     ```json
     {
       "username": "doctor3",
       "email":    "doctor3@example.com",
       "password": "doctorpass",
       "notificationChannel": "SMS"
     }
     ```
   * Kliknij **Execute** → otrzymasz `201 Created` i obiekt `Doctor` z polem `id`.

2. **Logowanie lekarza**  
   * AuthController → `POST /api/auth/login`  
   * Kliknij **Try it out** i wklej:
     ```json
     {
       "username": "doctor3",
       "password": "doctorpass"
     }
     ```
   * Kliknij **Execute** → w odpowiedzi:
     ```json
     {
       "token": "<JWT_TOKEN_DOCTOR>"
     }
     ```

3. **Ustawienie tokena lekarza w Swagger UI**  
   * Kliknij **Authorize** (ikonka kłódki) w prawym górnym rogu.  
   * W polu **Value** wpisz:
     ```
     Bearer <JWT_TOKEN_DOCTOR>
     ```
   * Kliknij **Authorize** → teraz wszystkie żądania będą wysyłane jako zalogowany lekarz.

4. **Przeglądanie wizyt przypisanych do lekarza**  
   * AppointmentController → `GET /api/appointments/doctor/{doctorId}`  
   * Kliknij **Try it out**, w polu *Path* podmień `{doctorId}` na `3` (ID z rejestracji).  
   * Kliknij **Execute** → otrzymasz listę wizyt:
     ```json
     [
       {
         "id": 1,
         "appointmentDateTime": "2025-06-15T09:00:00",
         "patient": { "id": 1, "firstName": "John", "lastName": "Doe", … },
         "doctor":  { "id": 3, "firstName": "Anna", "lastName": "Nowak", … },
         "description": "Konsultacja ogólna"
       },
       …
     ]
     ```

5. **Dodatkowe operacje dostępne dla lekarza**  
   * `GET    /api/doctors`                    – lista wszystkich lekarzy  
   * `GET    /api/patients`                   – lista pacjentów  
   * `GET    /api/appointments`               – wszystkie wizyty  
   * `POST   /api/appointments`               – tworzenie nowej wizyty  
   * `PUT    /api/appointments/{id}`          – aktualizacja wizyty  
   * `DELETE /api/appointments/{id}`          – usunięcie wizyty  
