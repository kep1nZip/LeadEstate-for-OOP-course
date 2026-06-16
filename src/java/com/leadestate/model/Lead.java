/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.leadestate.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Date;

public class Lead {

    // ATRIBUT (sesuai class diagram)

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

    private String source;

    private List<FollowUp> daftarFollowUp;

    // KONSTRUKTOR

    public Lead() {
        this.daftarFollowUp = new ArrayList<>();
    }

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

    // METHOD (sesuai class diagram)

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

    public void save() {
        if (!this.validateDate()) {
            System.out.println("[Lead] Penyimpanan dibatalkan karena data tidak valid.");
            return;
        }
        System.out.println("[Lead] Lead berhasil disimpan: " + this.toString());
    }

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

        this.notifySalesOfChange();

    }

    public void notifySalesOfChange() {
        String pesan = "Lead '" + this.name + "' (id=" + this.id
                + ") telah diperbarui. Silakan cek sistem LeadEstate.";
        System.out.println("[Lead] Notifikasi dikirim ke salesId=" + this.salesId
                + ": " + pesan);

        // TODO: Ganti dengan Notifikasi.send(this.salesId, pesan)
        //       saat kelas Notifikasi sudah tersedia.
    }

    // METHOD TAMBAHAN (pendukung relasi komposisi dengan FollowUp & utilitas)

    public void tambahFollowUp(FollowUp followUp) {
        if (followUp == null) {
            System.out.println("[Lead] FollowUp tidak boleh null.");
            return;
        }
        followUp.setLeadId(this.id);
        this.daftarFollowUp.add(followUp);
        System.out.println("[Lead] FollowUp berhasil ditambahkan ke leadId=" + this.id);
    }

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

    // GETTER & SETTER

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

    public String getPhone() {
        return this.phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getPropertyId() {
        return this.propertyId;
    }

    public void setPropertyId(int propertyId) {
        this.propertyId = propertyId;
    }

    public int getSalesId() {
        return this.salesId;
    }

    public void setSalesId(int salesId) {
        this.salesId = salesId;
    }

    public int getStatusId() {
        return this.statusId;
    }

    public void setStatusId(int statusId) {
        this.statusId = statusId;
    }

    public String getSource() {
        return this.source;
    }

    public void setSource(String source) {
        this.source = source;
    }
    
    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(java.util.Date createdAt) {
        this.createdAt = createdAt;
    }

    public List<FollowUp> getDaftarFollowUp() {
        return this.daftarFollowUp;
    }

    public void setDaftarFollowUp(List<FollowUp> daftarFollowUp) {
        this.daftarFollowUp = (daftarFollowUp != null) ? daftarFollowUp : new ArrayList<>();
    }

    // OVERRIDE toString()

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
