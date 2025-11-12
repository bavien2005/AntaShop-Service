package org.anta.controller;

import org.anta.client.MailClient;
import org.anta.config.JwtUtil;
import org.anta.dto.request.AdminCreateUserRequest;
import org.anta.dto.request.LoginRequest;
import org.anta.dto.request.RegisterRequest;
import org.anta.entity.User;
import org.anta.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtUtil jwtUtil;
    private final MailClient mailClient;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        User savedUser = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message", "Đăng ký thành công"));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        String input = request.getName() != null ? request.getName() : request.getEmail();
        User user = authService.login(input, request.getPassword());
        String accessToken = jwtUtil.generateAccessToken(user.getName(), user.getRole().toString());
        String refreshToken = jwtUtil.generateRefreshToken(user.getName());
        return ResponseEntity.ok(Map.of(
                "name", user.getName(),
                "role", user.getRole().toString(),
                "accessToken", accessToken,
                "refreshToken", refreshToken
        ));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        if (email == null) return ResponseEntity.badRequest().body("Missing email");
        String code = authService.createResetCode(email); // trả về mã để gửi mail
        try {
            mailClient.sendResetCodeEmail(email, code);
        } catch (Exception ignored) {}
        return ResponseEntity.ok(Map.of("message", "Reset code sent"));
    }

    @PostMapping("/verify-reset-code")
    public ResponseEntity<?> verifyResetCode(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String code = body.get("code");
        if (email == null || code == null) return ResponseEntity.badRequest().body("Missing fields");
        boolean verify = authService.verifyResetCode(email, code);
        if (!verify) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid or expired code");
        return ResponseEntity.ok(Map.of("message", "Code verified"));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String newPassword = body.get("newPassword");
        if (email == null || newPassword == null) return ResponseEntity.badRequest().body("Missing fields");
        authService.resetPassword(email, newPassword);
        return ResponseEntity.ok(Map.of("message", "Password reset successfully"));
    }

    @GetMapping("/validate-token")
    public ResponseEntity<?> validateToken(@RequestHeader(value = "Authorization"
            , required = false) String authHeader) {

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body(Map.of("valid",
                    false, "error", "Missing token"));
        }
        String token = authHeader.substring(7);
        try {
            String username = jwtUtil.extractUsername(token);
            List<String> roles = jwtUtil.extractRoles(token);
            boolean expired = jwtUtil.isTokenExpired(token);
            if (expired) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).
                        body(Map.of("valid", false, "error", "Token expired"));
            }
            return ResponseEntity.ok(Map.of("username", username,
                    "role", roles, "valid", true));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                    "valid", false, "error", "Invalid token"));
        }
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refresh(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");
        if (refreshToken == null) return ResponseEntity.badRequest().body("Missing refreshToken");
        try {
            if (!jwtUtil.isRefreshToken(refreshToken)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Not a refresh token");
            }
            String username = jwtUtil.extractUsername(refreshToken);
            User user = authService.findByUsername(username);
            String newAccessToken = jwtUtil.generateAccessToken(user.getName(), user.getRole().toString());
            return ResponseEntity.ok(Map.of("accessToken", newAccessToken));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired refresh token");
        }
    }

    @PostMapping("/admin/create-user")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createUserByAdmin(@RequestBody AdminCreateUserRequest request) {
        try {
            User savedUser = authService.createUserByAdmin(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of(
                            "message", "Create user successfully",
                            "username", savedUser.getName(),
                            "role", savedUser.getRole().toString()
                    ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
