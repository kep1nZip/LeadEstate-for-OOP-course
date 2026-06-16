import { useState, useEffect } from "react";
import apiClient from "../api/api";
import "../styles/Laporan.css";
import logo from "../assets/leadestate-logo.png";
import { useNavigate } from "react-router-dom";

/* ── BASE URL dari .env ── */
const API = import.meta.env.VITE_API_URL;

/* ── WARNA untuk mapping status & sumber ── */
const STATUS_COLORS = {
  "New Lead":    { color: "#3b82f6" },
  "Contacted":   { color: "#f59e0b" },
  "Follow Up":   { color: "#8b5cf6" },
  "Negotiation": { color: "#f97316" },
  "Closed":      { color: "#22c55e" },
  "Lost":        { color: "#ef4444" },
};

const SOURCE_META = {
  Instagram: { icon: "📸", color: "#e1306c", bg: "#fce7f3" },
  Facebook:  { icon: "👥", color: "#1877f2", bg: "#dbeafe" },
  Website:   { icon: "🌐", color: "#10b981", bg: "#dcfce7" },
  Referral:  { icon: "🤝", color: "#f59e0b", bg: "#fef3cd" },
  "Walk-in": { icon: "🚶", color: "#8b5cf6", bg: "#ede9fe" },
  TikTok:    { icon: "🎵", color: "#000000", bg: "#f3f4f6" },
};

const SALES_COLORS = ["#f59e0b", "#6366f1", "#10b981", "#ef4444", "#8b5cf6", "#3b82f6"];

const FUNNEL_COLORS = ["#3b82f6", "#6366f1", "#f59e0b", "#f97316", "#22c55e"];

/* ── HELPER: ambil role dari localStorage ── */
function getUserRole() {
  try {
    const user = JSON.parse(localStorage.getItem("user") || "{}");
    return user.role || localStorage.getItem("role") || "Admin";
  } catch {
    return "Admin";
  }
}

/* ── CUSTOM HOOK: fetch data dari satu endpoint ── */
function useFetch(endpoint) {
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    setLoading(true);
    fetch(`${API}${endpoint}`, {
      headers: {
        Authorization: `Bearer ${localStorage.getItem("token")}`,
        "Content-Type": "application/json",
        "Role": getUserRole(),
      },
    })
      .then((res) => {
        if (!res.ok) throw new Error(`HTTP ${res.status}`);
        return res.json();
      })
      .then((json) => { setData(json); setLoading(false); })
      .catch((err) => { setError(err.message); setLoading(false); });
  }, [endpoint]);

  return { data, loading, error };
}

/* ── HELPER: filter data berdasarkan periode ── */
// Backend return field: m (nama bulan singkat), leads, closing
// monthsData = array semua bulan dari backend
// period: "minggu" | "bulan" | "kuartal" | "tahun"
// selectedMonth: 1-12, selectedYear: 4-digit
const MONTH_NAMES = ["Jan","Feb","Mar","Apr","Mei","Jun","Jul","Agu","Sep","Okt","Nov","Des"];

function filterByPeriod(monthsData, period, selectedMonth, selectedYear) {
  if (!Array.isArray(monthsData) || monthsData.length === 0) return monthsData;

  // Buat map: nama bulan → index (0-based)
  const labelToIdx = {};
  MONTH_NAMES.forEach((m, i) => { labelToIdx[m] = i; });

  const getMonthIdx = (d) => {
    const label = d.m ?? d.label ?? d.month ?? "";
    return labelToIdx[label] ?? -1;
  };

  const now = new Date();
  const curMonth = selectedMonth - 1; // 0-based
  const curYear  = selectedYear;

  if (period === "bulan") {
    // Hanya bulan yang dipilih
    return monthsData.filter(d => getMonthIdx(d) === curMonth);
  }

  if (period === "kuartal") {
    // 3 bulan terakhir dari bulan dipilih
    const months = [];
    for (let i = 2; i >= 0; i--) {
      let m = curMonth - i;
      if (m < 0) m += 12;
      months.push(m);
    }
    return monthsData.filter(d => months.includes(getMonthIdx(d)));
  }

  if (period === "tahun") {
    // Semua bulan (return semua)
    return monthsData;
  }

  // "minggu" — tidak bisa filter per minggu dari data bulanan, tampilkan bulan ini saja
  return monthsData.filter(d => getMonthIdx(d) === curMonth);
}

function getLast6Months(monthsData, selectedMonth, selectedYear) {
  if (!Array.isArray(monthsData)) return [];
  const MONTH_IDX = {"Jan":0,"Feb":1,"Mar":2,"Apr":3,"Mei":4,"Jun":5,"Jul":6,"Agu":7,"Sep":8,"Okt":9,"Nov":10,"Des":11};
  const last6 = [];
  for (let i = 5; i >= 0; i--) {
    let m = (selectedMonth - 1) - i;
    if (m < 0) m += 12;
    last6.push(m);
  }
  return monthsData.filter(d => {
    const label = d.m ?? d.label ?? d.month ?? "";
    return last6.includes(MONTH_IDX[label] ?? -1);
  });
}

