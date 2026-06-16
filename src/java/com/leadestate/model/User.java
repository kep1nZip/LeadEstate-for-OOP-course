package com.leadestate.model;
import com.leadestate.dao.UserDAO;
import java.util.ArrayList;
import java.util.List;

/**
 * Kelas abstrak User.
 *
 * Merepresentasikan pengguna sistem LeadEstate secara umum.
 * Kelas ini akan diturunkan (extends) oleh kelas Admin dan Sales
 * (Supervisor tidak dipakai sesuai kesepakatan tim).
 *
 * Relasi sesuai class diagram:
 * - User 1 -- 1..* Lead (ASOSIASI)
 *   Satu User (khususnya Sales) dapat menangani banyak Lead.
 * - User 1 -- 1..* FollowUp (ASOSIASI)
 *   Satu User dapat membuat/mengelola banyak FollowUp.
 * - User 1 -- 1..* Notifikasi (ASOSIASI)
 *   Satu User dapat menerima banyak Notifikasi.
 *
 * Catatan relasi asosiasi di atas:
 * Untuk tahap pembuatan POJO ini, relasi-relasi tersebut belum
 * direalisasikan sebagai field referensi langsung ke List<Lead>,
 * List<FollowUp>, atau List<Notifikasi> di dalam User. Alasannya,
 * relasi ini bersifat asosiasi (bukan komposisi) dan datanya akan
 * jauh lebih tepat diambil melalui DAO/Service berdasarkan
 * userId/salesId (misalnya LeadDAO.findBySalesId(this.id)) daripada
 * disimpan langsung sebagai field di objek User. Method
 * viewLeads(), viewReminders(), dan viewDashboard() di bawah ini
 * menjadi tempat pemanggilan DAO tersebut nantinya.
 */
public abstract class User {

    // ===== Atribut (sesuai class diagram) =====
    private int id;
    private String name;
    private String email;
    private String password;
    private int roleId;

    // ===== Constructor =====

    /**
     * Constructor default.
     */
    public User() {
    }

    /**
     * Constructor dengan parameter lengkap.
     * Biasanya dipakai saat membentuk objek User dari data
     * yang sudah ada (misal hasil baca dari database).
     *
     * @param id       id user.
     * @param name     nama user.
     * @param email    email user.
     * @param password password user.
     * @param roleId   id role user (menentukan jenis turunan: Admin/Sales).
     */
    public User(int id, String name, String email, String password, int roleId) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.roleId = roleId;
    }

    /**
     * Constructor tanpa id.
     * Dipakai saat membuat User baru sebelum disimpan ke database,
     * id akan digenerate oleh DAO/Database.
     *
     * @param name     nama user.
     * @param email    email user.
     * @param password password user.
     * @param roleId   id role user.
     */
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

    // ===== Method sesuai class diagram =====

    /**
     * Melakukan validasi login berdasarkan email dan password.
     *
     * TODO: Saat DAO/Database tersedia, ganti pengecekan ini dengan
     *       UserDAO.findByEmail(email) lalu bandingkan password
     *       (sebaiknya menggunakan hashing, misal BCrypt).
     *
     * @param email    email yang diinput pada form login.
     * @param password password yang diinput pada form login.
     * @return true jika email dan password cocok dengan data User ini,
     *         false jika tidak cocok.
     */
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

    /**
     * Melakukan logout untuk user ini.
     *
     * TODO: Saat Service/Session tersedia, gunakan method ini untuk
     *       menghapus/invalidate session yang sedang aktif.
     */
    public void logout() {
        System.out.println("[User] " + this.name + " telah logout.");
    }

    /**
     * Mendaftarkan data diri user (nama, email, password) ke dalam
     * objek User ini.
     *
     * TODO: Saat DAO/Database tersedia, tambahkan pemanggilan
     *       UserDAO.save(this) agar data tersimpan ke database, dan
     *       lakukan validasi/cek duplikasi email sebelum menyimpan.
     *
     * @param name     nama lengkap user.
     * @param email    email user.
     * @param password password user.
     */
    public void register(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
        System.out.println("[User] Registrasi berhasil untuk: " + this.email);

        // TODO: Ganti println di atas dengan UserDAO.save(this)
        //       saat lapisan DAO sudah tersedia.
    }

    /**
     * Mereset password user berdasarkan email yang diberikan.
     *
     * TODO: Saat DAO/Database & Service tersedia, alur sebenarnya:
     *       1) cari User berdasarkan email,
     *       2) generate OTP 6 digit (berlaku 5 menit),
     *       3) verifikasi OTP,
     *       4) update password baru ke database.
     *       Implementasi di bawah ini hanya placeholder sederhana.
     *
     * @param email email user yang ingin reset password.
     */
 /**
 * Mereset password user berdasarkan email yang diberikan.
 *
 * Implementasi sederhana tanpa database tambahan:
 * 1. Validasi email
 * 2. Generate OTP random 6 digit
 * 3. Tampilkan OTP ke console
 * 4. Return OTP untuk diverifikasi
 *
 * @param email email user yang ingin reset password
 * @return OTP jika email valid, null jika email tidak ditemukan
 */
