package com.coppel.sla.config;

import com.coppel.sla.beans.AppProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final AppProperties props;

    public WebConfig(AppProperties props) {
        this.props = props;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {

        registry.addMapping("/**")
                .allowedOrigins(props.allowedOrigins().toArray(String[]::new))
                .allowedMethods(props.allowedMethods().toArray(String[]::new))
                .allowedHeaders(props.allowedHeaders().toArray(String[]::new))
                .exposedHeaders(props.exposedHeaders().toArray(String[]::new))
                .allowCredentials(true);
    }
}