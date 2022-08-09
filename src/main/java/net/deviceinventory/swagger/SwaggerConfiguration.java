package net.deviceinventory.swagger;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.OAuth2SchemeBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.SecurityConfiguration;
import springfox.documentation.swagger.web.SecurityConfigurationBuilder;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static java.util.List.of;

@Configuration
public class SwaggerConfiguration {

    private ApiInfo apiInfo() {
        return new ApiInfo("Device-Inventory Rest APIs",
                "APIs for Device-Inventory.",
                "0.0.1",
                "Terms of service",
                new Contact("test", "www.org.com", "test@emaildomain.com"),
                "License of API",
                "API license URL",
                Collections.emptyList()
        );
    }

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.OAS_30)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                .build()
                .securitySchemes(Collections.singletonList(authenticationScheme()))
                .securityContexts(Collections.singletonList(securityContext()));
    }

    @Bean
    public SecurityConfiguration security() {
        return SecurityConfigurationBuilder.builder()
                .clientId("542941100377-rktqhqpujilfkpo3l4jb6js8khm254d2.apps.googleusercontent.com")
                .clientSecret("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")
                .scopeSeparator(" ")
                .useBasicAuthenticationWithAccessCodeGrant(true)
                .build();
    }

    private List<AuthorizationScope> authorizationScopes() {
        return Arrays.asList(
                new AuthorizationScope("read_access", "read data"),
                new AuthorizationScope("write_access", "modify data")
        );
    }

    private SecurityScheme authenticationScheme() {
        return new OAuth2SchemeBuilder("implicit")
                .name("my_oAuth_security_schema")
                .authorizationUrl("http://localhost:8080/oauth2/authorization/google")
                .scopes(authorizationScopes())
                .build();
    }

    private SecurityContext securityContext() {
        return SecurityContext.builder()
                .securityReferences(readAccessAuth())
                .operationSelector(operationContext ->
                        {
                            operationContext.httpMethod();
                            return false;
                        }
                )
                .build();
    }

    private List<SecurityReference> readAccessAuth() {
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[] { authorizationScopes().get(0) };
        return of(new SecurityReference("my_oAuth_security_schema", authorizationScopes));
    }


}