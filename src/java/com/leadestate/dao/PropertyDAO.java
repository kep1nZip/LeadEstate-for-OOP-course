package com.leadestate.dao;

import com.leadestate.model.Property;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO (Data Access Object) untuk kelas Property.
 *
 * Bertugas menjembatani objek Property (model) dengan tabel `properties`
 * pada database (lihat leadestate_v2.sql).
 *
 * Kolom tabel properties: id, name, location, price.
 */
public class PropertyDAO {

    /**
     * Mengambil seluruh data properti dari database.
     * Berguna untuk dropdown pilihan properti saat menambah/mengedit Lead.
     *
     * @return List berisi semua Property.
     */
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

    /**
     * Mencari satu properti berdasarkan id.
     * Dipakai untuk menampilkan nama/lokasi/harga properti pada
     * detail Lead (relasi agregasi Lead -> Property).
     *
     * @param id id properti.
     * @return objek Property jika ditemukan, atau null jika tidak ada.
     */
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

    /**
     * Menyimpan properti baru ke database (INSERT).
     * id pada objek property akan diisi otomatis dengan id hasil generate
     * dari database setelah berhasil disimpan.
     *
     * @param property objek Property yang akan disimpan (tanpa id).
     * @return true jika berhasil, false jika gagal.
     */
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

    /**
     * Memperbarui seluruh data properti yang sudah ada (UPDATE berdasarkan id).
     *
     * @param property objek Property dengan id yang valid.
     * @return true jika berhasil, false jika gagal.
     */
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

    /**
     * Memperbarui hanya kolom price sebuah properti.
     * Dipakai oleh Property.updatePrice(newPrice) agar tidak perlu
     * mengirim seluruh data properti hanya untuk mengubah harga.
     *
     * @param propertyId id properti yang harganya akan diubah.
     * @param newPrice   harga baru.
     * @return true jika berhasil, false jika gagal.
     */
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

    /**
     * Menghapus properti berdasarkan id.
     *
     * Catatan: kolom `leads.propertyId` memiliki ON DELETE SET NULL,
     * sehingga Lead yang sebelumnya mengacu ke properti ini akan
     * memiliki propertyId = NULL (Lead tetap ada, sesuai relasi
     * agregasi pada class diagram).
     *
     * @param id id properti yang akan dihapus.
     * @return true jika berhasil, false jika gagal.
     */
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

    /**
     * Mengubah satu baris ResultSet menjadi objek Property.
     */
    private Property mapRow(ResultSet rs) throws SQLException {
        Property property = new Property();
        property.setId(rs.getInt("id"));
        property.setName(rs.getString("name"));
        property.setLocation(rs.getString("location"));
        property.setPrice(rs.getFloat("price"));
        return property;
    }
}