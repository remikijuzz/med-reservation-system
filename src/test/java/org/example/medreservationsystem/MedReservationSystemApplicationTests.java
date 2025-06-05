package org.example.medreservationsystem;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
    // wyłączamy Flyway w testach:
    "spring.flyway.enabled=false",
    // konfiguracja bazy H2:
    "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
    "spring.datasource.username=sa",
    "spring.datasource.password=",
    "spring.jpa.hibernate.ddl-auto=none",
    "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
    // wyłączamy Springfox/Swagger w testach, aby nie inicjował dokumentacji:
    "springfox.documentation.enabled=false"
})
class MedReservationSystemApplicationTests {

    @Test
    void contextLoads() {
        // pusty test, sprawdzający, czy kontekst Spring Boot uruchamia się bez błędów
    }

}


