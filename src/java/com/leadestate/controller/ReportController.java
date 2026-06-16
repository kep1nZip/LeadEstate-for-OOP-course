package com.leadestate.controller;

import com.leadestate.dao.DBConnection;
import com.leadestate.model.Report;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/laporan")
public class ReportController extends HttpServlet {


    // KONSTANTA

    private static final int STATUS_ID_CLOSING = 5;

    /** roleId = 1 merujuk ke "Admin" sesuai tabel roles di leadestate_v2.sql. */
    private static final int ROLE_ID_ADMIN = 1;

    // =========================================================================
    // doGet — entry point utama
    // =========================================================================

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // -----------------------------------------------------------------
        // 1. Validasi sesi
        // -----------------------------------------------------------------
        //HttpSession sesi = request.getSession(false);
        //if (sesi == null || sesi.getAttribute("userId") == null) {
        //    response.sendRedirect(request.getContextPath() + "/login");
        //    return;
        //}

        // -----------------------------------------------------------------
        // 2. Validasi hak akses: hanya Admin
        // -----------------------------------------------------------------
        //int roleId = (int) sesi.getAttribute("roleId");
        //if (roleId != ROLE_ID_ADMIN) {
        //    response.sendRedirect(request.getContextPath() + "/dashboard");
        //    return;
        //}

        // -----------------------------------------------------------------
        // 3. Kumpulkan semua data agregasi
        // -----------------------------------------------------------------
        int    totalLead    = queryTotalLead();
        int    totalClosing = queryTotalClosing();
        double closingRate  = hitungClosingRate(totalLead, totalClosing);

        Map<String, Integer>       leadPerStatus       = queryLeadPerStatus();
        Map<String, Integer>       trenLeadBulanan     = queryTrenLeadBulanan();
        Map<String, Integer>       trenClosingBulanan  = queryTrenClosingBulanan();
        List<Map<String, Object>>  rankingSales        = queryRankingSales();
        Map<String, Integer>       sumberLead          = querySumberLead();

        // -----------------------------------------------------------------
        // 4. Kemas ke objek Report (untuk referensi / toString di log)
        // -----------------------------------------------------------------
        Report report = new Report(
                "Laporan & Statistik LeadEstate",
                "Rekap performa penjualan dan konversi lead"
        );
        report.tambahData("totalLead",           totalLead);
        report.tambahData("totalClosing",         totalClosing);
        report.tambahData("closingRate",          closingRate);
        report.tambahData("leadPerStatus",        leadPerStatus);
        report.tambahData("trenLeadBulanan",      trenLeadBulanan);
        report.tambahData("trenClosingBulanan",   trenClosingBulanan);
        report.tambahData("rankingSales",         rankingSales);
        report.tambahData("sumberLead",           sumberLead);

        // -----------------------------------------------------------------
        // 5. Set ke request attribute
        //    — objek report utuh (untuk JSP yang mau iterasi report.data)
        //    — shortcut per key  (untuk JSP yang pakai ${totalLead} langsung)
        // -----------------------------------------------------------------
        request.setAttribute("report",               report);
        request.setAttribute("totalLead",            totalLead);
        request.setAttribute("totalClosing",         totalClosing);
        request.setAttribute("closingRate",          closingRate);
        request.setAttribute("leadPerStatus",        leadPerStatus);
        request.setAttribute("trenLeadBulanan",      trenLeadBulanan);
        request.setAttribute("trenClosingBulanan",   trenClosingBulanan);
        request.setAttribute("rankingSales",         rankingSales);
        request.setAttribute("sumberLead",           sumberLead);

