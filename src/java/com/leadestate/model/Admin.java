package com.leadestate.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Kelas Admin.
 *
 * Merepresentasikan pengguna dengan role Admin pada sistem LeadEstate.
 * Admin merupakan turunan (extends) dari User karena mewarisi seluruh
 * atribut dan method dasar User (id, name, email, password, roleId,
 * login(), logout(), register(), dst), serta menambahkan
 * method-method khusus untuk pengelolaan sistem secara penuh.
 *
 * Admin memiliki level akses tertinggi (akses penuh ke seluruh data:
 * User, Lead, Property, Reminder, FollowUp, Notifikasi, dan laporan).
 *
 * Catatan: Sesuai kesepakatan tim, kelas Supervisor tidak dipakai,
 * sehingga User hanya memiliki dua turunan: Admin dan Sales.
 */
public class Admin extends User {

    // ===== Konstanta level akses =====

    /**
     * Level akses Admin. Admin = akses tertinggi dalam sistem,
     * sehingga diberi nilai paling besar dibanding role lain (Sales).
     *
     * Catatan: ACCESS_LEVEL adalah nilai LOGIKA BISNIS (ranking hak akses),
     * TIDAK SAMA dengan roleId pada database. Lihat ROLE_ID di bawah.
     */
    public static final int ACCESS_LEVEL = 3;

    /**
     * ID role Admin pada tabel `roles` di database (roles.id = 1 untuk Admin,
     * sesuai gemini-code-1781355996980.sql). Nilai ini dipakai sebagai
     * roleId pada User, BUKAN ACCESS_LEVEL.
     */
    public static final int ROLE_ID = 1;

    /** Nama role untuk Admin, sesuai dengan penamaan pada class diagram. */
    public static final String ROLE_NAME = "Admin";

    /*
     * Penyimpanan sementara di memori (in-memory) untuk data yang
     * diarsipkan oleh Admin melalui archiveData().
     *
     * TODO: Ganti dengan tabel/log arsip pada database saat
     * DAO/Database sudah tersedia.
     */
    private static List<String> logArsip = new ArrayList<>();

    // ===== Constructor =====

    /**
     * Constructor sesuai class diagram: membentuk objek Admin dengan
     * id, name, email, dan password. roleId di-set otomatis sesuai
     * ROLE_ID Admin (roles.id = 1 pada database), TIDAK memakai
     * ACCESS_LEVEL.
     *
     * @param id       id user.
     * @param name     nama Admin.
     * @param email    email Admin.
     * @param password password Admin.
     */
    public Admin(int id, String name, String email, String password) {
        super(id, name, email, password, ROLE_ID);
    }

    // ===== Method sesuai class diagram =====

    /**
     * Mengelola data User dalam sistem (melihat, menambah, mengedit,
     * atau menghapus akun User baik Admin maupun Sales).
     *
     * TODO: Saat DAO/Database tersedia, implementasikan operasi CRUD
     *       sesungguhnya melalui UserDAO (misal UserDAO.findAll(),
     *       UserDAO.save(), UserDAO.update(), UserDAO.delete()).
     */
    public void manageUsers() {
        System.out.println("[Admin] " + this.getName()
                + " membuka halaman manajemen User.");

        // TODO: Ganti println di atas dengan pemanggilan UserDAO
        //       (findAll/save/update/delete) saat DAO sudah dibuat.
    }

    /**
     * Mengelola data Property dalam sistem (melihat, menambah,
     * mengedit, atau menghapus data properti).
     *
     * TODO: Saat DAO/Database tersedia, implementasikan operasi CRUD
     *       sesungguhnya melalui PropertyDAO.
     */
    public void manageProperties() {
        System.out.println("[Admin] " + this.getName()
                + " membuka halaman manajemen Property.");

        // TODO: Ganti println di atas dengan pemanggilan PropertyDAO
        //       (findAll/save/update/delete) saat DAO sudah dibuat.
    }

