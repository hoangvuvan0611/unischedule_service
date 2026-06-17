package com.example.unischeduleservice.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI unischeduleOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("UniSchedule Service API")
                        .description("API truy vấn account, user access log và các chức năng liên quan.")
                        .version("v1.0.0")
                        .contact(new Contact().name("UniSchedule Service")))
                .externalDocs(new ExternalDocumentation()
                        .description("Swagger UI")
                        .url("/swagger-ui/index.html"));
    }
}