public String resetPassword(String email) {

    if (email == null || email.trim().isEmpty()) {
        System.out.println("[User] Email tidak boleh kosong.");
        return null;
    }

    if (!email.equalsIgnoreCase(this.email)) {
        System.out.println("[User] Email tidak ditemukan: " + email);
        return null;
    }

    // Generate OTP random 6 digit
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

/**
 * Verifikasi OTP yang dimasukkan user.
 *
 * @param inputOtp OTP yang dimasukkan user
 * @param systemOtp OTP yang dihasilkan sistem
 * @return true jika OTP cocok
 */
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

/**
 * Mengubah password user dan menyimpannya ke database.
 *
 * @param newPassword password baru
 * @return true jika berhasil
 */
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

        // TODO: Ganti dengan UserDAO.update(this) saat DAO sudah dibuat.
    }

    /**
     * Menampilkan dashboard sesuai role user yang login.
     *
     * TODO: Saat Service tersedia, method ini akan mengambil data KPI
     *       (jumlah Lead aktif, FollowUp hari ini, dst) sesuai
     *       getAccessLevel() dari user ini.
     */
    public void viewDashboard() {
        System.out.println("[User] Menampilkan dashboard untuk: " + this.name
                + " (Role: " + getRoleName() + ")");
    }

    /**
     * Mengambil daftar Reminder yang terkait dengan user ini.
     *
     * TODO: Saat DAO/Database tersedia, ganti dengan
     *       ReminderDAO.findByUserId(this.id). Untuk sementara
     *       mengembalikan list kosong sebagai placeholder.
     *
     * @return List berisi Reminder milik user ini.
     */
    public List<Reminder> viewReminders() {
        System.out.println("[User] Mengambil daftar reminder untuk: " + this.name);
        // Placeholder: belum ada DAO, kembalikan list kosong.
        return new ArrayList<>();
    }

    /**
     * Mengambil daftar Lead yang terkait dengan user ini.
     *
     * TODO: Saat DAO/Database tersedia, ganti dengan
     *       LeadDAO.findByUserId(this.id). Untuk sementara
     *       mengembalikan list kosong sebagai placeholder.
     *
     * @return List berisi Lead yang terkait dengan user ini.
     */
    public List<Lead> viewLeads() {
        System.out.println("[User] Mengambil daftar lead untuk: " + this.name);
        // Placeholder: belum ada DAO, kembalikan list kosong.
        return new ArrayList<>();
    }

    /**
     * Mengambil nama role dari user ini (misal "Admin" atau "Sales").
     *
     * Method ini bersifat abstract karena nilai/nama role berbeda
     * untuk setiap subclass, dan wajib diimplementasikan oleh
     * masing-masing subclass (Admin, Sales).
     *
     * @return String nama role user.
     */
    public abstract String getRoleName();

    /**
     * Mengambil level akses dari user ini dalam bentuk angka.
     * Semakin tinggi nilainya, semakin tinggi hak aksesnya.
     *
     * Method ini bersifat abstract karena nilai level akses berbeda
     * untuk setiap subclass, dan wajib diimplementasikan oleh
     * masing-masing subclass (Admin, Sales).
     *
     * @return int level akses user.
     */
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
