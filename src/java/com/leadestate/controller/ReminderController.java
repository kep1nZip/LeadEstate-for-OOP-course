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

@WebServlet("/reminder")
public class ReminderController extends HttpServlet {

    private final ReminderDAO reminderDAO     = new ReminderDAO();
    private final FollowUpDAO followUpDAO     = new FollowUpDAO();
    private final NotifikasiDAO notifikasiDAO = new NotifikasiDAO();

    // doGet — menampilkan halaman Reminder & Follow-up

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
        List<FollowUp> followUpHariIni  = filterUnikPerLead(followUpDAO.findHariIniOlehSales(salesId));
        List<FollowUp> followUpTertunda = filterUnikPerLead(followUpDAO.findTertundaOlehSales(salesId));
        List<FollowUp> followUpSelesai  = filterUnikPerLead(cariSelesaiOlehSales(salesId));
        List<FollowUp> followUpSemua    = filterUnikPerLead(followUpDAO.findBySalesId(salesId));

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
            
            // --- BARIS TAMBAHAN: Kirim nama ke judul panel ---
            if (!riwayatFollowUp.isEmpty()) {
                request.setAttribute("namaLeadDipilih", riwayatFollowUp.get(0).getLeadName());
            }
        }

        // --- Badge notifikasi di header ---
        int jumlahNotifBelumDibaca = notifikasiDAO.countUnreadByUserId(salesId);
        request.setAttribute("jumlahNotif", jumlahNotifBelumDibaca);

        request.getRequestDispatcher("/WEB-INF/views/reminder.jsp").forward(request, response);
    }

    // doPost — aksi ubah data

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

    // HANDLER PRIVAT

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

    // UTILITAS PRIVAT

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

    private void isiReminder(List<FollowUp> daftarFollowUp) {
        for (FollowUp fu : daftarFollowUp) {
            List<Reminder> reminders = reminderDAO.findByFollowupId(fu.getId());
            fu.setDaftarReminder(reminders);
        }
    }
    
    private List<FollowUp> filterUnikPerLead(List<FollowUp> daftarMentah) {
        List<FollowUp> daftarUnik = new ArrayList<>();
        java.util.Set<Integer> setLeadId = new java.util.HashSet<>();
        
        for (FollowUp fu : daftarMentah) {
            if (!setLeadId.contains(fu.getLeadId())) {
                daftarUnik.add(fu);
                setLeadId.add(fu.getLeadId());
            }
        }
        return daftarUnik;
    }
}
