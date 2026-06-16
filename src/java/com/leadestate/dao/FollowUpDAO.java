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

public class FollowUpDAO {

    // READ

    public List<FollowUp> findAll() {
        List<FollowUp> daftar = new ArrayList<>();
        String sql = "SELECT f.id, f.leadId, f.salesId, f.notes, f.followupDate, f.status, l.name AS leadName "
           + "FROM followups f LEFT JOIN leads l ON f.leadId = l.id";

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

    public FollowUp findById(int id) {
        String sql = "SELECT f.id, f.leadId, f.salesId, f.notes, f.followupDate, f.status, l.name AS leadName "
           + "FROM followups f LEFT JOIN leads l ON f.leadId = l.id WHERE f.id = ?";

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

    public List<FollowUp> findByLeadId(int leadId) {
        List<FollowUp> daftar = new ArrayList<>();
        String sql = "SELECT f.id, f.leadId, f.salesId, f.notes, f.followupDate, f.status, l.name AS leadName "
           + "FROM followups f LEFT JOIN leads l ON f.leadId = l.id WHERE f.leadId = ? ORDER BY f.followupDate DESC";

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

    public List<FollowUp> findBySalesId(int salesId) {
        List<FollowUp> daftar = new ArrayList<>();
        String sql = "SELECT f.id, f.leadId, f.salesId, f.notes, f.followupDate, f.status, l.name AS leadName "
           + "FROM followups f LEFT JOIN leads l ON f.leadId = l.id WHERE f.salesId = ? ORDER BY f.followupDate ASC";

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

    public List<FollowUp> findHariIniOlehSales(int salesId) {
        List<FollowUp> daftar = new ArrayList<>();
        String sql = "SELECT f.id, f.leadId, f.salesId, f.notes, f.followupDate, f.status, l.name AS leadName "
           + "FROM followups f LEFT JOIN leads l ON f.leadId = l.id "
           + "WHERE f.salesId = ? AND DATE(f.followupDate) = CURDATE() AND f.status = 'Pending' ORDER BY f.followupDate ASC";

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

    public List<FollowUp> findTertundaOlehSales(int salesId) {
        List<FollowUp> daftar = new ArrayList<>();
        String sql = "SELECT f.id, f.leadId, f.salesId, f.notes, f.followupDate, f.status, l.name AS leadName "
           + "FROM followups f LEFT JOIN leads l ON f.leadId = l.id "
           + "WHERE f.salesId = ? AND f.followupDate < NOW() AND f.status = 'Pending' ORDER BY f.followupDate ASC";

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

    // CREATE

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

    // UPDATE

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

    // DELETE

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

    // HELPER (private)

    private FollowUp mapRow(ResultSet rs) throws SQLException {
        FollowUp f = new FollowUp();
        f.setId(rs.getInt("id"));
        f.setLeadId(rs.getInt("leadId"));
        f.setSalesId(rs.getInt("salesId"));
        f.setNotes(rs.getString("notes"));
        Timestamp ts = rs.getTimestamp("followupDate");
        f.setFollowupDate(ts != null ? new java.util.Date(ts.getTime()) : null);
        f.setStatus(rs.getString("status"));
        
        // --- BARIS TAMBAHAN UNTUK NAMA LEAD ---
        try {
            f.setLeadName(rs.getString("leadName"));
        } catch (SQLException e) { /* Abaikan jika kolom tidak ada */ }
        
        return f;
    }

    private Timestamp toTimestamp(java.util.Date date) {
        return date != null ? new Timestamp(date.getTime()) : null;
    }
}
