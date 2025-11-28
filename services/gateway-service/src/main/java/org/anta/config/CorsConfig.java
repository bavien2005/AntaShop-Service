//package org.anta.config;
//
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.cors.CorsConfiguration;
//import org.springframework.web.cors.reactive.CorsWebFilter;
//import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
//
//import java.util.List;
//
//@Configuration
//public class CorsConfig {
//
//    @Bean
//    public CorsWebFilter corsWebFilter() {
//
//        CorsConfiguration config = new CorsConfiguration();
//
//        // Cho phép gửi cookie, Authorization,...
//        config.setAllowCredentials(true);
//
//        // Các domain FE được phép gọi
//        config.setAllowedOriginPatterns(List.of(
//                "http://localhost:5173",
//                "http://127.0.0.1:5173",
//                "http://localhost:3000",
//                "http://127.0.0.1:3000"
//        ));
//
//        // Các HTTP method được phép
//        config.setAllowedMethods(List.of(
//                "GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"
//        ));
//
//        // Cho phép tất cả headers
//        config.setAllowedHeaders(List.of("*"));
//
//        // FE có thể đọc những header này trong response
//        config.setExposedHeaders(List.of(
//                "Authorization",
//                "X-User-Name",
//                "X-User-Role"
//        ));
//
//        // Cache preflight request 1 giờ
//        config.setMaxAge(3600L);
//
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", config);
//
//        return new CorsWebFilter(source);
//    }
//}
//
