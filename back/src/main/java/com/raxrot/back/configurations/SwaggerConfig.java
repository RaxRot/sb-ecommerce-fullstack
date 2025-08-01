package com.raxrot.back.configurations;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI apiInfo() {
        return new OpenAPI()
                .info(new Info()
                        .title("My Online Store API")
                        .description("Backend API Ecommerce")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Raxrot Dev Team")
                                .url("https://github.com/raxrot")
                                .email("dasistperfektos@gmail.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html"))
                );
    }
}