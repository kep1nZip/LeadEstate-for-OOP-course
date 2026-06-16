package com.leadestate.model;
import com.leadestate.dao.UserDAO;
import java.util.ArrayList;
import java.util.List;

public abstract class User {

    private int id;
    private String name;
    private String email;
    private String password;
    private int roleId;

    public User() {
    }

    public User(int id, String name, String email, String password, int roleId) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.roleId = roleId;
    }

    public User(String name, String email, String password, int roleId) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.roleId = roleId;
    }

    // ===== Getter & Setter =====
    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getRoleId() {
        return this.roleId;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }

    // ===== Method =====
    public boolean login(String email, String password) {
        if (email == null || password == null) {
            return false;
        }
        boolean emailCocok = email.equalsIgnoreCase(this.email);
        boolean passwordCocok = password.equals(this.password);

        if (emailCocok && passwordCocok) {
            System.out.println("[User] Login berhasil untuk: " + this.email);
            return true;
        }
        System.out.println("[User] Login gagal untuk email: " + email);
        return false;
    }

    public void logout() {
        System.out.println("[User] " + this.name + " telah logout.");
    }

    public void register(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
        System.out.println("[User] Registrasi berhasil untuk: " + this.email);

    }

    
public String resetPassword(String email) {

    if (email == null || email.trim().isEmpty()) {
        System.out.println("[User] Email tidak boleh kosong.");
        return null;
    }

    if (!email.equalsIgnoreCase(this.email)) {
        System.out.println("[User] Email tidak ditemukan: " + email);
        return null;
    }

    int otpNumber = (int) (Math.random() * 900000) + 100000;

    String otp = String.valueOf(otpNumber);

    System.out.println("====================================");
    System.out.println("       RESET PASSWORD OTP");
    System.out.println("====================================");
    System.out.println("Nama  : " + this.name);
    System.out.println("Email : " + this.email);
    System.out.println("OTP   : " + otp);
    System.out.println("====================================");

    return otp;
}

public boolean verifyOtp(String inputOtp, String systemOtp) {

    if (inputOtp == null || systemOtp == null) {
        return false;
    }

    boolean valid = inputOtp.equals(systemOtp);

    if (valid) {
        System.out.println("[User] OTP berhasil diverifikasi.");
    } else {
        System.out.println("[User] OTP tidak valid.");
    }

    return valid;
}

public boolean changePassword(String newPassword) {

    if (newPassword == null
            || newPassword.trim().isEmpty()) {

        return false;
    }

    this.password = newPassword;

    UserDAO dao =
            new UserDAO();

    return dao.update(this);
}
    public void updateProfile(String name, String email) {
        if (name != null && !name.trim().isEmpty()) {
            this.name = name;
        }
        if (email != null && !email.trim().isEmpty()) {
            this.email = email;
        }
        System.out.println("[User] Profil " + this.name + " berhasil diperbarui.");

    }

    public void viewDashboard() {
        System.out.println("[User] Menampilkan dashboard untuk: " + this.name
                + " (Role: " + getRoleName() + ")");
    }

    public List<Reminder> viewReminders() {
        System.out.println("[User] Mengambil daftar reminder untuk: " + this.name);

        return new ArrayList<>();
    }

    public List<Lead> viewLeads() {
        System.out.println("[User] Mengambil daftar lead untuk: " + this.name);
        // Placeholder: belum ada DAO, kembalikan list kosong.
        return new ArrayList<>();
    }

    public abstract String getRoleName();
    
    public abstract int getAccessLevel();

    // ===== Override toString() =====
    @Override
    public String toString() {
        return "User{"
                + "id=" + this.id
                + ", name='" + this.name + "'"
                + ", email='" + this.email + "'"
                + ", roleId=" + this.roleId
                + ", roleName='" + getRoleName() + "'"
                + "}";
    }
}
