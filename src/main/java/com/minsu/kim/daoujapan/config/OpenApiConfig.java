package com.minsu.kim.daoujapan.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author minsu.kim
 * @since 1.0
 */
@Configuration
public class OpenApiConfig {
  @Bean
  public OpenAPI openAPI() {
    return new OpenAPI()
        .addSecurityItem(new SecurityRequirement().addList("Authentication"))
        .components(new Components().addSecuritySchemes("Authentication", createAPIKeyScheme()))
        .info(
            new Info()
                .title("다우 재팬 경력 채용 과제 API")
                .description("다우 재팬 경력 채용 과제 API입니다.")
                .version("1.0")
                .contact(new Contact().name("Minsu kim").email("kds3335k@icloud.com")));
  }

  private SecurityScheme createAPIKeyScheme() {
    return new SecurityScheme()
        .type(SecurityScheme.Type.HTTP)
        .bearerFormat("Custom API Key")
        .scheme("bearer");
  }
}
