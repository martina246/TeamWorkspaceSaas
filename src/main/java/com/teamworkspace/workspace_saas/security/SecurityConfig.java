package com.teamworkspace.workspace_saas.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/api/auth/**",
                    "/swagger-ui/**",
                    "/v3/api-docs/**"
                ).permitAll()

                .requestMatchers("/api/subscription-plans/**")
                .hasRole("SUPERADMIN")

                .requestMatchers("/api/organizations/**")
                .hasAnyRole("ADMIN", "SUPERADMIN")

                .requestMatchers("/api/projects/**")
                .hasAnyRole("ADMIN", "SUPERADMIN")

                .requestMatchers("/api/tasks/**")
                .hasAnyRole("USER", "ADMIN", "SUPERADMIN")

                .requestMatchers("/api/activity-logs/**")
                .hasAnyRole("ADMIN", "SUPERADMIN")

                .anyRequest().authenticated()
            )
            .formLogin(form -> form.disable())
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
