package now.eyak.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI openAPI() {
        Info info = new Info()
                .title("데모 프로젝트 API Document")
                .version("v0.0.1")
                .description("데모 프로젝트의 API 명세서입니다.");

        //TODO: @SecurityRequirement(name = "Bearer Authentication") 메서드에 적용
        SecurityRequirement securityRequirement = new SecurityRequirement();
        securityRequirement.addList("Authorization");

        SecurityScheme securityScheme = new SecurityScheme();
        securityScheme.name("Bearer Authorization");
        securityScheme.type(SecurityScheme.Type.HTTP);
        securityScheme.bearerFormat("JWT");
        securityScheme.setDescription("Access Token");
        securityScheme.scheme("bearer");

        return new OpenAPI()
                .components(new Components().addSecuritySchemes("Access Token", securityScheme))
                .info(info)
                .addSecurityItem(securityRequirement);
    }
}
