package com.campusfood;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Title and description shown at the top of Swagger UI (/swagger-ui.html). */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI campusFoodOpenApi() {
        return new OpenAPI().info(new Info()
                .title("CampusFood API")
                .description("University cafeteria food ordering: browse restaurants and menus, "
                        + "place orders and move them through the kitchen.")
                .version("0.1.0"));
    }
}
