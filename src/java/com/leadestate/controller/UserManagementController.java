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

@WebServlet("/manajemen-sales")
public class UserManagementController extends HttpServlet {

    // Status id Closed Won (sesuai leadestate_v2.sql)
    private static final int STATUS_CLOSED_WON = 5;

    // DEPENDENSI DAO

    /** DAO untuk operasi CRUD user. */
    private UserDAO userDAO;

    /** DAO untuk statistik dan re-assign Lead. */
    private LeadDAO leadDAO;

    /** DAO untuk statistik FollowUp per Sales. */
    private FollowUpDAO followUpDAO;

    // INISIALISASI SERVLET

    @Override
    public void init() throws ServletException {
        userDAO     = new UserDAO();
        leadDAO     = new LeadDAO();
        followUpDAO = new FollowUpDAO();
    }

    // HTTP GET — Routing berdasarkan parameter "aksi"
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!cekAksesAdmin(request, response)) return;

        String aksi = request.getParameter("aksi");

        if (aksi == null || aksi.isEmpty()) {
            tampilDaftarSales(request, response);
            return;
        }

        switch (aksi) {
            case "detail":
                tampilDetailSales(request, response);
                break;
            case "tambah":
                tampilFormTambah(request, response);
                break;
            case "edit":
                tampilFormEdit(request, response);
                break;
            default:
                tampilDaftarSales(request, response);
                break;
        }
    }

    // HTTP POST — Routing berdasarkan parameter "aksi"
 
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!cekAksesAdmin(request, response)) return;

        String aksi = request.getParameter("aksi");
        if (aksi == null) {
            response.sendRedirect(request.getContextPath() + "/manajemen-sales");
            return;
        }

        switch (aksi) {
            case "simpan":
                prosesSimpanSales(request, response);
                break;
            case "hapus":
                prosesHapusSales(request, response);
                break;
            case "reassign":
                prosesReassignLead(request, response);
                break;
            default:
                response.sendRedirect(request.getContextPath() + "/manajemen-sales");
                break;
        }
    }

    // HANDLER GET — Daftar Sales

    private void tampilDaftarSales(HttpServletRequest request,
                                   HttpServletResponse response)
            throws ServletException, IOException {

        // Ambil semua user dengan role Sales menggunakan findByRole() yang tersedia
        List<User> semuaSales = userDAO.findByRole(Sales.ROLE_NAME);

        // Hitung statistik per Sales
        List<Object[]> daftarSalesData = new ArrayList<>();
        for (User u : semuaSales) {
            List<Lead> leadSales = leadDAO.findBySalesId(u.getId());
            int totalClosing = 0;
            for (Lead l : leadSales) {
                if (l.getStatusId() == STATUS_CLOSED_WON) {
                    totalClosing++;
                }
            }
            // Object[]: [User/Sales, totalLead, totalClosing]
            daftarSalesData.add(new Object[]{u, leadSales.size(), totalClosing});
        }

        // Urutkan: closing terbanyak di atas
        daftarSalesData.sort((a, b) -> Integer.compare((int) b[2], (int) a[2]));

        request.setAttribute("daftarSalesData", daftarSalesData);
        request.setAttribute("namaUser", request.getSession().getAttribute("namaUser"));

        // Ambil flash message dari session jika ada (hasil operasi sebelumnya)
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("flashPesan") != null) {
            request.setAttribute("flashPesan", session.getAttribute("flashPesan"));
            session.removeAttribute("flashPesan");
        }

        request.getRequestDispatcher("/WEB-INF/views/manajemen-sales.jsp")
               .forward(request, response);
    }

    // HANDLER GET — Detail Sales
    
    private void tampilDetailSales(HttpServletRequest request,
                                   HttpServletResponse response)
            throws ServletException, IOException {

        int salesId = parseId(request.getParameter("id"));
        if (salesId <= 0) {
            redirect(response, request, "/manajemen-sales");
            return;
        }

        User sales = userDAO.findById(salesId);
        if (sales == null || sales.getRoleId() != Sales.ROLE_ID) {
            setFlash(request, "Sales tidak ditemukan.");
            redirect(response, request, "/manajemen-sales");
            return;
        }

        // Statistik lead
        List<Lead> daftarLead = leadDAO.findBySalesId(salesId);
        int totalLead    = daftarLead.size();
        int totalClosing = 0;
        for (Lead l : daftarLead) {
            if (l.getStatusId() == STATUS_CLOSED_WON) totalClosing++;
        }

        // Statistik follow-up menggunakan findBySalesId() dari FollowUpDAO
        List<FollowUp> semuaFU  = followUpDAO.findBySalesId(salesId);
        int totalFollowUp       = semuaFU.size();

        // Persentase pencapaian (target default 10 closing)
        final int TARGET_CLOSING = 10;
        double pctPencapaian = totalClosing > 0
                ? Math.min((double) totalClosing / TARGET_CLOSING * 100.0, 100.0)
                : 0.0;

        // Ambil 5 aktivitas (follow-up) terbaru: findBySalesId sudah urut ASC,
        // ambil dari belakang untuk mendapat yang terbaru
        int dariIndex = Math.max(0, semuaFU.size() - 5);
        List<FollowUp> aktivitasTerbaru = semuaFU.subList(dariIndex, semuaFU.size());

        request.setAttribute("sales",            sales);
        request.setAttribute("totalLead",        totalLead);
        request.setAttribute("totalFollowUp",    totalFollowUp);
        request.setAttribute("totalClosing",     totalClosing);
        request.setAttribute("pctPencapaian",    pctPencapaian);
        request.setAttribute("daftarLead",       daftarLead);
        request.setAttribute("aktivitasTerbaru", aktivitasTerbaru);
        request.setAttribute("namaUser", request.getSession().getAttribute("namaUser"));

        request.getRequestDispatcher("/WEB-INF/views/detail-sales.jsp")
               .forward(request, response);
    }

    // HANDLER GET — Form Tambah

    private void tampilFormTambah(HttpServletRequest request,
                                  HttpServletResponse response)
            throws ServletException, IOException {

        request.setAttribute("mode",    "tambah");
        request.setAttribute("namaUser", request.getSession().getAttribute("namaUser"));
        request.getRequestDispatcher("/WEB-INF/views/form-sales.jsp")
               .forward(request, response);
    }

    // HANDLER GET — Form Edit

    private void tampilFormEdit(HttpServletRequest request,
                                HttpServletResponse response)
            throws ServletException, IOException {

        int salesId = parseId(request.getParameter("id"));
        if (salesId <= 0) {
            redirect(response, request, "/manajemen-sales");
            return;
        }

        User sales = userDAO.findById(salesId);
        if (sales == null) {
            redirect(response, request, "/manajemen-sales");
            return;
        }

        request.setAttribute("mode",    "edit");
        request.setAttribute("sales",   sales);
        request.setAttribute("namaUser", request.getSession().getAttribute("namaUser"));
        request.getRequestDispatcher("/WEB-INF/views/form-sales.jsp")
               .forward(request, response);
    }

    // HANDLER POST — Simpan (Tambah / Edit)

    private void prosesSimpanSales(HttpServletRequest request,
                                   HttpServletResponse response)
            throws ServletException, IOException {

        String idParam  = request.getParameter("id");
        String name     = request.getParameter("name");
        String email    = request.getParameter("email");
        String password = request.getParameter("password");

        // Validasi input wajib
        if (isKosong(name) || isKosong(email)) {
            setFlash(request, "Nama dan email wajib diisi.");
            String tujuan = isKosong(idParam)
                    ? "/manajemen-sales?aksi=tambah"
                    : "/manajemen-sales?aksi=edit&id=" + idParam;
            redirect(response, request, tujuan);
            return;
        }

        // Cek duplikasi email
        User existingUser = userDAO.findByEmail(email.trim());

        boolean berhasil;

        if (isKosong(idParam)) {
            // --- TAMBAH Sales baru ---
            if (isKosong(password)) {
                setFlash(request, "Password wajib diisi untuk Sales baru.");
                redirect(response, request, "/manajemen-sales?aksi=tambah");
                return;
            }
            // Cek email sudah terdaftar
            if (existingUser != null) {
                setFlash(request, "Email sudah terdaftar. Gunakan email lain.");
                redirect(response, request, "/manajemen-sales?aksi=tambah");
                return;
            }
            Sales salesBaru = new Sales(0, name.trim(), email.trim(), password.trim());
            userDAO.save(salesBaru);
            berhasil = true;

        } else {
            // --- EDIT Sales yang sudah ada ---
            int salesId = parseId(idParam);
            if (salesId <= 0) {
                redirect(response, request, "/manajemen-sales");
                return;
            }
            User salesLama = userDAO.findById(salesId);
            // Baca roleId jika dikirim dari form settings
String roleIdParam = request.getParameter("roleId");
if (roleIdParam != null && !roleIdParam.trim().isEmpty()) {
    try {
        salesLama.setRoleId(Integer.parseInt(roleIdParam.trim()));
    } catch (NumberFormatException ignored) {}
}
            if (salesLama == null) {
                redirect(response, request, "/manajemen-sales");
                return;
            }
            // Cek email duplikasi (kecuali milik diri sendiri)
            if (existingUser != null && existingUser.getId() != salesId) {
                setFlash(request, "Email sudah digunakan user lain.");
                redirect(response, request, "/manajemen-sales?aksi=edit&id=" + salesId);
                return;
            }
            salesLama.setName(name.trim());
            salesLama.setEmail(email.trim());
            if (!isKosong(password)) {
                salesLama.setPassword(password.trim());
            }
            berhasil = userDAO.update(salesLama);
        }

        setFlash(request, berhasil
                ? "Data Sales berhasil disimpan."
                : "Gagal menyimpan data Sales.");
        redirect(response, request, "/manajemen-sales");
    }

    // HANDLER POST — Hapus Sales

    private void prosesHapusSales(HttpServletRequest request,
                                  HttpServletResponse response)
            throws ServletException, IOException {

        int salesId     = parseId(request.getParameter("id"));
        int userIdLogin = (int) request.getSession().getAttribute("userId");

        if (salesId <= 0) {
            redirect(response, request, "/manajemen-sales");
            return;
        }
        if (salesId == userIdLogin) {
            setFlash(request, "Tidak dapat menghapus akun sendiri.");
            redirect(response, request, "/manajemen-sales");
            return;
        }

        boolean berhasil = userDAO.delete(salesId);
        setFlash(request, berhasil
                ? "Sales berhasil dihapus."
                : "Gagal menghapus Sales.");
        redirect(response, request, "/manajemen-sales");
    }

    // HANDLER POST — Re-assign Lead

    private void prosesReassignLead(HttpServletRequest request,
                                    HttpServletResponse response)
            throws ServletException, IOException {

        int salesIdLama = parseId(request.getParameter("salesIdLama"));
        int salesIdBaru = parseId(request.getParameter("salesIdBaru"));

        if (salesIdLama <= 0 || salesIdBaru <= 0 || salesIdLama == salesIdBaru) {
            setFlash(request, "Data re-assign tidak valid.");
            redirect(response, request, "/manajemen-sales");
            return;
        }

        // Ambil semua lead milik Sales lama lalu pindahkan satu per satu
        List<Lead> leadDipindah = leadDAO.findBySalesId(salesIdLama);
        int berhasilCount = 0;
        for (Lead l : leadDipindah) {
            l.setSalesId(salesIdBaru);
            if (leadDAO.update(l)) berhasilCount++;
        }

        setFlash(request, berhasilCount + " Lead berhasil dipindahkan ke Sales baru.");
        redirect(response, request, "/manajemen-sales?aksi=detail&id=" + salesIdBaru);
    }

    // UTILITY — Helper method

    private boolean cekAksesAdmin(HttpServletRequest request,
                                  HttpServletResponse response)
            throws IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return false;
        }
        int roleId = (int) session.getAttribute("roleId");
        if (roleId != Admin.ROLE_ID) {
            response.sendRedirect(request.getContextPath() + "/followup");
            return false;
        }
        return true;
    }


    private int parseId(String param) {
        if (param == null || param.trim().isEmpty()) return -1;
        try {
            return Integer.parseInt(param.trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    /** Cek apakah String null atau kosong (setelah trim). */
    private boolean isKosong(String s) {
        return s == null || s.trim().isEmpty();
    }

    /** Simpan flash message ke session. */
    private void setFlash(HttpServletRequest request, String pesan) {
        request.getSession().setAttribute("flashPesan", pesan);
    }

    /** Redirect ke path relatif (konteks sudah ditambahkan). */
    private void redirect(HttpServletResponse response,
                          HttpServletRequest request,
                          String path) throws IOException {
        response.sendRedirect(request.getContextPath() + path);
    }
}
