package com.swiggy.menu.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Swiggy Menu Service API")
                        .version("1.0")
                        .description("API for managing restaurant menus for Swiggy")
                        .termsOfService("https://www.swiggy.com/terms")
                        .license(new License().name("Swiggy License").url("https://www.swiggy.com/license")))
                .tags(List.of(
                        new Tag().name("Menu Management").description("APIs for managing restaurant menus")
                ));
    }
}