        // -----------------------------------------------------------------
        // 6. Forward ke JSP
        // -----------------------------------------------------------------
        request.getRequestDispatcher("/WEB-INF/views/laporan.jsp")
               .forward(request, response);
    }

    /**
     * POST tidak dipakai di halaman laporan (semua aksi bersifat read-only).
     * Jika tetap dipanggil, diteruskan ke doGet.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    // QUERY AGREGASI (private)
    // Setiap method membuka koneksi sendiri via try-with-resources
    // agar tidak ada koneksi yang bocor jika salah satu query gagal.


    private int queryTotalLead() {
        String sql = "SELECT COUNT(*) FROM leads";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println("[ReportController] Gagal queryTotalLead(): " + e.getMessage());
        }
        return 0;
    }

    private int queryTotalClosing() {
        String sql = "SELECT COUNT(*) FROM leads WHERE statusId = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, STATUS_ID_CLOSING);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.out.println("[ReportController] Gagal queryTotalClosing(): " + e.getMessage());
        }
        return 0;
    }

    private double hitungClosingRate(int totalLead, int totalClosing) {
        if (totalLead == 0) {
            return 0.0;
        }
        double rate = ((double) totalClosing / totalLead) * 100.0;
        return Math.round(rate * 10.0) / 10.0;
    }

    private Map<String, Integer> queryLeadPerStatus() {
        Map<String, Integer> hasil = new LinkedHashMap<>();
        String sql = "SELECT ls.statusName, COUNT(l.id) AS jumlah "
                   + "FROM lead_status ls "
                   + "LEFT JOIN leads l ON l.statusId = ls.id "
                   + "GROUP BY ls.id, ls.statusName "
                   + "ORDER BY jumlah DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                hasil.put(rs.getString("statusName"), rs.getInt("jumlah"));
            }
        } catch (SQLException e) {
            System.out.println("[ReportController] Gagal queryLeadPerStatus(): " + e.getMessage());
        }
        return hasil;
    }

    private Map<String, Integer> queryTrenLeadBulanan() {
        Map<String, Integer> hasil = new LinkedHashMap<>();
        String sql = "SELECT DATE_FORMAT(MIN(f.followupDate), '%b %Y') AS bulan, "
                   + "       YEAR(MIN(f.followupDate))  AS thn, "
                   + "       MONTH(MIN(f.followupDate)) AS bln, "
                   + "       COUNT(DISTINCT f.leadId)   AS jumlah "
                   + "FROM followups f "
                   + "WHERE f.followupDate >= DATE_SUB(CURDATE(), INTERVAL 6 MONTH) "
                   + "GROUP BY thn, bln "
                   + "ORDER BY thn ASC, bln ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                hasil.put(rs.getString("bulan"), rs.getInt("jumlah"));
            }
        } catch (SQLException e) {
            System.out.println("[ReportController] Gagal queryTrenLeadBulanan(): " + e.getMessage());
        }
        return hasil;
    }

    private Map<String, Integer> queryTrenClosingBulanan() {
        Map<String, Integer> hasil = new LinkedHashMap<>();
        String sql = "SELECT DATE_FORMAT(MAX(f.followupDate), '%b %Y') AS bulan, "
                   + "       YEAR(MAX(f.followupDate))  AS thn, "
                   + "       MONTH(MAX(f.followupDate)) AS bln, "
                   + "       COUNT(DISTINCT l.id)       AS jumlah "
                   + "FROM leads l "
                   + "JOIN followups f ON f.leadId = l.id "
                   + "WHERE l.statusId = ? "
                   + "  AND f.followupDate >= DATE_SUB(CURDATE(), INTERVAL 6 MONTH) "
                   + "GROUP BY thn, bln "
                   + "ORDER BY thn ASC, bln ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, STATUS_ID_CLOSING);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    hasil.put(rs.getString("bulan"), rs.getInt("jumlah"));
                }
            }
        } catch (SQLException e) {
            System.out.println("[ReportController] Gagal queryTrenClosingBulanan(): " + e.getMessage());
        }
        return hasil;
    }

    private List<Map<String, Object>> queryRankingSales() {
        List<Map<String, Object>> hasil = new ArrayList<>();
        String sql = "SELECT u.id AS salesId, u.name AS nama, "
                   + "       COUNT(l.id) AS closing "
                   + "FROM users u "
                   + "LEFT JOIN leads l ON l.salesId = u.id AND l.statusId = ? "
                   + "WHERE u.roleId = 2 "
                   + "GROUP BY u.id, u.name "
                   + "ORDER BY closing DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, STATUS_ID_CLOSING);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> baris = new LinkedHashMap<>();
                    baris.put("salesId", rs.getInt("salesId"));
                    baris.put("nama",    rs.getString("nama"));
                    baris.put("closing", rs.getInt("closing"));
                    hasil.add(baris);
                }
            }
        } catch (SQLException e) {
            System.out.println("[ReportController] Gagal queryRankingSales(): " + e.getMessage());
        }
        return hasil;
    }

    private Map<String, Integer> querySumberLead() {
        Map<String, Integer> hasil = new LinkedHashMap<>();
        String sql = "SELECT source, COUNT(*) AS jumlah "
                   + "FROM leads "
                   + "WHERE source IS NOT NULL AND source != '' "
                   + "GROUP BY source "
                   + "ORDER BY jumlah DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                hasil.put(rs.getString("source"), rs.getInt("jumlah"));
            }
        } catch (SQLException e) {
            System.out.println("[ReportController] Gagal querySumberLead(): " + e.getMessage());
        }
        return hasil;
    }
}
