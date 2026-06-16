/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.leadestate.dao;

import com.leadestate.model.FollowUp;
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
 * DAO (Data Access Object) untuk kelas FollowUp.
 *
 * Bertugas menjembatani objek FollowUp (model) dengan tabel {@code followups}
 * pada database (lihat leadestate_v2.sql).
 *
 * <p>Kolom tabel followups:
 * {@code id, leadId, salesId, notes, followupDate, status}</p>
 *
 * <p>Pola implementasi mengikuti {@code ReminderDAO.java}:
 * try-with-resources + PreparedStatement, koneksi dari
 * {@code DBConnection.getConnection()}.</p>
 *
 * @author Rafa Ahmad Aulia (103012400169)
 * @version 1.0
 */
public class FollowUpDAO {

    // =========================================================================
    // READ
    // =========================================================================

    /**
     * Mengambil seluruh data follow-up dari database.
     *
     * <p>Setiap objek FollowUp yang dikembalikan sudah dilengkapi dengan
     * daftar Reminder-nya (diisi via {@code ReminderDAO.findByFollowupId()}).</p>
     *
     * @return List berisi semua FollowUp, kosong jika tidak ada data.
     */
    public List<FollowUp> findAll() {
        List<FollowUp> daftar = new ArrayList<>();
        String sql = "SELECT id, leadId, salesId, notes, followupDate, status FROM followups";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                daftar.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.out.println("[FollowUpDAO] Gagal findAll(): " + e.getMessage());
        }
        return daftar;
    }

    /**
     * Mencari satu follow-up berdasarkan id.
     *
     * @param id id follow-up yang dicari.
     * @return objek FollowUp jika ditemukan, atau {@code null} jika tidak ada.
     */
    public FollowUp findById(int id) {
        String sql = "SELECT id, leadId, salesId, notes, followupDate, status "
                   + "FROM followups WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println("[FollowUpDAO] Gagal findById(): " + e.getMessage());
        }
        return null;
    }

    /**
     * Mencari semua follow-up yang terkait dengan satu leadId.
     * Dipakai di halaman Reminder & Follow-up untuk menampilkan
     * riwayat follow-up sebuah lead.
     *
     * @param leadId id lead yang dicari follow-up-nya.
     * @return List berisi FollowUp milik lead tersebut,
     *         diurutkan dari yang terbaru (followupDate DESC).
     */
    public List<FollowUp> findByLeadId(int leadId) {
        List<FollowUp> daftar = new ArrayList<>();
        String sql = "SELECT id, leadId, salesId, notes, followupDate, status "
                   + "FROM followups WHERE leadId = ? ORDER BY followupDate DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, leadId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    daftar.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("[FollowUpDAO] Gagal findByLeadId(): " + e.getMessage());
        }
        return daftar;
    }

    /**
     * Mencari semua follow-up yang dikerjakan oleh satu salesId.
     * Dipakai untuk menampilkan daftar reminder milik Sales yang sedang login.
     *
     * @param salesId id sales yang dicari follow-up-nya.
     * @return List berisi FollowUp milik sales tersebut,
     *         diurutkan dari yang paling dekat jadwalnya (followupDate ASC).
     */
    public List<FollowUp> findBySalesId(int salesId) {
        List<FollowUp> daftar = new ArrayList<>();
        String sql = "SELECT id, leadId, salesId, notes, followupDate, status "
                   + "FROM followups WHERE salesId = ? ORDER BY followupDate ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, salesId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    daftar.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("[FollowUpDAO] Gagal findBySalesId(): " + e.getMessage());
        }
        return daftar;
    }

    /**
     * Mencari semua follow-up yang dijadwalkan hari ini untuk satu salesId.
     * Dipakai di tab "Hari Ini" pada halaman Reminder & Follow-up.
     *
     * @param salesId id sales yang sedang login.
     * @return List berisi FollowUp hari ini milik sales tersebut.
     */
    public List<FollowUp> findHariIniOlehSales(int salesId) {
        List<FollowUp> daftar = new ArrayList<>();
        String sql = "SELECT id, leadId, salesId, notes, followupDate, status "
                   + "FROM followups "
                   + "WHERE salesId = ? AND DATE(followupDate) = CURDATE() "
                   + "ORDER BY followupDate ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, salesId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    daftar.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("[FollowUpDAO] Gagal findHariIniOlehSales(): " + e.getMessage());
        }
        return daftar;
    }

    /**
     * Mencari follow-up yang sudah lewat jadwal (overdue) untuk satu salesId.
     * Dipakai di tab "Tertunda" pada halaman Reminder & Follow-up.
     *
     * <p>Definisi tertunda: followupDate sudah lewat dari sekarang
     * dan status masih {@code Pending}.</p>
     *
     * @param salesId id sales yang sedang login.
     * @return List berisi FollowUp tertunda milik sales tersebut.
     */
    public List<FollowUp> findTertundaOlehSales(int salesId) {
        List<FollowUp> daftar = new ArrayList<>();
        String sql = "SELECT id, leadId, salesId, notes, followupDate, status "
                   + "FROM followups "
                   + "WHERE salesId = ? AND followupDate < NOW() AND status = 'Pending' "
                   + "ORDER BY followupDate ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, salesId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    daftar.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("[FollowUpDAO] Gagal findTertundaOlehSales(): " + e.getMessage());
        }
        return daftar;
    }

    /**
     * Menghitung jumlah follow-up berdasarkan status (dipakai Dashboard).
     *
     * @param status nilai status yang dihitung (misal "Pending", "Selesai").
     * @return jumlah baris yang cocok.
     */
    public int countByStatus(String status) {
        String sql = "SELECT COUNT(*) FROM followups WHERE status = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, status);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.out.println("[FollowUpDAO] Gagal countByStatus(): " + e.getMessage());
        }
        return 0;
    }

    // =========================================================================
    // CREATE
    // =========================================================================

    /**
     * Menyimpan follow-up baru ke database (INSERT).
     *
     * <p>id pada objek followUp akan diisi otomatis dengan id hasil
     * generate dari database setelah berhasil disimpan.</p>
     *
     * @param followUp objek FollowUp yang akan disimpan (tanpa id).
     * @return {@code true} jika berhasil, {@code false} jika gagal.
     */
    public boolean save(FollowUp followUp) {
        String sql = "INSERT INTO followups (leadId, salesId, notes, followupDate, status) "
                   + "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, followUp.getLeadId());
            ps.setInt(2, followUp.getSalesId());
            ps.setString(3, followUp.getNotes());
            ps.setTimestamp(4, toTimestamp(followUp.getFollowupDate()));
            ps.setString(5, followUp.getStatus() != null
                             ? followUp.getStatus()
                             : FollowUp.STATUS_PENDING);

            int baris = ps.executeUpdate();
            if (baris > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        followUp.setId(keys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.out.println("[FollowUpDAO] Gagal save(): " + e.getMessage());
        }
        return false;
    }

    // =========================================================================
    // UPDATE
    // =========================================================================

    /**
     * Memperbarui data follow-up yang sudah ada (UPDATE berdasarkan id).
     *
     * @param followUp objek FollowUp dengan id yang valid dan data baru.
     * @return {@code true} jika berhasil, {@code false} jika gagal.
     */
    public boolean update(FollowUp followUp) {
        String sql = "UPDATE followups "
                   + "SET leadId = ?, salesId = ?, notes = ?, followupDate = ?, status = ? "
                   + "WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, followUp.getLeadId());
            ps.setInt(2, followUp.getSalesId());
            ps.setString(3, followUp.getNotes());
            ps.setTimestamp(4, toTimestamp(followUp.getFollowupDate()));
            ps.setString(5, followUp.getStatus());
            ps.setInt(6, followUp.getId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("[FollowUpDAO] Gagal update(): " + e.getMessage());
            return false;
        }
    }

    /**
     * Memperbarui hanya kolom {@code status} pada follow-up tertentu.
     * Dipakai oleh {@code FollowUpController} saat tombol "Tandai Selesai" diklik.
     *
     * @param id     id follow-up yang akan diubah statusnya.
     * @param status nilai status baru (gunakan konstanta di {@code FollowUp}).
     * @return {@code true} jika berhasil, {@code false} jika gagal.
     */
    public boolean updateStatus(int id, String status) {
        String sql = "UPDATE followups SET status = ? WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, status);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("[FollowUpDAO] Gagal updateStatus(): " + e.getMessage());
            return false;
        }
    }

    // =========================================================================
    // DELETE
    // =========================================================================

    /**
     * Menghapus follow-up berdasarkan id.
     *
     * <p>Catatan: pastikan semua Reminder terkait dihapus terlebih dahulu
     * via {@code ReminderDAO.deleteByFollowupId()} sebelum memanggil method ini,
     * agar tidak melanggar foreign key constraint.</p>
     *
     * @param id id follow-up yang akan dihapus.
     * @return {@code true} jika berhasil, {@code false} jika gagal.
     */
    public boolean delete(int id) {
        String sql = "DELETE FROM followups WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("[FollowUpDAO] Gagal delete(): " + e.getMessage());
            return false;
        }
    }

    // =========================================================================
    // HELPER (private)
    // =========================================================================

    /**
     * Mengubah satu baris ResultSet menjadi objek FollowUp.
     *
     * <p>Reminder terkait tidak di-load di sini secara otomatis untuk
     * menghindari N+1 query. Jika diperlukan, panggil
     * {@code new ReminderDAO().findByFollowupId(followUp.getId())} secara terpisah
     * lalu set ke objek via {@code followUp.setDaftarReminder(...)}.</p>
     *
     * @param rs ResultSet yang sedang dibaca (pointer sudah di baris yang benar).
     * @return objek FollowUp hasil pemetaan.
     * @throws SQLException jika kolom tidak ditemukan.
     */
    private FollowUp mapRow(ResultSet rs) throws SQLException {
        FollowUp f = new FollowUp();
        f.setId(rs.getInt("id"));
        f.setLeadId(rs.getInt("leadId"));
        f.setSalesId(rs.getInt("salesId"));
        f.setNotes(rs.getString("notes"));
        Timestamp ts = rs.getTimestamp("followupDate");
        f.setFollowupDate(ts != null ? new java.util.Date(ts.getTime()) : null);
        f.setStatus(rs.getString("status"));
        return f;
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
