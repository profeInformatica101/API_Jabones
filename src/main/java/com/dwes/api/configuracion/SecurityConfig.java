package com.dwes.api.configuracion;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CorsConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig{
	@Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationConfiguration authenticationConfiguration) throws Exception {
        http
        .authorizeHttpRequests((authz) -> authz
               // .requestMatchers(HttpMethod.GET, "/api/v1/jabones/**").permitAll() 
                .requestMatchers("/api/v1/jabones/**").permitAll() // Permite todas las solicitudes a /api/v1/jabones/**
                .requestMatchers("/**").permitAll() // Permite todas las solicitudes a Swagger UI
                .anyRequest().authenticated()); // Todas las demás solicitudes necesitan autenticación
        

        // Si utilizas CSRF y deseas mantenerlo, podrías necesitar una configuración específica aquí
        // Para APIs REST, comúnmente CSRF se deshabilita así:
        http.csrf((csrf) -> csrf.disable());
        return http.build();
    }

	}