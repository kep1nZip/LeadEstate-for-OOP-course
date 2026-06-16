package com.leadestate.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Kelas Notifikasi.
 *
 * Merepresentasikan notifikasi yang dikirimkan kepada pengguna (User)
 * dalam sistem LeadEstate.
 *
 * Relasi sesuai class diagram:
 * - Notifikasi extends Reminder (INHERITANCE/GENERALISASI)
 *   Notifikasi mewarisi seluruh atribut dan method dari Reminder
 *   (id, followupId, reminderDate, status, isOverdue(), checkSchedule(),
 *   generateReminder(), sendNotification()).
 *
 * - User 1 -- 1..* Notifikasi (ASOSIASI)
 *   Satu User dapat menerima banyak Notifikasi. Relasi ini direalisasikan
 *   dengan menambahkan field userId: int pada kelas Notifikasi.
 *   Field userId tidak tercantum eksplisit di class diagram, namun
 *   ditambahkan agar relasi asosiasi ke User terealisasi secara konkret
 *   dalam kode — tanpa field ini, tidak ada cara untuk mengetahui User
 *   mana yang menjadi penerima sebuah Notifikasi.
 *
 * Catatan tentang getstatus() vs getStatus():
 * - Superclass Reminder sudah memiliki getStatus() (getter standar).
 * - getstatus() pada class diagram kemungkinan adalah typo, namun untuk
 *   menghormati diagram, method ini diimplementasikan sebagai method
 *   OVERRIDE dari getStatus() dengan nama yang diperbaiki menjadi
 *   getStatus() (pendekatan: override). Alasan memilih override daripada
 *   menambah method baru bernama getstatus() (huruf kecil):
 *   (1) Konsistensi konvensi Java (camelCase, nama method bermakna).
 *   (2) Menghindari duplikasi dua method dengan fungsi serupa.
 *   (3) Subclass memang perlu menambahkan informasi status "IsRead" ke
 *       dalam representasi statusnya, sehingga override masuk akal.
 *   Jika dosen/spesifikasi mengharuskan nama getstatus() apa adanya,
 *   hapus @Override dan sesuaikan nama method di bawah.
 */
public class Notifikasi extends Reminder {

    // ===== Atribut tambahan khusus Notifikasi (sesuai class diagram) =====

    private int notifId;
    private String message;
    private Date sentAt;

    /**
     * Field isRead menggunakan nama konvensi Java: "isRead" (camelCase,
     * bukan "IsRead" seperti di diagram). Getter disediakan sebagai
     * isRead() mengikuti konvensi boolean getter Java (bukan getIsRead()),
     * agar kompatibel dengan library seperti JSP EL dan JavaBeans.
     * Setter tetap setIsRead(boolean) agar simetris.
     */
    private boolean isRead;

    /**
     * Field userId ditambahkan untuk merealisasikan relasi asosiasi
     * User 1 -- 1..* Notifikasi. Tidak tercantum di class diagram,
     * namun wajib ada agar Notifikasi mengetahui siapa penerimanya.
     * Saat DAO/Database dibuat, field ini akan menjadi foreign key
     * yang merujuk ke tabel User.
     */
    private int userId;

    /*
     * Penyimpanan sementara di memori (in-memory).
     * Digunakan sebagai pengganti sumber data sebelum DAO/Database
     * dibuat. Method send(), markAsRead(), resend(),
     * cancelNotification(), dan getUnreadCount() akan bekerja terhadap
     * daftar ini.
     *
     * TODO: Ganti implementasi ini dengan DAO/Database saat sudah tersedia.
     */
    private static List<Notifikasi> daftarNotifikasi = new ArrayList<>();
    private static int nextNotifId = 1;

    // ===== Constructor =====

    /**
     * Constructor default.
     * Status awal reminder di-set ke "Pending" melalui super(),
     * dan isRead di-set ke false (belum dibaca).
     */
    public Notifikasi() {
        super();
        this.isRead = false;
    }

