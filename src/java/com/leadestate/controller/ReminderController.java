/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.leadestate.controller;

import com.leadestate.dao.FollowUpDAO;
import com.leadestate.dao.NotifikasiDAO;
import com.leadestate.dao.ReminderDAO;
import com.leadestate.model.FollowUp;
import com.leadestate.model.Notifikasi;
import com.leadestate.model.Reminder;
import com.leadestate.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller (Servlet) untuk halaman utama "Reminder & Follow-up".
 *
 * <p>Servlet ini adalah titik masuk halaman {@code /reminder} yang menampilkan
 * tiga tab (Hari Ini, Tertunda, Selesai) beserta panel detail follow-up
 * pada sisi kanan halaman.</p>
 *
 * <p>URL pattern: {@code /reminder} — aksi dibedakan via parameter
 * {@code action} pada request.</p>
 *
 * <p>Aksi yang didukung:</p>
 * <ul>
 *   <li>GET  {@code (default / tab)}   — tampilkan halaman utama reminder + tab aktif</li>
 *   <li>POST {@code batalkan}          — batalkan satu reminder (UPDATE status → Dibatalkan)</li>
 *   <li>POST {@code cekOverdue}        — cek & update reminder yang sudah overdue</li>
 * </ul>
 *
 * <p>Untuk aksi catat/edit/selesai/hapus pada follow-up, lihat
 * {@code FollowUpController.java}.</p>
 *
 * <p>Semua aksi membutuhkan user yang sudah login (cek session {@code userLogin}).
 * Jika belum login, redirect ke {@code /login}.</p>
 *
 * @author Rafa Ahmad Aulia (103012400169)
 * @version 1.0
 */
@WebServlet("/reminder")
public class ReminderController extends HttpServlet {

    private final ReminderDAO reminderDAO     = new ReminderDAO();
    private final FollowUpDAO followUpDAO     = new FollowUpDAO();
    private final NotifikasiDAO notifikasiDAO = new NotifikasiDAO();

    // =========================================================================
    // doGet — menampilkan halaman Reminder & Follow-up
    // =========================================================================

    /**
     * Menangani HTTP GET — menampilkan halaman utama Reminder & Follow-up.
     *
     * <p>Menyiapkan tiga daftar sesuai tab pada mockup:</p>
     * <ul>
     *   <li>{@code followUpHariIni}  — tab "Hari Ini"</li>
     *   <li>{@code followUpTertunda} — tab "Tertunda"</li>
     *   <li>{@code followUpSelesai}  — tab "Selesai"</li>
     * </ul>
     *
     * <p>Jika ada parameter {@code leadId}, panel kanan menampilkan
     * detail follow-up untuk lead tersebut.</p>
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // --- Cek sesi login ---
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userLogin") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String action = request.getParameter("action");

        if ("cekOverdue".equals(action)) {
            cekDanUpdateOverdue(request, response);
            return;
        }

        // Ambil salesId dari sesi (user yang sedang login)
        User userLogin = (User) session.getAttribute("userLogin");
        int salesId = userLogin.getId();

        // --- Isi tiga tab ---
        List<FollowUp> followUpHariIni  = followUpDAO.findHariIniOlehSales(salesId);
        List<FollowUp> followUpTertunda = followUpDAO.findTertundaOlehSales(salesId);
        List<FollowUp> followUpSelesai  = cariSelesaiOlehSales(salesId);
        List<FollowUp> followUpSemua = followUpDAO.findBySalesId(salesId);




        // Isi daftarReminder tiap FollowUp (untuk ditampilkan di panel detail)
        isiReminder(followUpHariIni);
        isiReminder(followUpTertunda);
        isiReminder(followUpSelesai);
        isiReminder(followUpSemua);

        request.setAttribute("followUpHariIni",  followUpHariIni);
        request.setAttribute("followUpTertunda", followUpTertunda);
        request.setAttribute("followUpSelesai",  followUpSelesai);
        request.setAttribute("followUpSemua", followUpSemua);

        // Tab aktif (default: hariIni)
        String tab = request.getParameter("tab");
        request.setAttribute("tabAktif", tab != null ? tab : "hariIni");

        // --- Panel kanan: detail lead yang dipilih ---
        String leadIdStr = request.getParameter("leadId");
        if (leadIdStr != null && !leadIdStr.trim().isEmpty()) {
            int leadId = Integer.parseInt(leadIdStr);
            List<FollowUp> riwayatFollowUp = followUpDAO.findByLeadId(leadId);
            isiReminder(riwayatFollowUp);
            request.setAttribute("leadIdDipilih", leadId);
            request.setAttribute("riwayatFollowUp", riwayatFollowUp);
        }

        // --- Badge notifikasi di header ---
        int jumlahNotifBelumDibaca = notifikasiDAO.countUnreadByUserId(salesId);
        request.setAttribute("jumlahNotif", jumlahNotifBelumDibaca);

        request.getRequestDispatcher("/WEB-INF/views/reminder.jsp").forward(request, response);
    }

    // =========================================================================
    // doPost — aksi ubah data
    // =========================================================================

    /**
     * Menangani HTTP POST.
     *
     * <p>Routing berdasarkan parameter {@code action}:</p>
     * <ul>
     *   <li>{@code batalkan} — UPDATE status reminder → Dibatalkan</li>
     * </ul>
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // --- Cek sesi login ---
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userLogin") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String action = request.getParameter("action");
        if (action == null) {
            response.sendRedirect(request.getContextPath() + "/reminder");
            return;
        }

        switch (action) {
            case "batalkan":
                batalkanReminder(request, response);
                break;
            default:
                response.sendRedirect(request.getContextPath() + "/reminder");
        }
    }

    // =========================================================================
    // HANDLER PRIVAT
    // =========================================================================

    /**
     * Mengecek seluruh reminder di database dan mengubah status yang sudah
     * melewati batas waktu menjadi {@code Overdue}.
     *
     * <p>Biasanya dipanggil via AJAX atau scheduler ringan dari halaman JSP
     * saat halaman pertama kali dibuka. Di sini dipanggil via GET
     * {@code ?action=cekOverdue}.</p>
     *
     * <p>Setelah selesai, redirect kembali ke halaman reminder.</p>
     */
    private void cekDanUpdateOverdue(HttpServletRequest request,
                                     HttpServletResponse response)
            throws IOException {

        List<Reminder> semua = reminderDAO.findAll();
        int jumlahDiupdate = 0;

        for (Reminder r : semua) {
            if (r.isOverdue()) {
                r.setStatus(Reminder.STATUS_OVERDUE);
                reminderDAO.update(r);
                jumlahDiupdate++;
            }
        }

        System.out.println("[ReminderController] cekOverdue: "
                + jumlahDiupdate + " reminder diubah ke Overdue.");

        response.sendRedirect(request.getContextPath() + "/reminder");
    }

