package com.leadestate.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.leadestate.backend.entity.User;
import com.leadestate.backend.dto.UserResponse;
import com.leadestate.backend.repository.UserRepository;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    private Map<String, String> resetTokens = new HashMap<>();
    private Map<String, Long> tokenExpiry = new HashMap<>();

    public UserResponse login(String email, String password) {

        System.out.println("=== LOGIN ATTEMPT ===");
        System.out.println("Email input: " + email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    System.out.println("❌ USER TIDAK DITEMUKAN");
                    return new RuntimeException("User tidak ditemukan");
                });

        System.out.println("✅ USER DITEMUKAN: " + user.getEmail());
        // DEBUG
        if (!user.getPassword().equals(password)) {
            System.out.println("❌ PASSWORD SALAH");
            throw new RuntimeException("Password salah");
        }

        System.out.println("🎉 LOGIN BERHASIL");
        System.out.println("ROLE ID: " + user.getRoleId());

        String roleName = switch (user.getRoleId() != null ? user.getRoleId() : 0) {
            case 1 -> "Admin";
            case 3 -> "Sales";
            default -> "Supervisor";
        };

        return new UserResponse(
            user.getId(),
            user.getName(),
            user.getEmail(),
            roleName,
            user.getPhone()
        );
    }

    public User register(User user) {

        if (user.getName() == null || user.getName().isBlank()) {
            throw new RuntimeException("Nama wajib diisi");
        }

        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new RuntimeException("Email wajib diisi");
        }

        if (user.getPhone() == null || user.getPhone().isBlank()) {
            throw new RuntimeException("Nomor telepon wajib diisi");
        }

        if (!user.getPhone().matches("^\\d{10,15}$")) {
            throw new RuntimeException("Nomor telepon tidak valid");
        }

        if (user.getPassword() == null || user.getPassword().length() < 8) {
            throw new RuntimeException("Password minimal 8 karakter");
        }

        if (user.getRoleId() == null) {
            throw new RuntimeException("Role wajib dipilih");
        }

        if (user.getRoleId() != 1 && user.getRoleId() != 3) {
            throw new RuntimeException("Role tidak valid");
        }

        userRepository.findByEmail(user.getEmail()).ifPresent(u -> {
            throw new RuntimeException("Email sudah terdaftar!");
        });

        return userRepository.save(user);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    // ==========================
    // RESET PASSWORD (UPDATED)
    // ==========================

    public String createResetToken(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Email tidak ditemukan"));

        String token;
        do {
            token = String.valueOf((int)(Math.random() * 900000) + 100000);
        } while (resetTokens.containsKey(token));

        resetTokens.put(token, user.getEmail());

        tokenExpiry.put(token, System.currentTimeMillis() + (5 * 60 * 1000));

        return token;
    }

    public String validateToken(String token) {
        String email = resetTokens.get(token);
        Long expiry = tokenExpiry.get(token);

        if (email == null || expiry == null) {
            throw new RuntimeException("Token tidak valid");
        }

        if (System.currentTimeMillis() > expiry) {
            resetTokens.remove(token);
            tokenExpiry.remove(token);
            throw new RuntimeException("Token expired");
        }

        return email;
    }

    public void resetPassword(String token, String newPassword) {
        String email = validateToken(token);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User tidak ditemukan"));

        //validasi password baru
        if (newPassword == null || newPassword.length() < 8) {
            throw new RuntimeException("Password minimal 8 karakter");
        }

        if (!newPassword.matches(".*[A-Z].*")) {
            throw new RuntimeException("Password harus mengandung huruf besar");
        }

        if (!newPassword.matches(".*\\d.*")) {
            throw new RuntimeException("Password harus mengandung angka");
        }

        if (!newPassword.matches(".*[^A-Za-z0-9].*")) {
            throw new RuntimeException("Password harus mengandung karakter spesial");
        }

        if (user.getPassword().equals(newPassword)) {
            throw new RuntimeException("Gagal update: password baru tidak boleh sama dengan password lama");
        }

        user.setPassword(newPassword);
        userRepository.save(user);

        resetTokens.remove(token);
        tokenExpiry.remove(token);
    }

    // ==========================
    // HELPER AUTHORIZATION
    // ==========================

    public void checkAdmin(String role) {
        if (!"Admin".equals(role)) {
            throw new RuntimeException("Access denied: Admin only");
        }
    }

    public void checkSalesOrAdmin(String role) {
        if (!"Admin".equals(role) && !"Sales".equals(role)) {
            throw new RuntimeException("Access denied");
        }
    }
}