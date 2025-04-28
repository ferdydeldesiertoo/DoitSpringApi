package org.api.doit.security;

import org.api.doit.exception.handler.CustomAuthenticationEntryPoint;
import org.api.doit.jwt.JwtFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtFilter jwtFilter;
    private final CustomUserDetailsService userDetailsService;
    private final CustomAuthenticationEntryPoint authenticationEntryPoint;

    /**
     * Constructor to inject dependencies into the SecurityConfig class.
     * This constructor ensures the configuration has the necessary components like
     * JWT filter, user details service, and custom authentication entry point.
     *
     * @param jwtFilter                Custom JWT filter used for intercepting HTTP requests and validating JWTs.
     * @param userDetailsService       Service responsible for retrieving user details for authentication.
     * @param authenticationEntryPoint Custom entry point that handles unauthorized access attempts.
     */
    public SecurityConfig(final JwtFilter jwtFilter,
                          final CustomUserDetailsService userDetailsService,
                          final CustomAuthenticationEntryPoint authenticationEntryPoint) {
        this.jwtFilter = jwtFilter;
        this.userDetailsService = userDetailsService;
        this.authenticationEntryPoint = authenticationEntryPoint;
    }

    /**
     * Configures the HTTP security filter chain for the application.
     * This method sets up the security configurations, such as JWT filtering, session management, and authorization rules.
     *
     * @param http the HttpSecurity object provided by Spring Security to configure HTTP security.
     * @return a fully configured SecurityFilterChain.
     * @throws Exception in case of configuration errors, such as misconfigured filters or incorrect settings.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // CSRF protection is disabled since we're using stateless JWTs.
                .cors((cors) -> cors
                        .configurationSource((request -> {
                            CorsConfiguration config = new CorsConfiguration();
                            config.setAllowedOrigins(List.of("http://127.0.0.1:5500"));
                            config.setAllowedMethods(List.of("*"));
                            config.setAllowedHeaders(List.of("*"));

                            return config;
                        }))
                )
                .authorizeHttpRequests((request) -> request
                        .requestMatchers("/api/v1/auth/**").permitAll() // Allow public access to authentication-related endpoints.
                        .anyRequest().authenticated() // All other requests require authentication.
                )
                .exceptionHandling((ex) -> ex
                        .authenticationEntryPoint(authenticationEntryPoint)) // Custom handling of authentication exceptions.
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Stateless session management as JWTs are used.
                .authenticationProvider(authenticationProvider()) // Uses custom authentication provider.
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class); // Adds JWT filter before the default username/password authentication filter.

        return http.build();
    }

    /**
     * Configures the authentication provider to handle user authentication.
     * This provider uses a custom user details service and a password encoder to authenticate users.
     *
     * @return a configured AuthenticationProvider instance.
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService); // Use the custom user details service to load user information.
        provider.setPasswordEncoder(passwordEncoder()); // Set up the password encoder to validate user passwords.
        return provider;
    }

    /**
     * Provides the AuthenticationManager bean from the given AuthenticationConfiguration.
     * The AuthenticationManager is used to authenticate user credentials.
     *
     * @param authConfiguration the Spring AuthenticationConfiguration that supplies the AuthenticationManager.
     * @return the AuthenticationManager instance.
     * @throws Exception if there is an error retrieving the AuthenticationManager.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfiguration) throws Exception {
        return authConfiguration.getAuthenticationManager(); // Retrieve the AuthenticationManager from the configuration.
    }

    /**
     * Provides a PasswordEncoder bean using Spring's DelegatingPasswordEncoder.
     * The encoder is used for encoding and validating passwords during authentication.
     *
     * @return a PasswordEncoder for securing passwords.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder(); // A factory method to create a password encoder.
    }
}
