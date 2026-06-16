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
import com.leadestate.model.Sales;
import com.leadestate.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Controller (Servlet) untuk fitur Follow-up.
 *
 * <p>Menangani semua aksi yang berkaitan dengan tabel {@code followups}:
 * menampilkan daftar follow-up per lead, mencatat aktivitas (logActivity),
 * mengedit catatan (editNotes), dan menandai selesai (markAsDone).</p>
 *
 * <p>URL pattern: {@code /followup} — aksi dibedakan via parameter
 * {@code action} pada request.</p>
 *
 * <p>Aksi yang didukung:</p>
 * <ul>
 *   <li>{@code list}      — GET  — daftar follow-up untuk satu leadId</li>
 *   <li>{@code catat}     — POST — catat aktivitas baru (INSERT)</li>
 *   <li>{@code edit}      — POST — edit catatan notes (UPDATE)</li>
 *   <li>{@code selesai}   — POST — tandai follow-up selesai (UPDATE status)</li>
 *   <li>{@code hapus}     — POST — hapus follow-up (DELETE)</li>
 * </ul>
 *
 * <p>Semua aksi membutuhkan user yang sudah login (cek session {@code userLogin}).
 * Jika belum login, redirect ke {@code /login}.</p>
 *
 * @author Rafa Ahmad Aulia (103012400169)
 * @version 1.0
 */
@WebServlet("/followup")
public class FollowUpController extends HttpServlet {

    private final FollowUpDAO followUpDAO = new FollowUpDAO();
    private final ReminderDAO reminderDAO = new ReminderDAO();
    private final NotifikasiDAO notifikasiDAO = new NotifikasiDAO();

    // Format tanggal yang dikirim dari form JSP (input datetime-local)
    private static final String FORMAT_DATETIME = "yyyy-MM-dd'T'HH:mm";

    // =========================================================================
    // doGet — menampilkan data (read)
    // =========================================================================

    /**
     * Menangani HTTP GET.
     *
     * <p>Routing berdasarkan parameter {@code action}:</p>
     * <ul>
     *   <li>Tidak ada / {@code list} — tampilkan daftar follow-up untuk leadId tertentu</li>
     * </ul>
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
        if (action == null) {
            action = "list";
        }

        switch (action) {
            case "list":
                tampilkanDaftarFollowUp(request, response);
                break;
            default:
                response.sendRedirect(request.getContextPath() + "/reminder");
        }
    }

    // =========================================================================
    // doPost — aksi ubah data (create / update / delete)
    // =========================================================================

    /**
     * Menangani HTTP POST.
     *
     * <p>Routing berdasarkan parameter {@code action}:</p>
     * <ul>
     *   <li>{@code catat}   — INSERT follow-up baru (logActivity)</li>
     *   <li>{@code edit}    — UPDATE notes follow-up (editNotes)</li>
     *   <li>{@code selesai} — UPDATE status menjadi Selesai (markAsDone)</li>
     *   <li>{@code hapus}   — DELETE follow-up</li>
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
            case "catat":
                catatAktivitas(request, response);
                break;
            case "edit":
                editCatatan(request, response);
                break;
            case "selesai":
                tandaiSelesai(request, response);
                break;
            case "hapus":
                hapusFollowUp(request, response);
                break;
            default:
                response.sendRedirect(request.getContextPath() + "/reminder");
        }
    }

    // =========================================================================
    // HANDLER PRIVAT
    // =========================================================================

    /**
     * Menampilkan daftar riwayat follow-up untuk satu lead.
     *
     * <p>Membutuhkan parameter request: {@code leadId} (int).</p>
     * <p>Men-set attribute {@code daftarFollowUp} ke request lalu
     * forward ke JSP {@code followup/list.jsp}.</p>
     */
    private void tampilkanDaftarFollowUp(HttpServletRequest request,
                                          HttpServletResponse response)
            throws ServletException, IOException {

        String leadIdStr = request.getParameter("leadId");
        if (leadIdStr == null || leadIdStr.trim().isEmpty()) {
            request.setAttribute("pesan", "leadId tidak boleh kosong.");
            request.getRequestDispatcher("/WEB-INF/jsp/error.jsp").forward(request, response);
            return;
        }

        int leadId = Integer.parseInt(leadIdStr);
        List<FollowUp> daftar = followUpDAO.findByLeadId(leadId);

        // Isi daftarReminder tiap FollowUp agar bisa ditampilkan di JSP
        for (FollowUp fu : daftar) {
            List<Reminder> reminders = reminderDAO.findByFollowupId(fu.getId());
            fu.setDaftarReminder(reminders);
        }

        request.setAttribute("daftarFollowUp", daftar);
        request.setAttribute("leadId", leadId);
        request.getRequestDispatcher("/WEB-INF/jsp/followup/list.jsp").forward(request, response);
    }

