/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.leadestate.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Date;

/**
 * Kelas Lead merepresentasikan calon pembeli (prospek) yang dikelola oleh Sales
 * dalam sistem LeadEstate.
 *
 * <p>Relasi:</p>
 * <ul>
 *   <li>KOMPOSISI dengan FollowUp (satu Lead memiliki banyak FollowUp,
 *       FollowUp tidak ada tanpa Lead — disimpan sebagai {@code List<FollowUp>}
 *       di dalam Lead. Setiap FollowUp menyimpan leadId yang merujuk ke id
 *       Lead ini, sehingga relasi terjaga dari dua sisi).
 *
 *       <p><b>Pendekatan komposisi yang dipilih:</b> Lead memegang
 *       {@code List<FollowUp>} secara langsung. Ini dipilih karena:
 *       (1) komposisi mengharuskan "pemilik" (Lead) yang bertanggung jawab
 *       atas siklus hidup "bagian" (FollowUp) — ketika Lead dihapus,
 *       semua FollowUp-nya ikut dihapus; (2) pendekatan ini konsisten
 *       dengan cara Reminder disimpan di dalam FollowUp (lihat FollowUp.java).
 *       Saat DAO tersedia nanti, daftarFollowUp akan diisi via
 *       {@code FollowUpDAO.findByLeadId(this.id)}.</p></li>
 *   <li>AGREGASI dengan Property (Lead menyimpan propertyId saja; Property
 *       tetap ada walau Lead dihapus).</li>
 *   <li>ASOSIASI dengan LeadStatus (Lead menyimpan statusId saja; LeadStatus
 *       tetap ada walau Lead dihapus).</li>
 *   <li>ASOSIASI dengan User/Sales (Lead menyimpan salesId saja; tidak
 *       menyimpan objek User secara langsung).</li>
 * </ul>
 *
 * <p>Catatan: Kelas ini adalah POJO murni (Java + JSP — bukan Spring Boot/JPA).
 * Tidak ada anotasi @Entity, @Service, dsb.</p>
 *
 * @author [Nama Anggota Tim - bagian Data Lead]
 * @version 1.0
 */
public class Lead {

    // =========================================================================
    // ATRIBUT (sesuai class diagram)
    // =========================================================================

    /** ID unik lead. */
    private int id;

    /** Nama calon pembeli. */
    private String name;

    /** Nomor telepon calon pembeli. */
    private String phone;

    /** Alamat email calon pembeli. */
    private String email;

    /** ID properti yang diminati oleh lead (representasi agregasi ke Property). */
    private int propertyId;

    /** ID Sales yang bertanggung jawab atas lead ini (representasi asosiasi ke User/Sales). */
    private int salesId;

    /** ID status lead saat ini (representasi asosiasi ke LeadStatus). */
    private int statusId;
    
    private java.util.Date createdAt;




    /**
     * Sumber perolehan lead (contoh: "Instagram", "Referral", "Website",
     * "Facebook", "TikTok", "Walk-in").
     */
    private String source;

    /**
     * Daftar FollowUp yang terkait dengan Lead ini (KOMPOSISI).
     *
     * <p>Lead menyimpan {@code List<FollowUp>} karena FollowUp adalah bagian
     * dari Lead (komposisi). Setiap FollowUp memiliki field leadId yang nilainya
     * sama dengan id Lead ini, sehingga relasi terjaga dari dua sisi.</p>
     *
     * <p>TODO: Saat DAO sudah dibuat, list ini akan diisi via
     * {@code FollowUpDAO.findByLeadId(this.id)} — ganti inisialisasi
     * in-memory ini dengan pemanggilan DAO yang sesuai.</p>
     */
    private List<FollowUp> daftarFollowUp;

    // =========================================================================
    // KONSTRUKTOR
    // =========================================================================

    /**
     * Konstruktor default.
     * Menginisialisasi daftarFollowUp ke kosong.
     */
    public Lead() {
        this.daftarFollowUp = new ArrayList<>();
    }

