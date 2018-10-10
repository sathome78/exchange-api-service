package me.exrates.openapi.configurations;

import com.google.common.base.Predicates;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableWebMvc
@EnableSwagger2
public class SwaggerConfiguration {

    @Value("${swagger.enabled:true}")
    private boolean enabled;

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2) // Docket, Springfox’s, primary api configuration mechanism is initialized for swagger specification 2.0
                .enable(enabled)
                .apiInfo(apiInfo())
                .groupName("jsonDoc") // по ссылке получаем сгенеренный json API:
                .select() // method returns an instance of ApiSelectorBuilder, which provides a way to control the endpoints exposed by Swagger.
                .apis(RequestHandlerSelectors.any())//  allows selection of RequestHandler’s using a predicate. The example here uses an `any predicate (default). Out of the box predicates provided are any, none, withClassAnnotation, withMethodAnnotation and basePackage.
                .paths(Predicates.not(PathSelectors.regex("/error.*")))// Avoiding default basic-error-controller from swagger api
                .paths(PathSelectors.any())// allows selection of Path’s using a predicate. The example here uses an `any predicate (default)
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Open api service")
                .description("Describe REST Web services 'Open api'.")
                .version("1.0")
                .termsOfServiceUrl("http://terms-of-services.url")
                .license("Licence Type if need")
                .licenseUrl("http://url-to-license.com")
                .build();
    }
}
