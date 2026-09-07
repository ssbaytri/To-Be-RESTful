package fr._42.educationcenter.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI educationCenterOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Education Center API")
                        .description("REST API to manage a training center: users, courses and lessons.")
                        .version("1.0.0"));

    }
}
