package com.striveconnect.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtRequestFilter jwtRequestFilter;
    
    private static final String[] PUBLIC_URLS = {
            "/api/auth/**",
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/api/content/**",
            "/api/courses",
            "/api/centers",
            "/api/batches",
            "/api/courses/**",
            "/api/content/**"
    };

    public SecurityConfig(JwtRequestFilter jwtRequestFilter) {
        this.jwtRequestFilter = jwtRequestFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(Customizer.withDefaults()) 
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers(PUBLIC_URLS).permitAll()
                    .requestMatchers("/actuator/**").permitAll()

                    // --- Centralized Authorization Rules ---
                    
                    // Alumnus-specific endpoints
                    .requestMatchers(HttpMethod.GET, "/api/users/me").hasRole("ALUMNUS")
                    .requestMatchers(HttpMethod.PUT, "/api/users/me").hasRole("ALUMNUS")
                    .requestMatchers(HttpMethod.POST, "/api/jobs/{jobId}/apply").hasRole("ALUMNUS")
                    .requestMatchers(HttpMethod.POST, "/api/jobs/submit").hasRole("ALUMNUS")
                    .requestMatchers(HttpMethod.GET, "/api/jobs/my-applications").hasRole("ALUMNUS")
                    .requestMatchers(HttpMethod.POST, "/api/upskilling/{opportunityId}/apply").hasRole("ALUMNUS")
                    .requestMatchers(HttpMethod.GET, "/api/upskilling/my-applications").hasRole("ALUMNUS") // NEW RULE
                    .requestMatchers("/api/connections/**").hasRole("ALUMNUS")
                    .requestMatchers(HttpMethod.GET, "/api/employment-history/me").hasRole("ALUMNUS")
                    .requestMatchers(HttpMethod.POST, "/api/employment-history").hasRole("ALUMNUS")
                    .requestMatchers(HttpMethod.PUT, "/api/employment-history/{historyId}").hasRole("ALUMNUS")


                    // Admin-specific endpoints
                    .requestMatchers("/api/users/pending").hasAnyRole("CENTER_ADMIN", "SUPER_ADMIN")
                    .requestMatchers(HttpMethod.POST, "/api/users/*/approve").hasAnyRole("CENTER_ADMIN", "SUPER_ADMIN")
                    .requestMatchers(HttpMethod.GET, "/api/users/count").hasAnyRole("CENTER_ADMIN", "SUPER_ADMIN")
                    .requestMatchers(HttpMethod.GET, "/api/employment-history/pending").hasAnyRole("CENTER_ADMIN", "SUPER_ADMIN")
                    .requestMatchers(HttpMethod.GET, "/api/employment-history/user/*").hasAnyRole("CENTER_ADMIN", "SUPER_ADMIN")
                    .requestMatchers(HttpMethod.POST, "/api/employment-history/*/verify").hasAnyRole("CENTER_ADMIN", "SUPER_ADMIN")
                    .requestMatchers(HttpMethod.POST, "/api/jobs").hasAnyRole("CENTER_ADMIN", "SUPER_ADMIN")
                    .requestMatchers(HttpMethod.GET, "/api/jobs/pending").hasAnyRole("CENTER_ADMIN", "SUPER_ADMIN")
                    .requestMatchers(HttpMethod.POST, "/api/jobs/{jobId}/approve").hasAnyRole("CENTER_ADMIN", "SUPER_ADMIN")
                    .requestMatchers(HttpMethod.POST, "/api/content").hasAnyRole("CENTER_ADMIN", "SUPER_ADMIN") // NEW RULE

                    .anyRequest().authenticated()
            )
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
    
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