    /**
     * Constructor dengan parameter lengkap (semua atribut Notifikasi
     * termasuk atribut warisan dari Reminder).
     *
     * Biasanya dipakai saat membentuk objek Notifikasi dari data
     * yang sudah ada (misal hasil baca dari database).
     *
     * @param id           id Reminder (warisan dari superclass).
     * @param followupId   id FollowUp terkait (warisan dari superclass).
     * @param reminderDate tanggal reminder (warisan dari superclass).
     * @param status       status reminder (warisan dari superclass).
     * @param notifId      id unik notifikasi ini.
     * @param message      isi pesan notifikasi.
     * @param sentAt       tanggal dan waktu notifikasi dikirim.
     * @param isRead       apakah notifikasi sudah dibaca.
     * @param userId       id User penerima notifikasi.
     */
    public Notifikasi(int id, int followupId, Date reminderDate, String status,
                      int notifId, String message, Date sentAt,
                      boolean isRead, int userId) {
        super(id, followupId, reminderDate, status);
        this.notifId  = notifId;
        this.message  = message;
        this.sentAt   = sentAt;
        this.isRead   = isRead;
        this.userId   = userId;
    }

    /**
     * Constructor ringkas untuk membuat Notifikasi baru sebelum
     * disimpan ke database. notifId akan digenerate otomatis
     * (lihat send()), sentAt akan diisi saat pengiriman, dan
     * isRead di-set false (belum dibaca).
     *
     * @param followupId id FollowUp terkait.
     * @param message    isi pesan notifikasi.
     * @param userId     id User penerima notifikasi.
     */
    public Notifikasi(int followupId, String message, int userId) {
        super(followupId, new Date());
        this.message = message;
        this.userId  = userId;
        this.isRead  = false;
    }

    // ===== Getter & Setter =====

    public int getNotifId() {
        return this.notifId;
    }

