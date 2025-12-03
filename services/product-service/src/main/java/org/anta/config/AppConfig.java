package org.anta.config;

import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.util.Timeout;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;


@Configuration
public class AppConfig {

    @Bean
    public RestTemplate restTemplate() {
        // Cấu hình timeout sử dụng Timeout của HttpClient5
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(Timeout.ofMilliseconds(3000))         // connect timeout
                .setResponseTimeout(Timeout.ofMilliseconds(10000))       // socket (read) timeout
                .setConnectionRequestTimeout(Timeout.ofMilliseconds(2000)) // lấy connection từ pool
                .build();

        CloseableHttpClient httpClient = HttpClients.custom()
                .setDefaultRequestConfig(requestConfig)
                .disableAutomaticRetries() // tuỳ chọn: tắt retry tự động
                .build();

        // HttpComponentsClientHttpRequestFactory từ Spring 6.1+ hỗ trợ HttpClient5 CloseableHttpClient
        HttpComponentsClientHttpRequestFactory factory =
                new HttpComponentsClientHttpRequestFactory(httpClient);

        return new RestTemplate(factory);
    }
}
