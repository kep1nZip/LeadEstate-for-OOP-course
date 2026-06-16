package com.leadestate.backend.service;

import com.leadestate.backend.entity.FollowUp;
import com.leadestate.backend.entity.Reminder;
import com.leadestate.backend.repository.FollowUpRepository;
import com.leadestate.backend.repository.ReminderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.util.*;

@Service
public class ReminderService {

    @Autowired
    private ReminderRepository reminderRepository;

    @Autowired
    private FollowUpRepository followUpRepository;

    public List<Map<String, Object>> getAllReminders(Integer userId, String role) {

        List<Object[]> results;
        if ("Admin".equalsIgnoreCase(role)) {

            results =
                reminderRepository
                .getAllReminderDetails();

        } else {

            results =
                reminderRepository
                .getReminderDetailsBySalesId(userId);
        }

        List<Map<String, Object>> data =
                new ArrayList<>();

        for (Object[] row : results) {

            Map<String, Object> item =
                    new HashMap<>();

            item.put("id", row[0]);
            item.put("reminderDate", row[1]);
            item.put("status", row[2]);

            item.put("leadId", row[3]);
            
            item.put("leadName", row[4]);
            item.put("phone", row[5]);
            item.put("source", row[6]);

            item.put("propertyName", row[7]);
            item.put("salesName", row[8]);

            data.add(item);
        }

        return data;
    }

    public void markAsDone(Long id, Integer userId, String role) {

        Reminder reminder = reminderRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Reminder tidak ditemukan"));

        if (!"Admin".equalsIgnoreCase(role)) {

            Integer ownerSalesId =
                reminderRepository
                .getSalesIdByReminderId(id);

            if (!ownerSalesId.equals(userId)) {

                throw new RuntimeException(
                    "Akses ditolak. Reminder bukan milik Anda."
                );
            }
        }

        FollowUp followUp =
            reminder.getFollowUp();

        // ── FORCE UPDATE STATUS FOLLOWUP & REMINDER
        followUp.setStatus("done");
        followUpRepository.save(followUp);

        // Update status reminder agar sinkronisasi data bersih
        reminder.setIsSent(true);
        reminderRepository.save(reminder);
    }

    public void createReminder(Map<String, Object> request) {

        Long leadId = Long.valueOf(request.get("leadId").toString());
        Long salesId = Long.valueOf(request.get("salesId").toString());

        LocalDateTime reminderDate;
        String rawDate = request.get("reminderDate").toString();
        try {
            reminderDate = OffsetDateTime.parse(rawDate).toLocalDateTime();
        } catch (DateTimeParseException e) {
            reminderDate = LocalDateTime.parse(rawDate); // fallback format biasa
        }

        FollowUp followUp = new FollowUp();
        followUp.setLeadId(leadId);
        followUp.setSalesId(salesId);
        followUp.setStatus("pending");
        followUpRepository.save(followUp);

        Reminder reminder = new Reminder();
        reminder.setFollowUp(followUp);
        reminder.setReminderDate(reminderDate);
        reminder.setIsSent(false);

        reminderRepository.save(reminder);
    }
}