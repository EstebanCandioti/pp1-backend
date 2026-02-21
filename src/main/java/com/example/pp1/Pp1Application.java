package com.example.pp1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
@EnableScheduling
public class Pp1Application {

	public static void main(String[] args) {
		SpringApplication.run(Pp1Application.class, args);
	}

	// CORS GLOBAL Y PERMISIVO (para desarrollo)
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**") // todos los endpoints
                        .allowedOrigins("*") // permite cualquier origen
                        .allowedMethods("*") // GET, POST, PUT, DELETE, etc.
                        .allowedHeaders("*") // todos los headers
                        .allowCredentials(false); // debe ser false si allowedOrigins = "*"
            }
        };
	}
}
