import { useState, useEffect } from "react";
import "../styles/ManajemenSalesPage.css";
import { useNavigate } from "react-router-dom";  // ← tambah ini
import "../styles/Dashboard.css";

const API = import.meta.env.VITE_API_URL;

// ── CONSTANTS ─────────────────────────────────────────────────────────────────
const COLORS = [
  "#f59e0b", "#6366f1", "#10b981", "#ef4444",
  "#8b5cf6", "#0ea5e9", "#ec4899", "#14b8a6", "#f97316",
];

const LEAD_COLORS = [
  "#f59e0b", "#6366f1", "#10b981", "#ef4444", "#8b5cf6", "#0ea5e9",
];

const STATUS_MAP = {
  hot:     { cls: "tag-hot",     lbl: "🔥 Hot"      },
  warm:    { cls: "tag-warm",    lbl: "🌤 Warm"     },
  cold:    { cls: "tag-cold",    lbl: "❄️ Cold"     },
  new:     { cls: "tag-new",     lbl: "🆕 New"      },
  closing: { cls: "tag-closing", lbl: "✅ Closing"  },
};

const ROLE_OPTIONS = ["Senior Sales", "Sales Agent", "Junior Sales", "Team Leader"];

// ── HELPERS ───────────────────────────────────────────────────────────────────
function initials(name = "") {
  return name.split(" ").slice(0, 2).map((w) => w[0]).join("").toUpperCase();
}

function getRankStyle(rank) {
  if (rank === 0) return { background: "var(--brand-gold)", color: "var(--sidebar-bg)" };
  if (rank === 1) return { background: "#e5e7eb", color: "#374151" };
  if (rank === 2) return { background: "#fed7aa", color: "#9a3412" };
  return null;
}

function formatJoinDate(dateStr) {
  if (!dateStr) return "–";
  return new Date(dateStr).toLocaleDateString("id-ID", {
    day: "numeric", month: "long", year: "numeric",
  });
}