    /**
     * Menampilkan seluruh laporan yang tersedia dalam sistem
     * (laporan closing rate, performa Sales, statistik Lead, dsb).
     *
     * TODO: Saat DAO/Service tersedia, ganti placeholder Report ini
     *       dengan data agregasi sesungguhnya, misalnya hasil query
     *       LeadDAO dan FollowUpDAO yang dikelompokkan per bulan.
     *
     * @return objek Report berisi ringkasan seluruh laporan sistem.
     */
    public Report viewAllReports() {
        System.out.println("[Admin] " + this.getName()
                + " menampilkan seluruh laporan sistem.");

        Report report = new Report(
                "Laporan Keseluruhan Sistem",
                "Ringkasan laporan untuk seluruh Lead, Sales, dan closing rate."
        );

        // TODO: Isi report.setData(...) dengan hasil agregasi
        //       LeadDAO/FollowUpDAO saat DAO sudah dibuat.
        return report;
    }

    /**
     * Menugaskan (assign) sebuah Lead kepada Sales tertentu.
     *
     * TODO: Saat DAO/Database tersedia, ganti implementasi ini dengan:
     *       1) Lead lead = LeadDAO.findById(leadId);
     *       2) lead.setSalesId(salesId);
     *       3) LeadDAO.update(lead);
     *
     * @param leadId  id Lead yang akan ditugaskan.
     * @param salesId id Sales yang akan menangani Lead tersebut.
     */
    public void assignLeadToSales(int leadId, int salesId) {
        System.out.println("[Admin] Lead id=" + leadId
                + " ditugaskan ke Sales id=" + salesId + ".");

        // TODO: Ganti println di atas dengan logika update
        //       lead.setSalesId(salesId) via LeadDAO saat DAO sudah dibuat.
    }

    /**
     * Mengarsipkan sebuah data (entity) berdasarkan jenis entity dan id-nya.
     *
     * Parameter entity berupa String yang merepresentasikan nama/jenis
     * entity yang ingin diarsipkan, misalnya "Lead", "User", atau
     * "FollowUp". Pendekatan ini dipilih untuk tahap POJO karena belum
     * ada DAO/Service yang menyatukan berbagai jenis entity ke dalam
     * satu tipe data umum.
     *
     * TODO: Saat DAO/Database tersedia, gunakan parameter entity untuk
     *       menentukan DAO mana yang dipanggil, misalnya:
     *       if (entity.equalsIgnoreCase("Lead")) { LeadDAO.archive(id); }
     *
     * @param entity jenis entity yang akan diarsipkan (contoh: "Lead").
     * @param id     id dari entity yang akan diarsipkan.
     */
    public void archiveData(String entity, int id) {
        String catatan = "[Arsip] Entity='" + entity + "', id=" + id
                + ", oleh Admin='" + this.getName() + "'";
        logArsip.add(catatan);
        System.out.println("[Admin] " + catatan);

        // TODO: Ganti penyimpanan in-memory (logArsip) di atas dengan
        //       pemanggilan DAO arsip yang sesuai saat DAO sudah dibuat.
    }

    /**
     * Mengambil seluruh log arsip yang tersimpan di memori.
     * Berguna untuk pengujian sebelum DAO/Database tersedia.
     *
     * @return List berisi catatan arsip dalam bentuk String.
     */
    public static List<String> getLogArsip() {
        return logArsip;
    }

    // ===== Implementasi method abstract dari User =====

    /**
     * {@inheritDoc}
     *
     * @return "Admin" sebagai nama role.
     */
    @Override
    public String getRoleName() {
        return ROLE_NAME;
    }

    /**
     * {@inheritDoc}
     *
     * Admin memiliki level akses tertinggi pada sistem LeadEstate.
     *
     * @return nilai ACCESS_LEVEL milik Admin.
     */
    @Override
    public int getAccessLevel() {
        return ACCESS_LEVEL;
    }

    // ===== Override toString() =====

    @Override
    public String toString() {
        return "Admin{"
                + "id=" + this.getId()
                + ", name='" + this.getName() + "'"
                + ", email='" + this.getEmail() + "'"
                + ", roleId=" + this.getRoleId()
                + "}";
    }
}