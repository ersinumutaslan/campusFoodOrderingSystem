package com.campusfood;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Title, description and the "Authorize" (login) button shown in Swagger UI (/swagger-ui.html). */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI campusFoodOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("CampusFood API")
                        .description("University cafeteria food ordering: browse restaurants and menus, "
                                + "place orders and move them through the kitchen.")
                        .version("0.1.0"))
                .components(new Components().addSecuritySchemes("basicAuth",
                        new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("basic")))
                .addSecurityItem(new SecurityRequirement().addList("basicAuth"));
    }
}
