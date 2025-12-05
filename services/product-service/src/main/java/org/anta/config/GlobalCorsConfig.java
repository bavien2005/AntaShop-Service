//package org.anta.config;
//
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.Ordered;
//import org.springframework.core.annotation.Order;
//import org.springframework.boot.web.servlet.FilterRegistrationBean;
//import org.springframework.web.filter.CorsFilter;
//import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
//import org.springframework.web.cors.CorsConfiguration;
//
//import java.util.Arrays;
//
//@Configuration
//public class GlobalCorsConfig {
//
//    @Bean
//    @Order(Ordered.HIGHEST_PRECEDENCE)
//    public FilterRegistrationBean<CorsFilter> corsFilterRegistration() {
//        CorsConfiguration config = new CorsConfiguration();
//        config.setAllowCredentials(true);
//        config.setAllowedOrigins(Arrays.asList("http://localhost:5173", "http://127.0.0.1:5173"));
//        config.addAllowedHeader(CorsConfiguration.ALL);
//        config.addAllowedMethod(CorsConfiguration.ALL);
//        config.setExposedHeaders(Arrays.asList("Authorization","Content-Type","Location"));
//
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", config);
//
//        FilterRegistrationBean<CorsFilter> bean = new FilterRegistrationBean<>(new CorsFilter(source));
//        bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
//        return bean;
//    }
//}