    /**
     * Membatalkan satu reminder — UPDATE status menjadi {@code Dibatalkan}.
     *
     * <p>Membutuhkan parameter request: {@code reminderId}.</p>
     * <p>Redirect ke halaman reminder setelah aksi selesai.</p>
     */
    private void batalkanReminder(HttpServletRequest request,
                                  HttpServletResponse response)
            throws ServletException, IOException {

        String reminderIdStr = request.getParameter("reminderId");

        if (reminderIdStr == null) {
            response.sendRedirect(request.getContextPath() + "/reminder");
            return;
        }

        int reminderId = Integer.parseInt(reminderIdStr);
        Reminder reminder = reminderDAO.findById(reminderId);

        if (reminder == null) {
            request.setAttribute("pesan", "Reminder tidak ditemukan.");
            request.getRequestDispatcher("/WEB-INF/jsp/error.jsp").forward(request, response);
            return;
        }

        reminder.setStatus("Dibatalkan");
        boolean berhasil = reminderDAO.update(reminder);

        response.sendRedirect(request.getContextPath()
                + "/reminder" + (berhasil ? "?sukses=batalkan" : "?gagal=batalkan"));
    }

    // =========================================================================
    // UTILITAS PRIVAT
    // =========================================================================

    /**
     * Mengambil follow-up yang sudah selesai milik satu salesId dari database.
     *
     * <p>Digunakan untuk tab "Selesai" pada halaman Reminder & Follow-up.</p>
     *
     * @param salesId id sales yang sedang login.
     * @return List FollowUp dengan status {@code Selesai}, terbaru di atas.
     */
    private List<FollowUp> cariSelesaiOlehSales(int salesId) {
        List<FollowUp> semuaMilikSales = followUpDAO.findBySalesId(salesId);
        List<FollowUp> selesai = new ArrayList<>();
        for (FollowUp fu : semuaMilikSales) {
            if (FollowUp.STATUS_DONE.equalsIgnoreCase(fu.getStatus())) {
                selesai.add(fu);
            }
        }
        return selesai;
    }

    /**
     * Mengisi {@code daftarReminder} pada setiap FollowUp dalam daftar.
     *
     * <p>Dipanggil sebelum forward ke JSP agar JSP bisa menampilkan
     * detail reminder tanpa query tambahan di layer view.</p>
     *
     * @param daftarFollowUp list FollowUp yang akan diisi reminder-nya.
     */
    private void isiReminder(List<FollowUp> daftarFollowUp) {
        for (FollowUp fu : daftarFollowUp) {
            List<Reminder> reminders = reminderDAO.findByFollowupId(fu.getId());
            fu.setDaftarReminder(reminders);
        }
    }
}
