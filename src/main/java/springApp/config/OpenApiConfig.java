package springApp.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI customOpenApi(){
        return new OpenAPI().info(new Info()
                .title("User-service Api definition")
                .version("1.0")
                .description("Микросервис для управления пользователями")
                );
    }
}
