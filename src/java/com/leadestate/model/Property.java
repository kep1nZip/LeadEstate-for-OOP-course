/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.leadestate.model;

/**
 * Kelas Property merepresentasikan data properti (rumah, apartemen, vila, dsb.)
 * yang tersedia dalam sistem LeadEstate.
 *
 * <p>Relasi:</p>
 * <ul>
 *   <li>AGREGASI dengan Lead (satu Property dapat diacu oleh banyak Lead melalui
 *       field propertyId di kelas Lead). Property tetap ada meskipun data Lead
 *       dihapus — bukan komposisi, melainkan agregasi/asosiasi).</li>
 * </ul>
 *
 * <p>Catatan: Kelas ini adalah POJO murni (Java + JSP — bukan Spring Boot/JPA).
 * Tidak ada anotasi @Entity, @Service, dsb.</p>
 *
 * @author [Nama Anggota Tim - bagian Data Lead]
 * @version 1.0
 */
public class Property {

    // =========================================================================
    // ATRIBUT (sesuai class diagram)
    // =========================================================================

    /** ID unik properti. */
    private int id;

    /** Nama properti (contoh: "Perumahan Grand Cibubur", "Apartemen Sudirman"). */
    private String name;

    /** Lokasi properti (contoh: "Bandung", "Jakarta Selatan"). */
    private String location;

    /**
     * Harga properti dalam satuan Rupiah.
     * Menggunakan float sesuai class diagram; pertimbangkan
     * double atau long untuk kebutuhan presisi lebih tinggi di masa depan.
     */
    private float price;

    // =========================================================================
    // KONSTRUKTOR
    // =========================================================================

    /**
     * Konstruktor default.
     * Menginisialisasi semua atribut ke nilai default Java (0, null).
     */
    public Property() {
    }

    /**
     * Konstruktor lengkap dengan semua parameter.
     *
     * @param id       ID unik properti
     * @param name     Nama properti
     * @param location Lokasi properti
     * @param price    Harga properti (dalam Rupiah)
     */
    public Property(int id, String name, String location, float price) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.price = price;
    }

    /**
     * Konstruktor tanpa ID (untuk pembuatan properti baru sebelum disimpan ke DB).
     *
     * @param name     Nama properti
     * @param location Lokasi properti
     * @param price    Harga properti (dalam Rupiah)
     */
    public Property(String name, String location, float price) {
        this.name = name;
        this.location = location;
        this.price = price;
    }

    // =========================================================================
    // METHOD (sesuai class diagram)
    // =========================================================================

    /**
     * Memperbarui harga properti ini dengan harga baru.
     *
     * <p>Validasi: harga baru tidak boleh negatif atau nol.</p>
     *
     * <p>TODO: Setelah DAO dibuat, tambahkan pemanggilan PropertyDAO.update(this)
     * di sini agar perubahan harga tersimpan ke database.</p>
     *
     * @param newPrice Harga baru properti (dalam Rupiah, harus lebih dari 0)
     */
    public void updatePrice(double newPrice) {
        if (newPrice <= 0) {
            System.out.println("[Property] Harga baru tidak valid: " + newPrice
                    + ". Harga harus lebih dari 0.");
            return;
        }
        double hargaLama = this.price;
        this.price = (float) newPrice;
        System.out.println("[Property] Harga properti id=" + this.id
                + " diperbarui dari Rp " + hargaLama + " menjadi Rp " + newPrice);

        // TODO: Ganti dengan PropertyDAO.update(this) saat DAO sudah dibuat.
    }

    // =========================================================================
    // GETTER & SETTER
    // =========================================================================

    /**
     * Mengambil ID properti.
     *
     * @return id properti
     */
    public int getId() {
        return this.id;
    }

    /**
     * Mengatur ID properti.
     *
     * @param id ID properti
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Mengambil nama properti.
     *
     * @return name
     */
    public String getName() {
        return this.name;
    }

    /**
     * Mengatur nama properti.
     *
     * @param name nama properti
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Mengambil lokasi properti.
     *
     * @return location
     */
    public String getLocation() {
        return this.location;
    }

    /**
     * Mengatur lokasi properti.
     *
     * @param location lokasi properti
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * Mengambil harga properti.
     *
     * @return price (dalam Rupiah)
     */
    public float getPrice() {
        return this.price;
    }

    /**
     * Mengatur harga properti secara langsung (tanpa validasi).
     * Untuk memperbarui harga dengan validasi, gunakan {@link #updatePrice(double)}.
     *
     * @param price harga properti
     */
    public void setPrice(float price) {
        this.price = price;
    }

    // =========================================================================
    // OVERRIDE toString()
    // =========================================================================

    /**
     * Representasi teks dari objek Property.
     *
     * @return String berisi informasi properti
     */
    @Override
    public String toString() {
        return "Property{"
                + "id=" + this.id
                + ", name='" + this.name + "'"
                + ", location='" + this.location + "'"
                + ", price=" + this.price
                + "}";
    }
}