/* ── SKELETON placeholder saat loading ── */
function Skeleton({ height = 20, width = "100%", style = {} }) {
  return (
    <div
      style={{
        height,
        width,
        background: "linear-gradient(90deg,#f0f0f0 25%,#e0e0e0 50%,#f0f0f0 75%)",
        backgroundSize: "200% 100%",
        animation: "shimmer 1.2s infinite",
        borderRadius: 6,
        ...style,
      }}
    />
  );
}

/* ── NAV ICONS ── */
const IconDashboard = () => (
  <svg viewBox="0 0 24 24" fill="currentColor">
    <path d="M3 13h8V3H3v10zm0 8h8v-6H3v6zm10 0h8V11h-8v10zm0-18v6h8V3h-8z" />
  </svg>
);
const IconBell = () => (
  <svg viewBox="0 0 24 24" fill="currentColor">
    <path d="M12 22c1.1 0 2-.9 2-2h-4c0 1.1.9 2 2 2zm6-6v-5c0-3.07-1.63-5.64-4.5-6.32V4c0-.83-.67-1.5-1.5-1.5s-1.5.67-1.5 1.5v.68C7.64 5.36 6 7.92 6 11v5l-2 2v1h16v-1l-2-2z" />
  </svg>
);
const IconPeople = () => (
  <svg viewBox="0 0 24 24" fill="currentColor">
    <path d="M16 11c1.66 0 2.99-1.34 2.99-3S17.66 5 16 5c-1.66 0-3 1.34-3 3s1.34 3 3 3zm-8 0c1.66 0 2.99-1.34 2.99-3S9.66 5 8 5C6.34 5 5 6.34 5 8s1.34 3 3 3zm0 2c-2.33 0-7 1.17-7 3.5V19h14v-2.5c0-2.33-4.67-3.5-7-3.5zm8 0c-.29 0-.62.02-.97.05 1.16.84 1.97 1.97 1.97 3.45V19h6v-2.5c0-2.33-4.67-3.5-7-3.5z" />
  </svg>
);
const IconPerson = () => (
  <svg viewBox="0 0 24 24" fill="currentColor">
    <path d="M19 3H5c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2zm-7 3c1.93 0 3.5 1.57 3.5 3.5S13.93 13 12 13s-3.5-1.57-3.5-3.5S10.07 6 12 6zm7 13H5v-.23c0-.62.28-1.2.76-1.58C7.47 15.82 9.64 15 12 15s4.53.82 6.24 2.19c.48.38.76.97.76 1.58V19z" />
  </svg>
);
const IconChart = () => (
  <svg viewBox="0 0 24 24" fill="currentColor">
    <path d="M19 3H5c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2zM9 17H7v-7h2v7zm4 0h-2V7h2v10zm4 0h-2v-4h2v4z" />
  </svg>
);
const IconSettings = () => (
  <svg viewBox="0 0 24 24" fill="currentColor">
    <path d="M19.14 12.94c.04-.3.06-.61.06-.94 0-.32-.02-.64-.07-.94l2.03-1.58c.18-.14.23-.41.12-.61l-1.92-3.32c-.12-.22-.37-.29-.59-.22l-2.39.96c-.5-.38-1.03-.7-1.62-.94l-.36-2.54c-.04-.24-.24-.41-.48-.41h-3.84c-.24 0-.43.17-.47.41l-.36 2.54c-.59.24-1.13.57-1.62.94l-2.39-.96c-.22-.08-.47 0-.59.22L2.74 8.87c-.12.21-.08.47.12.61l2.03 1.58c-.05.3-.09.63-.09.94s.02.64.07.94l-2.03 1.58c-.18.14-.23.41-.12.61l1.92 3.32c.12.22.37.29.59.22l2.39-.96c.5.38 1.03.7 1.62.94l.36 2.54c.05.24.24.41.48.41h3.84c.24 0 .44-.17.47-.41l.36-2.54c.59-.24 1.13-.56 1.62-.94l2.39.96c.22.08.47 0 .59-.22l1.92-3.32c.12-.22.07-.47-.12-.61l-2.01-1.58zM12 15.6c-1.98 0-3.6-1.62-3.6-3.6s1.62-3.6 3.6-3.6 3.6 1.62 3.6 3.6-1.62 3.6-3.6 3.6z" />
  </svg>
);
const IconExport = () => (
  <svg width="13" height="13" viewBox="0 0 24 24" fill="currentColor">
    <path d="M14 2H6c-1.1 0-2 .9-2 2v16c0 1.1.9 2 2 2h12c1.1 0 2-.9 2-2V8l-6-6zm4 18H6V4h7v5h5v11z" />
  </svg>
);

/* ════════════════════════════════════════
   KPI GRID  →  GET /api/report
   ════════════════════════════════════════ */
