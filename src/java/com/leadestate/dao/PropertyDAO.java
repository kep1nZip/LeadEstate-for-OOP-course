package com.leadestate.dao;

import com.leadestate.model.Property;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;


public class PropertyDAO {

    public List<Property> findAll() {
        List<Property> daftar = new ArrayList<>();
        String sql = "SELECT id, name, location, price FROM properties";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                daftar.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.out.println("[PropertyDAO] Gagal findAll(): " + e.getMessage());
        }
        return daftar;
    }

    public Property findById(int id) {
        String sql = "SELECT id, name, location, price FROM properties WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println("[PropertyDAO] Gagal findById(): " + e.getMessage());
        }
        return null;
    }

    public boolean save(Property property) {
        String sql = "INSERT INTO properties (name, location, price) VALUES (?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, property.getName());
            ps.setString(2, property.getLocation());
            ps.setFloat(3, property.getPrice());

            int baris = ps.executeUpdate();
            if (baris > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        property.setId(keys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.out.println("[PropertyDAO] Gagal save(): " + e.getMessage());
        }
        return false;
    }

    public boolean update(Property property) {
        String sql = "UPDATE properties SET name = ?, location = ?, price = ? WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, property.getName());
            ps.setString(2, property.getLocation());
            ps.setFloat(3, property.getPrice());
            ps.setInt(4, property.getId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("[PropertyDAO] Gagal update(): " + e.getMessage());
            return false;
        }
    }

    public boolean updatePrice(int propertyId, double newPrice) {
        String sql = "UPDATE properties SET price = ? WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setFloat(1, (float) newPrice);
            ps.setInt(2, propertyId);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("[PropertyDAO] Gagal updatePrice(): " + e.getMessage());
            return false;
        }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM properties WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("[PropertyDAO] Gagal delete(): " + e.getMessage());
            return false;
        }
    }

    // ===== Helper =====

    private Property mapRow(ResultSet rs) throws SQLException {
        Property property = new Property();
        property.setId(rs.getInt("id"));
        property.setName(rs.getString("name"));
        property.setLocation(rs.getString("location"));
        property.setPrice(rs.getFloat("price"));
        return property;
    }
}