    /**
     * Mencatat aktivitas follow-up baru (INSERT ke tabel followups).
     *
     * <p>Membutuhkan parameter request:
     * {@code leadId}, {@code salesId}, {@code notes}, {@code followupDate} (format yyyy-MM-dd'T'HH:mm).</p>
     *
     * <p>Setelah berhasil INSERT, otomatis membuat Reminder terkait dan
     * mengirim Notifikasi ke Sales yang bersangkutan.</p>
     */
    private void catatAktivitas(HttpServletRequest request,
                                HttpServletResponse response)
            throws ServletException, IOException {

        // Ambil dan validasi parameter
        String leadIdStr   = request.getParameter("leadId");
        String salesIdStr  = request.getParameter("salesId");
        String notes       = request.getParameter("notes");
        String tglStr      = request.getParameter("followupDate");

        if (leadIdStr == null || salesIdStr == null
                || notes == null || notes.trim().isEmpty()
                || tglStr == null || tglStr.trim().isEmpty()) {
            request.setAttribute("pesan", "Semua field wajib diisi.");
            request.getRequestDispatcher("/WEB-INF/jsp/error.jsp").forward(request, response);
            return;
        }

        int leadId  = Integer.parseInt(leadIdStr);
        int salesId = Integer.parseInt(salesIdStr);
        Date followupDate = parseDate(tglStr);

        if (followupDate == null) {
            request.setAttribute("pesan", "Format tanggal tidak valid.");
            request.getRequestDispatcher("/WEB-INF/jsp/error.jsp").forward(request, response);
            return;
        }

        // Buat & simpan FollowUp
        FollowUp followUp = new FollowUp(leadId, salesId, notes.trim(), followupDate);
        boolean berhasil = followUpDAO.save(followUp);

        if (berhasil) {
            // Buat Reminder otomatis untuk follow-up ini
            Reminder reminder = new Reminder(followUp.getId(), followupDate);
            reminderDAO.save(reminder);

            // Kirim notifikasi ke Sales
            kirimNotifikasi(salesId, followUp.getId(),
                    "Follow-up baru dijadwalkan untuk lead #" + leadId, followupDate);
        }

        // Redirect kembali ke daftar follow-up lead ini
        response.sendRedirect(request.getContextPath()
                + "/followup?action=list&leadId=" + leadId
                + (berhasil ? "&sukses=catat" : "&gagal=catat"));
    }

    /**
     * Mengedit catatan (notes) pada follow-up yang sudah ada (UPDATE).
     *
     * <p>Membutuhkan parameter request: {@code followupId}, {@code notes}.</p>
     */
    private void editCatatan(HttpServletRequest request,
                             HttpServletResponse response)
            throws ServletException, IOException {

        String followupIdStr = request.getParameter("followupId");
        String notes         = request.getParameter("notes");

        if (followupIdStr == null || notes == null || notes.trim().isEmpty()) {
            request.setAttribute("pesan", "followupId dan catatan wajib diisi.");
            request.getRequestDispatcher("/WEB-INF/jsp/error.jsp").forward(request, response);
            return;
        }

        int followupId = Integer.parseInt(followupIdStr);
        FollowUp followUp = followUpDAO.findById(followupId);

        if (followUp == null) {
            request.setAttribute("pesan", "Follow-up tidak ditemukan.");
            request.getRequestDispatcher("/WEB-INF/jsp/error.jsp").forward(request, response);
            return;
        }

        followUp.setNotes(notes.trim());
        boolean berhasil = followUpDAO.update(followUp);

        response.sendRedirect(request.getContextPath()
                + "/followup?action=list&leadId=" + followUp.getLeadId()
                + (berhasil ? "&sukses=edit" : "&gagal=edit"));
    }

