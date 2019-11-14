package com.example.ss12kopenapi.config;

import org.springdoc.ui.SwaggerWelcome;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.http.HttpServletRequest;

import static org.springdoc.core.Constants.DEFAULT_VALIDATOR_URL;
import static org.springdoc.core.Constants.SWAGGER_UI_PATH;
import static org.springdoc.core.Constants.SWAGGER_UI_URL;
import static org.springframework.util.AntPathMatcher.DEFAULT_PATH_SEPARATOR;
import static org.springframework.web.servlet.view.UrlBasedViewResolver.REDIRECT_URL_PREFIX;


@Configuration
public class SwaggerConfiguration {
    @Value(SWAGGER_UI_PATH)
    private String swaggerPath;

    @Bean
    @Primary
    public SwaggerWelcome swaggerWelcome() {
        return new SwaggerWelcome() {
            @Override
            public String redirectToUi(HttpServletRequest request) {
                String contextPath = request.getContextPath();
                String uiRootPath = "";
                if (swaggerPath.contains("/")) {
                    uiRootPath = swaggerPath.substring(0, swaggerPath.lastIndexOf('/'));
                }
                StringBuilder sbUrl = new StringBuilder();
                sbUrl.append(REDIRECT_URL_PREFIX);
                sbUrl.append(uiRootPath);
                sbUrl.append(SWAGGER_UI_URL);
                if (contextPath.endsWith(DEFAULT_PATH_SEPARATOR)) {
                    contextPath = contextPath.substring(0, contextPath.length() - 1);
                    sbUrl.append(contextPath).append("/openapi.yaml");
                } else {
                    sbUrl.append(contextPath).append("/openapi.yaml");
                }
                sbUrl.append(DEFAULT_VALIDATOR_URL);
                return sbUrl.toString();
            }
        };
    }

    @Bean
    public WebMvcConfigurer mvcConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addResourceHandlers(ResourceHandlerRegistry registry) {
                registry.addResourceHandler("/openapi.yaml")
                        .addResourceLocations("classpath:META-INF/resources/openapi.yaml");
                registry.addResourceHandler("/paths/**")
                        .addResourceLocations("classpath:META-INF/resources/paths/");
                registry.addResourceHandler("/schemas/**")
                        .addResourceLocations("classpath:META-INF/resources/schemas/");
            }

            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedMethods(HttpMethod.GET.name());
            }
        };
    }
}
