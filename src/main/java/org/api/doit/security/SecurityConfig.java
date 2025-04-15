package org.api.doit.security;

import org.api.doit.exception.CustomAuthenticationEntryPoint;
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

/**
 * Security configuration class for the application.
 * Sets up HTTP security, authentication mechanisms, and JWT filtering.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtFilter jwtFilter;
    private final CustomUserDetailsService userDetailsService;
    private final CustomAuthenticationEntryPoint authenticationEntryPoint;

    /**
     * Constructor to inject dependencies.
     *
     * @param jwtFilter           Custom JWT filter.
     * @param userDetailsService  Service for retrieving user details.
     */
    public SecurityConfig(final JwtFilter jwtFilter,
                          final CustomUserDetailsService userDetailsService,
                          final CustomAuthenticationEntryPoint authenticationEntryPoint) {
        this.jwtFilter = jwtFilter;
        this.userDetailsService = userDetailsService;
        this.authenticationEntryPoint = authenticationEntryPoint;
    }

    /**
     * Configures the HTTP security filter chain.
     *
     * @param http the HttpSecurity object provided by Spring Security.
     * @return a configured SecurityFilterChain.
     * @throws Exception in case of configuration errors.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests((request) -> request
                        .requestMatchers("/api/v1/doit/auth/**").permitAll()
                        .anyRequest().authenticated()
                )
                .exceptionHandling((ex) -> ex
                        .authenticationEntryPoint(authenticationEntryPoint))
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }

    /**
     * Defines the authentication provider using a custom user details service and password encoder.
     *
     * @return the configured AuthenticationProvider.
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    /**
     * Retrieves the AuthenticationManager from the given authentication configuration.
     *
     * @param authConfiguration the Spring AuthenticationConfiguration.
     * @return the AuthenticationManager bean.
     * @throws Exception in case of errors.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfiguration) throws Exception {
        return authConfiguration.getAuthenticationManager();
    }

    /**
     * Provides a PasswordEncoder bean using Spring’s DelegatingPasswordEncoder.
     *
     * @return a PasswordEncoder.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
