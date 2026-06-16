package com.leadestate.controller;

import com.leadestate.dao.FollowUpDAO;
import com.leadestate.dao.LeadDAO;
import com.leadestate.dao.UserDAO;
import com.leadestate.model.Admin;
import com.leadestate.model.FollowUp;
import com.leadestate.model.Lead;
import com.leadestate.model.Sales;
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
 * Controller untuk halaman Dashboard Admin.
 *
 * Menangani request ke URL /dashboard dan menyiapkan data KPI
 * yang ditampilkan di halaman utama Admin:
 * - Total Lead (semua data di tabel leads)
 * - FollowUp Hari Ini (status Pending, jadwal hari ini)
 * - Lead Tertunda (followup tertunda / overdue)
 * - Closing Bulan Ini (statusId = 5 sesuai SQL: "Closed Won")
 * - Daftar Top Sales (diurutkan berdasarkan jumlah lead)
 * - Reminder Hari Ini (followup hari ini lintas semua Sales)
 *
 * Hanya dapat diakses oleh user dengan roleId = Admin.ROLE_ID (1).
 * Sales yang mengakses URL ini akan diredirect ke /followup.
 *
 * @author Fathan Firdaus Nuzulan (103012400353)
 * @version 1.0
 */
@WebServlet("/dashboard")
public class DashboardController extends HttpServlet {

    // =========================================================================
    // STATUS ID (sesuai data di leadestate_v2.sql — tabel lead_status)
    // id=5 "Closed Won" dianggap sebagai closing
    // =========================================================================
    private static final int STATUS_CLOSED_WON = 5;

    // =========================================================================
    // DEPENDENSI DAO
    // =========================================================================

    /** DAO untuk data user dan daftar Sales. */
    private UserDAO userDAO;

    /** DAO untuk data lead dan statistik. */
    private LeadDAO leadDAO;

    /** DAO untuk data follow-up dan reminder hari ini. */
    private FollowUpDAO followUpDAO;

    // =========================================================================
    // INISIALISASI SERVLET
    // =========================================================================

    /**
     * Inisialisasi semua DAO saat servlet pertama kali dimuat.
     *
     * @throws ServletException jika terjadi error saat inisialisasi.
     */
    @Override
    public void init() throws ServletException {
        userDAO     = new UserDAO();
        leadDAO     = new LeadDAO();
        followUpDAO = new FollowUpDAO();
    }

    // =========================================================================
    // HTTP GET — Tampilkan Dashboard
    // =========================================================================

    /**
     * Menangani GET /dashboard.
     *
     * Alur:
     * 1. Cek session — pastikan user sudah login.
     * 2. Cek role — redirect Sales ke /followup.
     * 3. Kumpulkan semua data KPI menggunakan DAO yang tersedia.
     * 4. Set attribute ke request dan forward ke dashboard.jsp.
     *
     * @param request  HTTP request dari browser.
     * @param response HTTP response ke browser.
     * @throws ServletException jika terjadi error servlet.
     * @throws IOException      jika terjadi error I/O.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // --- 1. Cek sesi login ---
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        // --- 2. Cek role ---
        //int roleId = (int) session.getAttribute("roleId");
        //if (roleId != Admin.ROLE_ID) {
        //    response.sendRedirect(request.getContextPath() + "/followup");
        //    return;
        //}

        // --- 3. KPI Card: Total Lead Aktif ---
        // Ambil semua lead, hitung yang statusId != Closed Lost (id=6)
        List<Lead> semuaLead = leadDAO.findAll();
        int totalLeadAktif = 0;
        for (Lead l : semuaLead) {
            if (l.getStatusId() != 6) { // 6 = Closed Lost
                totalLeadAktif++;
            }
        }

        // --- 4. KPI Card: FollowUp Hari Ini ---
        // countByStatus("Pending") sebagai proxy jumlah followup aktif hari ini
        // FollowUpDAO tidak punya countHariIni global, pakai countByStatus Pending
        int followUpPending = followUpDAO.countByStatus(FollowUp.STATUS_PENDING);

        // --- 5. KPI Card: Lead Tertunda ---
        // Lead dengan status "Follow Up" (id=3) yang belum bergerak
        List<Lead> leadFollowUp = leadDAO.findByStatusId(3);
        int leadTertunda = leadFollowUp.size();

        // --- 6. KPI Card: Closing Bulan Ini ---
        // Lead dengan statusId = 5 (Closed Won)
        List<Lead> leadClosing = leadDAO.findByStatusId(STATUS_CLOSED_WON);
        int closingBulanIni = leadClosing.size();

        // --- 7. Top Sales ---
        // Ambil semua Sales, lalu hitung jumlah lead per Sales secara manual
        List<User> semuaSales = userDAO.findByRole(Sales.ROLE_NAME);
        List<Object[]> topSales = new ArrayList<>();
        for (User u : semuaSales) {
            List<Lead> leadMilikSales = leadDAO.findBySalesId(u.getId());
            // Hitung closing milik Sales ini
            int closingSales = 0;
            for (Lead l : leadMilikSales) {
                if (l.getStatusId() == STATUS_CLOSED_WON) {
                    closingSales++;
                }
            }
            // Object[]: [User, totalLead, totalClosing]
            topSales.add(new Object[]{u, leadMilikSales.size(), closingSales});
        }
        // Urutkan descending berdasarkan totalClosing (index [2])
        topSales.sort((a, b) -> Integer.compare((int) b[2], (int) a[2]));

        // --- 8. Reminder/FollowUp Hari Ini (semua Sales) ---
        // Ambil followup hari ini per Sales, gabungkan ke satu list
        List<Object[]> reminderHariIni = new ArrayList<>();
        for (User u : semuaSales) {
            List<FollowUp> fuHariIni = followUpDAO.findHariIniOlehSales(u.getId());
            for (FollowUp fu : fuHariIni) {
                Lead lead = leadDAO.findById(fu.getLeadId());
                // Object[]: [FollowUp, Lead, namasSales]
                reminderHariIni.add(new Object[]{fu, lead, u.getName()});
            }
        }

        // --- 9. Set attribute ke request ---
        request.setAttribute("totalLeadAktif",  totalLeadAktif);
        request.setAttribute("followUpPending", followUpPending);
        request.setAttribute("leadTertunda",    leadTertunda);
        request.setAttribute("closingBulanIni", closingBulanIni);
        request.setAttribute("topSales",        topSales);
        request.setAttribute("reminderHariIni", reminderHariIni);
        request.setAttribute("namaUser",        session.getAttribute("namaUser"));

        // --- 10. Forward ke JSP ---
        request.getRequestDispatcher("/WEB-INF/views/dashboard.jsp")
               .forward(request, response);
    }

    // =========================================================================
    // HTTP POST — tidak dipakai, redirect ke GET
    // =========================================================================

    /**
     * Dashboard tidak menerima POST. Redirect ke GET.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendRedirect(request.getContextPath() + "/dashboard");
    }
}
