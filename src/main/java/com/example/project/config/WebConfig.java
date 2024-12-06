package com.example.project.config;


import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")  // อนุญาตทุกเส้นทาง
                .allowedOrigins("http://localhost:4200")  // อนุญาต origin ที่ต้องการ
                .allowedMethods("GET", "POST", "PUT", "DELETE")  // กำหนด methods ที่อนุญาต
                .allowedHeaders("*");  // อนุญาต headers ทั้งหมด
    }
    
}
