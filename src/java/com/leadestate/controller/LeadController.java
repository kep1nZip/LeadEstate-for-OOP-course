package com.leadestate.controller;

import com.leadestate.dao.FollowUpDAO;
import com.leadestate.dao.LeadDAO;
import com.leadestate.dao.UserDAO;
import com.leadestate.dao.LeadStatusDAO;
import com.leadestate.dao.PropertyDAO;
import com.leadestate.model.Lead;
import com.leadestate.model.LeadStatus;
import com.leadestate.model.Property;

import java.io.IOException;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Controller (Servlet) untuk fitur Core Lead Management (Data Lead).
 *
 * <p>
 * Versi adaptasi untuk halaman tunggal {@code lead.jsp} dengan modal (form
 * tambah/edit & detail muncul sebagai overlay di atas list, tanpa file JSP
 * terpisah seperti versi awal). Modal dikendalikan lewat attribute
 * {@code modalMode} ("none" / "form" / "detail") yang dibaca oleh lead.jsp
 * untuk menampilkan/menyembunyikan elemen modal.</p>
 *
 * <p>
 * Aksi (action) dikirim lewat parameter request {@code action} ATAU
 * {@code aksi} (alias — keduanya didukung agar fleksibel dengan gaya penamaan
 * form di lead.jsp).</p>
 *
 * <p>
 * Daftar aksi:</p>
 * <ul>
 * <li>GET (default / "list") -&gt; tampilkan list, modal tertutup.</li>
 * <li>GET "form" -&gt; tampilkan list + modal form (tambah jika tanpa parameter
 * id, edit jika ada id).</li>
 * <li>GET "detail" -&gt; tampilkan list + modal detail lead (termasuk
 * daftarFollowUp).</li>
 * <li>GET "delete" -&gt; hapus lead, redirect ke list.</li>
 * <li>POST "add" -&gt; tambah lead baru.</li>
 * <li>POST "edit" -&gt; ubah data lead.</li>
 * <li>POST "changeStatus" -&gt; ubah statusId lead.</li>
 * </ul>
 *
 * <p>
 * Forward tunggal ke: {@code /WEB-INF/views/laporan.jsp}.</p>
 *
 * <p>
 * Catatan implementasi: Method Lead.save(), Lead.validateDate(), dan
 * Lead.changeStatus() pada tahap POJO hanya berisi println/placeholder (belum
 * terhubung DAO). Controller ini TETAP memanggil method-method tersebut untuk
 * validasi (validateDate(), validateStatus()) dan notifikasi
 * (notifySalesOfChange()), namun PENYIMPANAN SESUNGGUHNYA dilakukan lewat
 * LeadDAO secara langsung
 * (leadDAO.save()/update()/updateStatus()/delete()).</p>
 */
@WebServlet(name = "LeadController", urlPatterns = {"/lead"})
public class LeadController extends HttpServlet {

    private final LeadDAO leadDAO = new LeadDAO();
    private final PropertyDAO propertyDAO = new PropertyDAO();
    private final LeadStatusDAO leadStatusDAO = new LeadStatusDAO();
    private final FollowUpDAO followUpDAO = new FollowUpDAO();
    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = getAction(request);

        switch (action) {
            case "detail":
                showListWithModal(request, response, "detail");
                break;
            case "delete":
                delete(request, response);
                break;
            case "editStatus":
                showListWithModal(request, response, "editStatus");
                break;

            case "form":
                showListWithModal(request, response, "form");
                break;
            default:
                showListWithModal(request, response, "none");
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = getAction(request);

        switch (action) {
            case "add":
                addLead(request, response);
                break;
            case "edit":
                editLead(request, response);
                break;
            case "changeStatus":
                changeStatus(request, response);
                break;
            default:
                response.sendRedirect(request.getContextPath() + "/lead");
                break;
        }
    }

