package org.anta.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.http.SessionCreationPolicy;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // tắt CSRF cho API JSON
                .csrf(csrf -> csrf.disable())

                // không dùng session (stateless) -> không set cookie JSESSIONID
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // CORS: nếu identity không thêm CORS, gateway sẽ xử lý. Nếu muốn cho identity xử lý,
                // bạn cần cấu hình CorsConfigurationSource bean riêng (không làm cả 2).
                .cors(Customizer.withDefaults())

                // cấu hình permit public
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/auth/**",
                                "/api/user/**",          // <-- SỬA LẠI: thêm dấu /
                                "/api/auth/verify/**",
                                "/api/public/**",
                                "/actuator/**",
                                "/api/address/**"
                        ).permitAll()
                        .anyRequest().authenticated()
                )

                // tắt http basic (không trả WWW-Authenticate)
                .httpBasic(basic -> basic.disable())

                // tắt formLogin vì đây là API
                .formLogin(form -> form.disable());

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