    /**
     * Konstruktor lengkap dengan semua parameter utama.
     *
     * @param id         ID unik lead
     * @param name       Nama calon pembeli
     * @param phone      Nomor telepon calon pembeli
     * @param email      Alamat email calon pembeli
     * @param propertyId ID properti yang diminati
     * @param salesId    ID Sales yang menangani lead ini
     * @param statusId   ID status lead saat ini
     * @param source     Sumber perolehan lead
     */
    public Lead(int id, String name, String phone, String email,
                int propertyId, int salesId, int statusId, String source) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.propertyId = propertyId;
        this.salesId = salesId;
        this.statusId = statusId;
        this.source = source;
        this.daftarFollowUp = new ArrayList<>();
    }

    /**
     * Konstruktor tanpa ID (untuk pembuatan lead baru sebelum disimpan ke DB).
     *
     * @param name       Nama calon pembeli
     * @param phone      Nomor telepon calon pembeli
     * @param email      Alamat email calon pembeli
     * @param propertyId ID properti yang diminati
     * @param salesId    ID Sales yang menangani lead ini
     * @param source     Sumber perolehan lead
     */
    public Lead(String name, String phone, String email,
                int propertyId, int salesId, String source) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.propertyId = propertyId;
        this.salesId = salesId;
        this.source = source;
        this.daftarFollowUp = new ArrayList<>();
    }

    // =========================================================================
    // METHOD (sesuai class diagram)
    // =========================================================================

    /**
     * Mengisi data utama lead secara sekaligus (alternatif setter satu per satu).
     *
     * <p>Method ini tidak otomatis menyimpan ke database.
     * Panggil {@link #save()} setelah method ini untuk menyimpan.</p>
     *
     * <p>TODO: Validasi tambahan (format email, format nomor HP) dapat ditambahkan
     * di sini saat kebutuhan sudah lebih jelas.</p>
     *
     * @param name       Nama calon pembeli
     * @param email      Alamat email calon pembeli
     * @param phone      Nomor telepon calon pembeli
     * @param propertyId ID properti yang diminati
     */
    public void inputData(String name, String email, String phone, int propertyId) {
        if (name == null || name.trim().isEmpty()) {
            System.out.println("[Lead] Nama tidak boleh kosong.");
            return;
        }
        if (email == null || email.trim().isEmpty()) {
            System.out.println("[Lead] Email tidak boleh kosong.");
            return;
        }
        if (phone == null || phone.trim().isEmpty()) {
            System.out.println("[Lead] Nomor telepon tidak boleh kosong.");
            return;
        }
        if (propertyId <= 0) {
            System.out.println("[Lead] propertyId tidak valid: " + propertyId);
            return;
        }

        this.name = name;
        this.email = email;
        this.phone = phone;
        this.propertyId = propertyId;

        System.out.println("[Lead] Data berhasil diinput untuk lead: " + this.name);
    }

    /**
     * Memvalidasi kelengkapan dan kebenaran data lead.
     *
     * <p>Validasi yang dilakukan:</p>
     * <ul>
     *   <li>name tidak boleh null/kosong</li>
     *   <li>email tidak boleh null/kosong dan harus mengandung '@'</li>
     *   <li>phone tidak boleh null/kosong</li>
     *   <li>propertyId harus lebih dari 0</li>
     *   <li>salesId harus lebih dari 0</li>
     * </ul>
     *
     * <p>Catatan: Nama method di class diagram adalah {@code validateDate()},
     * tetapi berdasarkan konteks atribut Lead (tidak ada field Date yang berdiri
     * sendiri) dan logika bisnis yang dijelaskan di proposal, method ini
     * diinterpretasikan sebagai validasi data lead secara umum.</p>
     *
     * <p>TODO: Tambahkan validasi format email dan nomor HP yang lebih ketat
     * (regex) saat kebutuhan sudah lebih jelas.</p>
     *
     * @return true jika semua data valid, false jika ada yang tidak valid
     */
    public boolean validateDate() {
        if (this.name == null || this.name.trim().isEmpty()) {
            System.out.println("[Lead] Validasi gagal: nama kosong.");
            return false;
        }
        if (this.email == null || !this.email.contains("@")) {
            System.out.println("[Lead] Validasi gagal: email tidak valid.");
            return false;
        }
        if (this.phone == null || this.phone.trim().isEmpty()) {
            System.out.println("[Lead] Validasi gagal: nomor telepon kosong.");
            return false;
        }
        if (this.propertyId <= 0) {
            System.out.println("[Lead] Validasi gagal: propertyId tidak valid.");
            return false;
        }
        if (this.salesId <= 0) {
            System.out.println("[Lead] Validasi gagal: salesId tidak valid.");
            return false;
        }
        System.out.println("[Lead] Validasi berhasil untuk lead: " + this.name);
        return true;
    }

    /**
     * Menyimpan data lead ke penyimpanan (sementara: in-memory / log ke console).
     *
     * <p>Method ini memanggil {@link #validateDate()} terlebih dahulu sebelum
     * menyimpan. Jika validasi gagal, penyimpanan dibatalkan.</p>
     *
     * <p>TODO: Ganti implementasi in-memory ini dengan pemanggilan
     * LeadDAO.save(this) saat DAO sudah dibuat.</p>
     */
    public void save() {
        if (!this.validateDate()) {
            System.out.println("[Lead] Penyimpanan dibatalkan karena data tidak valid.");
            return;
        }
        System.out.println("[Lead] Lead berhasil disimpan: " + this.toString());

        // TODO: Ganti dengan LeadDAO.save(this) saat DAO sudah dibuat.
    }

    /**
     * Memvalidasi apakah statusId baru yang diberikan adalah valid (terdaftar
     * di sistem).
     *
     * <p>Saat ini validasi hanya memastikan statusId lebih dari 0 (placeholder).
     * Saat DAO tersedia, validasi ini akan mengecek ke tabel LeadStatus
     * di database.</p>
     *
     * <p>TODO: Ganti dengan pengecekan {@code LeadStatusDAO.findById(newStatusId) != null}
     * saat DAO sudah dibuat.</p>
     *
     * @param newStatusId ID status baru yang akan divalidasi
     * @return true jika statusId valid, false jika tidak
     */
    public boolean validateStatus(int newStatusId) {
        if (newStatusId <= 0) {
            System.out.println("[Lead] validateStatus gagal: statusId tidak valid ("
                    + newStatusId + ").");
            return false;
        }
        // TODO: LeadStatusDAO.findById(newStatusId) != null
        System.out.println("[Lead] validateStatus: statusId=" + newStatusId + " dianggap valid.");
        return true;
    }

    /**
     * Mengubah status lead ke status baru.
     *
     * <p>Method ini:</p>
     * <ol>
     *   <li>Memanggil {@link #validateStatus(int)} untuk memastikan status baru valid.</li>
     *   <li>Mencatat riwayat perubahan via {@code LeadStatus.saveHistory()}.</li>
     *   <li>Memperbarui field statusId.</li>
     *   <li>Memanggil {@link #notifySalesOfChange()} untuk memberi tahu Sales.</li>
     * </ol>
     *
     * <p>TODO: Setelah DAO dibuat, tambahkan {@code LeadDAO.update(this)}
     * di sini agar perubahan tersimpan ke database.</p>
     *
     * @param newStatusId ID status baru yang akan diterapkan
     */
    public void changeStatus(int newStatusId) {
        if (!this.validateStatus(newStatusId)) {
            System.out.println("[Lead] changeStatus dibatalkan untuk leadId=" + this.id);
            return;
        }
        int statusLama = this.statusId;
        this.statusId = newStatusId;

        // Catat riwayat perubahan status
        LeadStatus leadStatus = new LeadStatus();
        leadStatus.saveHistory(this.id, statusLama, newStatusId);

        System.out.println("[Lead] Status lead id=" + this.id
                + " berubah dari statusId=" + statusLama
                + " menjadi statusId=" + newStatusId);

        // Beritahu Sales bahwa status lead telah berubah
        this.notifySalesOfChange();

        // TODO: Ganti dengan LeadDAO.update(this) saat DAO sudah dibuat.
    }

    /**
     * Memberi tahu Sales bahwa data atau status lead telah berubah.
     *
     * <p>Saat ini notifikasi hanya berupa log ke console (placeholder).
     * Implementasi sesungguhnya akan menggunakan kelas Notifikasi untuk
     * mengirimkan pemberitahuan ke Sales yang bersangkutan.</p>
     *
     * <p>TODO: Ganti dengan {@code Notifikasi.send(this.salesId, message)}
     * saat kelas Notifikasi dan DAO sudah dibuat.</p>
     */
    public void notifySalesOfChange() {
        String pesan = "Lead '" + this.name + "' (id=" + this.id
                + ") telah diperbarui. Silakan cek sistem LeadEstate.";
        System.out.println("[Lead] Notifikasi dikirim ke salesId=" + this.salesId
                + ": " + pesan);

        // TODO: Ganti dengan Notifikasi.send(this.salesId, pesan)
        //       saat kelas Notifikasi sudah tersedia.
    }

    // =========================================================================
    // METHOD TAMBAHAN (pendukung relasi komposisi dengan FollowUp & utilitas)
    // =========================================================================

    /**
     * Menambahkan FollowUp ke dalam daftar follow-up lead ini.
     *
     * <p>FollowUp yang ditambahkan akan otomatis di-set leadId-nya sama dengan
     * id Lead ini, agar relasi komposisi konsisten dari dua sisi.</p>
     *
     * @param followUp Objek FollowUp yang akan ditambahkan
     */
    public void tambahFollowUp(FollowUp followUp) {
        if (followUp == null) {
            System.out.println("[Lead] FollowUp tidak boleh null.");
            return;
        }
        followUp.setLeadId(this.id);
        this.daftarFollowUp.add(followUp);
        System.out.println("[Lead] FollowUp berhasil ditambahkan ke leadId=" + this.id);
    }

    /**
     * Menghapus FollowUp dari daftar berdasarkan id FollowUp.
     *
     * <p>Karena ini komposisi, penghapusan FollowUp dari Lead berarti FollowUp
     * tersebut tidak lagi ada di sistem (lifecycle tergantung pada Lead).</p>
     *
     * @param followUpId ID FollowUp yang akan dihapus
     * @return true jika berhasil dihapus, false jika tidak ditemukan
     */
    public boolean hapusFollowUp(int followUpId) {
        for (int i = 0; i < this.daftarFollowUp.size(); i++) {
            if (this.daftarFollowUp.get(i).getId() == followUpId) {
                this.daftarFollowUp.remove(i);
                System.out.println("[Lead] FollowUp id=" + followUpId + " berhasil dihapus.");
                return true;
            }
        }
        System.out.println("[Lead] FollowUp id=" + followUpId + " tidak ditemukan.");
        return false;
    }

    // =========================================================================
    // GETTER & SETTER
    // =========================================================================

    /**
     * Mengambil ID lead.
     *
     * @return id lead
     */
    public int getId() {
        return this.id;
    }

    /**
     * Mengatur ID lead.
     *
     * @param id ID lead
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Mengambil nama calon pembeli.
     *
     * @return name
     */
    public String getName() {
        return this.name;
    }

    /**
     * Mengatur nama calon pembeli.
     *
     * @param name nama calon pembeli
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Mengambil nomor telepon calon pembeli.
     *
     * @return phone
     */
    public String getPhone() {
        return this.phone;
    }

    /**
     * Mengatur nomor telepon calon pembeli.
     *
     * @param phone nomor telepon
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * Mengambil email calon pembeli.
     *
     * @return email
     */
    public String getEmail() {
        return this.email;
    }

    /**
     * Mengatur email calon pembeli.
     *
     * @param email alamat email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Mengambil ID properti yang diminati.
     *
     * @return propertyId
     */
    public int getPropertyId() {
        return this.propertyId;
    }

    /**
     * Mengatur ID properti yang diminati.
     *
     * @param propertyId ID properti
     */
    public void setPropertyId(int propertyId) {
        this.propertyId = propertyId;
    }

    /**
     * Mengambil ID sales yang menangani lead ini.
     *
     * @return salesId
     */
    public int getSalesId() {
        return this.salesId;
    }

    /**
     * Mengatur ID sales yang menangani lead ini.
     *
     * @param salesId ID sales
     */
    public void setSalesId(int salesId) {
        this.salesId = salesId;
    }

    /**
     * Mengambil ID status lead saat ini.
     *
     * @return statusId
     */
    public int getStatusId() {
        return this.statusId;
    }

    /**
     * Mengatur ID status lead secara langsung (tanpa validasi dan tanpa notifikasi).
     * Untuk mengubah status dengan validasi, gunakan {@link #changeStatus(int)}.
     *
     * @param statusId ID status baru
     */
    public void setStatusId(int statusId) {
        this.statusId = statusId;
    }

    /**
     * Mengambil sumber perolehan lead.
     *
     * @return source
     */
    public String getSource() {
        return this.source;
    }

    /**
     * Mengatur sumber perolehan lead.
     *
     * @param source sumber lead (contoh: "Instagram", "Referral")
     */
    public void setSource(String source) {
        this.source = source;
    }
    
    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(java.util.Date createdAt) {
        this.createdAt = createdAt;
    }
    /**
     * Mengambil seluruh daftar FollowUp yang terkait dengan Lead ini.
     *
     * @return List berisi objek FollowUp
     */
    public List<FollowUp> getDaftarFollowUp() {
        return this.daftarFollowUp;
    }

    /**
     * Mengganti seluruh daftar FollowUp (misalnya saat memuat data dari DAO).
     *
     * <p>TODO: Method ini akan dipanggil oleh DAO/Service untuk mengisi
     * daftarFollowUp dari database.</p>
     *
     * @param daftarFollowUp daftar follow-up baru
     */
    public void setDaftarFollowUp(List<FollowUp> daftarFollowUp) {
        this.daftarFollowUp = (daftarFollowUp != null) ? daftarFollowUp : new ArrayList<>();
    }

    // =========================================================================
    // OVERRIDE toString()
    // =========================================================================

    /**
     * Representasi teks dari objek Lead.
     *
     * @return String berisi informasi lead
     */
    @Override
    public String toString() {
        return "Lead{"
                + "id=" + this.id
                + ", name='" + this.name + "'"
                + ", phone='" + this.phone + "'"
                + ", email='" + this.email + "'"
                + ", propertyId=" + this.propertyId
                + ", salesId=" + this.salesId
                + ", statusId=" + this.statusId
                + ", source='" + this.source + "'"
                + ", jumlahFollowUp=" + (this.daftarFollowUp != null ? this.daftarFollowUp.size() : 0)
                + "}";
    }
}
