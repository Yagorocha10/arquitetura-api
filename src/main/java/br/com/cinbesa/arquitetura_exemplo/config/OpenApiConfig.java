package br.com.cinbesa.arquitetura_exemplo.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API de Gestão de Arquivos (Cinbesa)") // <-- O TÍTULO QUE VOCÊ QUER
                        .version("1.0.0")
                        .description("Esta API gerencia a estrutura de pastas e documentos do projeto exemplo."));
    }
}