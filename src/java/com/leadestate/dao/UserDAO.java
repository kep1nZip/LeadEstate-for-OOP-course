package com.leadestate.dao;

import com.leadestate.model.Admin;
import com.leadestate.model.Sales;
import com.leadestate.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    // FIND ALL
    public List<User> findAll() {
        List<User> users = new ArrayList<>();

        String sql = """
                SELECT u.*, r.role_name
                FROM users u
                LEFT JOIN roles r ON u.roleId = r.id
                ORDER BY u.id
                """;

        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()
        ) {

            while (rs.next()) {
                users.add(mapUser(rs));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return users;
    }

    // FIND BY ID
    public User findById(int id) {

        String sql = """
                SELECT u.*, r.role_name
                FROM users u
                LEFT JOIN roles r ON u.roleId = r.id
                WHERE u.id = ?
                """;

        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapUser(rs);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    // FIND BY EMAIL
public User findByEmail(String email) {

    System.out.println("MENCARI EMAIL = [" + email + "]");

    String sql =
        "SELECT * FROM users WHERE email = ?";

    try (
        Connection conn = DBConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)
    ) {

        ps.setString(1, email);

        ResultSet rs = ps.executeQuery();

        if(rs.next()) {
            System.out.println("EMAIL DITEMUKAN!");
            return mapUser(rs);
        }

        System.out.println("EMAIL TIDAK ADA DI DATABASE");

    } catch(Exception e) {
        e.printStackTrace();
    }

    return null;
}

    // FIND BY ROLE
    public List<User> findByRole(String roleName) {

        List<User> users = new ArrayList<>();

        String sql = """
                SELECT u.*, r.role_name
                FROM users u
                JOIN roles r ON u.roleId = r.id
                WHERE r.role_name = ?
                """;

        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setString(1, roleName);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    users.add(mapUser(rs));
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return users;
    }

    // CEK EMAIL
    public boolean isEmailExist(String email) {
        return findByEmail(email) != null;
    }

    // SAVE USER
    public void save(User user) {

        String sql = """
                INSERT INTO users
                (name,email,password,roleId)
                VALUES (?,?,?,?)
                """;

        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps =
                        conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
        ) {

            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPassword());
            ps.setInt(4, user.getRoleId());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    user.setId(rs.getInt(1));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // UPDATE USER
    public boolean update(User user) {

        String sql = """
                UPDATE users
                SET name=?,
                    email=?,
                    password=?,
                    roleId=?
                WHERE id=?
                """;

        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPassword());
            ps.setInt(4, user.getRoleId());
            ps.setInt(5, user.getId());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    // DELETE USER
    public boolean delete(int id) {

        String sql = "DELETE FROM users WHERE id=?";

        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setInt(1, id);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    // MAPPER RESULTSET -> USER
    private User mapUser(ResultSet rs) throws SQLException {

        int id = rs.getInt("id");
        String name = rs.getString("name");
        String email = rs.getString("email");
        String password = rs.getString("password");
        int roleId = rs.getInt("roleId");

        if (roleId == 1) {
            return new Admin(id, name, email, password);
        } else {
            return new Sales(id, name, email, password);
        }
    }
    
    
// UPDATE PASSWORD
    
public boolean updatePassword(String email, String newPassword) {

    String sql =
        "UPDATE users SET password=? WHERE email=?";

    try (
        Connection conn = DBConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)
    ) {

        ps.setString(1, newPassword);
        ps.setString(2, email);

        return ps.executeUpdate() > 0;

    } catch (Exception e) {
        e.printStackTrace();
    }

    return false;
}
}