function KpiGrid() {
  const { data, loading } = useFetch("/api/report");

  if (loading) {
    return (
      <div className="kpi-grid">
        {[0, 1, 2, 3].map((i) => (
          <div key={i} className="kpi-card kc-gold" style={{ gap: 10, display: "flex", flexDirection: "column" }}>
            <Skeleton height={36} width={36} />
            <Skeleton height={32} width="60%" />
            <Skeleton height={14} width="80%" />
            <Skeleton height={12} width="90%" />
          </div>
        ))}
      </div>
    );
  }

  // Backend return: { totalLeads, totalFollowUps, closedLeads, activeLeads, estimatedRevenue }
  const totalLeads   = data?.totalLeads   ?? 0;
  const totalClosing = data?.closedLeads  ?? data?.totalClosing ?? data?.closingCount ?? 0;
  const prevLeads    = data?.prevLeads    ?? 0;
  const prevClosing  = data?.prevClosing  ?? 0;
  const revenue      = data?.estimatedRevenue ?? data?.revenue ?? 0;
  const prevRevenue  = data?.prevRevenue  ?? 0;
  const rate         = totalLeads > 0 ? Math.round((totalClosing / totalLeads) * 100) : 0;
  const prevRate     = prevLeads  > 0 ? Math.round((prevClosing  / prevLeads)  * 100) : 0;

  // Format revenue rupiah: T = triliun, M = miliar, Jt = juta
  const formatRevenue = (val) => {
    if (!val || val === 0) return "–";
    if (val >= 1_000_000_000_000) return `Rp ${(val / 1_000_000_000_000).toFixed(1)} T`;
    if (val >= 1_000_000_000)     return `Rp ${(val / 1_000_000_000).toFixed(1)} M`;
    if (val >= 1_000_000)         return `Rp ${(val / 1_000_000).toFixed(1)} Jt`;
    return `Rp ${Number(val).toLocaleString("id-ID")}`;
  };

  const cards = [
    {
      icon: "📋", cls: "kc-gold", icls: "ki-gold",
      val: totalLeads,
      lbl: "Total Lead Masuk",
      trend: `${totalLeads >= prevLeads ? "↑" : "↓"} ${Math.abs(totalLeads - prevLeads)} dari bulan lalu`,
      up: totalLeads >= prevLeads,
    },
    {
      icon: "✅", cls: "kc-green", icls: "ki-green",
      val: totalClosing,
      lbl: "Total Closing",
      trend: `${totalClosing >= prevClosing ? "↑" : "↓"} ${Math.abs(totalClosing - prevClosing)} dari bulan lalu`,
      up: totalClosing >= prevClosing,
    },
    {
      icon: "📈", cls: "kc-blue", icls: "ki-blue",
      val: `${rate}%`,
      lbl: "Closing Rate",
      trend: `Bulan lalu: ${prevRate}%`,
      up: rate >= prevRate,
    },
    {
      icon: "💰", cls: "kc-purple", icls: "ki-purple",
      val: formatRevenue(revenue),
      lbl: "Est. Nilai Transaksi",
      trend: prevRevenue > 0
        ? `+${Math.round(((revenue - prevRevenue) / prevRevenue) * 100)}% dari bulan lalu`
        : "–",
      up: revenue >= prevRevenue,
    },
  ];

  return (
    <div className="kpi-grid">
      {cards.map((c, i) => (
        <div key={i} className={`kpi-card ${c.cls}`}>
          <div className="kpi-header">
            <div className={`kpi-icon ${c.icls}`}>{c.icon}</div>
            <div className={`kpi-trend ${c.up ? "trend-up" : "trend-down"}`}>
              {c.up ? "↑ +" : "↓ "}
            </div>
          </div>
          <div className="kpi-val">{c.val}</div>
          <div className="kpi-label">{c.lbl}</div>
          <div className="kpi-sub">{c.trend}</div>
        </div>
      ))}
    </div>
  );
}

/* ════════════════════════════════════════
   BAR CHART  →  GET /api/report/lead-per-month
   Selalu tampil 6 bulan terakhir, filter sesuai period
   ════════════════════════════════════════ */
