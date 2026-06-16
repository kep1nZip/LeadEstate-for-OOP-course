package com.leadestate.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Kelas Sales.
 *
 * Merepresentasikan pengguna dengan role Sales pada sistem LeadEstate.
 * Sales merupakan turunan (extends) dari User karena mewarisi seluruh
 * atribut dan method dasar User (id, name, email, password, roleId,
 * login(), logout(), register(), dst), serta menambahkan
 * method-method khusus untuk menangani Lead dan FollowUp miliknya.
 *
 * Sales memiliki level akses lebih rendah dibanding Admin, karena
 * hanya dapat mengelola Lead dan FollowUp yang menjadi tanggung
 * jawabnya sendiri (berdasarkan salesId).
 *
 * Catatan: Sesuai kesepakatan tim, kelas Supervisor tidak dipakai,
 * sehingga User hanya memiliki dua turunan: Admin dan Sales.
 */
public class Sales extends User {

    // ===== Konstanta level akses =====

    /**
     * Level akses Sales. Nilai lebih kecil dibanding ACCESS_LEVEL
     * milik Admin, karena Sales hanya memiliki akses terbatas pada
     * data miliknya sendiri.
     *
     * Catatan: ACCESS_LEVEL adalah nilai LOGIKA BISNIS (ranking hak akses),
     * TIDAK SAMA dengan roleId pada database. Lihat ROLE_ID di bawah.
     */
    public static final int ACCESS_LEVEL = 1;

    /**
     * ID role Sales pada tabel `roles` di database (roles.id = 2 untuk Sales,
     * sesuai gemini-code-1781355996980.sql). Nilai ini dipakai sebagai
     * roleId pada User, BUKAN ACCESS_LEVEL.
     */
    public static final int ROLE_ID = 2;

    /** Nama role untuk Sales, sesuai dengan penamaan pada class diagram. */
    public static final String ROLE_NAME = "Sales";

    // ===== Constructor =====

    /**
     * Constructor sesuai class diagram: membentuk objek Sales dengan
     * id, name, email, dan password. roleId di-set otomatis sesuai
     * ROLE_ID Sales (roles.id = 2 pada database), TIDAK memakai
     * ACCESS_LEVEL.
     *
     * @param id       id user.
     * @param name     nama Sales.
     * @param email    email Sales.
     * @param password password Sales.
     */
    public Sales(int id, String name, String email, String password) {
        super(id, name, email, password, ROLE_ID);
    }

    // ===== Method sesuai class diagram =====

    /**
     * Menambahkan Lead baru yang ditangani oleh Sales ini.
     *
     * Lead yang ditambahkan akan otomatis di-set salesId-nya sama
     * dengan id Sales ini, agar relasi asosiasi antara Sales dan
     * Lead konsisten.
     *
     * TODO: Saat DAO/Database tersedia, ganti println di bawah
     *       dengan pemanggilan LeadDAO.save(lead) setelah
     *       lead.validateData() dipanggil dan bernilai valid.
     *
     * @param lead objek Lead yang akan ditambahkan/ditangani oleh Sales ini.
     */
    public void addLead(Lead lead) {
        if (lead == null) {
            System.out.println("[Sales] Lead tidak boleh null.");
            return;
        }
        lead.setSalesId(this.getId());
        System.out.println("[Sales] " + this.getName()
                + " menambahkan lead baru: " + lead.getName());

        // TODO: Ganti println di atas dengan LeadDAO.save(lead)
        //       saat lapisan DAO sudah tersedia.
    }

