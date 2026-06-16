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

/**
 * DAO (Data Access Object) untuk kelas Reminder.
 *
 * Bertugas menjembatani objek Reminder (model) dengan tabel `reminders`
 * pada database (lihat leadestate_v2.sql).
 *
 * Kolom tabel reminders: id, followupId, reminderDate, status.
 */
public class ReminderDAO {

    /**
     * Mengambil seluruh data reminder dari database.
     *
     * @return List berisi semua Reminder.
     */
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

    /**
     * Mencari satu reminder berdasarkan id.
     *
     * @param id id reminder.
     * @return objek Reminder jika ditemukan, atau null jika tidak ada.
     */
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

    /**
     * Mencari semua reminder yang terkait dengan satu followupId.
     * Dipakai oleh FollowUp untuk mengisi daftarReminder-nya.
     *
     * @param followupId id follow-up terkait.
     * @return List berisi Reminder milik follow-up tersebut.
     */
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

    /**
     * Menyimpan reminder baru ke database (INSERT).
     * id pada objek reminder akan diisi otomatis dengan id hasil
     * generate dari database setelah berhasil disimpan.
     *
     * @param reminder objek Reminder yang akan disimpan (tanpa id).
     * @return true jika berhasil, false jika gagal.
     */
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

    /**
     * Memperbarui data reminder yang sudah ada (UPDATE berdasarkan id).
     *
     * @param reminder objek Reminder dengan id yang valid.
     * @return true jika berhasil, false jika gagal.
     */
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

    /**
     * Menghapus reminder berdasarkan id.
     *
     * @param id id reminder yang akan dihapus.
     * @return true jika berhasil, false jika gagal.
     */
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

    /**
     * Mengubah satu baris ResultSet menjadi objek Reminder.
     */
    private Reminder mapRow(ResultSet rs) throws SQLException {
        Reminder r = new Reminder();
        r.setId(rs.getInt("id"));
        r.setFollowupId(rs.getInt("followupId"));
        Timestamp ts = rs.getTimestamp("reminderDate");
        r.setReminderDate(ts != null ? new java.util.Date(ts.getTime()) : null);
        r.setStatus(rs.getString("status"));
        return r;
    }

    /**
     * Mengubah java.util.Date menjadi java.sql.Timestamp (boleh null).
     */
    private Timestamp toTimestamp(java.util.Date date) {
        return date != null ? new Timestamp(date.getTime()) : null;
    }
}