function BarChart({ period, selectedMonth, selectedYear }) {
  const { data, loading } = useFetch("/api/report/lead-per-month");
  const [hint, setHint] = useState("Hover bar untuk detail");

  if (loading) return (
    <div className="card">
      <div className="card-head">
        <div><div className="card-title">📈 Tren Lead &amp; Closing</div></div>
      </div>
      <div className="chart-body" style={{ display: "flex", gap: 8, alignItems: "flex-end", height: 160, padding: "16px 18px" }}>
        {[80, 110, 90, 130, 120, 140].map((h, i) => (
          <div key={i} style={{ flex: 1, display: "flex", gap: 3, alignItems: "flex-end" }}>
            <Skeleton height={h} width={18} style={{ borderRadius: "4px 4px 0 0" }} />
            <Skeleton height={Math.round(h * 0.3)} width={18} style={{ borderRadius: "4px 4px 0 0" }} />
          </div>
        ))}
      </div>
    </div>
  );

  const allData = Array.isArray(data) ? data : [];

  // Pilih data sesuai periode
  let monthsData;
  if (period === "bulan" || period === "minggu") {
    // 6 bulan terakhir dari bulan yang dipilih
    monthsData = getLast6Months(allData, selectedMonth, selectedYear);
  } else if (period === "kuartal") {
    // 3 bulan terakhir
    monthsData = getLast6Months(allData, selectedMonth, selectedYear).slice(-3);
  } else {
    // tahun: semua data yang ada
    monthsData = allData;
  }

  // Jika data dari backend kurang dari 6 bulan, tetap tampilkan slot kosong
  const MONTH_IDX = {"Jan":0,"Feb":1,"Mar":2,"Apr":3,"Mei":4,"Jun":5,"Jul":6,"Agu":7,"Sep":8,"Okt":9,"Nov":10,"Des":11};
  const dataMap = {};
  allData.forEach(d => {
    const label = d.m ?? d.label ?? d.month ?? "";
    dataMap[label] = d;
  });

  // Bangun 6 slot bulan terakhir dengan data atau 0
  const slots = [];
  const slotCount = period === "kuartal" ? 3 : period === "tahun" ? 12 : 6;
  for (let i = slotCount - 1; i >= 0; i--) {
    let mIdx = (selectedMonth - 1) - i;
    if (mIdx < 0) mIdx += 12;
    const label = MONTH_NAMES[mIdx];
    const found = dataMap[label] ?? null;
    slots.push({
      label,
      leads:   found ? (found.leads   ?? found.count ?? 0) : 0,
      closing: found ? (found.closing ?? found.closingCount ?? 0) : 0,
    });
  }

  const max = Math.max(...slots.map(d => Math.max(d.leads, d.closing)), 1);

  const periodLabel = {
    minggu: "6 bulan terakhir", bulan: "6 bulan terakhir",
    kuartal: "3 bulan terakhir", tahun: "12 bulan (setahun)"
  }[period] ?? "6 bulan terakhir";

  return (
    <div className="card">
      <div className="card-head">
        <div>
          <div className="card-title">📈 Tren Lead &amp; Closing</div>
          <div className="card-sub">{periodLabel}</div>
        </div>
        <div style={{ display: "flex", gap: 6 }}>
          <div className="chip active">Bar</div>
          <div className="chip">Trend</div>
        </div>
      </div>
      <div className="chart-body">
        <div className="chart-container">
          <div className="chart-grid">
            <div className="grid-line" /><div className="grid-line" />
            <div className="grid-line" /><div className="grid-line" />
          </div>
          {slots.map((d, i) => {
            const h1 = Math.round((d.leads   / max) * 140);
            const h2 = Math.round((d.closing / max) * 140);
            return (
              <div
                key={i}
                className="bar-group"
                onMouseEnter={() =>
                  setHint(`${d.label}: ${d.leads} lead · ${d.closing} closing · Rate ${d.leads > 0 ? Math.round((d.closing / d.leads) * 100) : 0}%`)
                }
                onMouseLeave={() => setHint("Hover bar untuk detail")}
              >
                <div className="bars">
                  <div className="bar b-lead"
                    style={{ width: 18, height: Math.max(h1, 2) }}
                    title={`${d.leads} lead`}
                  />
                  <div className="bar b-closing"
                    style={{ width: 18, height: d.closing > 0 ? Math.max(h2, 8) : 0 }}
                    title={`${d.closing} closing`}
                  />
                </div>
                <div className="b-month">{d.label}</div>
              </div>
            );
          })}
        </div>
      </div>
      <div className="chart-legend">
        <div className="legend-item">
          <div className="legend-dot" style={{ background: "var(--brand-gold)" }} /> Lead Masuk
        </div>
        <div className="legend-item">
          <div className="legend-dot" style={{ background: "#3b82f6" }} /> Closing
        </div>
        <div className="legend-item" style={{ marginLeft: "auto", fontSize: 11, color: "var(--text-muted)" }}>
          {hint}
        </div>
      </div>
    </div>
  );
}

/* ════════════════════════════════════════
   DONUT CHART  →  GET /api/report/lead-status
   Ekspektasi response: [ { status: "Hot", count: 16 }, ... ]
   ════════════════════════════════════════ */
