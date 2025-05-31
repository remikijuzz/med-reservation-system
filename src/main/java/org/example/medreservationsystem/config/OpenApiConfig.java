package org.example.medreservationsystem.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
  info = @Info(
    title       = "Medical Reservation API",
    version     = "v1.0",
    description = "REST API for managing patients, doctors and appointments",
    contact     = @Contact(name = "Remigiusz", email = "remigiusz.gawenda@gmail.com")
  ),
  servers = {
    @Server(url = "/", description = "Default Server URL")
  }
)
public class OpenApiConfig {
}