    /**
     * Menyiapkan SEMUA data yang dibutuhkan halaman {@code lead.jsp} (daftar
     * Lead + dropdown Property/LeadStatus), lalu menambahkan data tambahan
     * sesuai {@code modalMode}:
     * <ul>
     * <li>"none" : tidak ada data tambahan, modal tertutup.</li>
     * <li>"form" : jika ada parameter id -> mode edit (attribute {@code lead}
     * diisi); jika tidak -> mode tambah.</li>
     * <li>"detail" : attribute {@code lead}, {@code property}, {@code status}
     * diisi, serta {@code lead.daftarFollowUp} diisi via FollowUpDAO.</li>
     * </ul>
     *
     * Mendukung filter opsional lewat parameter request {@code salesId}
     * dan/atau {@code statusId} untuk daftar Lead (filter pada list tetap
     * berfungsi walau modal sedang terbuka).
     *
     * Forward ke: /WEB-INF/views/lead.jsp
     */
    private void showListWithModal(HttpServletRequest request, HttpServletResponse response,
            String modalMode) throws ServletException, IOException {

        // --- 1. Daftar Lead (dengan filter opsional) ---
        String salesIdParam = request.getParameter("salesId");
        String statusIdParam = request.getParameter("statusId");

        List<Lead> daftarLead;
        if (salesIdParam != null && !salesIdParam.isEmpty()) {
            daftarLead = leadDAO.findBySalesId(Integer.parseInt(salesIdParam));
        } else if (statusIdParam != null && !statusIdParam.isEmpty()
                && "none".equals(modalMode)) {
            // Filter statusId hanya dipakai untuk filter LIST.
            // Saat modalMode != none, "statusId" pada parameter biasanya
            // merujuk pada id Lead yang dibuka modal-nya (lihat di bawah),
            // jadi filter list tidak ikut memakainya untuk menghindari
            // ambiguitas.
            daftarLead = leadDAO.findByStatusId(Integer.parseInt(statusIdParam));
        } else {
            daftarLead = leadDAO.findAll();
        }

        request.setAttribute("daftarLead", daftarLead);
        request.setAttribute("daftarProperty", propertyDAO.findAll());
        request.setAttribute("daftarStatus", leadStatusDAO.findAll());
        request.setAttribute("modalMode", modalMode);
        request.setAttribute("daftarSales", userDAO.findByRole("Sales"));

        // --- 2. Data tambahan sesuai modalMode ---
        switch (modalMode) {
            case "form":
                String idParam = request.getParameter("id");
                if (idParam != null && !idParam.isEmpty()) {
                    Lead lead = leadDAO.findById(Integer.parseInt(idParam));
                    if (lead == null) {
                        request.setAttribute("errorMessage", "Lead tidak ditemukan.");
                        request.setAttribute("modalMode", "none");
                    } else {
                        request.setAttribute("lead", lead);
                    }
                }
                // Jika idParam kosong -> mode tambah, attribute "lead" tidak di-set,
                // lead.jsp menampilkan form kosong.
                break;

            case "detail":
                int id = parseIntOrZero(request.getParameter("id"));
                Lead lead = leadDAO.findById(id);

                if (lead == null) {
                    request.setAttribute("errorMessage", "Lead dengan id=" + id + " tidak ditemukan.");
                    request.setAttribute("modalMode", "none");
                    break;
                }

                Property property = propertyDAO.findById(lead.getPropertyId());
                LeadStatus status = leadStatusDAO.findById(lead.getStatusId());

                // Isi riwayat FollowUp untuk ditampilkan di timeline modal detail.
                lead.setDaftarFollowUp(followUpDAO.findByLeadId(lead.getId()));

                request.setAttribute("lead", lead);
                request.setAttribute("property", property);
                request.setAttribute("status", status);
                break;

            case "editStatus":
                int idStatus = parseIntOrZero(request.getParameter("id"));
                Lead leadStatus = leadDAO.findById(idStatus);
                if (leadStatus == null) {
                    request.setAttribute("errorMessage", "Lead tidak ditemukan");
                    request.setAttribute("modalMode", "none");
                } else {
                    request.setAttribute("lead", leadStatus);
                }
                break;

            default:
                // "none" -> tidak ada data tambahan.
                break;
        }

        RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/views/lead.jsp");
        rd.forward(request, response);
    }

    /**
     * Menambahkan Lead baru.
     *
     * Alur: bentuk objek Lead dari parameter form -> inputData() ->
     * validateDate() -> jika valid, leadDAO.save().
     *
     * Jika gagal validasi, redirect kembali ke list dengan modal form terbuka
     * kembali (lewat parameter action=form) dan errorMessage dikirim via flash
     * attribute sederhana (query param).
     */
    private void addLead(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Lead lead = new Lead();
        lead.setSalesId(parseIntOrZero(request.getParameter("salesId")));
        lead.setStatusId(parseIntOrZero(request.getParameter("statusId")));
        lead.setSource(request.getParameter("source"));

        // inputData() mengisi name, email, phone, propertyId sekaligus
        // melakukan validasi dasar (lihat Lead.java).
        lead.inputData(
                request.getParameter("name"),
                request.getParameter("email"),
                request.getParameter("phone"),
                parseIntOrZero(request.getParameter("propertyId"))
        );

        if (!lead.validateDate()) {
            // Tampilkan kembali list dengan modal form terbuka + pesan error.
            request.setAttribute("errorMessage", "Data lead tidak valid. Periksa kembali isian form.");
            request.setAttribute("lead", lead);
            showListWithModal(request, response, "form");
            return;
        }

        lead.setCreatedAt(new java.util.Date());

        boolean berhasil = leadDAO.save(lead);
        String redirectUrl = request.getContextPath() + "/lead"
                + (berhasil ? "?sukses=tambah" : "?gagal=tambah");
        response.sendRedirect(redirectUrl);
    }