    /**
     * Melakukan follow-up terhadap sebuah Lead berdasarkan id-nya.
     *
     * TODO: Saat DAO/Database tersedia, ganti implementasi ini dengan:
     *       1) Lead lead = LeadDAO.findById(leadId);
     *       2) FollowUp followUp = new FollowUp(leadId, this.getId(),
     *          null, new Date());
     *       3) lead.tambahFollowUp(followUp);
     *       4) FollowUpDAO.save(followUp);
     *
     * @param leadId id Lead yang akan di-follow-up.
     */
    public void doFollowUp(int leadId) {
        System.out.println("[Sales] " + this.getName()
                + " melakukan follow-up untuk leadId=" + leadId + ".");

        // TODO: Ganti println di atas dengan pembuatan objek FollowUp
        //       baru dan penyimpanannya via FollowUpDAO saat DAO
        //       sudah dibuat.
    }

    /**
     * Memperbarui status sebuah Lead berdasarkan id-nya.
     *
     * TODO: Saat DAO/Database tersedia, ganti implementasi ini dengan:
     *       1) Lead lead = LeadDAO.findById(leadId);
     *       2) if (lead.validateStatus(statusId)) {
     *              lead.changeStatus(statusId);
     *          }
     *
     * @param leadId   id Lead yang statusnya akan diperbarui.
     * @param statusId id status baru untuk Lead tersebut.
     */
    public void updateLeadStatus(int leadId, int statusId) {
        System.out.println("[Sales] " + this.getName()
                + " memperbarui status leadId=" + leadId
                + " menjadi statusId=" + statusId + ".");

        // TODO: Ganti println di atas dengan pemanggilan
        //       lead.changeStatus(statusId) via LeadDAO saat DAO
        //       sudah dibuat.
    }

    /**
     * Mengambil daftar Lead yang ditangani oleh Sales ini
     * (berdasarkan salesId).
     *
     * TODO: Saat DAO/Database tersedia, ganti dengan
     *       LeadDAO.findBySalesId(this.getId()). Untuk sementara
     *       mengembalikan list kosong sebagai placeholder.
     *
     * @return List berisi Lead yang ditangani oleh Sales ini.
     */
    public List<Lead> viewMyLeads() {
        System.out.println("[Sales] " + this.getName()
                + " menampilkan daftar Lead miliknya.");

        // Placeholder: belum ada DAO, kembalikan list kosong.
        return new ArrayList<>();
    }

    /**
     * Menampilkan laporan performa pribadi Sales ini, misalnya
     * jumlah Lead yang ditangani, jumlah closing, dan closing rate.
     *
     * TODO: Saat DAO/Service tersedia, isi data laporan ini dengan
     *       hasil agregasi sesungguhnya, contoh:
     *       - totalLead   = LeadDAO.countBySalesId(this.getId())
     *       - totalClosing = LeadDAO.countClosingBySalesId(this.getId())
     *       - closingRate  = totalClosing / totalLead * 100%
     *
     * @return objek Report berisi ringkasan performa Sales ini.
     */
    public Report viewMyPerformance() {
        System.out.println("[Sales] " + this.getName()
                + " menampilkan laporan performa pribadi.");

        Report report = new Report(
                "Laporan Performa " + this.getName(),
                "Ringkasan performa penjualan untuk Sales id=" + this.getId() + "."
        );

        // TODO: Isi report.setData(...) dengan hasil agregasi
        //       LeadDAO/FollowUpDAO saat DAO sudah dibuat.
        return report;
    }

    // ===== Implementasi method abstract dari User =====

    /**
     * {@inheritDoc}
     *
     * @return "Sales" sebagai nama role.
     */
    @Override
    public String getRoleName() {
        return ROLE_NAME;
    }

    /**
     * {@inheritDoc}
     *
     * Sales memiliki level akses terbatas, hanya pada data yang
     * menjadi tanggung jawabnya sendiri.
     *
     * @return nilai ACCESS_LEVEL milik Sales.
     */
    @Override
    public int getAccessLevel() {
        return ACCESS_LEVEL;
    }

    // ===== Override toString() =====

    @Override
    public String toString() {
        return "Sales{"
                + "id=" + this.getId()
                + ", name='" + this.getName() + "'"
                + ", email='" + this.getEmail() + "'"
                + ", roleId=" + this.getRoleId()
                + "}";
    }
}