    /**
     * Menandai follow-up sebagai selesai (UPDATE status → "Selesai").
     *
     * <p>Membutuhkan parameter request: {@code followupId}.</p>
     *
     * <p>Setelah status di-update, semua Reminder terkait juga
     * ditandai selesai.</p>
     */
    private void tandaiSelesai(HttpServletRequest request,
                               HttpServletResponse response)
            throws ServletException, IOException {

        String followupIdStr = request.getParameter("followupId");

        if (followupIdStr == null) {
            response.sendRedirect(request.getContextPath() + "/reminder");
            return;
        }

        int followupId = Integer.parseInt(followupIdStr);
        FollowUp followUp = followUpDAO.findById(followupId);

        if (followUp == null) {
            request.setAttribute("pesan", "Follow-up tidak ditemukan.");
            request.getRequestDispatcher("/WEB-INF/jsp/error.jsp").forward(request, response);
            return;
        }

        // Update status FollowUp
        boolean berhasil = followUpDAO.updateStatus(followupId, FollowUp.STATUS_DONE);

        if (berhasil) {
            // Update status semua Reminder terkait
            List<Reminder> reminders = reminderDAO.findByFollowupId(followupId);
            for (Reminder r : reminders) {
                r.setStatus(Reminder.STATUS_DONE);
                reminderDAO.update(r);
            }
        }

        response.sendRedirect(request.getContextPath()
                + "/followup?action=list&leadId=" + followUp.getLeadId()
                + (berhasil ? "&sukses=selesai" : "&gagal=selesai"));
    }

    /**
     * Menghapus follow-up beserta semua Reminder-nya (DELETE).
     *
     * <p>Membutuhkan parameter request: {@code followupId}.</p>
     *
     * <p>Urutan penghapusan: Reminder dulu → baru FollowUp,
     * agar tidak melanggar foreign key constraint.</p>
     */
    private void hapusFollowUp(HttpServletRequest request,
                               HttpServletResponse response)
            throws ServletException, IOException {

        String followupIdStr = request.getParameter("followupId");

        if (followupIdStr == null) {
            response.sendRedirect(request.getContextPath() + "/reminder");
            return;
        }

        int followupId = Integer.parseInt(followupIdStr);
        FollowUp followUp = followUpDAO.findById(followupId);

        if (followUp == null) {
            request.setAttribute("pesan", "Follow-up tidak ditemukan.");
            request.getRequestDispatcher("/WEB-INF/jsp/error.jsp").forward(request, response);
            return;
        }

        int leadId = followUp.getLeadId();

        // Hapus semua Reminder terkait dulu
        List<Reminder> reminders = reminderDAO.findByFollowupId(followupId);
        for (Reminder r : reminders) {
            reminderDAO.delete(r.getId());
        }

        // Hapus FollowUp
        boolean berhasil = followUpDAO.delete(followupId);

        response.sendRedirect(request.getContextPath()
                + "/followup?action=list&leadId=" + leadId
                + (berhasil ? "&sukses=hapus" : "&gagal=hapus"));
    }

    // =========================================================================
    // UTILITAS PRIVAT
    // =========================================================================

    /**
     * Mem-parse String tanggal dari input datetime-local HTML ke {@code java.util.Date}.
     *
     * @param tglStr String tanggal format "yyyy-MM-dd'T'HH:mm".
     * @return objek Date, atau {@code null} jika parsing gagal.
     */
    private Date parseDate(String tglStr) {
        try {
            return new SimpleDateFormat(FORMAT_DATETIME).parse(tglStr);
        } catch (ParseException e) {
            System.out.println("[FollowUpController] Format tanggal tidak valid: " + tglStr);
            return null;
        }
    }

    /**
     * Membuat dan menyimpan Notifikasi ke database setelah aksi follow-up.
     *
     * @param userId      id user penerima notifikasi.
     * @param followupId  id follow-up terkait.
     * @param message     isi pesan notifikasi.
     * @param reminderDate tanggal reminder (sama dengan followupDate).
     */
    private void kirimNotifikasi(int userId, int followupId,
                                 String message, Date reminderDate) {
        Notifikasi notif = new Notifikasi();
        notif.setUserId(userId);
        notif.setFollowupId(followupId);
        notif.setMessage(message);
        notif.setSentAt(new Date());
        notif.setReminderDate(reminderDate);
        notif.setIsRead(false);
        notif.setStatus(Reminder.STATUS_PENDING);
        notifikasiDAO.save(notif);
    }
}