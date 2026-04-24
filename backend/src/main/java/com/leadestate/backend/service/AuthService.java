package com.leadestate.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.leadestate.backend.entity.User;
import com.leadestate.backend.repository.UserRepository;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    public User login(String email, String password) {

        System.out.println("=== LOGIN ATTEMPT ===");
        System.out.println("Email input: " + email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    System.out.println("❌ USER TIDAK DITEMUKAN");
                    return new RuntimeException("User tidak ditemukan");
                });

        System.out.println("✅ USER DITEMUKAN: " + user.getEmail());
        System.out.println("Password input: " + password);
        System.out.println("Password DB: " + user.getPassword());

        if (!user.getPassword().equals(password)) {
            System.out.println("❌ PASSWORD SALAH");
            throw new RuntimeException("Password salah");
        }

        System.out.println("🎉 LOGIN BERHASIL");

        return user;
    }

    public User register(User user) {
        userRepository.findByEmail(user.getEmail()).ifPresent(u -> {
        throw new RuntimeException("Email sudah terdaftar!");
        });

   
        return userRepository.save(user);
    }

    public boolean existsByEmail(String email) {
    return userRepository.existsByEmail(email); 
   
}
}