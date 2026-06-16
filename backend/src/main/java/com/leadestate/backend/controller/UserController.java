package com.leadestate.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.leadestate.backend.entity.User;
import com.leadestate.backend.service.UserService;
import com.leadestate.backend.service.AuthService;
import com.leadestate.backend.repository.LeadRepository;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthService authService;

    @Autowired
    private LeadRepository leadRepository;

    // =========================
    // GET ALL USERS (Admin Only)
    // =========================
    @GetMapping
    public ResponseEntity<?> getAllUsers(
            @RequestHeader("Role") String role
    ) {
        try {
            authService.checkAdmin(role);
            List<User> users = userService.getAllUsers();
            return ResponseEntity.ok(users);
        } catch (RuntimeException e) {
            return handleUserException(e);
        }
    }

    // =========================
    // CREATE USER (FR10) (Admin Only)
    // =========================
    @PostMapping
    public ResponseEntity<?> addUser(
            @RequestHeader("Role") String role,
            @RequestBody User user
    ) {
        try {
            authService.checkAdmin(role);
            User newUser = userService.createUser(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
        } catch (RuntimeException e) {
            return handleUserException(e);
        }
    }

    // =========================
    // UPDATE USER (ADMIN) (Admin Only)
    // =========================
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(
            @RequestHeader("Role") String role,
            @PathVariable Integer id,
            @RequestBody User user) {
        try {
            authService.checkAdmin(role);
            User updatedUser = userService.updateUser(id, user);
            return ResponseEntity.ok(updatedUser);
        } catch (RuntimeException e) {
            return handleUserException(e);
        }
    }

    // =========================
    // UPDATE ROLE ONLY
    // =========================
    @PutMapping("/{id}/role")
    public ResponseEntity<?> updateRole(
            @RequestHeader("Role") String role,
            @PathVariable Integer id,
            @RequestBody User user
    ) {
        try {

            authService.checkAdmin(role);

            User updatedUser =
                userService.updateUserRole(
                    id,
                    user.getRoleId()
                );

            return ResponseEntity.ok(updatedUser);

        } catch (RuntimeException e) {
            return handleUserException(e);
        }
    }

    // =========================
    // DELETE USER (FR11) (Admin Only)
    // =========================
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(
            @RequestHeader("Role") String role,
            @PathVariable Integer id
    ) {
        try {
            authService.checkAdmin(role);
            String message = userService.deleteUser(id);
            return ResponseEntity.ok(message);
        } catch (RuntimeException e) {
            return handleUserException(e);
        }
    }

    // =========================
    // UPDATE PROFILE (FR12) 
    // =========================
    @PutMapping("/profile/{id}")
    public ResponseEntity<?> updateProfile(
            @PathVariable Integer id,
            @RequestBody User user) {
        try {
            User updatedUser = userService.updateProfile(id, user);
            return ResponseEntity.ok(updatedUser);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                 .body(e.getMessage());
        }
    }

    // =========================
    // GET SALES STATS (closing, followup, leads bulan ini)
    // =========================
    @GetMapping("/{id}/stats")
    public ResponseEntity<?> getSalesStats(
            @RequestHeader("Role") String role,
            @PathVariable Integer id
    ) {
        try {
            authService.checkAdmin(role);

            int month = java.time.LocalDate.now().getMonthValue();
            int year  = java.time.LocalDate.now().getYear();

            long closing    = leadRepository.countClosingBySalesAndMonth(id, month, year);
            long followup   = leadRepository.countFollowUpBySalesAndMonth(id, month, year);
            long leadsTotal = leadRepository.countLeadsBySalesAndMonth(id, month, year);

            java.util.List<java.util.Map<String, Object>> leads =
                leadRepository.findLeadsBySalesAndMonth(id, month, year);

            java.util.Map<String, Object> result = new java.util.HashMap<>();
            result.put("closing",    closing);
            result.put("followup",   followup);
            result.put("leadsTotal", leadsTotal);
            result.put("leads",      leads);

            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return handleUserException(e);
        }
    }

    /**
     * Helper method untuk menangani pengecekan pesan error Access Denied
     */
    private ResponseEntity<?> handleUserException(RuntimeException e) {
        if (e.getMessage() != null && e.getMessage().contains("Access denied")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                 .body(e.getMessage());
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                             .body(e.getMessage());
    }
}