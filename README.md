## Spis treści

1. [Temat i Cel Projektu](#temat-i-cel-projektu)
2. [Wymagania Formalne](#wymagania-formalne)
3. [Wymagania Funkcjonalne](#wymagania-funkcjonalne)
4. [Technologie i Narzędzia](#technologie-i-narzędzia)
5. [Architektura Systemu](#architektura-systemu)
6. [Instalacja i Uruchomienie](#instalacja-i-uruchomienie)
7. [Konfiguracja i Migracje Bazy](#konfiguracja-i-migracje-bazy)
8. [Przykładowe Dane (Seed)](#przyk%C5%82adowe-dane-seed)
9. [Endpointy API i Autoryzacja](#endpointy-api-i-autoryzacja)
10. [Testy i Pokrycie Kodem](#testy-i-pokrycie-kodem)
11. [Commity i Repozytorium Git](#commity-i-repozytorium-git)
12. [Przykłady Użycia (Demo)](#przyk%C5%82ady-u%C5%BCycia-demo)
13. [Diagram ERD](#diagram-erd)
14. [Zastosowane Wzorce Projektowe i SOLID](#zastosowane-wzorce-projektowe-i-solid)
15. [Screenshots](#screenshots)

---

## Temat i Cel Projektu

* **Temat:** Medical Reservation System (zatwierdzony przez prowadzącego)
* **Cel:** Umożliwienie rejestracji, logowania oraz zarządzania wizytami medycznymi z podziałem na role użytkownika i administratora.

## Wymagania Formalne

1. **Obiektowość i zasady SOLID** – wszystkie warstwy (model, serwisy, kontrolery) oparte na interfejsach, wstrzykiwanie zależności, separation of concerns.
2. **Role-Based Access Control** – dwie role: `ROLE_USER` i `ROLE_ADMIN`, zarządzane przez Spring Security.
3. **Polimorfizm i wzorzec projektowy** – przykładowo wzorzec **Strategy** w implementacji powiadomień (Email/SMS).
4. **Repozytorium Git** – projekt znajduje się na GitHubie, commity opisane wg konwencji `type(scope): description`.
5. **Docker** – aplikacja i baza PostgreSQL uruchamiane przez `docker-compose.yml`.
6. **Maven** – standardowa struktura projektu z `pom.xml` zawierającym zależności i konfigurację build.
7. **Spring Framework + Spring Security** – Spring Boot dla logiki biznesowej i REST API, Spring Security do autoryzacji.
8. **Springdoc OpenAPI (Swagger UI)** – dostępny pod `/swagger-ui.html`.
9. **Hibernate + PostgreSQL + Flyway** – encje JPA, konfiguracja bazy PostgreSQL, migracje Flyway w `src/main/resources/db/migration`.
10. **JUnit + JaCoCo ≥80% pokrycia** – testy jednostkowe we wszystkich warstwach, pokrycie kodem raportowane.
11. **Dokumentacja** – plik `README.md` zawiera instrukcje, diagram ERD i zrzuty ekranu.

## Wymagania Funkcjonalne

* Rejestracja i logowanie użytkowników
* Zarządzanie rolami (użytkownik, administrator)
* Przeglądanie dostępnych terminów wizyt
* Rezerwacja wizyty przez użytkownika
* Zarządzanie wizytami przez administratora
* Powiadomienia e-mail i SMS

## Technologie i Narzędzia

* Java 17
* Spring Boot 3
* Spring Security, Spring Data JPA
* PostgreSQL
* Flyway
* Springdoc OpenAPI
* Docker, Docker Compose
* Maven
* JUnit, JaCoCo

## Architektura Systemu

Opis warstw:

* **Controller** – warstwa prezentacji (REST API)
* **Service** – logika biznesowa
* **Repository** – dostęp do bazy danych

## Instalacja i Uruchomienie

1. Sklonuj repozytorium:

   ```bash
   git clone <repo_url>
   cd med-reservation-system
   ```
2. Uruchom Docker Compose (uruchamia PostgreSQL i aplikację, jeśli skonfigurowane):

   ```bash
   docker-compose up -d
   ```
3. Zbuduj i uruchom aplikację lokalnie (jeśli nie w Dockerze):

   ```bash
   mvn clean package
   mvn spring-boot:run
   ```
4. Sprawdź logi Flyway i Hibernate, aby upewnić się, że migracje i walidacja schematu przebiegły poprawnie.

## Konfiguracja i Migracje Bazy

* Ustawienia w `application.properties` lub `application.yml`:

  ```properties
  spring.datasource.url=jdbc:postgresql://localhost:5432/meddb
  spring.datasource.username=postgres
  spring.datasource.password=...
  spring.jpa.hibernate.ddl-auto=validate
  spring.flyway.enabled=true
  spring.flyway.locations=classpath:db/migration
  jwt.secret=<Base64 32B secret>
  jwt.expiration-ms=86400000
  springdoc.api-docs.path=/v3/api-docs
  springdoc.swagger-ui.path=/swagger-ui.html
  ```
* Flyway migracje w `src/main/resources/db/migration`, np. `V1__init.sql` tworzy tabele z kolumną `notification_channel`.
* Aby odtworzyć schemat od nowa w dewelopmencie, usuń wolumen bazy i uruchom ponownie Docker Compose.

## Przykładowe Dane (Seed)

Poniżej przykład sadzenia bazy danymi testowymi. Możesz dodać te inserty do migracji Flyway lub uruchomić bezpośrednio w SQL klientcie.

```sql
-- Sample users: basic user i admin
INSERT INTO "user" (id, username, email, password, notification_channel)
VALUES
  (1, 'user1', 'user1@example.com', '$2a$10$ABCDEFG...', 'EMAIL'),
  (2, 'admin', 'admin@example.com', '$2a$10$HIJKLMN...', 'EMAIL');

INSERT INTO user_roles (user_id, role)
VALUES
  (1, 'ROLE_USER'),
  (2, 'ROLE_USER'),
  (2, 'ROLE_ADMIN');

-- Sample doctors
INSERT INTO doctor (id, first_name, last_name, specialization, phone_number, user_id)
VALUES
  (1, 'John', 'Doe', 'Cardiology', '123456789', 2),
  (2, 'Alice', 'Smith', 'Dermatology', '987654321', 2);

-- Sample patients
INSERT INTO patient (id, first_name, last_name, phone_number, user_id)
VALUES
  (1, 'Anna', 'Kowalska', '555111222', 1),
  (2, 'Piotr', 'Nowak', '555333444', 1);
```

## Endpointy API i Autoryzacja

* **Publiczne:**

  * `POST /api/auth/register` – rejestracja zwykłego użytkownika (ROLE\_USER).
  * `POST /api/auth/register-admin` – rejestracja administratora (ROLE\_ADMIN).
  * `POST /api/auth/register/patient` – rejestracja pacjenta z danymi.
  * `POST /api/auth/register/doctor` – rejestracja lekarza z danymi.
  * `POST /api/auth/login` – logowanie i otrzymanie JWT.
  * Swagger UI: `/swagger-ui.html`, OpenAPI: `/v3/api-docs`.
* **Chronione (wymagają Authorization: Bearer <token>):**

  * `GET /api/doctors` – dostęp dla zalogowanych (USER, ADMIN, DOCTOR, PATIENT).
  * `POST /api/doctors` – tylko ADMIN.
  * `PUT /api/doctors/{id}` – tylko ADMIN.
  * `DELETE /api/doctors/{id}` – tylko ADMIN.
  * `GET /api/patients` – dostęp dla zalogowanych.
  * `POST /api/patients` – tylko ADMIN.
  * `PUT /api/patients/{id}` – tylko ADMIN.
  * `DELETE /api/patients/{id}` – tylko ADMIN.
  * `GET /api/appointments/**` – każdy zalogowany.
  * `POST /api/appointments` – każdy zalogowany.
  * `PUT /api/appointments/{id}` – każdy zalogowany.
  * `DELETE /api/appointments/{id}` – każdy zalogowany.
  * Dodatkowe: `GET /api/appointments/patient/{patientId}`, `GET /api/appointments/doctor/{doctorId}` – każdy zalogowany.

## Testy i Pokrycie Kodem

* Uruchamianie testów: `mvn test`.
* Raport JaCoCo w `target/site/jacoco/index.html`. Celem jest pokrycie ≥80%.
* Testy integracyjne z MockMvc sprawdzające rejestrację, login, dostęp z tokenem, CRUD endpointów.

## Commity i Repozytorium Git

* Regularne, opisowe commity wg konwencji, np. `feat(auth): add JWT authentication`, `fix(security): correct role saving`.
* Utwórz pull requesty, code review.

## Przykłady Użycia (Demo)

Poniżej krok po kroku opis testowania endpointów w Swagger UI oraz przy pomocy curl:

### 1. Swagger UI

1. Otwórz w przeglądarce: `http://localhost:8080/swagger-ui.html`.

2. Sekcja Auth:

   * **POST /api/auth/register** – rejestracja zwykłego użytkownika.
   * **POST /api/auth/register-admin** – rejestracja administratora.
   * **POST /api/auth/register/patient?firstName=...\&lastName=...\&phoneNumber=...** – rejestracja pacjenta.
   * **POST /api/auth/register/doctor?firstName=...\&lastName=...\&specialization=...\&phoneNumber=...** – rejestracja lekarza.
   * **POST /api/auth/login** – logowanie.

3. Rejestracja użytkowników:

   * **Zwykły użytkownik:**

     1. Kliknij `POST /api/auth/register`, Try it out.
     2. Wklej body:

        ```json
        {
          "username": "user1",
          "email": "user1@example.com",
          "password": "Haslo123",
          "notificationChannel": "EMAIL"
        }
        ```
     3. Execute → otrzymasz Status 201 i JSON z rolą `ROLE_USER`.

   * **Administrator:**

     1. Kliknij `POST /api/auth/register-admin`, Try it out.
     2. Body:

        ```json
        {
          "username": "admin",
          "email": "admin@example.com",
          "password": "AdminPass123",
          "notificationChannel": "EMAIL"
        }
        ```
     3. Execute → Status 201 i JSON z rolami `ROLE_USER` i `ROLE_ADMIN`.

### 2. Login i autoryzacja w Swagger UI

1. **Zwykły użytkownik:**

   * Kliknij `POST /api/auth/login`, Try it out.
   * Body:

     ```json
     { "username": "user1", "password": "Haslo123" }
     ```
   * Execute → Status 200 i zwrócony token:

     ```json
     { "token": "<USER_JWT_TOKEN>" }
     ```

2. **Administrator:**

   * Kliknij `POST /api/auth/login`, Try it out.
   * Body:

     ```json
     { "username": "admin", "password": "AdminPass123" }
     ```
   * Execute → Status 200 i zwrócony token:

     ```json
     { "token": "<ADMIN_JWT_TOKEN>" }
     ```

3. Kliknij **Authorize** (ikonka kłódki) w Swagger UI.

4. Wklej najpierw `Bearer <USER_JWT_TOKEN>`, zatwierdź, przetestuj endpointy dla USER.

5. Powtórz, wklejając `Bearer <ADMIN_JWT_TOKEN>`, aby przetestować endpointy ADMIN.

### 3. Testowanie endpointów jako zwykły użytkownik (ROLE\_USER)

> **Uwaga:** ROLE\_USER **nie ma dostępu** do endpointów ADMIN-only.

1. **GET /api/doctors** – 200 OK.
2. **POST /api/doctors** – 403 Forbidden.
3. **GET /api/patients** – 200 OK (konfiguracja `hasAnyRole("USER",...)`).
4. **POST /api/patients** – 403 Forbidden.
5. **POST /api/appointments** – 201 Created.
6. **GET /api/appointments/patient/1** – 200 OK.
7. **PUT /api/appointments/{id}**, **DELETE /api/appointments/{id}** – dostępne, ale warto dodatkowo ograniczyć do własnych wizyt.

### 4. Testowanie endpointów jako administrator (ROLE\_ADMIN)

1. **GET /api/doctors** – 200 OK.
2. **POST /api/doctors** – 201 Created.
3. **PUT /api/doctors/{id}** – 200 OK.
4. **DELETE /api/doctors/{id}** – 204 No Content.
5. **GET/POST/PUT/DELETE /api/patients** – admin ma pełne prawa.
6. **GET /api/appointments** – 200 OK, lista wszystkich wizyt.
7. **DELETE /api/appointments/{id}** – 204 No Content.

### 5. Przykłady curl

#### Rejestracja user

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"user1","email":"user1@example.com","password":"Haslo123","notificationChannel":"EMAIL"}'
```

#### Rejestracja admin

```bash
curl -X POST http://localhost:8080/api/auth/register-admin \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","email":"admin@example.com","password":"AdminPass123","notificationChannel":"EMAIL"}'
```

#### Login user

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"user1","password":"Haslo123"}'
```

#### Login admin

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"AdminPass123"}'
```

#### Tworzenie lekarza jako ADMIN

```bash
curl -X POST http://localhost:8080/api/doctors \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"username":"drnew","email":"newdoc@example.com","password":"DocPass","firstName":"New","lastName":"Doctor","specialization":"Pediatrics","phoneNumber":"111222333","notificationChannel":"EMAIL"}'
```

#### Próba tworzenia lekarza jako USER

```bash
USER_TOKEN="<USER_JWT_TOKEN>"
curl -X POST http://localhost:8080/api/doctors \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $USER_TOKEN" \
  -d '{"username":"drfail","email":"fail@example.com","password":"pass","firstName":"Fail","lastName":"Test","specialization":"Test","phoneNumber":"000","notificationChannel":"EMAIL"}'
```

## Diagram ERD

(Tutaj wstaw diagram ERD, np. w formie obrazka lub linku do narzędzia.)

## Zastosowane Wzorce Projektowe i SOLID

* **Strategy** – powiadomienia Email/SMS.
* **Dependency Injection** – serwisy i repozytoria.
* **Separation of Concerns** – warstwy Controller/Service/Repository.
* **SOLID**:

  * Single Responsibility: każda klasa ma jedną odpowiedzialność.
  * Open/Closed: serwisy otwarte na rozszerzenia przez interfejsy.
  * Liskov Substitution: encje dziedziczące User (Doctor, Patient) zastępują prawidłowo.
  * Interface Segregation: dedykowane interfejsy repozytoriów i serwisów.
  * Dependency Inversion: zależność od abstrakcji przez Spring.

## Screenshots

(Tutaj wstaw zrzuty ekranu z Swagger UI, logów, wyników migracji.)
