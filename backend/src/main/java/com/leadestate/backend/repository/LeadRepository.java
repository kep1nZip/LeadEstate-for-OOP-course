package com.leadestate.backend.repository;

import com.leadestate.backend.entity.Lead;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository 
public interface LeadRepository extends JpaRepository<Lead, Integer> {

    // Filter lead berdasarkan ID sales
    List<Lead> findBySales_Id(Integer salesId);

    long count();
    long countByStatus_Id(int statusId);

    // LEFT JOIN agar lead tanpa status tetap terhitung
    @Query("SELECT COALESCE(l.status.statusName, 'Tidak Ada Status'), COUNT(l) " +
       "FROM Lead l " +
       "LEFT JOIN l.status " +
       "GROUP BY l.status.statusName")
    List<Object[]> countLeadsByStatus();

    // Lead per Month
    @Query("SELECT FUNCTION('MONTH', l.createdAt), COUNT(l) " +
        "FROM Lead l " +
        "GROUP BY FUNCTION('MONTH', l.createdAt) " +
        "ORDER BY FUNCTION('MONTH', l.createdAt)")
    List<Object[]> countLeadsPerMonth();

    // Performance per Sales — pakai native query agar dapat nama sales dari tabel users
    @Query(value = """
        SELECT u.name, COUNT(l.id)
        FROM leads l
        JOIN users u ON l.sales_id = u.id
        GROUP BY u.name
        ORDER BY COUNT(l.id) DESC
    """, nativeQuery = true)
    List<Object[]> countLeadsBySales();

    // Filter Leads
    @Query("""
        SELECT l FROM Lead l WHERE
        (:salesId IS NULL OR l.sales.id = :salesId) AND
        (:propertyId IS NULL OR l.property.id = :propertyId) AND
        (:start IS NULL OR l.createdAt >= :start) AND
        (:end IS NULL OR l.createdAt <= :end)
    """)
    List<Lead> filterLeads(
        Integer salesId,
        Integer propertyId,
        java.time.LocalDateTime start,
        java.time.LocalDateTime end
    );

    // Top Sales berdasarkan jumlah closing
    @Query(value = """
        SELECT u.name, COUNT(l.id)
        FROM leads l
        JOIN users u ON l.sales_id = u.id
        WHERE l.status_id = 5
        GROUP BY u.name
        ORDER BY COUNT(l.id) DESC
    """, nativeQuery = true)
    List<Object[]> getTopSales();

    // LEAD VS CLOSING PER BULAN
    @Query(value = """
        SELECT 
        MONTH(created_at) as month,
        COUNT(*) as leads,
        SUM(CASE WHEN status_id = 5 THEN 1 ELSE 0 END) as closing
        FROM leads
        GROUP BY MONTH(created_at)
        ORDER BY MONTH(created_at)
    """, nativeQuery = true)
    List<Object[]> getChartData();

    // Sumber Lead
    @Query("SELECT l.source, COUNT(l) FROM Lead l GROUP BY l.source ORDER BY COUNT(l) DESC")
    List<Object[]> countLeadsBySource();

    // ── STATS PER SALES PER BULAN (untuk Manajemen Sales) ──────────────────────

    // Hitung closing (status_id = 5) per sales per bulan
    @Query(value = """
        SELECT COUNT(*)
        FROM leads
        WHERE sales_id = :salesId
          AND status_id = 5
          AND MONTH(created_at) = :month
          AND YEAR(created_at) = :year
    """, nativeQuery = true)
    long countClosingBySalesAndMonth(
        @org.springframework.data.repository.query.Param("salesId") int salesId,
        @org.springframework.data.repository.query.Param("month")   int month,
        @org.springframework.data.repository.query.Param("year")    int year
    );

    // Hitung follow up per sales per bulan
    @Query(value = """
        SELECT COUNT(*)
        FROM leads
        WHERE sales_id = :salesId
          AND MONTH(created_at) = :month
          AND YEAR(created_at) = :year
    """, nativeQuery = true)
    long countFollowUpBySalesAndMonth(
        @org.springframework.data.repository.query.Param("salesId") int salesId,
        @org.springframework.data.repository.query.Param("month")   int month,
        @org.springframework.data.repository.query.Param("year")    int year
    );

    // Hitung total lead per sales per bulan
    @Query(value = """
        SELECT COUNT(*)
        FROM leads
        WHERE sales_id = :salesId
          AND MONTH(created_at) = :month
          AND YEAR(created_at) = :year
    """, nativeQuery = true)
    long countLeadsBySalesAndMonth(
        @org.springframework.data.repository.query.Param("salesId") int salesId,
        @org.springframework.data.repository.query.Param("month")   int month,
        @org.springframework.data.repository.query.Param("year")    int year
    );

    // Ambil list lead per sales per bulan (untuk tampil di detail card)
    @Query(value = """
        SELECT l.name AS name,
               p.name AS propertyName,
               ls.status_name AS statusName
        FROM leads l
        LEFT JOIN properties p  ON l.property_id = p.id
        LEFT JOIN lead_status ls ON l.status_id  = ls.id
        WHERE l.sales_id = :salesId
          AND MONTH(l.created_at) = :month
          AND YEAR(l.created_at)  = :year
        ORDER BY l.created_at DESC
        LIMIT 10
    """, nativeQuery = true)
    java.util.List<java.util.Map<String, Object>> findLeadsBySalesAndMonth(
        @org.springframework.data.repository.query.Param("salesId") int salesId,
        @org.springframework.data.repository.query.Param("month")   int month,
        @org.springframework.data.repository.query.Param("year")    int year
    );

    // ── BARU: Estimasi revenue dari lead Closed (status_id = 5) × harga property
    @Query(value = """
        SELECT COALESCE(SUM(p.price), 0)
        FROM leads l
        JOIN properties p ON l.property_id = p.id
        WHERE l.status_id = 5
    """, nativeQuery = true)
    double sumEstimatedRevenue();
}