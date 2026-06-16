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

/**
 * DAO (Data Access Object) untuk kelas Notifikasi.
 *
 * Bertugas menjembatani objek Notifikasi (model) dengan tabel {@code notifikasi}
 * pada database (lihat leadestate_v2.sql).
 *
 * <p>Kolom tabel notifikasi:
 * {@code notifId, message, sentAt, isRead, followupId, reminderDate, status, userId}</p>
 *
 * <p>Notifikasi merupakan turunan (extends) dari Reminder, sehingga tabel ini
 * menyimpan kolom tambahan dibanding tabel {@code reminders}: yaitu
 * {@code message}, {@code sentAt}, {@code isRead}, dan {@code userId}.</p>
 *
 * <p>Pola implementasi mengikuti {@code ReminderDAO.java}:
 * try-with-resources + PreparedStatement, koneksi dari
 * {@code DBConnection.getConnection()}.</p>
 *
 * @author Rafa Ahmad Aulia (103012400169)
 * @version 1.0
 */
public class NotifikasiDAO {

    // =========================================================================
    // READ
    // =========================================================================

    /**
     * Mengambil seluruh data notifikasi dari database.
     *
     * @return List berisi semua Notifikasi, kosong jika tidak ada data.
     */
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

    /**
     * Mencari satu notifikasi berdasarkan notifId.
     *
     * @param notifId id notifikasi yang dicari.
     * @return objek Notifikasi jika ditemukan, atau {@code null} jika tidak ada.
     */
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

    /**
     * Mengambil semua notifikasi milik satu user, diurutkan dari terbaru.
     * Dipakai untuk menampilkan daftar notifikasi di header/bell icon.
     *
     * @param userId id user penerima notifikasi.
     * @return List Notifikasi milik user tersebut, terbaru di atas.
     */
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

    /**
     * Mengambil notifikasi yang belum dibaca milik satu user.
     * Dipakai untuk badge notifikasi (menampilkan jumlah & isi yang belum dibaca).
     *
     * @param userId id user penerima notifikasi.
     * @return List Notifikasi yang belum dibaca (isRead = 0), terbaru di atas.
     */
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

    /**
     * Menghitung jumlah notifikasi yang belum dibaca milik satu user.
     * Dipakai untuk angka badge pada ikon notifikasi di header.
     *
     * @param userId id user penerima notifikasi.
     * @return jumlah notifikasi yang belum dibaca (isRead = 0).
     */
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

    /**
     * Mencari semua notifikasi yang terkait dengan satu followupId.
     *
     * @param followupId id follow-up terkait.
     * @return List Notifikasi terkait follow-up tersebut.
     */
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

    // =========================================================================
    // CREATE
    // =========================================================================

    /**
     * Menyimpan notifikasi baru ke database (INSERT).
     *
     * <p>notifId pada objek notifikasi akan diisi otomatis dengan id hasil
     * generate dari database setelah berhasil disimpan.</p>
     *
     * @param notifikasi objek Notifikasi yang akan disimpan (tanpa notifId).
     * @return {@code true} jika berhasil, {@code false} jika gagal.
     */
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

    // =========================================================================
    // UPDATE
    // =========================================================================

    /**
     * Memperbarui data notifikasi yang sudah ada (UPDATE berdasarkan notifId).
     *
     * @param notifikasi objek Notifikasi dengan notifId yang valid dan data baru.
     * @return {@code true} jika berhasil, {@code false} jika gagal.
     */
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

    /**
     * Menandai satu notifikasi sebagai sudah dibaca (isRead = true).
     * Dipanggil saat user mengklik notifikasi.
     *
     * @param notifId id notifikasi yang akan ditandai sudah dibaca.
     * @return {@code true} jika berhasil, {@code false} jika gagal.
     */
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

    /**
     * Menandai semua notifikasi milik satu user sebagai sudah dibaca.
     * Dipanggil saat user menekan tombol "Tandai semua sudah dibaca".
     *
     * @param userId id user yang notifikasinya akan ditandai.
     * @return {@code true} jika berhasil, {@code false} jika gagal.
     */
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

    // =========================================================================
    // DELETE
    // =========================================================================

    /**
     * Menghapus notifikasi berdasarkan notifId.
     *
     * @param notifId id notifikasi yang akan dihapus.
     * @return {@code true} jika berhasil, {@code false} jika gagal.
     */
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

    // =========================================================================
    // HELPER (private)
    // =========================================================================

    /**
     * Mengubah satu baris ResultSet menjadi objek Notifikasi.
     *
     * @param rs ResultSet yang sedang dibaca (pointer sudah di baris yang benar).
     * @return objek Notifikasi hasil pemetaan.
     * @throws SQLException jika kolom tidak ditemukan.
     */
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

    /**
     * Mengubah {@code java.util.Date} menjadi {@code java.sql.Timestamp} (boleh null).
     *
     * @param date tanggal yang akan dikonversi.
     * @return Timestamp hasil konversi, atau {@code null} jika input {@code null}.
     */
    private Timestamp toTimestamp(java.util.Date date) {
        return date != null ? new Timestamp(date.getTime()) : null;
    }
}
