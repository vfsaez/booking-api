package com.victorsaez.bookingapi.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(apiInfo());
    }

    private Info apiInfo() {
        return new Info()
                .title("Booking API")
                .description("API to control users, properties and bookings.")
                .contact(new Contact()
                        .name("Victor Saez")
                        .email("victor.fontana.saez@gmail.com"))
                .license(new License()
                        .name("Apache License Version 2.0")
                        .url("https://example.io/EXAMPLE"))
                .version("1.0");
    }

}