package com.leadestate.dao;

import com.leadestate.model.Lead;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.sql.Timestamp;

/**
 * DAO (Data Access Object) untuk kelas Lead.
 *
 * Bertugas menjembatani objek Lead (model) dengan tabel `leads`
 * pada database (lihat leadestate_v2.sql).
 *
 * Kolom tabel leads: id, name, phone, email, propertyId, salesId, statusId, source.
 *
 * Catatan: List<FollowUp> (daftarFollowUp) pada objek Lead TIDAK diisi oleh
 * DAO ini. Untuk mengisinya, gunakan FollowUpDAO.findByLeadId(lead.getId())
 * lalu lead.setDaftarFollowUp(...) — DAO tersebut dibuat oleh anggota tim
 * yang menangani fitur Reminder & Follow-up.
 */
public class LeadDAO {

    /**
     * Mengambil seluruh data lead dari database.
     *
     * @return List berisi semua Lead.
     */
    public List<Lead> findAll() {
        List<Lead> daftar = new ArrayList<>();
        String sql = "SELECT id, name, phone, email, propertyId, salesId, statusId, source, created_at FROM leads";;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                daftar.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.out.println("[LeadDAO] Gagal findAll(): " + e.getMessage());
        }
        return daftar;
    }

    /**
     * Mencari satu lead berdasarkan id.
     *
     * @param id id lead.
     * @return objek Lead jika ditemukan, atau null jika tidak ada.
     */
    public Lead findById(int id) {
        String sql = "SELECT id, name, phone, email, propertyId, salesId, statusId, source, created_at "
                + "FROM leads WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println("[LeadDAO] Gagal findById(): " + e.getMessage());
        }
        return null;
    }

    /**
     * Mencari semua lead yang ditangani oleh Sales tertentu.
     * Dipakai oleh Sales.viewMyLeads() / User.viewLeads().
     *
     * @param salesId id Sales.
     * @return List berisi Lead milik Sales tersebut.
     */
    public List<Lead> findBySalesId(int salesId) {
        List<Lead> daftar = new ArrayList<>();
        String sql = "SELECT id, name, phone, email, propertyId, salesId, statusId, source, created_at "
                + "FROM leads WHERE salesId = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, salesId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    daftar.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("[LeadDAO] Gagal findBySalesId(): " + e.getMessage());
        }
        return daftar;
    }

    /**
     * Mencari semua lead dengan status tertentu.
     * Berguna untuk halaman Data Lead (filter berdasarkan status)
     * dan halaman Laporan & Statistik (hitung jumlah per status).
     *
     * @param statusId id LeadStatus.
     * @return List berisi Lead dengan statusId tersebut.
     */
    public List<Lead> findByStatusId(int statusId) {
        List<Lead> daftar = new ArrayList<>();
        String sql = "SELECT id, name, phone, email, propertyId, salesId, statusId, source, created_at "
                + "FROM leads WHERE statusId = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, statusId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    daftar.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("[LeadDAO] Gagal findByStatusId(): " + e.getMessage());
        }
        return daftar;
    }

    /**
     * Menyimpan lead baru ke database (INSERT).
     * id pada objek lead akan diisi otomatis dengan id hasil generate
     * dari database setelah berhasil disimpan.
     *
     * Dipanggil oleh Lead.save() setelah validateDate() bernilai true.
     *
     * @param lead objek Lead yang akan disimpan (tanpa id).
     * @return true jika berhasil, false jika gagal.
     */
    public boolean save(Lead lead) {
        String sql = "INSERT INTO leads (name, phone, email, propertyId, salesId, statusId, source) "
                + "VALUES (?, ?, ?, ?, ?, ?,?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, lead.getName());
            ps.setString(2, lead.getPhone());
            ps.setString(3, lead.getEmail());
            ps.setInt(4, lead.getPropertyId());
            ps.setInt(5, lead.getSalesId());
            ps.setInt(6, lead.getStatusId());
            ps.setString(7, lead.getSource());

            int baris = ps.executeUpdate();
            if (baris > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        lead.setId(keys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.out.println("[LeadDAO] Gagal save(): " + e.getMessage());
        }
        return false;
    }

    /**
     * Memperbarui seluruh data lead yang sudah ada (UPDATE berdasarkan id).
     *
     * @param lead objek Lead dengan id yang valid.
     * @return true jika berhasil, false jika gagal.
     */
    public boolean update(Lead lead) {
        String sql = "UPDATE leads SET name = ?, phone = ?, email = ?, propertyId = ?, "
                + "salesId = ?, statusId = ?, source = ?, created_at = ? WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, lead.getName());
            ps.setString(2, lead.getPhone());
            ps.setString(3, lead.getEmail());
            ps.setInt(4, lead.getPropertyId());
            ps.setInt(5, lead.getSalesId());
            ps.setInt(6, lead.getStatusId());
            ps.setString(7, lead.getSource());
            ps.setTimestamp(8, lead.getCreatedAt() != null
                    ? new java.sql.Timestamp(lead.getCreatedAt().getTime()) : null);
            ps.setInt(9, lead.getId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("[LeadDAO] Gagal update(): " + e.getMessage());
            return false;
        }
    }

    /**
     * Memperbarui hanya kolom statusId sebuah lead.
     * Dipakai oleh Lead.changeStatus(newStatusId) agar tidak perlu
     * mengirim seluruh data lead hanya untuk mengubah status.
     *
     * @param leadId      id lead yang statusnya akan diubah.
     * @param newStatusId id status baru.
     * @return true jika berhasil, false jika gagal.
     */
    public boolean updateStatus(int leadId, int newStatusId) {
        String sql = "UPDATE leads SET statusId = ? WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, newStatusId);
            ps.setInt(2, leadId);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("[LeadDAO] Gagal updateStatus(): " + e.getMessage());
            return false;
        }
    }

    /**
     * Menghapus lead berdasarkan id.
     * Karena `followups.leadId` memiliki ON DELETE CASCADE, seluruh
     * FollowUp (dan Reminder di dalamnya) milik lead ini akan ikut terhapus
     * otomatis oleh database.
     *
     * @param id id lead yang akan dihapus.
     * @return true jika berhasil, false jika gagal.
     */
    public boolean delete(int id) {
        String sql = "DELETE FROM leads WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("[LeadDAO] Gagal delete(): " + e.getMessage());
            return false;
        }
    }

    // ===== Helper =====

    /**
     * Mengubah satu baris ResultSet menjadi objek Lead.
     */
    private Lead mapRow(ResultSet rs) throws SQLException {
        Lead lead = new Lead();
        java.sql.Timestamp ts = rs.getTimestamp("created_at");
        lead.setId(rs.getInt("id"));
        lead.setName(rs.getString("name"));
        lead.setPhone(rs.getString("phone"));
        lead.setEmail(rs.getString("email"));
        lead.setPropertyId(rs.getInt("propertyId"));
        lead.setSalesId(rs.getInt("salesId"));
        lead.setStatusId(rs.getInt("statusId"));
        lead.setSource(rs.getString("source"));
       
        if (ts != null) {
        lead.setCreatedAt(new java.util.Date(ts.getTime())); // Konversi aman ke util.Date
            } else {
        lead.setCreatedAt(null);
             }
        return lead;
    }
}