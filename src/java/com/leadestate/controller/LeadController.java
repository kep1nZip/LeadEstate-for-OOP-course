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

        lead.changeStatus(newStatusId);

        // Simpan perubahan statusId ke database.
        leadDAO.updateStatus(id, newStatusId);

        response.sendRedirect(request.getContextPath()
                + "/lead?action=detail&id=" + id + "&sukses=status");
    }

    private void delete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int id = parseIntOrZero(request.getParameter("id"));
        boolean berhasil = leadDAO.delete(id);
        response.sendRedirect(request.getContextPath() + "/lead"
                + (berhasil ? "?sukses=hapus" : "?gagal=hapus"));
    }

    // ===== Helper =====
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