// ── SIDEBAR ───────────────────────────────────────────────────────────────────
function Sidebar() {
  const navigate = useNavigate();

  const currentUser = (() => {
    try { return JSON.parse(localStorage.getItem("user") || "{}"); } catch { return {}; }
  })();
  const userInitials = (currentUser.name || "U").split(" ").slice(0,2).map(w=>w[0]).join("").toUpperCase();
  const userRole = currentUser.role === "Admin" ? "Administrator"
    : currentUser.role === "Supervisor" ? "Supervisor"
    : currentUser.role === "Sales" ? "Sales"
    : currentUser.role || "Administrator";

  return (
    <aside className="le-sidebar">
      <div className="le-brand">
        <div className="le-brand-icon">L</div>
        <div className="le-brand-name">Lead<span>Estate</span></div>
      </div>

      <nav className="le-nav">
        <div className="le-nav-label">Menu Utama</div>

        <div className="le-nav-item" onClick={() => navigate("/dashboard")}>
          <span className="le-nav-icon">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.8">
              <rect x="3" y="3" width="7" height="7" rx="1.5" />
              <rect x="14" y="3" width="7" height="7" rx="1.5" />
              <rect x="3" y="14" width="7" height="7" rx="1.5" />
              <rect x="14" y="14" width="7" height="7" rx="1.5" />
            </svg>
          </span>
          Dashboard
        </div>

        <div className="le-nav-item" onClick={() => navigate("/reminder")}>
          <span className="le-nav-icon">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.8">
              <circle cx="12" cy="12" r="10" />
              <polygon points="10,8 16,12 10,16" fill="currentColor" />
            </svg>
          </span>
          Reminder &amp; Follow-Up
          <span className="le-nav-badge">5</span>
        </div>

        <div className="le-nav-item" onClick={() => navigate("/dataLeads")}>
          <span className="le-nav-icon">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.8">
              <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2" />
              <circle cx="9" cy="7" r="4" />
            </svg>
          </span>
          Data Lead
        </div>

        <div className="le-nav-item active">
          <span className="le-nav-icon">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.8">
              <rect x="2" y="3" width="20" height="18" rx="2" />
              <circle cx="12" cy="10" r="3" />
              <path d="M7 21v-1a5 5 0 0 1 10 0v1" />
            </svg>
          </span>
          Manajemen Sales
        </div>

        <div className="le-nav-label">Laporan</div>

        <div className="le-nav-item" onClick={() => navigate("/laporan")}>
          <span className="le-nav-icon">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.8">
              <line x1="18" y1="20" x2="18" y2="10" />
              <line x1="12" y1="20" x2="12" y2="4" />
              <line x1="6" y1="20" x2="6" y2="14" />
            </svg>
          </span>
          Laporan &amp; Statistik
        </div>

        <div className="le-nav-item" onClick={() => navigate("/settings")}>
          <span className="le-nav-icon">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.8">
              <circle cx="12" cy="12" r="3" />
              <path d="M19.07 4.93a10 10 0 0 1 0 14.14M4.93 4.93a10 10 0 0 0 0 14.14" />
            </svg>
          </span>
          Pengaturan
        </div>
      </nav>

      <div className="le-sidebar-footer">
        <div className="le-avatar">{userInitials}</div>
        <div>
          <div className="le-user-name">{currentUser.name || "User"}</div>
          <div className="le-user-role">{userRole}</div>
        </div>
      </div>
    </aside>
  );
}
// ── SALES ITEM ────────────────────────────────────────────────────────────────
function SalesItem({ sales, rank, selected, onClick }) {
  const pct = Math.round((sales.closing / sales.target) * 100);
  const barColor = pct >= 100 ? "#22c55e" : pct >= 60 ? "#f59e0b" : "#ef4444";
  const rankStyle = getRankStyle(rank);

  return (
    <div
      className={`sales-item${selected ? " selected" : ""}`}
      onClick={onClick}
    >
      <div className="si-av" style={{ background: sales.color }}>
        {initials(sales.name)}
        <div
          className="online-dot"
          style={{ background: sales.online ? "#22c55e" : "#9ca3af" }}
        />
      </div>

      <div className="si-info">
        <div className="si-name">{sales.name}</div>
        <div className="si-role">{sales.role}</div>
        <div className="si-progress">
          <div className="si-prog-label">
            <span>Target</span>
            <span>{sales.closing}/{sales.target}</span>
          </div>
          <div className="si-prog-track">
            <div
              className="si-prog-fill"
              style={{ width: `${Math.min(pct, 100)}%`, background: barColor }}
            />
          </div>
        </div>
      </div>

      <div className="si-right">
        <div className="si-closing">{sales.closing}</div>
        <div className="si-lbl">closing</div>
      </div>

      {rankStyle && rank < 3 && (
        <div className="rank-badge" style={rankStyle}>
          {rank + 1}
        </div>
      )}
    </div>
  );
}

// ── PROFILE CARD ──────────────────────────────────────────────────────────────
function ProfileCard({ sales, onEdit, onDelete }) {
  const joinDate = formatJoinDate(sales.join);

  return (
    <div className="profile-card">
      <div className="profile-banner" />
      <div className="profile-body">
        <div className="profile-av" style={{ background: sales.color }}>
          {initials(sales.name)}
        </div>

        <div className="profile-name-row">
          <div>
            <div className="profile-name">
              {sales.name}{" "}
              {sales.online ? (
                <span className="online-pill online">● Online</span>
              ) : (
                <span className="online-pill offline">Offline</span>
              )}
            </div>
            <div className="profile-role-txt">{sales.role}</div>
          </div>

          <div className="profile-actions">
            <button
  className="pa-btn wa"
  onClick={() => {
    if (!sales.phone) {
      alert("Nomor HP tidak tersedia");
      return;
    }

    let phone = sales.phone.replace(/\D/g, "");

    if (phone.startsWith("08")) {
      phone = "62" + phone.slice(1);
    }

    const text = `Halo ${sales.name}, bagaimana progress lead hari ini?`;

    window.open(
      `https://wa.me/${phone}?text=${encodeURIComponent(text)}`,
      "_blank"
    );
  }}
>
  💬 WA
</button>
            <button className="pa-btn edit" onClick={() => onEdit(sales)}>
              ✏️ Edit
            </button>
            <button className="pa-btn del" onClick={() => onDelete(sales.id)}>
              🗑
            </button>
          </div>
        </div>

        <div className="profile-meta">
          <div className="pm-item">📞 {sales.phone}</div>
          <div className="pm-item">✉️ {sales.email}</div>
          <div className="pm-item">📅 Bergabung {joinDate}</div>
          <div className="pm-item">🎯 Target: {sales.target} closing/bulan</div>
        </div>
      </div>
    </div>
  );
}

