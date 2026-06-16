package com.leadestate.dao;

import com.leadestate.model.Reminder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class ReminderDAO {

    public List<Reminder> findAll() {
        List<Reminder> daftar = new ArrayList<>();
        String sql = "SELECT id, followupId, reminderDate, status FROM reminders";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                daftar.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.out.println("[ReminderDAO] Gagal findAll(): " + e.getMessage());
        }
        return daftar;
    }

    public Reminder findById(int id) {
        String sql = "SELECT id, followupId, reminderDate, status FROM reminders WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println("[ReminderDAO] Gagal findById(): " + e.getMessage());
        }
        return null;
    }

    public List<Reminder> findByFollowupId(int followupId) {
        List<Reminder> daftar = new ArrayList<>();
        String sql = "SELECT id, followupId, reminderDate, status FROM reminders WHERE followupId = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, followupId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    daftar.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("[ReminderDAO] Gagal findByFollowupId(): " + e.getMessage());
        }
        return daftar;
    }

    public boolean save(Reminder reminder) {
        String sql = "INSERT INTO reminders (followupId, reminderDate, status) VALUES (?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, reminder.getFollowupId());
            ps.setTimestamp(2, toTimestamp(reminder.getReminderDate()));
            ps.setString(3, reminder.getStatus());

            int baris = ps.executeUpdate();
            if (baris > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        reminder.setId(keys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.out.println("[ReminderDAO] Gagal save(): " + e.getMessage());
        }
        return false;
    }

    public boolean update(Reminder reminder) {
        String sql = "UPDATE reminders SET followupId = ?, reminderDate = ?, status = ? WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, reminder.getFollowupId());
            ps.setTimestamp(2, toTimestamp(reminder.getReminderDate()));
            ps.setString(3, reminder.getStatus());
            ps.setInt(4, reminder.getId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("[ReminderDAO] Gagal update(): " + e.getMessage());
            return false;
        }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM reminders WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("[ReminderDAO] Gagal delete(): " + e.getMessage());
            return false;
        }
    }

    // ===== Helper =====

    private Reminder mapRow(ResultSet rs) throws SQLException {
        Reminder r = new Reminder();
        r.setId(rs.getInt("id"));
        r.setFollowupId(rs.getInt("followupId"));
        Timestamp ts = rs.getTimestamp("reminderDate");
        r.setReminderDate(ts != null ? new java.util.Date(ts.getTime()) : null);
        r.setStatus(rs.getString("status"));
        return r;
    }

    private Timestamp toTimestamp(java.util.Date date) {
        return date != null ? new Timestamp(date.getTime()) : null;
    }
}