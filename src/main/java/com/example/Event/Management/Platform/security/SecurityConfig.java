package com.example.Event.Management.Platform.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthFilter jwtAuthFilter;
    private final UserDetailsService userDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth

                        .requestMatchers(HttpMethod.POST, "/api/users/register").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/events/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/users/login").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/tickets/eventIds/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/tickets/types").permitAll()
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**"
                        ).permitAll()

                        .requestMatchers(HttpMethod.GET, "/api/users/me").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/users/me").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/users/me").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/bookings").hasRole("USER")
                        .requestMatchers(HttpMethod.GET, "/api/bookings/{bookingId}").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/bookings/users/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/bookings/*/confirm").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/bookings/*/cancel").hasAnyRole("USER", "ADMIN")

                        .requestMatchers(HttpMethod.POST, "/api/events").hasAnyRole("ORGANIZER", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/events/**").hasAnyRole("ORGANIZER", "ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/events/**").hasAnyRole("ORGANIZER", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/events/**").hasAnyRole("ORGANIZER", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/tickets").hasAnyRole("ORGANIZER", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/tickets/**").hasAnyRole("ORGANIZER", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/tickets/**").hasAnyRole("ORGANIZER", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/bookings/events/**").hasAnyRole("ORGANIZER", "ADMIN")

                        .requestMatchers(HttpMethod.GET, "/api/users").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/users/{id}").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/users/{id}").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/users/{id}/role").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/bookings").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/bookings/status").hasRole("ADMIN")

                        .anyRequest().permitAll()
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
