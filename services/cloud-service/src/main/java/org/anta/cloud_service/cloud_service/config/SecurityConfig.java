package org.anta.cloud_service.cloud_service.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                // public endpoint cho upload và các API cloud
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/cloud/**", "/actuator/**").permitAll()
                        .anyRequest().authenticated()
                )
                // stateless (không dùng session)
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // tắt httpBasic và form login (không muốn Basic auth prompt)
                .httpBasic(b -> b.disable())
                .formLogin(f -> f.disable());

        return http.build();
    }

    // nếu project có nơi cần PasswordEncoder, define 1 bean an toàn
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
