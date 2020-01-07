package com.js.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class StaticConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static-m/**").addResourceLocations("classpath:/page-mobile/static-m/");
        registry.addResourceHandler("/page/**").addResourceLocations("classpath:/page-mobile/");
    }
}
