package com.leadestate.dao;

import com.leadestate.model.LeadStatus;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO (Data Access Object) untuk kelas LeadStatus.
 *
 * Bertugas menjembatani objek LeadStatus (model) dengan tabel `lead_status`
 * pada database (lihat leadestate_v2.sql).
 *
 * Kolom tabel lead_status: id, statusName.
 *
 * Catatan: tabel lead_status bersifat referensi/master data (7 status:
 * Baru, Dihubungi, Prospect, Negosiasi, Closing, Batal, Tidak Merespons)
 * dan biasanya tidak sering ditambah/diubah, sehingga DAO ini fokus pada
 * operasi baca (findAll, findById, findByName). Method save/update/delete
 * tetap disediakan untuk kebutuhan halaman pengaturan master data jika
 * diperlukan nanti.
 */
public class LeadStatusDAO {

    /**
     * Mengambil seluruh status lead dari database.
     * Berguna untuk dropdown pilihan status saat menambah/mengedit Lead.
     *
     * @return List berisi semua LeadStatus.
     */
    public List<LeadStatus> findAll() {
        List<LeadStatus> daftar = new ArrayList<>();
        String sql = "SELECT id, statusName FROM lead_status";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                daftar.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.out.println("[LeadStatusDAO] Gagal findAll(): " + e.getMessage());
        }
        return daftar;
    }

    /**
     * Mencari satu status lead berdasarkan id.
     * Dipakai oleh Lead.validateStatus(newStatusId) untuk memastikan
     * statusId yang diberikan benar-benar terdaftar di database, dan
     * untuk menampilkan nama status pada detail Lead.
     *
     * @param id id status.
     * @return objek LeadStatus jika ditemukan, atau null jika tidak ada.
     */
    public LeadStatus findById(int id) {
        String sql = "SELECT id, statusName FROM lead_status WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println("[LeadStatusDAO] Gagal findById(): " + e.getMessage());
        }
        return null;
    }

    /**
     * Mencari satu status lead berdasarkan nama status (contoh: "Closing").
     * Berguna jika kode lain memiliki nama status (String) tapi butuh id-nya,
     * misalnya saat mengisi data dummy atau filter berdasarkan nama status
     * dari form JSP.
     *
     * @param statusName nama status, gunakan konstanta LeadStatus.STATUS_*.
     * @return objek LeadStatus jika ditemukan, atau null jika tidak ada.
     */
    public LeadStatus findByName(String statusName) {
        String sql = "SELECT id, statusName FROM lead_status WHERE statusName = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, statusName);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println("[LeadStatusDAO] Gagal findByName(): " + e.getMessage());
        }
        return null;
    }

    /**
     * Menyimpan status lead baru ke database (INSERT).
     * id pada objek leadStatus akan diisi otomatis dengan id hasil
     * generate dari database setelah berhasil disimpan.
     *
     * @param leadStatus objek LeadStatus yang akan disimpan (tanpa id).
     * @return true jika berhasil, false jika gagal.
     */
    public boolean save(LeadStatus leadStatus) {
        String sql = "INSERT INTO lead_status (statusName) VALUES (?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, leadStatus.getStatusName());

            int baris = ps.executeUpdate();
            if (baris > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        leadStatus.setId(keys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.out.println("[LeadStatusDAO] Gagal save(): " + e.getMessage());
        }
        return false;
    }

    /**
     * Memperbarui nama status yang sudah ada (UPDATE berdasarkan id).
     *
     * @param leadStatus objek LeadStatus dengan id yang valid.
     * @return true jika berhasil, false jika gagal.
     */
    public boolean update(LeadStatus leadStatus) {
        String sql = "UPDATE lead_status SET statusName = ? WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, leadStatus.getStatusName());
            ps.setInt(2, leadStatus.getId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("[LeadStatusDAO] Gagal update(): " + e.getMessage());
            return false;
        }
    }

    /**
     * Menghapus status lead berdasarkan id.
     *
     * Catatan: kolom `leads.statusId` memiliki ON DELETE SET NULL,
     * sehingga Lead yang sebelumnya mengacu ke status ini akan
     * memiliki statusId = NULL (Lead tetap ada, sesuai relasi
     * asosiasi pada class diagram).
     *
     * @param id id status yang akan dihapus.
     * @return true jika berhasil, false jika gagal.
     */
    public boolean delete(int id) {
        String sql = "DELETE FROM lead_status WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("[LeadStatusDAO] Gagal delete(): " + e.getMessage());
            return false;
        }
    }

    // ===== Helper =====

    /**
     * Mengubah satu baris ResultSet menjadi objek LeadStatus.
     */
    private LeadStatus mapRow(ResultSet rs) throws SQLException {
        LeadStatus leadStatus = new LeadStatus();
        leadStatus.setId(rs.getInt("id"));
        leadStatus.setStatusName(rs.getString("statusName"));
        return leadStatus;
    }
}