function DonutChart() {
  const { data, loading } = useFetch("/api/report/lead-status");

  if (loading) return (
    <div className="card">
      <div className="card-head"><div className="card-title">🔵 Status Lead</div></div>
      <div className="donut-wrap">
        <Skeleton height={140} width={140} style={{ borderRadius: "50%" }} />
        <div style={{ width: "100%", marginTop: 12, display: "flex", flexDirection: "column", gap: 8 }}>
          {[1, 2, 3, 4, 5].map((i) => <Skeleton key={i} height={14} />)}
        </div>
      </div>
    </div>
  );

  const rawList = Array.isArray(data) ? data : [];
  // Backend return: { m: "New Lead", leads: 3, closing: 0 }
  const statusDist = rawList.map((d) => {
    const labelKey = d.m ?? d.label ?? d.status ?? d.name ?? "–";
    return {
      label: labelKey,
      val:   d.leads ?? d.count ?? d.val ?? 0,
      color: (STATUS_COLORS[labelKey] ?? { color: "#9ca3af" }).color,
    };
  });

  const total = statusDist.reduce((a, b) => a + b.val, 0) || 1;
  const r = 50, cx = 70, cy = 70;
  const circ = 2 * Math.PI * r;
  let offset = 0;

  const slices = statusDist.map((d, index) => {
    const dash = circ * (d.val / total);
    const el = (
      <circle
        key={d.label || d.color || index}
        cx={cx} cy={cy} r={r}
        fill="none"
        stroke={d.color}
        strokeWidth="22"
        strokeDasharray={`${dash} ${circ - dash}`}
        strokeDashoffset={-offset}
        transform={`rotate(-90 ${cx} ${cy})`}
      />
    );
    offset += dash;
    return el;
  });

  return (
    <div className="card">
      <div className="card-head">
        <div>
          <div className="card-title">🔵 Status Lead</div>
          <div className="card-sub">Distribusi saat ini</div>
        </div>
      </div>
      <div className="donut-wrap">
        <svg className="donut-svg" viewBox="0 0 140 140">
          {slices}
          <text x="70" y="65" textAnchor="middle" fontSize="20" fontWeight="800" fill="#1a1a2e">{total}</text>
          <text x="70" y="82" textAnchor="middle" fontSize="10" fill="#9ca3af">Total</text>
        </svg>
        <div className="donut-legend">
          {statusDist.map((d, index) => (
            <div key={`legend-${d.label}-${index}`} className="dl-item">
              <div className="dl-dot" style={{ background: d.color }} />
              <div style={{ flex: 1 }}>
                <div style={{ display: "flex", justifyContent: "space-between" }}>
                  <span className="dl-label">{d.label}</span>
                  <span className="dl-val">{d.val}</span>
                </div>
                <div className="dl-bar-wrap">
                  <div className="dl-bar-fill" style={{ width: `${Math.round((d.val / total) * 100)}%`, background: d.color }} />
                </div>
              </div>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}

/* ════════════════════════════════════════
   SALES RANKING  →  GET /api/report/sales-performance
   Ekspektasi response: [ { salesName: "Budi", closingCount: 5, leadCount: 8 }, ... ]
   ════════════════════════════════════════ */
function SalesRanking() {
  const { data, loading } = useFetch("/api/report/sales-performance");

  if (loading) return (
    <div className="card">
      <div className="card-head"><div className="card-title">🏆 Ranking Sales</div></div>
      <div className="rank-table">
        {[1, 2, 3, 4, 5].map((i) => (
          <div key={i} className="rt-row">
            <Skeleton height={22} width={22} style={{ borderRadius: "50%" }} />
            <Skeleton height={28} width={28} style={{ borderRadius: "50%" }} />
            <div className="rt-info"><Skeleton height={12} /><Skeleton height={4} style={{ marginTop: 4 }} /></div>
            <Skeleton height={16} width={24} />
          </div>
        ))}
      </div>
    </div>
  );

  const rawList = Array.isArray(data) ? data : [];
  // Backend getTopSales() return ChartResponse(salesName, 0, closingCount)
  // → field: m = nama sales, leads = 0, closing = jumlah closing
  // Sort by closing DESC, top 5
  const sorted = [...rawList]
    .sort((a, b) => (b.closing ?? 0) - (a.closing ?? 0))
    .slice(0, 5);
  const max = sorted[0]?.closing ?? 1;
  const rankCls = (i) => (i === 0 ? "r1" : i === 1 ? "r2" : i === 2 ? "r3" : "r-oth");
  const initials = (name = "") => name.split(" ").map((w) => w[0]).join("").substring(0, 2).toUpperCase();

  return (
    <div className="card">
      <div className="card-head">
        <div className="card-title">🏆 Ranking Sales</div>
        <div className="chip active">Closing</div>
      </div>
      <div className="rank-table">
        {sorted.map((s, i) => {
          // Backend return: { m: "Sales 2", leads: 3, closing: 0 }
          const name    = s.m ?? s.salesName ?? s.name ?? s.label ?? "–";
          const closing = s.closing ?? 0; // dari ChartResponse.closing (getTopSales)
          const color   = SALES_COLORS[i % SALES_COLORS.length];
          return (
            <div key={`sales-${i}-${name}`} className="rt-row">
              <div className={`rt-rank ${rankCls(i)}`}>{i + 1}</div>
              <div className="rt-av" style={{ background: color }}>{initials(name)}</div>
              <div className="rt-info">
                <div className="rt-name">{name}</div>
                <div className="rt-bar-wrap">
                  <div className="rt-bar-fill" style={{ width: `${Math.round((closing / max) * 100)}%`, background: color }} />
                </div>
              </div>
              <div className="rt-val">{closing}</div>
            </div>
          );
        })}
      </div>
    </div>
  );
}

/* ════════════════════════════════════════
   SUMBER LEAD  →  GET /api/report/followup-status
   ⚠️  CATATAN: Backend saat ini belum punya endpoint khusus sumber lead.
   Endpoint /followup-status mengembalikan status follow-up (Pending, Done, dll),
   BUKAN sumber lead (Instagram, Facebook, dll).
   Solusi sementara: tampilkan data follow-up status.
   Solusi permanen: tambah endpoint /api/report/lead-source di backend.
   ════════════════════════════════════════ */
function SumberLead() {
  const { data, loading } = useFetch("/api/report/lead-source");

  if (loading) return (
    <div className="card">
      <div className="card-head"><div className="card-title">📣 Sumber Lead</div></div>
      <div className="src-list">
        {[1, 2, 3, 4, 5, 6].map((i) => (
          <div key={i} className="src-item">
            <Skeleton height={28} width={28} style={{ borderRadius: 8 }} />
            <div className="src-info"><Skeleton height={12} /><Skeleton height={4} style={{ marginTop: 4 }} /></div>
            <Skeleton height={14} width={24} />
          </div>
        ))}
      </div>
    </div>
  );

  const rawList = Array.isArray(data) ? data : [];
  const sources = rawList.map((d) => {
    // Backend return: { m: "Facebook", leads: 3, closing: 0 }
    const name  = d.m ?? d.source ?? d.label ?? d.name ?? d.status ?? "Lainnya";
    const count = d.leads ?? d.count ?? d.total ?? 0;
    const meta  = SOURCE_META[name] ?? { icon: "📌", color: "#6b7280", bg: "#f3f4f6" };
    return { name, count, ...meta };
  });

  const max   = Math.max(...sources.map((s) => s.count), 1);
  const total = sources.reduce((a, s) => a + s.count, 0) || 1;

  return (
    <div className="card">
      <div className="card-head">
        <div className="card-title">📣 Sumber Lead</div>
        <div className="chip active">Bulan Ini</div>
      </div>
      <div className="src-list">
        {sources.map((s, index) => (
          <div key={`src-${s.name}-${index}`} className="src-item">
            <div className="src-icon" style={{ background: s.bg }}>{s.icon}</div>
            <div className="src-info">
              <div className="src-name">{s.name}</div>
              <div className="src-bar-wrap">
                <div className="src-bar-fill" style={{ width: `${Math.round((s.count / max) * 100)}%`, background: s.color }} />
              </div>
            </div>
            <div className="src-count">{s.count}</div>
            <div className="src-pct">{Math.round((s.count / total) * 100)}%</div>
          </div>
        ))}
      </div>
    </div>
  );
}

/* ════════════════════════════════════════
   CONVERSION FUNNEL  →  GET /api/report/leads (tanpa filter)
   Dihitung dari field status di list leads
   ════════════════════════════════════════ */
function ConversionFunnel() {
  const { data, loading } = useFetch("/api/report/leads");

  if (loading) return (
    <div className="card">
      <div className="card-head"><div className="card-title">🔽 Conversion Funnel</div></div>
      <div className="funnel-wrap">
        {[1, 2, 3, 4, 5].map((i) => (
          <div key={i} className="funnel-step">
            <div className="funnel-bar-row">
              <Skeleton height={14} width={80} />
              <Skeleton height={28} style={{ flex: 1 }} />
              <Skeleton height={14} width={36} />
            </div>
          </div>
        ))}
      </div>
    </div>
  );

  // Backend return: { id, name, status: { id, statusName }, source, ... }
  const leads = Array.isArray(data) ? data : [];
  const total = leads.length;

  const countByStatus = (statuses) =>
    leads.filter((l) => {
      const statusName = l.status?.statusName ?? l.status ?? l.leadStatus ?? "";
      return statuses.includes(statusName);
    }).length;

  // Warna & label konsisten dengan Status Lead di DonutChart
  const funnelSteps = [
    { label: "Lead Masuk",  val: total,                          color: "#3b82f6" }, // sama dgn New Lead
    { label: "Contacted",   val: countByStatus(["Contacted"]),   color: "#f59e0b" }, // sama dgn Contacted
    { label: "Follow Up",   val: countByStatus(["Follow Up"]),   color: "#8b5cf6" }, // sama dgn Follow Up
    { label: "Negotiation", val: countByStatus(["Negotiation"]), color: "#f97316" }, // sama dgn Negotiation
    { label: "Closed",      val: countByStatus(["Closed"]),      color: "#22c55e" }, // sama dgn Closed
  ];

  const max = funnelSteps[0].val || 1;

  return (
    <div className="card">
      <div className="card-head">
        <div className="card-title">🔽 Conversion Funnel</div>
        <div className="chip active">Bulan Ini</div>
      </div>
      <div className="funnel-wrap">
        {funnelSteps.map((f, i) => (
          <div key={f.label} className="funnel-step">
            <div className="funnel-bar-row">
              <div className="funnel-label">{f.label}</div>
              <div className="funnel-track">
                <div
                  className="funnel-fill"
                  style={{ width: `${Math.round((f.val / max) * 100)}%`, background: f.color }}
                >
                  {f.val}
                </div>
              </div>
              <div className="funnel-num">{Math.round((f.val / max) * 100)}%</div>
            </div>
            {i < funnelSteps.length - 1 && (
              <div className="funnel-arrow">
                ↓ {funnelSteps[i + 1].val} ({funnelSteps[i].val > 0 ? Math.round((funnelSteps[i + 1].val / funnelSteps[i].val) * 100) : 0}% lanjut)
              </div>
            )}
          </div>
        ))}
      </div>
    </div>
  );
}

/* ════════════════════════════════════════
   SUMMARY TABLE  →  GET /api/report/lead-per-month
   ════════════════════════════════════════ */
function SummaryTable() {
  const { data, loading } = useFetch("/api/report/lead-per-month");

  const headers = ["Bulan", "Lead Masuk", "Closing", "Closing Rate", "Follow Up", "Est. Revenue", "vs Bulan Lalu"];

  if (loading) return (
    <div className="card">
      <div className="card-head"><div className="card-title">📋 Rekap Bulanan</div></div>
      <div style={{ padding: 16, display: "flex", flexDirection: "column", gap: 10 }}>
        {[1, 2, 3, 4, 5, 6].map((i) => <Skeleton key={i} height={36} />)}
      </div>
    </div>
  );

  const rows = Array.isArray(data) ? [...data].reverse() : [];
  const formatRevIdr = (val) => {
    if (!val || val === 0) return "Rp 0";
    if (val >= 1_000_000_000_000) return `Rp ${(val / 1_000_000_000_000).toFixed(1)} T`;
    if (val >= 1_000_000_000)     return `Rp ${(val / 1_000_000_000).toFixed(1)} M`;
    if (val >= 1_000_000)         return `Rp ${(val / 1_000_000).toFixed(1)} Jt`;
    return `Rp ${Number(val).toLocaleString("id-ID")}`;
  };

  return (
    <div className="card">
      <div className="card-head">
        <div>
          <div className="card-title">📋 Rekap Bulanan</div>
          <div className="card-sub">Perbandingan performa 6 bulan terakhir</div>
        </div>
        <div className="chip">Download CSV</div>
      </div>
      <div style={{ overflowX: "auto" }}>
        <table className="sum-table">
          <thead>
            <tr>{headers.map((h) => <th key={h}>{h}</th>)}</tr>
          </thead>
          <tbody>
            {rows.map((d, i) => {
              const prev      = rows[i + 1];
              const leads     = d.leads ?? d.count ?? d.leadCount ?? 0;
              const closing   = d.closing ?? d.closingCount  ?? 0;
              const followup  = d.followup  ?? d.followUpCount ?? 0;
              const revenue   = d.revenue   ?? d.estimatedRevenue ?? 0;
              const label     = d.m ?? d.label ?? d.month ?? `Bln ${i + 1}`;
              const rate      = leads > 0 ? Math.round((closing / leads) * 100) : 0;
              const prevClose = prev ? (prev.closing ?? prev.closingCount ?? 0) : null;
              const diff      = prevClose !== null ? closing - prevClose : null;

              return (
                <tr key={label}>
                  <td style={{ fontWeight: 700 }}>{label}</td>
                  <td>{leads}</td>
                  <td style={{ fontWeight: 700, color: "var(--success)" }}>{closing}</td>
                  <td>
                    <span style={{
                      display: "inline-flex", alignItems: "center", gap: 4,
                      padding: "2px 8px", borderRadius: 20, fontSize: 11, fontWeight: 700,
                      background: rate >= 25 ? "#dcfce7" : "#fef3cd",
                      color:      rate >= 25 ? "#16a34a" : "#b45309",
                    }}>
                      {rate}%
                    </span>
                  </td>
                  <td>{followup}</td>
                  <td style={{ fontWeight: 700 }}>{formatRevIdr(revenue)}</td>
                  <td className={diff === null ? "" : diff >= 0 ? "td-green" : "td-red"}>
                    {diff === null ? "–" : diff >= 0 ? `▲ +${diff}` : `▼ ${diff}`}
                  </td>
                </tr>
              );
            })}
          </tbody>
        </table>
      </div>
    </div>
  );
}

/* ════════════════════════════════════════
   MAIN COMPONENT
   ════════════════════════════════════════ */
export default function LeadEstateLaporan() {
  const navigate = useNavigate();

  // ── currentUser & isAdmin (sama persis dengan Dashboard) ────────────────
  const [activeNav, setActiveNav] = useState("report");
  const [currentUser, setCurrentUser] = useState(null);

  const isAdmin = currentUser?.role?.toLowerCase() === "admin";

  useEffect(() => {
    const userData = localStorage.getItem("user");
    if (!userData) { navigate("/"); return; }
    try {
      setCurrentUser(JSON.parse(userData));
    } catch {
      navigate("/");
    }
  }, [navigate]);

  // ── State filter periode ────────────────────────────────────────────────
  const now = new Date();
  const [activePeriod, setActivePeriod]   = useState("bulan");
  const [selectedMonth, setSelectedMonth] = useState(now.getMonth() + 1);
  const [selectedYear,  setSelectedYear]  = useState(now.getFullYear());

  const periods = [
    { key: "minggu",  label: "Minggu Ini" },
    { key: "bulan",   label: "Bulan Ini"  },
    { key: "kuartal", label: "Kuartal"    },
    { key: "tahun",   label: "Tahun Ini"  },
  ];

  const handlePeriod = (key) => {
    setActivePeriod(key);
    if (key === "tahun") {
      setSelectedMonth(12);
      setSelectedYear(now.getFullYear());
    } else {
      setSelectedMonth(now.getMonth() + 1);
      setSelectedYear(now.getFullYear());
    }
  };

  const MONTH_LABELS = ["Januari","Februari","Maret","April","Mei","Juni",
                        "Juli","Agustus","September","Oktober","November","Desember"];
  const monthOptions = [];
  for (let i = 0; i < 12; i++) {
    const d = new Date(now.getFullYear(), now.getMonth() - i, 1);
    monthOptions.push({
      label: `${MONTH_LABELS[d.getMonth()]} ${d.getFullYear()}`,
      month: d.getMonth() + 1,
      year:  d.getFullYear(),
    });
  }

  const handleMonthChange = (e) => {
    const [m, y] = e.target.value.split("-").map(Number);
    setSelectedMonth(m);
    setSelectedYear(y);
  };

  return (
    <>
      <style>{`
        @keyframes shimmer {
          0%   { background-position: 200% 0; }
          100% { background-position: -200% 0; }
        }
      `}</style>

      <div className="app-wrapper">

        {/* ── SIDEBAR (identik dengan Dashboard) ── */}
        <aside className="le-sidebar">
          <div className="le-brand">
            <img src={logo} alt="LeadEstate Logo" className="brand-logo" />
            <div className="le-brand-name">Lead<span>Estate</span></div>
          </div>

          <nav className="le-nav">
            <div className="le-nav-label">Menu Utama</div>

            <div className={`le-nav-item${activeNav === "dashboard" ? " active" : ""}`} onClick={() => { setActiveNav("dashboard"); navigate("/dashboard"); }}>
              <span className="le-nav-icon"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.8"><rect x="3" y="3" width="7" height="7" rx="1.5" /><rect x="14" y="3" width="7" height="7" rx="1.5" /><rect x="3" y="14" width="7" height="7" rx="1.5" /><rect x="14" y="14" width="7" height="7" rx="1.5" /></svg></span>
              Dashboard
            </div>

            <div className={`le-nav-item${activeNav === "reminder" ? " active" : ""}`} onClick={() => { setActiveNav("reminder"); navigate("/reminder"); }}>
              <span className="le-nav-icon"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.8"><circle cx="12" cy="12" r="10" /><polygon points="10,8 16,12 10,16" fill="currentColor" /></svg></span>
              Reminder &amp; Follow-Up
            </div>

            <div className={`le-nav-item${activeNav === "lead" ? " active" : ""}`} onClick={() => { setActiveNav("lead"); navigate("/dataLeads"); }}>
              <span className="le-nav-icon"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.8"><path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2" /><circle cx="9" cy="7" r="4" /></svg></span>
              Data Lead
            </div>

            {/* ── PROTEKSI MENU ADMIN (sama dengan Dashboard) ── */}
            {isAdmin && (
              <>
                <div className={`le-nav-item${activeNav === "sales" ? " active" : ""}`} onClick={() => { setActiveNav("sales"); navigate("/Manajemen_sales"); }}>
                  <span className="le-nav-icon"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.8"><rect x="2" y="3" width="20" height="18" rx="2" /><circle cx="12" cy="10" r="3" /><path d="M7 21v-1a5 5 0 0 1 10 0v1" /></svg></span>
                  Manajemen Sales
                </div>

                <div className="le-nav-label">Laporan</div>

                <div className={`le-nav-item${activeNav === "report" ? " active" : ""}`} onClick={() => setActiveNav("report")}>
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

        {/* MAIN */}
        <main className="main">
          <div className="topbar">
            <div className="topbar-title">Laporan &amp; Statistik</div>
            <div className="topbar-right">
              <div className="date-chip">📅 {now.toLocaleDateString("id-ID", { weekday: "long", day: "numeric", month: "long", year: "numeric" })}</div>
            </div>
          </div>

          <div className="content">
            {/* TOOLBAR */}
            <div className="toolbar">
              <div className="period-tabs">
                {periods.map((p) => (
                  <button
                    key={p.key}
                    className={`ptab${activePeriod === p.key ? " active" : ""}`}
                    onClick={() => handlePeriod(p.key)}
                  >
                    {p.label}
                  </button>
                ))}
              </div>

              <select
                className="period-select"
                value={`${selectedMonth}-${selectedYear}`}
                onChange={handleMonthChange}
                disabled={activePeriod === "tahun"}
                style={{ opacity: activePeriod === "tahun" ? 0.5 : 1 }}
              >
                {monthOptions.map((opt) => (
                  <option key={`${opt.month}-${opt.year}`} value={`${opt.month}-${opt.year}`}>
                    {opt.label}
                  </option>
                ))}
              </select>

              <select className="period-select">
                <option>Semua Sales</option>
              </select>
              <button
                className="print-btn"
                onClick={() => {
                  document.title = `Laporan-LeadEstate-${selectedMonth}-${selectedYear}`;
                  window.print();
                }}
              >
                🖨️ Print PDF
              </button>
            </div>

            <KpiGrid />

            <div className="row2">
              <BarChart period={activePeriod} selectedMonth={selectedMonth} selectedYear={selectedYear} />
              <DonutChart />
            </div>

            <div className="row3">
              <SalesRanking />
              <SumberLead />
              <ConversionFunnel />
            </div>

            <SummaryTable />
          </div>
        </main>
      </div>
    </>
  );
}