// ── STATS ROW ─────────────────────────────────────────────────────────────────
function StatsRow({ sales }) {
  const pct = Math.round((sales.closing / sales.target) * 100);
  const rateColor = pct >= 100 ? "#22c55e" : pct >= 60 ? "#f59e0b" : "#ef4444";
  const subText =
    pct >= 100 ? "🎉 Target tercapai!" : pct >= 60 ? "🔥 Hampir sampai" : "⚠️ Perlu ditingkatkan";

  return (
    <div className="stats-row">
      <div className="stat-box sb-gold">
        <div className="stat-box-val">{sales.leads_total}</div>
        <div className="stat-box-lbl">Total Lead</div>
        <div className="stat-box-sub">Ditangani bulan ini</div>
      </div>
      <div className="stat-box sb-blue">
        <div className="stat-box-val">{sales.followup}</div>
        <div className="stat-box-lbl">Follow Up</div>
        <div className="stat-box-sub">Total aktivitas</div>
      </div>
      <div className="stat-box sb-green">
        <div className="stat-box-val">{sales.closing}</div>
        <div className="stat-box-lbl">Closing</div>
        <div className="stat-box-sub">Dari target {sales.target}</div>
      </div>
      <div className="stat-box sb-red">
        <div className="stat-box-val" style={{ color: rateColor }}>{pct}%</div>
        <div className="stat-box-lbl">Pencapaian</div>
        <div className="stat-box-sub">{subText}</div>
      </div>
    </div>
  );
}

