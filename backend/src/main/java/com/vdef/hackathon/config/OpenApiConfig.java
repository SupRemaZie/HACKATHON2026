package com.vdef.hackathon.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.Components;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("CarbonTrack API")
                        .description("API REST pour le calcul d'empreinte carbone de sites physiques — Hackathon #26 Capgemini × SUP Vinci")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Hackathon #26 Team")
                                .url("https://github.com/SupRemaZie/HACKATHON2026")))
                .components(new Components()
                        .addSecuritySchemes("bearer-jwt",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("JWT Bearer Token")))
                .addSecurityItem(new SecurityRequirement().addList("bearer-jwt"));
    }
}
