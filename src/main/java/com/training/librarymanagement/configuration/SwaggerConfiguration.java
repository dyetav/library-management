package com.training.librarymanagement.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.http.ResponseEntity;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableSwagger2
public class SwaggerConfiguration {

    @Bean
    public Docket api() {
        return new Docket( DocumentationType.SWAGGER_2 )
            .select()
            .apis(RequestHandlerSelectors.any())
            .paths(PathSelectors.any())
            .build()
            .securitySchemes(Arrays.asList(apiKey()))
            .apiInfo(metaData())
            .forCodeGeneration(true)
            .directModelSubstitute(java.nio.ByteBuffer.class, String.class)
            .genericModelSubstitutes(ResponseEntity.class)
            .select()
            .apis(RequestHandlerSelectors.basePackage( "com.training.librarymanagement"))
            .build();
    }

    private SecurityScheme apiKey() {
        return new ApiKey("library-jwt", "Authorization", "header");
    }

    private ApiInfo metaData() {
        return new ApiInfoBuilder()
            .title("Library Management API")
            .description("Library Management Application")
            .version("1.0.0")
            .contact(new Contact("Diego Tavolaro", "", "diegotavolaro@gmail.com"))
            .build();
    }
}