// ── PERFORMANCE CARD ──────────────────────────────────────────────────────────
function PerformanceCard({ sales }) {
  const pct = Math.round((sales.closing / sales.target) * 100);
  const rateColor = pct >= 100 ? "#22c55e" : pct >= 60 ? "#f59e0b" : "#ef4444";

  const metrics = [
    {
      label: "Target Closing",
      val: `${sales.closing} / ${sales.target}`,
      pct: Math.min(pct, 100),
      color: rateColor,
    },
    {
      label: "Tingkat Respons Lead",
      val: `${Math.min(95, 60 + sales.closing * 5)}%`,
      pct: Math.min(95, 60 + sales.closing * 5),
      color: "#3b82f6",
    },
    {
      label: "Follow Up Tepat Waktu",
      val: `${Math.min(98, 75 + sales.closing * 4)}%`,
      pct: Math.min(98, 75 + sales.closing * 4),
      color: "#8b5cf6",
    },
    {
      label: "Avg. Waktu Closing",
      val: `${7 + (sales.id % 5)} hari`,
      pct: 70,
      color: "#f59e0b",
    },
    {
      label: "Kepuasan Pelanggan",
      val: `⭐ ${(4 + sales.closing * 0.1).toFixed(1)}/5`,
      pct: ((4 + sales.closing * 0.1) / 5) * 100,
      color: "#ec4899",
    },
  ];

  return (
    <div className="section-card">
      <div className="sc-head">
        <div className="sc-title">📊 Performa Detail</div>
        <div className="sc-badge">
          {new Date().toLocaleDateString("id-ID", { month: "long", year: "numeric" })}
        </div>
      </div>
      <div className="perf-list">
        {metrics.map((m, i) => (
          <div className="perf-item" key={i}>
            <div className="perf-top">
              <span className="perf-label">{m.label}</span>
              <span className="perf-val" style={{ color: i === 0 ? rateColor : undefined }}>
                {m.val}
              </span>
            </div>
            <div className="perf-bar-wrap">
              <div
                className="perf-bar-fill"
                style={{ width: `${m.pct}%`, background: m.color }}
              />
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}

// ── LEAD MINI LIST ────────────────────────────────────────────────────────────
function LeadMiniList({ leads = [] }) {
  return (
    <div className="section-card">
      <div className="sc-head">
        <div className="sc-title">📋 Lead Ditangani</div>
        <div className="sc-badge">{leads.length} lead</div>
      </div>
      <div className="lead-mini-list">
        {leads.length === 0 ? (
          <div style={{ padding: "16px", textAlign: "center", color: "var(--text-muted)", fontSize: 12 }}>
            Belum ada lead
          </div>
        ) : (
          leads.map((l, i) => {
            const sm = STATUS_MAP[l.status] || STATUS_MAP.new;
            return (
              <div className="lml-item" key={i}>
                <div
                  className="lml-av"
                  style={{ background: LEAD_COLORS[i % LEAD_COLORS.length] }}
                >
                  {initials(l.name)}
                </div>
                <div className="lml-info">
                  <div className="lml-name">{l.name}</div>
                  <div className="lml-prop">🏠 {l.prop}</div>
                </div>
                <span className={`tag-sm ${sm.cls}`}>{sm.lbl}</span>
              </div>
            );
          })
        )}
      </div>
    </div>
  );
}

// ── ACTIVITY LIST ─────────────────────────────────────────────────────────────
function ActivityList({ activity = [] }) {
  return (
    <div className="section-card">
      <div className="sc-head">
        <div className="sc-title">🕐 Aktivitas Terbaru</div>
      </div>
      <div className="activity-list">
        {activity.length === 0 ? (
          <div style={{ color: "var(--text-muted)", fontSize: 12 }}>
            Belum ada aktivitas
          </div>
        ) : (
          activity.map((a, i) => (
            <div className="act-item" key={i}>
              <div className="act-dot" style={{ background: a.color }} />
              <div>
                <div className="act-txt">{a.txt}</div>
                <div className="act-time">{a.time}</div>
              </div>
            </div>
          ))
        )}
      </div>
    </div>
  );
}

// ── DETAIL PANEL ──────────────────────────────────────────────────────────────
function DetailPanel({ sales, onEdit, onDelete }) {
  if (!sales) {
    return (
      <div className="right-panel">
        <div className="empty-state">
          <svg viewBox="0 0 24 24" fill="currentColor">
            <path d="M19 3H5c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2zm-7 3c1.93 0 3.5 1.57 3.5 3.5S13.93 13 12 13s-3.5-1.57-3.5-3.5S10.07 6 12 6zm7 13H5v-.23c0-.62.28-1.2.76-1.58C7.47 15.82 9.64 15 12 15s4.53.82 6.24 2.19c.48.38.76.97.76 1.58V19z" />
          </svg>
          <p>Pilih sales dari daftar<br />untuk melihat profil &amp; performa</p>
        </div>
      </div>
    );
  }

  return (
    <div className="right-panel">
      <ProfileCard sales={sales} onEdit={onEdit} onDelete={onDelete} />
      <StatsRow sales={sales} />
      <div className="mid-row">
        <PerformanceCard sales={sales} />
        <LeadMiniList leads={sales.leads} />
      </div>
      <ActivityList activity={sales.activity} />
    </div>
  );
}

// ── SALES MODAL ───────────────────────────────────────────────────────────────
function SalesModal({ isOpen, editData, onClose, onSave }) {
  const [form, setForm] = useState({
    name: "",
    phone: "",
    email: "",
    role: "Sales",
    target: 3,
    join: new Date().toISOString().split("T")[0],
    color: COLORS[0],
  });

  useEffect(() => {
    if (editData) {
      setForm({
        name: editData.name || "",
        phone: editData.phone || "",
        email: editData.email || "",
        role: "Sales",
        target: editData.target || 3,
        join:
          editData.join || new Date().toISOString().split("T")[0],
        color: editData.color || COLORS[0],
      });
    } else {
      setForm({
        name: "",
        phone: "",
        email: "",
        role: "Sales",
        target: 3,
        join: new Date().toISOString().split("T")[0],
        color: COLORS[0],
      });
    }
  }, [editData, isOpen]);

  function handleChange(field, value) {
    setForm((prev) => ({
      ...prev,
      [field]: value,
    }));
  }

  function handleSubmit() {
    if (!form.name.trim() || !form.phone.trim()) {
      alert("Nama dan Nomor HP wajib diisi!");
      return;
    }

    onSave(form);
  }

  if (!isOpen) return null;

  return (
    <div
      className="modal-overlay"
      onClick={(e) =>
        e.target === e.currentTarget && onClose()
      }
    >
      <div className="modal">
        {/* HEADER */}
        <div className="modal-head">
          <div className="modal-title">
            {editData
              ? "Edit Data Sales"
              : "Tambah Sales Baru"}
          </div>

          <button
            className="modal-close"
            onClick={onClose}
          >
            ✕
          </button>
        </div>

        {/* BODY */}
        <div className="modal-body">
          {/* ROW 1 */}
          <div className="form-row">
            <div className="form-group">
              <label className="form-label">
                Nama Lengkap *
              </label>

              <input
                className="form-input"
                type="text"
                placeholder="Contoh: Budi Santoso"
                value={form.name}
                onChange={(e) =>
                  handleChange("name", e.target.value)
                }
              />
            </div>

            <div className="form-group">
              <label className="form-label">
                Nomor HP *
              </label>

              <input
                className="form-input"
                type="text"
                placeholder="0812-xxxx-xxxx"
                value={form.phone}
                onChange={(e) =>
                  handleChange("phone", e.target.value)
                }
              />
            </div>
          </div>

          {/* ROW 2 */}
          <div className="form-row">
            <div className="form-group">
              <label className="form-label">
                Email
              </label>

              <input
                className="form-input"
                type="email"
                placeholder="email@domain.com"
                value={form.email}
                onChange={(e) =>
                  handleChange("email", e.target.value)
                }
              />
            </div>

            <div className="form-group">
              <label className="form-label">
                Jabatan
              </label>

              {/* FIXED ROLE */}
              <input
                className="form-input"
                type="text"
                value="Sales"
                disabled
              />
            </div>
          </div>

          {/* ROW 3 */}
          <div className="form-row">
            <div className="form-group">
              <label className="form-label">
                Target Closing / Bulan
              </label>

              <input
                className="form-input"
                type="number"
                min="1"
                placeholder="Contoh: 5"
                value={form.target}
                onChange={(e) =>
                  handleChange(
                    "target",
                    parseInt(e.target.value) || 1
                  )
                }
              />
            </div>

            <div className="form-group">
              <label className="form-label">
                Bergabung Sejak
              </label>

              <input
                className="form-input"
                type="date"
                value={form.join}
                onChange={(e) =>
                  handleChange("join", e.target.value)
                }
              />
            </div>
          </div>

          {/* COLOR */}
          <div className="form-row form-row-full">
            <div className="form-group">
              <label className="form-label">
                Warna Avatar
              </label>

              <div className="color-row">
                {COLORS.map((c) => (
                  <div
                    key={c}
                    className={`color-opt${
                      form.color === c ? " sel" : ""
                    }`}
                    style={{ background: c }}
                    onClick={() =>
                      handleChange("color", c)
                    }
                  />
                ))}
              </div>
            </div>
          </div>
        </div>

        {/* FOOTER */}
        <div className="modal-foot">
          <button
            className="btn-cancel"
            onClick={onClose}
          >
            Batal
          </button>

          <button
            className="btn-save"
            onClick={handleSubmit}
          >
            💾 Simpan
          </button>
        </div>
      </div>
    </div>
  );
}

// ── MAIN PAGE ─────────────────────────────────────────────────────────────────
export default function ManajemenSalesPage() {
  const [salesTeam, setSalesTeam] = useState([]);
  const [selectedId, setSelectedId] = useState(null);
  const [searchQ, setSearchQ] = useState("");
  const [modalOpen, setModalOpen] = useState(false);
  const [editData, setEditData] = useState(null);
  const [loading, setLoading] = useState(true);

  // ── API CALLS ──────────────────────────────────────────────────────────────

  /**
   * GET /sales
   * Response: Array of sales objects
   * {
   *   id, name, phone, email, role, color, target, join, online,
   *   closing, followup, leads_total,
   *   leads: [{ name, prop, status }],
   *   activity: [{ txt, time, color }]
   * }
   */
  async function fetchSales() {
  try {
    setLoading(true);

    const token = localStorage.getItem("token");
    const currentUser = JSON.parse(localStorage.getItem("user") || "{}");
    const headers = {
      ...(token ? { Authorization: `Bearer ${token}` } : {}),
      Role: currentUser.role || "Admin",
    };

    const res = await fetch(`${API}/api/users`, { headers });

    const rawText = await res.text();
    console.log("RAW RESPONSE:", rawText);

    if (!res.ok) throw new Error(rawText);

    const data = JSON.parse(rawText);

    const salesOnly = (Array.isArray(data) ? data : []).filter(
      (u) => u.roleId === 3
    );

    // Fetch stats (closing, followup, leads) tiap sales secara paralel
    const mapped = await Promise.all(
      salesOnly.map(async (u, i) => {
        let closing = 0, followup = 0, leads = [], leadsTotal = 0;
        try {
          const statsRes = await fetch(`${API}/api/users/${u.id}/stats`, { headers });
          if (statsRes.ok) {
            const stats = await statsRes.json();
            closing    = stats.closing    ?? 0;
            followup   = stats.followup   ?? 0;
            leadsTotal = stats.leadsTotal ?? 0;
            leads      = (stats.leads     ?? []).slice(0, 10).map((l) => ({
              name:   l.name   ?? "–",
              prop:   l.propertyName ?? l.property ?? "–",
              status: l.statusName?.toLowerCase() ?? "new",
            }));
          }
        } catch (_) {
          console.warn(`Gagal fetch stats untuk user ${u.id}`);
        }

        return {
          id:          u.id,
          name:        u.name  ?? "–",
          email:       u.email ?? "–",
          phone:       u.phone ?? "–",
          role:        "Sales",
          color:       COLORS[i % COLORS.length],
          target:      10,
          closing,
          followup,
          leads_total: leadsTotal,
          online:      false,
          join:        null,
          leads,
          activity:    [],
        };
      })
    );

    setSalesTeam(mapped);

    if (mapped.length > 0 && !selectedId) {
      setSelectedId(mapped[0].id);
    }

  } catch (err) {
    console.error("ERROR fetchSales:", err);
    alert("Gagal mengambil data sales.\n\nCek console browser untuk detail error.");
  } finally {
    setLoading(false);
  }
}

  /**
   * POST /sales
   * Body: { name, phone, email, role, color, target, join }
   * Response: created sales object with id
   */
  async function createSales(formData) {
  try {
    const token = localStorage.getItem("token");
    const currentUser = JSON.parse(localStorage.getItem("user") || "{}");

    const payload = {
      name: formData.name,
      email: formData.email,
      phone: formData.phone,
      password: "sales123",
      roleId: 3,
    };

    const res = await fetch(`${API}/api/users`, {
      method: "POST",
      headers: {
        ...(token ? { Authorization: `Bearer ${token}` } : {}),
        Role: currentUser.role || "Admin",
        "Content-Type": "application/json",
      },
      body: JSON.stringify(payload),
    });

    const text = await res.text();

    if (!res.ok) {
      throw new Error(text);
    }

    const created = JSON.parse(text);

    const mapped = {
      id: created.id,
      name: created.name,
      email: created.email,
      phone: created.phone,
      role: "Sales",
      color: COLORS[salesTeam.length % COLORS.length],
      target: 10,
      closing: 0,
      followup: 0,
      online: false,
      join: null,
      leads: [],
      activity: [],
    };

    setSalesTeam((prev) => [...prev, mapped]);
    setSelectedId(mapped.id);

    alert("Sales berhasil ditambahkan");

  } catch (err) {
    console.error("ERROR createSales:", err);
    alert("Gagal menambah sales:\n" + err.message);
  }
}
 async function updateSales(id, formData) {
  try {
    const token = localStorage.getItem("token");
    const currentUser = JSON.parse(localStorage.getItem("user") || "{}");

    const existing = salesTeam.find((s) => s.id === id);

    const payload = {
      name: formData.name,
      email: formData.email,
      phone: formData.phone,
      password: "sales123",
      roleId: 3,
    };

    const res = await fetch(`${API}/api/users/${id}`, {
      method: "PUT",
      headers: {
        ...(token ? { Authorization: `Bearer ${token}` } : {}),
        Role: currentUser.role || "Admin",
        "Content-Type": "application/json",
      },
      body: JSON.stringify(payload),
    });

    const text = await res.text();

    if (!res.ok) {
      throw new Error(text);
    }

    const updated = JSON.parse(text);

    setSalesTeam((prev) =>
      prev.map((s) =>
        s.id === id
          ? {
              ...s,
              name: updated.name,
              email: updated.email,
              phone: updated.phone,
            }
          : s
      )
    );

    alert("Sales berhasil diupdate");

  } catch (err) {
    console.error("ERROR updateSales:", err);
    alert("Gagal update sales:\n" + err.message);
  }
}

  /**
   * DELETE /sales/:id
   * Response: { success: true }
   */
 async function deleteSales(id) {
  if (!confirm("Hapus data sales ini?")) return;

  try {
    const token = localStorage.getItem("token");
    const currentUser = JSON.parse(localStorage.getItem("user") || "{}");

    const res = await fetch(`${API}/api/users/${id}`, {
      method: "DELETE",
      headers: {
        ...(token ? { Authorization: `Bearer ${token}` } : {}),
        Role: currentUser.role || "Admin",
      },
    });

    const text = await res.text();

    if (!res.ok) {
      throw new Error(text);
    }

    setSalesTeam((prev) => prev.filter((s) => s.id !== id));

    if (selectedId === id) {
      const remaining = salesTeam.filter((s) => s.id !== id);
      setSelectedId(remaining.length > 0 ? remaining[0].id : null);
    }

    alert("Sales berhasil dihapus");

  } catch (err) {
    console.error("ERROR deleteSales:", err);
    alert("Gagal hapus sales:\n" + err.message);
  }
}

  useEffect(() => {
    fetchSales();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  // ── HANDLERS ──────────────────────────────────────────────────────────────
  function handleOpenAdd() {
    setEditData(null);
    setModalOpen(true);
  }

  function handleOpenEdit(sales) {
    setEditData(sales);
    setModalOpen(true);
  }

  function handleSave(formData) {
    if (editData) {
      updateSales(editData.id, formData);
    } else {
      createSales(formData);
    }
    setModalOpen(false);
    setEditData(null);
  }

  // ── DERIVED ───────────────────────────────────────────────────────────────
  const sorted = [...salesTeam].sort((a, b) => b.closing - a.closing);
  const filtered = sorted.filter((s) =>
    s.name.toLowerCase().includes(searchQ.toLowerCase())
  );
  const selectedSales = salesTeam.find((s) => s.id === selectedId) || null;

  // ── RENDER ────────────────────────────────────────────────────────────────
  return (
    <div className="page-root">
      <Sidebar />

      <main className="main">
        {/* TOPBAR */}
        <div className="topbar">
          <div className="topbar-title">Manajemen Sales</div>
          <div className="topbar-right">
            <div className="date-chip">
              📅{" "}
              {new Date().toLocaleDateString("id-ID", {
                weekday: "long", day: "numeric",
                month: "long", year: "numeric",
              })}
            </div>

          </div>
        </div>

        <div className="body-split">
          {/* LEFT PANEL */}
          <div className="left-panel">
            <div className="lp-header">
              <div className="lp-title">
                Tim Sales{" "}
                <span className="lp-count">({salesTeam.length} orang)</span>
              </div>
              <button className="add-btn" onClick={handleOpenAdd}>+ Tambah</button>
            </div>

            <div className="lp-search">
              <div className="search-wrap">
                <svg className="search-icon" width="14" height="14" viewBox="0 0 24 24" fill="currentColor">
                  <path d="M15.5 14h-.79l-.28-.27A6.47 6.47 0 0 0 16 9.5 6.5 6.5 0 1 0 9.5 16c1.61 0 3.09-.59 4.23-1.57l.27.28v.79l5 4.99L20.49 19l-4.99-5zm-6 0C7.01 14 5 11.99 5 9.5S7.01 5 9.5 5 14 7.01 14 9.5 11.99 14 9.5 14z" />
                </svg>
                <input
                  className="search-input"
                  placeholder="Cari nama sales..."
                  value={searchQ}
                  onChange={(e) => setSearchQ(e.target.value)}
                />
              </div>
            </div>

            <div className="sales-list">
              {loading ? (
                <div className="loading-state">Memuat data...</div>
              ) : filtered.length === 0 ? (
                <div className="empty-state">
                  <svg viewBox="0 0 24 24" fill="currentColor">
                    <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm1 15h-2v-2h2v2zm0-4h-2V7h2v6z" />
                  </svg>
                  <p>Tidak ada sales ditemukan</p>
                </div>
              ) : (
                filtered.map((s) => {
                  const rank = sorted.findIndex((x) => x.id === s.id);
                  return (
                    <SalesItem
                      key={s.id}
                      sales={s}
                      rank={rank}
                      selected={selectedId === s.id}
                      onClick={() => setSelectedId(s.id)}
                    />
                  );
                })
              )}
            </div>
          </div>

          {/* RIGHT PANEL */}
          <DetailPanel
            sales={selectedSales}
            onEdit={handleOpenEdit}
            onDelete={deleteSales}
          />
        </div>
      </main>

      {/* MODAL */}
      <SalesModal
        isOpen={modalOpen}
        editData={editData}
        onClose={() => { setModalOpen(false); setEditData(null); }}
        onSave={handleSave}
      />
    </div>
  );
}