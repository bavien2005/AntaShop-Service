package org.anta.service;


import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Duration;

@Service
@RequiredArgsConstructor
public class OtpRedisService {

    private final StringRedisTemplate redis;
    private final SecureRandom rnd = new SecureRandom();

    private static final Duration OTP_TTL = Duration.ofMinutes(2);     // hiệu lực OTP
    private static final Duration COOLDOWN = Duration.ofSeconds(60);   // chặn spam gửi lại
    private static final int MAX_ATTEMPTS = 5;                          // tối đa nhập sai

    private String norm(String email) {
        return email == null ? "" : email.trim().toLowerCase();
    }

    private String keyOtp(String email)       {
        return "otp:code:" + email;
    }
    private String keyCooldown(String email)  {
        return "otp:cooldown:" + email;
    }
    private String keyAttempts(String email)  {
        return "otp:attempts:" + email;
    }

    //Tạo OTP, lưu Redis (keyOtp TTL=2m),cooldown 60s (keyCooldown TTL=60s)
    public String generateAndSave(String rawEmail) {
        String email = norm(rawEmail);

        Long ttl = redis.getExpire(keyCooldown(email));
        if (ttl != null && ttl > 0) {
            throw new IllegalStateException("You can only request OTP again after " + ttl + " seconds");
        }

        String code = String.format("%06d", rnd.nextInt(1_000_000));

        redis.opsForValue().set(keyOtp(email), code, OTP_TTL);

        redis.delete(keyAttempts(email));

        redis.opsForValue().set(keyCooldown(email), "1", COOLDOWN);

        return code;
    }

    public boolean verify(String rawEmail, String otp) {
        String email = norm(rawEmail);

        String saved = redis.opsForValue().get(keyOtp(email));
        if (saved == null) {
            return false;
        }

        if (!saved.equals(otp)) {

            String aKey = keyAttempts(email);
            Long attempts = redis.opsForValue().increment(aKey);
            Long otpTtl = redis.getExpire(keyOtp(email));
            if (otpTtl != null && otpTtl > 0) {
                redis.expire(aKey, Duration.ofSeconds(otpTtl));
            }
            if (attempts != null && attempts >= MAX_ATTEMPTS) {
                redis.delete(keyOtp(email));
                redis.delete(aKey);
            }
            return false;
        }

        redis.delete(keyOtp(email));
        redis.delete(keyAttempts(email));
        return true;
    }
}