    public void setNotifId(int notifId) {
        this.notifId = notifId;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getSentAt() {
        return this.sentAt;
    }

    public void setSentAt(Date sentAt) {
        this.sentAt = sentAt;
    }

    /**
     * Getter boolean untuk isRead, mengikuti konvensi JavaBeans:
     * nama getter untuk field boolean adalah isXxx(), bukan getXxx().
     *
     * @return true jika notifikasi sudah dibaca, false jika belum.
     */
    public boolean isRead() {
        return this.isRead;
    }

    public void setIsRead(boolean isRead) {
        this.isRead = isRead;
    }

    public int getUserId() {
        return this.userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    // ===== Method sesuai class diagram =====

    /**
     * Mengirimkan notifikasi kepada seorang pengguna.
     * Membuat objek Notifikasi baru, men-generate notifId secara
     * otomatis, menyimpannya ke daftarNotifikasi (in-memory), dan
     * mencatat waktu pengiriman (sentAt).
     *
     * TODO: Saat DAO/Database tersedia, ganti logika penyimpanan
     *       in-memory ini dengan pemanggilan NotifikasiDAO.save().
     *
     * @param userId  id User yang akan menerima notifikasi.
     * @param message isi pesan yang akan dikirim.
     */
    public void send(int userId, String message) {
        Notifikasi notifBaru = new Notifikasi();
        notifBaru.setNotifId(nextNotifId);
        notifBaru.setUserId(userId);
        notifBaru.setMessage(message);
        notifBaru.setSentAt(new Date());
        notifBaru.setIsRead(false);

        daftarNotifikasi.add(notifBaru);
        nextNotifId++;

        System.out.println("[Notifikasi #" + notifBaru.getNotifId()
                + " → User #" + userId + "] " + message);
    }

    /**
     * Menandai sebuah notifikasi sebagai sudah dibaca berdasarkan
     * notifId-nya.
     *
     * TODO: Saat DAO/Database tersedia, ganti pencarian in-memory ini
     *       dengan NotifikasiDAO.findById(notifId).setIsRead(true).
     *
     * @param notifId id notifikasi yang akan ditandai sudah dibaca.
     */
    public void markAsRead(int notifId) {
        for (Notifikasi n : daftarNotifikasi) {
            if (n.getNotifId() == notifId) {
                n.setIsRead(true);
                System.out.println("[Notifikasi #" + notifId
                        + "] Ditandai sudah dibaca.");
                return;
            }
        }
        System.out.println("[Notifikasi] Notifikasi dengan id "
                + notifId + " tidak ditemukan.");
    }

    /**
     * Mengirim ulang notifikasi ini kepada User dengan userId tertentu.
     * Waktu pengiriman (sentAt) diperbarui ke waktu sekarang dan status
     * isRead di-reset ke false.
     *
     * TODO: Saat DAO/Database tersedia, perbarui record yang ada di
     *       database alih-alih menambah entri baru ke list in-memory.
     *
     * @param userId id User yang akan menerima ulang notifikasi ini.
     */
    public void resend(int userId) {
        String pesanUlang = (this.message != null) ? this.message
                : "(Notifikasi tanpa pesan)";
        send(userId, "[KIRIM ULANG] " + pesanUlang);
    }

    /**
     * Membatalkan (menghapus) sebuah notifikasi dari daftar
     * berdasarkan notifId-nya.
     *
     * TODO: Saat DAO/Database tersedia, ganti penghapusan in-memory ini
     *       dengan NotifikasiDAO.deleteById(notifId).
     *
     * @param notifId id notifikasi yang akan dibatalkan.
     */
    public void cancelNotification(int notifId) {
        boolean dihapus = daftarNotifikasi
                .removeIf(n -> n.getNotifId() == notifId);

        if (dihapus) {
            System.out.println("[Notifikasi #" + notifId
                    + "] Notifikasi berhasil dibatalkan.");
        } else {
            System.out.println("[Notifikasi] Notifikasi dengan id "
                    + notifId + " tidak ditemukan.");
        }
    }

    /**
     * Mengembalikan representasi status notifikasi ini secara lengkap,
     * menggabungkan status reminder dari superclass dengan status
     * isRead dari Notifikasi.
     *
     * Ini merupakan OVERRIDE dari getStatus() milik Reminder.
     * Alasan override (bukan method baru bernama getstatus()):
     * (1) getstatus() di class diagram terlihat sebagai typo dari
     *     getStatus() (konvensi Java tidak menggunakan huruf kecil
     *     setelah "get" pada kata berikutnya).
     * (2) Override memungkinkan polimorfisme — kode yang memegang
     *     referensi Reminder dapat memanggil getStatus() dan
     *     mendapatkan informasi yang lebih kaya saat objek aktualnya
     *     adalah Notifikasi.
     * (3) Menghindari dua method dengan tujuan serupa di kelas yang sama.
     *
     * Jika dosen/spesifikasi mengharuskan nama "getstatus()" apa adanya,
     * hapus @Override dan sesuaikan nama method ini.
     *
     * @return String status lengkap, misal "Pending | Belum Dibaca".
     */
    @Override
    public String getStatus() {
        String statusReminder = super.getStatus();
        String statusBaca     = this.isRead ? "Sudah Dibaca" : "Belum Dibaca";
        return statusReminder + " | " + statusBaca;
    }

    /**
     * Menjadwalkan pengiriman notifikasi ini pada tanggal dan waktu
     * tertentu di masa mendatang. sentAt di-set ke tanggal yang
     * diberikan.
     *
     * Implementasi aktual penjadwalan (misalnya menggunakan
     * ScheduledExecutorService atau job scheduler) akan ditangani
     * di lapisan Service.
     *
     * TODO: Saat Service tersedia, integrasikan dengan scheduler
     *       (misalnya Quartz atau java.util.Timer).
     *
     * @param date tanggal dan waktu yang direncanakan untuk pengiriman.
     */
    public void scheduleNotif(Date date) {
        this.sentAt = date;
        System.out.println("[Notifikasi #" + this.notifId
                + "] Dijadwalkan untuk dikirim pada: " + date);
    }

    /**
     * Menghitung jumlah notifikasi yang belum dibaca (isRead = false)
     * dari seluruh daftarNotifikasi untuk User tertentu (this.userId).
     *
     * TODO: Saat DAO/Database tersedia, ganti iterasi in-memory ini
     *       dengan query: SELECT COUNT(*) FROM Notifikasi WHERE
     *       userId = ? AND isRead = false.
     *
     * @return jumlah notifikasi yang belum dibaca oleh User pemilik
     *         objek Notifikasi ini.
     */
    public int getUnreadCount() {
        int jumlah = 0;
        for (Notifikasi n : daftarNotifikasi) {
            if (n.getUserId() == this.userId && !n.isRead()) {
                jumlah++;
            }
        }
        return jumlah;
    }

    // ===== Akses ke daftar notifikasi (in-memory) =====

    /**
     * Mengambil seluruh notifikasi yang tersimpan di memori.
     * Berguna untuk pengujian sebelum DAO/Database tersedia.
     */
    public static List<Notifikasi> getDaftarNotifikasi() {
        return daftarNotifikasi;
    }

    @Override
    public String toString() {
        return "Notifikasi{"
                + "notifId=" + this.notifId
                + ", userId=" + this.userId
                + ", message='" + this.message + "'"
                + ", sentAt=" + this.sentAt
                + ", isRead=" + this.isRead
                + ", followupId=" + this.getFollowupId()
                + ", reminderDate=" + this.getReminderDate()
                + ", status='" + super.getStatus() + "'"
                + "}";
    }
}
