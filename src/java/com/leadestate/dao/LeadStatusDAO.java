package com.leadestate.dao;

import com.leadestate.model.LeadStatus;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LeadStatusDAO {

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

    private LeadStatus mapRow(ResultSet rs) throws SQLException {
        LeadStatus leadStatus = new LeadStatus();
        leadStatus.setId(rs.getInt("id"));
        leadStatus.setStatusName(rs.getString("statusName"));
        return leadStatus;
    }
}