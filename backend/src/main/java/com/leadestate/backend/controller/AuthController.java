package com.leadestate.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.leadestate.backend.service.AuthService;
import com.leadestate.backend.entity.User;
import com.leadestate.backend.dto.RegisterRequest;
import com.leadestate.backend.dto.UserResponse;

import com.leadestate.backend.dto.ForgotPasswordRequest;
import com.leadestate.backend.dto.ResetPasswordRequest;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> req) {

        String email = req.get("email");
        String password = req.get("password");

        try {
            UserResponse user = authService.login(email, password);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest req) {
        try {

            User user = new User();
            user.setName(req.getName());
            user.setEmail(req.getEmail());
            user.setPhone(req.getPhone());
            user.setPassword(req.getPassword());
            user.setRoleId(req.getRoleId());

            authService.register(user);

            return ResponseEntity.ok("Register berhasil");

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @GetMapping("/check-email")
    public ResponseEntity<?> checkEmail(@RequestParam String email) {
        boolean exists = authService.existsByEmail(email);
        if (exists) {
            return ResponseEntity.badRequest().body("Gagal daftar: Email sudah terdaftar!");
        }
        return ResponseEntity.ok("Email tersedia");
    }
    
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequest req) {

        try {

            String token = authService.createResetToken(req.getEmail());

            return ResponseEntity.ok(Map.of(
                "message", "OTP berhasil dikirim",
                "token", token
            ));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/validate-token")
    public ResponseEntity<?> validateToken(@RequestParam String token) {
        try {
            String email = authService.validateToken(token);
            return ResponseEntity.ok(Map.of(
                "message", "Token valid",
                "email", email
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest req) {

        try {

            authService.resetPassword(
                req.getToken(),
                req.getNewPassword()
            );

            return ResponseEntity.ok("Password berhasil diupdate");

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}