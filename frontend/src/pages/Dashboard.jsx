import { useState, useEffect } from "react";
import apiClient from "../api/api";
import "../styles/Dashboard.css";
import logo from "../assets/leadestate-logo.png";
import { useNavigate } from "react-router-dom";

// ── COMPONENTS (Tetap Sama) ──────────────────────────────────────────────────
function StatCard({ color, icon, trend, trendDir, value, label, sub }) {
  return (
    <div className={`le-stat-card ${color}`}>
      <div className="le-stat-header">
        <div className={`le-stat-icon ${color}`}>{icon}</div>
        <div className={`le-stat-trend ${trendDir}`}>{trend}</div>
      </div>
      <div className="le-stat-value">{value}</div>
      <div className="le-stat-label">{label}</div>
      <div className="le-stat-sub">{sub}</div>
    </div>
  );
}

function BarChart({ data, activeTab, onTabChange }) {
  const max = data.length > 0 ? Math.max(...data.map((d) => d.leads || 0)) : 10;
  return (
    <div className="le-card">
      <div className="le-card-header">
        <div>
          <div className="le-card-title">Grafik Closing Rate</div>
          <div className="le-card-sub">Perbandingan lead masuk vs closing per bulan</div>
        </div>
        <div style={{ display: "flex", gap: 6 }}>
          <div className={`le-chip${activeTab === "6" ? " active" : ""}`} onClick={() => onTabChange("6")}>6 Bulan</div>
          <div className={`le-chip${activeTab === "12" ? " active" : ""}`} onClick={() => onTabChange("12")}>1 Tahun</div>
        </div>
      </div>
      <div className="le-chart-area">
        <div className="le-bar-chart">
          {data.map((d, index) => {
            const h1 = Math.round(((d.leads || 0) / max) * 110);
            const h2 = Math.round(((d.closing || 0) / max) * 110);
            return (
              <div className="le-bar-group" key={index}>
                <div className="le-bars">
                  <div className="le-bar b1" style={{ height: h1 }} title={`${d.leads} leads`} />
                  <div className="le-bar b2" style={{ height: h2 }} title={`${d.closing} closing`} />
                </div>
                <div className="le-bar-month">{d.m}</div>
              </div>
            );
          })}
        </div>
      </div>
      <div className="le-chart-legend">
        <div className="le-legend-item"><div className="le-legend-dot" style={{ background: "var(--brand-gold)" }} /> Lead Masuk</div>
        <div className="le-legend-item"><div className="le-legend-dot" style={{ background: "#93c5fd" }} /> Closing</div>
      </div>
    </div>
  );
}

function Leaderboard({ data }) {
  const rankClass = (rank) => rank <= 3 ? `le-rank-${rank}` : "le-rank-other";
  return (
    <div className="le-card">
      <div className="le-card-header">
        <div>
          <div className="le-card-title">🏆 Top Sales</div>
          <div className="le-card-sub">Performa bulan ini</div>
        </div>
      </div>
      <div className="le-leader-list">
        {data.length > 0 ? data.map((s, index) => (
          <div className="le-leader-item" key={index}>
            <div className={`le-leader-rank ${rankClass(index + 1)}`}>{index + 1}</div>
            <div className="le-leader-av" style={{ background: s.color || "#6366f1" }}>{s.initials}</div>
            <div className="le-leader-info">
              <div className="le-leader-name">{s.name}</div>
              <span className="le-leader-badge">{s.followUp} follow-up</span>
            </div>
            <div className="le-leader-closing">{s.closing} ✅</div>
          </div>
        )) : <div className="le-view-all">Belum ada data sales</div>}
      </div>
    </div>
  );
}

function ReminderSection({ data, activeFilter, onFilterChange }) {
  const tagLabel = { today: "Hari ini", late: "Terlambat", soon: "Segera" };
  return (
    <div className="le-card">
      <div className="le-card-header">
        <div>
          <div className="le-card-title">🔔 Reminder Hari Ini</div>
          <div className="le-card-sub">Lead perlu di-follow up</div>
        </div>
      </div>
      <div className="le-reminder-list">
        {data.length > 0 ? data.map((r, index) => (
          <div className="le-reminder-item" key={index}>
            <div className="le-ri-avatar" style={{ background: r.color || "#f59e0b" }}>{r.initials}</div>
            <div className="le-ri-info">
              <div className="le-ri-name">{r.name}</div>
              <div className="le-ri-prop">{r.prop}</div>
            </div>
            <div className="le-ri-right">
              <div className="le-ri-time">{r.time}</div>
              <div className={`le-ri-tag tag-${r.tag}`}>{tagLabel[r.tag]}</div>
            </div>
          </div>
        )) : <div className="le-view-all">Tidak ada reminder</div>}
      </div>
    </div>
  );
}

