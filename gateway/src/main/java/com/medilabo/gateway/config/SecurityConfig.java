package com.medilabo.gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Security Configuration for the gateway.
 * Handle the authentication with two ways
 * - A form login for the human user (doctor) to access the gateway;
 * - A HTTP Basic for internal communication, using a system user (gatewayUser), so the ressources can only be access by the gateway.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Value("${gateway.services.front.url}")
    private String frontServiceUrl;

    @Value("${doctor.username}")
    private String doctorUsername;

    @Value("${doctor.password}")
    private String doctorPassword;

    @Value("${gateway.username}")
    private String gatewayUsername;

    @Value("${gateway.password}")
    private String gatewayPassword;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login", "/css/**").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .defaultSuccessUrl(frontServiceUrl + "/patients/search", true)
                        .permitAll()
                )
                .httpBasic(httpBasic -> {})
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login")
                        .permitAll()
                );

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
        UserDetails doctor = User.builder()
                .username(doctorUsername)
                .password(passwordEncoder.encode(doctorPassword))
                .roles("USER")
                .build();

        UserDetails gatewayUser = User.builder()
                .username(gatewayUsername)
                .password(passwordEncoder.encode(gatewayPassword))
                .roles("GATEWAY")
                .build();

        return new InMemoryUserDetailsManager(doctor, gatewayUser);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
