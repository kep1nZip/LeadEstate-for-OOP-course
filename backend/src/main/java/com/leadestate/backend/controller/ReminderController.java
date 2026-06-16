package com.leadestate.backend.controller;

import com.leadestate.backend.service.ReminderService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/reminders")
public class ReminderController {

    @Autowired
    private ReminderService reminderService;

    @GetMapping
    public ResponseEntity<?> getAllReminders(@RequestParam Integer userId, @RequestParam String role) {

        try {
            List<Map<String, Object>> reminders = reminderService.getAllReminders(userId, role);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Daftar reminder berhasil diambil");
            response.put("data", reminders);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message","Gagal mengambil reminder" );
            errorResponse.put("error", e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(errorResponse);
        }
    }
    
    @PutMapping("/{id}/done")
    public ResponseEntity<?> markReminderDone(@PathVariable Long id, @RequestParam Integer userId, @RequestParam String role) {
        try {
            reminderService.markAsDone(id, userId, role);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Reminder berhasil diselesaikan");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", "Gagal update reminder");
            error.put("error", e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PostMapping
public ResponseEntity<?> createReminder(@RequestBody Map<String, Object> request) {
    try {
        reminderService.createReminder(request);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Reminder berhasil dibuat");

        return ResponseEntity.ok(response);

    } catch (Exception e) {
        Map<String, Object> error = new HashMap<>();
        error.put("message", "Gagal membuat reminder");
        error.put("error", e.getMessage());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
}