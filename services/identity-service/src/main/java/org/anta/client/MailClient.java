package org.anta.client;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;

import java.util.Map;
import java.util.UUID;

@Component
public class MailClient {

    private final RestTemplate restTemplate;

    public MailClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void sendResetCodeEmail(String to, String resetCode) {

        var url = "http://localhost:8083/api/notifications/email";

        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = Map.of(
                "to", to,
                "subject", " Mã xác thực OTP của bạn",
                "body", "Xin chào,\n\nMã OTP của bạn là: "
                        + resetCode + "\nMã này có hiệu lực trong 2 phút.\n\nTrân trọng,\nĐội ngũ AntaShop",
                "idempotencyKey", UUID.randomUUID().toString()
        );

        try {
            restTemplate.postForEntity(url, new HttpEntity<>(body, headers), String.class);
        } catch (HttpClientErrorException e) {
            System.err.println("Response body: " + e.getResponseBodyAsString());
            throw e;
        } catch (ResourceAccessException e) {
            System.err.println(" Không thể kết nối tới notification-service (8083): " + e.getMessage());
            throw e;
        } catch (Exception e) {
            System.err.println("Lỗi khác khi gửi email OTP: " + e.getMessage());
            throw e;
        }
    }

}
