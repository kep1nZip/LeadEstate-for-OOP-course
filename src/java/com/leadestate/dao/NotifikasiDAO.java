/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.leadestate.dao;

import com.leadestate.model.Notifikasi;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class NotifikasiDAO {

    // READ

    public List<Notifikasi> findAll() {
        List<Notifikasi> daftar = new ArrayList<>();
        String sql = "SELECT notifId, message, sentAt, isRead, "
                   + "followupId, reminderDate, status, userId FROM notifikasi";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                daftar.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.out.println("[NotifikasiDAO] Gagal findAll(): " + e.getMessage());
        }
        return daftar;
    }

    public Notifikasi findById(int notifId) {
        String sql = "SELECT notifId, message, sentAt, isRead, "
                   + "followupId, reminderDate, status, userId "
                   + "FROM notifikasi WHERE notifId = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, notifId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println("[NotifikasiDAO] Gagal findById(): " + e.getMessage());
        }
        return null;
    }

    public List<Notifikasi> findByUserId(int userId) {
        List<Notifikasi> daftar = new ArrayList<>();
        String sql = "SELECT notifId, message, sentAt, isRead, "
                   + "followupId, reminderDate, status, userId "
                   + "FROM notifikasi WHERE userId = ? ORDER BY sentAt DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    daftar.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("[NotifikasiDAO] Gagal findByUserId(): " + e.getMessage());
        }
        return daftar;
    }

    public List<Notifikasi> findBelumDibacaByUserId(int userId) {
        List<Notifikasi> daftar = new ArrayList<>();
        String sql = "SELECT notifId, message, sentAt, isRead, "
                   + "followupId, reminderDate, status, userId "
                   + "FROM notifikasi WHERE userId = ? AND isRead = 0 ORDER BY sentAt DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    daftar.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("[NotifikasiDAO] Gagal findBelumDibacaByUserId(): " + e.getMessage());
        }
        return daftar;
    }

    public int countUnreadByUserId(int userId) {
        String sql = "SELECT COUNT(*) FROM notifikasi WHERE userId = ? AND isRead = 0";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.out.println("[NotifikasiDAO] Gagal countUnreadByUserId(): " + e.getMessage());
        }
        return 0;
    }

    public List<Notifikasi> findByFollowupId(int followupId) {
        List<Notifikasi> daftar = new ArrayList<>();
        String sql = "SELECT notifId, message, sentAt, isRead, "
                   + "followupId, reminderDate, status, userId "
                   + "FROM notifikasi WHERE followupId = ? ORDER BY sentAt DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, followupId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    daftar.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("[NotifikasiDAO] Gagal findByFollowupId(): " + e.getMessage());
        }
        return daftar;
    }

    // CREATE

    public boolean save(Notifikasi notifikasi) {
        String sql = "INSERT INTO notifikasi "
                   + "(message, sentAt, isRead, followupId, reminderDate, status, userId) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, notifikasi.getMessage());
            ps.setTimestamp(2, toTimestamp(notifikasi.getSentAt()));
            ps.setBoolean(3, notifikasi.isRead());
            ps.setInt(4, notifikasi.getFollowupId());
            ps.setTimestamp(5, toTimestamp(notifikasi.getReminderDate()));
            ps.setString(6, notifikasi.getStatus());
            ps.setInt(7, notifikasi.getUserId());

            int baris = ps.executeUpdate();
            if (baris > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        notifikasi.setNotifId(keys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.out.println("[NotifikasiDAO] Gagal save(): " + e.getMessage());
        }
        return false;
    }

    // UPDATE

    public boolean update(Notifikasi notifikasi) {
        String sql = "UPDATE notifikasi "
                   + "SET message = ?, sentAt = ?, isRead = ?, "
                   + "followupId = ?, reminderDate = ?, status = ?, userId = ? "
                   + "WHERE notifId = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, notifikasi.getMessage());
            ps.setTimestamp(2, toTimestamp(notifikasi.getSentAt()));
            ps.setBoolean(3, notifikasi.isRead());
            ps.setInt(4, notifikasi.getFollowupId());
            ps.setTimestamp(5, toTimestamp(notifikasi.getReminderDate()));
            ps.setString(6, notifikasi.getStatus());
            ps.setInt(7, notifikasi.getUserId());
            ps.setInt(8, notifikasi.getNotifId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("[NotifikasiDAO] Gagal update(): " + e.getMessage());
            return false;
        }
    }

    public boolean markAsRead(int notifId) {
        String sql = "UPDATE notifikasi SET isRead = 1 WHERE notifId = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, notifId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("[NotifikasiDAO] Gagal markAsRead(): " + e.getMessage());
            return false;
        }
    }

    public boolean markAllAsReadByUserId(int userId) {
        String sql = "UPDATE notifikasi SET isRead = 1 WHERE userId = ? AND isRead = 0";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            return ps.executeUpdate() >= 0;
        } catch (SQLException e) {
            System.out.println("[NotifikasiDAO] Gagal markAllAsReadByUserId(): " + e.getMessage());
            return false;
        }
    }

    // DELETE

    public boolean delete(int notifId) {
        String sql = "DELETE FROM notifikasi WHERE notifId = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, notifId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("[NotifikasiDAO] Gagal delete(): " + e.getMessage());
            return false;
        }
    }

    // HELPER (private)

    private Notifikasi mapRow(ResultSet rs) throws SQLException {
        Notifikasi n = new Notifikasi();
        n.setNotifId(rs.getInt("notifId"));
        n.setMessage(rs.getString("message"));

        Timestamp sentAt = rs.getTimestamp("sentAt");
        n.setSentAt(sentAt != null ? new java.util.Date(sentAt.getTime()) : null);

        n.setIsRead(rs.getBoolean("isRead"));
        n.setFollowupId(rs.getInt("followupId"));

        Timestamp reminderDate = rs.getTimestamp("reminderDate");
        n.setReminderDate(reminderDate != null ? new java.util.Date(reminderDate.getTime()) : null);

        n.setStatus(rs.getString("status"));
        n.setUserId(rs.getInt("userId"));
        return n;
    }

    private Timestamp toTimestamp(java.util.Date date) {
        return date != null ? new Timestamp(date.getTime()) : null;
    }
}