// ── APP ───────────────────────────────────────────────────────────────────────
export default function LeadEstateDashboard() {
  const [activeNav, setActiveNav] = useState("dashboard");
  const [dashboardData, setDashboardData] = useState(null);
  const [currentUser, setCurrentUser] = useState(null);
  const [loading, setLoading] = useState(true);

  const [chartTab, setChartTab] = useState("6");
  const [reminderFilter, setReminderFilter] = useState("Semua");

  const navigate = useNavigate();

  const isAdmin =
    currentUser?.role?.toLowerCase() === "admin";

  useEffect(() => {
    const userData =
      localStorage.getItem(
        "user"
      );

    if (!userData) {
      navigate("/");
      return;
    }

    try {
      const parsedUser =
        JSON.parse(userData);

      setCurrentUser(
        parsedUser
      );

      fetchDashboard();

    } catch (error) {
      console.error(
        "Session invalid:",
        error
      );

      navigate("/");
    }

  }, [navigate]);

  const fetchDashboard = async () => {
    try {
      const res = await apiClient.get("/dashboard");
      setDashboardData(res.data);
    } catch (err) {
      console.error("Dashboard fetch error:", err);
      if (err.response?.status === 403) {
        alert("Akses ditolak oleh server.");
      }
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <div className="le-root" style={{ justifyContent: 'center', alignItems: 'center' }}>
        <div className="le-card-title">Memuat data Dashboard...</div>
      </div>
    );
  }

  return (
    <div className="le-root">
      <aside className="le-sidebar">
        <div className="le-brand">
          <img src={logo} alt="LeadEstate Logo" className="brand-logo" />
          <div className="le-brand-name">Lead<span>Estate</span></div>
        </div>

        <nav className="le-nav">
          <div className="le-nav-label">Menu Utama</div>

          <div className={`le-nav-item${activeNav === "dashboard" ? " active" : ""}`} onClick={() => setActiveNav("dashboard")}>
            <span className="le-nav-icon"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.8"><rect x="3" y="3" width="7" height="7" rx="1.5" /><rect x="14" y="3" width="7" height="7" rx="1.5" /><rect x="3" y="14" width="7" height="7" rx="1.5" /><rect x="14" y="14" width="7" height="7" rx="1.5" /></svg></span>
            Dashboard
          </div>

          <div className={`le-nav-item${activeNav === "reminder" ? " active" : ""}`} onClick={() => { setActiveNav("reminder"); navigate("/reminder"); }}>
            <span className="le-nav-icon"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.8"><circle cx="12" cy="12" r="10" /><polygon points="10,8 16,12 10,16" fill="currentColor" /></svg></span>
            Reminder &amp; Follow-Up
            <span className="le-nav-badge">{dashboardData?.todayFollowups || 0}</span>
          </div>

          <div className={`le-nav-item${activeNav === "lead" ? " active" : ""}`} onClick={() => { setActiveNav("lead"); navigate("/dataLeads"); }}>
            <span className="le-nav-icon"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.8"><path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2" /><circle cx="9" cy="7" r="4" /></svg></span>
            Data Lead
          </div>

          {/* ── PROTEKSI MENU ADMIN ── */}
          {isAdmin && (
            <>
              <div className={`le-nav-item${activeNav === "sales" ? " active" : ""}`} onClick={() => { setActiveNav("sales"); navigate("/Manajemen_sales"); }}>
                <span className="le-nav-icon"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.8"><rect x="2" y="3" width="20" height="18" rx="2" /><circle cx="12" cy="10" r="3" /><path d="M7 21v-1a5 5 0 0 1 10 0v1" /></svg></span>
                Manajemen Sales
              </div>

              <div className="le-nav-label">Laporan</div>

              <div className={`le-nav-item${activeNav === "report" ? " active" : ""}`} onClick={() => { setActiveNav("report"); navigate("/laporan"); }}>
                <span className="le-nav-icon"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.8"><line x1="18" y1="20" x2="18" y2="10" /><line x1="12" y1="20" x2="12" y2="4" /><line x1="6" y1="20" x2="6" y2="14" /></svg></span>
                Laporan &amp; Statistik
              </div>
            </>
          )}

          <div className={`le-nav-item${activeNav === "settings" ? " active" : ""}`} onClick={() => { setActiveNav("settings"); navigate("/settings"); }}>
            <span className="le-nav-icon"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.8"><circle cx="12" cy="12" r="3" /><path d="M19.07 4.93a10 10 0 0 1 0 14.14M4.93 4.93a10 10 0 0 0 0 14.14" /></svg></span>
            Pengaturan
          </div>
        </nav>

        <div className="le-sidebar-footer">
          <div className="le-avatar">{currentUser?.name?.split(" ").slice(0,2).map(w=>w[0]).join("").toUpperCase() || "A"}</div>
          <div>
            <div className="le-user-name">{currentUser?.name || "User"}</div>
            <div className="le-user-role">{currentUser?.role || "Sales"}</div>
          </div>
        </div>
      </aside>

      <main className="le-main">
        {/* Header & Content (Sama seperti sebelumnya) */}
        <div className="le-topbar">
          <div className="le-topbar-title">Dashboard Overview</div>
            <div className="le-topbar-right">
              <div className="le-date-chip">
                📅{" "}
                {new Date().toLocaleDateString("id-ID", {
                  weekday: "long",
                  day: "numeric",
                  month: "long",
                  year: "numeric",
                })}
              </div>
            </div>
        </div>

        <div className="le-content">
          <div className="le-stats-grid">
            <StatCard color="gold" icon="📋" value={dashboardData?.totalLeads || 0} label="Total Lead Aktif" sub="Total leads" />
            <StatCard color="blue" icon="🔔" value={dashboardData?.todayFollowups || 0} label="Follow Up Hari Ini" sub="Cek reminder" />
            <StatCard color="red" icon="⏳" value={dashboardData?.pendingLeads || 0} label="Lead Tertunda" sub="Tindak segera" />
            <StatCard color="green" icon="✅" value={dashboardData?.monthlyClosing || 0} label="Closing Bulan Ini" sub="Closing" />
          </div>

          <div className="le-mid-row">
            <BarChart data={dashboardData?.chartData || []} activeTab={chartTab} onTabChange={setChartTab} />
            <Leaderboard data={dashboardData?.topSales || []} />
          </div>

          <ReminderSection data={dashboardData?.reminders || []} activeFilter={reminderFilter} onFilterChange={setReminderFilter} />
        </div>
      </main>
    </div>
  );
}