    /**
     * Mengubah data Lead yang sudah ada (kecuali statusId, lihat
     * changeStatus()).
     */
    private void editLead(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int id = parseIntOrZero(request.getParameter("id"));
        Lead lead = leadDAO.findById(id);

        if (lead == null) {
            response.sendRedirect(request.getContextPath() + "/lead?gagal=editNotFound");
            return;
        }

        lead.inputData(
                request.getParameter("name"),
                request.getParameter("email"),
                request.getParameter("phone"),
                parseIntOrZero(request.getParameter("propertyId"))
        );
        lead.setSalesId(parseIntOrZero(request.getParameter("salesId")));
        lead.setSource(request.getParameter("source"));

        if (!lead.validateDate()) {
            request.setAttribute("errorMessage", "Data lead tidak valid. Periksa kembali isian form.");
            request.setAttribute("lead", lead);
            showListWithModal(request, response, "form");
            return;
        }

        leadDAO.update(lead);
        response.sendRedirect(request.getContextPath()
                + "/lead?action=detail&id=" + lead.getId() + "&sukses=edit");
    }

    /**
     * Mengubah status Lead.
     *
     * Alur: ambil Lead -> validateStatus(newStatusId) -> jika valid,
     * leadDAO.updateStatus() -> catat riwayat via LeadStatus.saveHistory()
     * (dipanggil di dalam Lead.changeStatus()) -> notifySalesOfChange().
     *
     * Catatan: Lead.changeStatus() di model TIDAK menyimpan ke database (masih
     * placeholder println), jadi controller memanggil leadDAO.updateStatus()
     * secara terpisah setelah validasi.
     */
    private void changeStatus(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int id = parseIntOrZero(request.getParameter("id"));
        int newStatusId = parseIntOrZero(request.getParameter("statusId"));

        Lead lead = leadDAO.findById(id);
        if (lead == null) {
            response.sendRedirect(request.getContextPath() + "/lead?gagal=statusNotFound");
            return;
        }

        if (!lead.validateStatus(newStatusId)) {
            response.sendRedirect(request.getContextPath()
                    + "/lead?action=detail&id=" + id + "&gagal=statusInvalid");
            return;
        }

        // changeStatus() di model akan: mencatat riwayat (LeadStatus.saveHistory),
        // memperbarui field statusId pada objek lead di memori, dan memanggil
        // notifySalesOfChange().
        lead.changeStatus(newStatusId);

        // Simpan perubahan statusId ke database.
        leadDAO.updateStatus(id, newStatusId);

        response.sendRedirect(request.getContextPath()
                + "/lead?action=detail&id=" + id + "&sukses=status");
    }

    /**
     * Menghapus Lead berdasarkan id (FollowUp & Reminder terkait ikut terhapus
     * otomatis lewat ON DELETE CASCADE pada database).
     */
    private void delete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int id = parseIntOrZero(request.getParameter("id"));
        boolean berhasil = leadDAO.delete(id);
        response.sendRedirect(request.getContextPath() + "/lead"
                + (berhasil ? "?sukses=hapus" : "?gagal=hapus"));
    }

    // ===== Helper =====
    /**
     * Mengambil nilai aksi dari parameter request, mendukung dua nama
     * parameter: {@code action} (default, dipakai versi sebelumnya) dan
     * {@code aksi} (alias, gaya penamaan Bahasa Indonesia). Jika kedua
     * parameter tidak ada, mengembalikan "list".
     */
    private String getAction(HttpServletRequest request) {
        String action = request.getParameter("action");
        if (action == null || action.trim().isEmpty()) {
            action = request.getParameter("aksi");
        }
        if (action == null || action.trim().isEmpty()) {
            action = "list";
        }
        return action.trim();
    }

    /**
     * Mengubah String menjadi int. Mengembalikan 0 jika String null, kosong,
     * atau tidak bisa di-parse, agar controller tidak crash akibat
     * NumberFormatException dari input form yang kosong.
     */
    private int parseIntOrZero(String value) {
        if (value == null || value.trim().isEmpty()) {
            return 0;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
