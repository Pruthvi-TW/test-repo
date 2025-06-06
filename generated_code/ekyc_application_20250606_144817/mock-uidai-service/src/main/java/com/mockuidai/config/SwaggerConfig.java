package com.mockuidai.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes("bearer-key", 
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")))
                .info(new Info()
                        .title("Mock UIDAI API")
                        .description("Mock UIDAI API for Aadhaar-based OTP and eKYC flows")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Mock UIDAI Team")
                                .email("mockuidai@example.com"))
                        .license(new License()
                                .name("For Development and Testing Only")
                                .url("https://example.com/license")));
    }
}