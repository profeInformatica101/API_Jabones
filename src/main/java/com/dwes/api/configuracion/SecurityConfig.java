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
			

			.addFilterAfter(new JWTAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class)
	        .authorizeHttpRequests((authz) -> authz
	               // .requestMatchers(HttpMethod.GET, "/api/v1/jabones/**").permitAll() 
	        		.requestMatchers("/api/v1/jabones/**").permitAll() // Permite todas las solicitudes a /api/v1/jabones/**
	        		
	        		.requestMatchers("/**").permitAll() // Permite todas las solicitudes a Swagger UI
	                .anyRequest().authenticated()); // Todas las demás solicitudes necesitan autenticación
	       /**
	        * Esto significa que tu aplicación no creará sesiones de usuario.
	        *  En lugar de eso, cada solicitud se considera independiente 
	        *  y no se mantiene información de sesión entre ellas. 
	        *  Esta configuración es comúnmente utilizada en aplicaciones 
	        *  RESTful o APIs donde la autenticación se realiza en cada 
	        *  solicitud y no se necesita mantener un estado de sesión.
	        */
			http.sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
	        // Para APIs REST, comúnmente CSRF se deshabilita así:
           
			http.csrf(csrf -> csrf
                    .ignoringRequestMatchers("/h2-console/**"));
			http.headers(headers -> headers.frameOptions().disable());
	        
	        return http.build();
	    }

	}
