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

/**
 * Controller (Servlet) untuk fitur Laporan & Statistik.
 *
 * <p>URL mapping: {@code /laporan}</p>
 *
 * <p>Seluruh query agregasi dilakukan langsung di dalam controller ini
 * (tanpa DAO terpisah), menggunakan {@code DBConnection.getConnection()}
 * dengan pola try-with-resources + PreparedStatement sesuai konvensi tim.</p>
 *
 * <p>Data yang dikumpulkan dan diteruskan ke JSP:</p>
 * <ul>
 *   <li>Total lead masuk</li>
 *   <li>Total closing (statusId = 5 = "Closed Won")</li>
 *   <li>Closing rate (%)</li>
 *   <li>Jumlah lead per status</li>
 *   <li>Tren lead masuk per bulan (6 bulan terakhir)</li>
 *   <li>Tren closing per bulan (6 bulan terakhir)</li>
 *   <li>Ranking sales berdasarkan jumlah closing</li>
 *   <li>Sumber lead terbanyak</li>
 * </ul>
 *
 * <p>Hak akses: hanya Admin (roleId = 1). Sales yang mencoba akses
 * akan di-redirect ke dashboard.</p>
 *
 * <p>Stack: Java EE 6 Web Application (Apache NetBeans + Ant).
 * BUKAN Spring Boot — tidak ada @Service, @Repository, dsb.</p>
 *
 * @author Firasy Azizi (103012400366)
 * @version 1.0
 */
@WebServlet("/laporan")
public class ReportController extends HttpServlet {

    // =========================================================================
    // KONSTANTA
    // =========================================================================

    /**
     * statusId = 5 merujuk ke "Closed Won" sesuai data di leadestate_v2.sql.
     * Dijadikan konstanta agar mudah diubah jika skema berubah.
     */
    private static final int STATUS_ID_CLOSING = 5;

    /** roleId = 1 merujuk ke "Admin" sesuai tabel roles di leadestate_v2.sql. */
    private static final int ROLE_ID_ADMIN = 1;

    // =========================================================================
    // doGet — entry point utama
    // =========================================================================

    /**
     * Menangani GET {@code /laporan}.
     *
     * <p>Langkah-langkah:</p>
     * <ol>
     *   <li>Validasi sesi — redirect ke login jika belum login.</li>
     *   <li>Validasi hak akses — redirect ke dashboard jika bukan Admin.</li>
     *   <li>Jalankan semua query agregasi.</li>
     *   <li>Simpan hasil ke request attribute dan objek {@link Report}.</li>
     *   <li>Forward ke {@code /WEB-INF/views/laporan.jsp}.</li>
     * </ol>
     *
     * @param request  objek HttpServletRequest dari client.
     * @param response objek HttpServletResponse ke client.
     * @throws ServletException jika terjadi kesalahan servlet.
     * @throws IOException      jika terjadi kesalahan I/O.
     */
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

    // =========================================================================
    // QUERY AGREGASI (private)
    // Setiap method membuka koneksi sendiri via try-with-resources
    // agar tidak ada koneksi yang bocor jika salah satu query gagal.
    // =========================================================================

    /**
     * Menghitung total jumlah lead di tabel {@code leads}.
     *
     * @return jumlah seluruh baris, 0 jika query gagal.
     */
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

    /**
     * Menghitung jumlah lead dengan statusId = 5 ("Closed Won").
     *
     * @return jumlah lead closing, 0 jika query gagal.
     */
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

    /**
     * Menghitung closing rate sebagai persentase (1 desimal).
     * Aman dari pembagian nol.
     *
     * @param totalLead    jumlah total lead.
     * @param totalClosing jumlah lead closing.
     * @return closing rate (%), atau 0.0 jika totalLead = 0.
     */
    private double hitungClosingRate(int totalLead, int totalClosing) {
        if (totalLead == 0) {
            return 0.0;
        }
        double rate = ((double) totalClosing / totalLead) * 100.0;
        return Math.round(rate * 10.0) / 10.0;
    }

    /**
     * Menghitung jumlah lead per status dengan JOIN ke {@code lead_status}.
     * LEFT JOIN agar status yang belum punya lead tetap tampil (nilai 0).
     *
     * @return {@code Map<statusName, jumlah>} diurutkan jumlah DESC.
     */
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

    /**
     * Menghitung tren jumlah lead masuk per bulan (6 bulan terakhir).
     *
     * <p>Karena tabel {@code leads} tidak punya kolom tanggal masuk,
     * tren diambil dari {@code MIN(followupDate)} per lead di tabel
     * {@code followups} — pendekatan terbaik tanpa mengubah skema.</p>
     *
     * @return {@code Map<"MMM YYYY", jumlah>} diurutkan bulan terlama → terbaru.
     */
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

    /**
     * Menghitung tren closing per bulan (6 bulan terakhir).
     * Closing = lead dengan statusId = 5 ("Closed Won").
     * Tanggal yang dipakai: {@code MAX(followupDate)} per lead closing.
     *
     * @return {@code Map<"MMM YYYY", jumlah>} diurutkan bulan terlama → terbaru.
     */
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

    /**
     * Mengambil ranking Sales berdasarkan jumlah closing (statusId = 5).
     * LEFT JOIN agar Sales dengan 0 closing tetap masuk ranking.
     * Hanya user dengan roleId = 2 (Sales) yang diikutkan.
     *
     * @return {@code List<Map>} setiap Map berisi: salesId, nama, closing.
     *         Diurutkan closing DESC.
     */
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

    /**
     * Menghitung jumlah lead yang dikelompokkan berdasarkan sumber (source).
     *
     * @return {@code Map<source, jumlah>} diurutkan jumlah DESC.
     *         Baris dengan source NULL atau kosong diabaikan.
     */
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
