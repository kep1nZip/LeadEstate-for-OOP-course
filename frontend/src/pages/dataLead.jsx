import { useEffect, useState, useMemo } from "react";
import "../styles/dataLead.css";
import { useLocation, useNavigate } from "react-router-dom";
import "../styles/Dashboard.css";
import logo from "../assets/leadestate-logo.png";

const API = import.meta.env.VITE_API_URL;

// ── CONSTANTS ─────────────────────────────────────────────────────────────────
const COLORS = ["#f59e0b","#6366f1","#10b981","#ef4444","#8b5cf6","#0ea5e9","#ec4899","#14b8a6"];

const STATUS_OPTIONS = [
  { val: "",  lbl: "Semua Status"  },
  { val: "1", lbl: "🆕 New Lead"   },
  { val: "2", lbl: "📞 Contacted"  },
  { val: "3", lbl: "🔄 Follow Up"  },
  { val: "4", lbl: "🤝 Negotiation"},
  { val: "5", lbl: "✅ Closed"     },
  { val: "6", lbl: "❌ Lost"       },
];

const SOURCE_OPTIONS = ["","Instagram","Facebook","Website","Referral","Walk-in","Whatsapp","TikTok"];

const STATUS_TAG = {
  "New Lead":    { cls:"tag-new",     lbl:"🆕 New Lead"    },
  "Contacted":   { cls:"tag-warm",    lbl:"📞 Contacted"   },
  "Follow Up":   { cls:"tag-warm",    lbl:"🔄 Follow Up"   },
  "Negotiation": { cls:"tag-hot",     lbl:"🤝 Negotiation" },
  "Closed":      { cls:"tag-done",    lbl:"✅ Closed"      },
  "Lost":        { cls:"tag-cold",    lbl:"❌ Lost"        },
};

const ROWS_PER_PAGE = 10;

// ── HELPERS ───────────────────────────────────────────────────────────────────
function initials(name = "") {
  return name.split(" ").slice(0,2).map(w => w[0]).join("").toUpperCase();
}

function formatDate(str) {
  if (!str) return "–";
  return new Date(str).toLocaleDateString("id-ID", { day:"numeric", month:"short", year:"numeric" });
}

function formatRupiah(num) {
  if (!num) return "–";
  if (num >= 1_000_000_000_000) return `Rp ${(num/1_000_000_000_000).toFixed(1)}T`;
  if (num >= 1_000_000_000)     return `Rp ${(num/1_000_000_000).toFixed(1)}M`;
  if (num >= 1_000_000)         return `Rp ${(num/1_000_000).toFixed(0)}Jt`;
  return `Rp ${num.toLocaleString("id-ID")}`;
}

// ── SIDEBAR ───────────────────────────────────────────────────────────────────
function Sidebar() {
  const navigate = useNavigate();

  const user = JSON.parse(localStorage.getItem("user") || "{}");

  const isAdmin = user?.role?.toLowerCase() === "admin";

  return (
    <aside className="le-sidebar">
      <div className="le-brand">
        <img src={logo} alt="LeadEstate Logo" className="brand-logo" />

        <div className="le-brand-name">
          Lead<span>Estate</span>
        </div>
      </div>

      <nav className="le-nav">
        <div className="le-nav-label">Menu Utama</div>

        {/* DASHBOARD */}
        <div className="le-nav-item" onClick={() => navigate("/dashboard")}>
          <span className="le-nav-icon">
            <svg
              viewBox="0 0 24 24"
              fill="none"
              stroke="currentColor"
              strokeWidth="1.8"
            >
              <rect x="3" y="3" width="7" height="7" rx="1.5" />
              <rect x="14" y="3" width="7" height="7" rx="1.5" />
              <rect x="3" y="14" width="7" height="7" rx="1.5" />
              <rect x="14" y="14" width="7" height="7" rx="1.5" />
            </svg>
          </span>
          Dashboard
        </div>

        {/* REMINDER */}
        <div className="le-nav-item" onClick={() => navigate("/reminder")}>
          <span className="le-nav-icon">
            <svg
              viewBox="0 0 24 24"
              fill="none"
              stroke="currentColor"
              strokeWidth="1.8"
            >
              <circle cx="12" cy="12" r="10" />
              <polygon points="10,8 16,12 10,16" fill="currentColor" />
            </svg>
          </span>
          Reminder &amp; Follow-Up
          <span className="le-nav-badge">5</span>
        </div>

        {/* DATA LEAD */}
        <div className="le-nav-item active">
          <span className="le-nav-icon">
            <svg
              viewBox="0 0 24 24"
              fill="none"
              stroke="currentColor"
              strokeWidth="1.8"
            >
              <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2" />
              <circle cx="9" cy="7" r="4" />
            </svg>
          </span>
          Data Lead
        </div>

        {/* ADMIN ONLY */}
        {isAdmin && (
          <>
            <div
              className="le-nav-item"
              onClick={() => navigate("/Manajemen_sales")}
            >
              <span className="le-nav-icon">
                <svg
                  viewBox="0 0 24 24"
                  fill="none"
                  stroke="currentColor"
                  strokeWidth="1.8"
                >
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
                <svg
                  viewBox="0 0 24 24"
                  fill="none"
                  stroke="currentColor"
                  strokeWidth="1.8"
                >
                  <line x1="18" y1="20" x2="18" y2="10" />
                  <line x1="12" y1="20" x2="12" y2="4" />
                  <line x1="6" y1="20" x2="6" y2="14" />
                </svg>
              </span>
              Laporan &amp; Statistik
            </div>
          </>
        )}

        {/* SETTINGS */}
        <div className="le-nav-item" onClick={() => navigate("/settings")}>
          <span className="le-nav-icon">
            <svg
              viewBox="0 0 24 24"
              fill="none"
              stroke="currentColor"
              strokeWidth="1.8"
            >
              <circle cx="12" cy="12" r="3" />
              <path d="M19.07 4.93a10 10 0 0 1 0 14.14M4.93 4.93a10 10 0 0 0 0 14.14" />
            </svg>
          </span>
          Pengaturan
        </div>
      </nav>

      <div className="le-sidebar-footer">
        <div className="le-avatar">{initials(user.name || "AR")}</div>

        <div>
          <div className="le-user-name">{user.name || "Admin"}</div>

          <div className="le-user-role">{user.role || "Administrator"}</div>
        </div>
      </div>
    </aside>
  );
}

// ── MODAL TAMBAH / EDIT LEAD ──────────────────────────────────────────────────
function LeadModal({ isOpen, editData, onClose, onSave }) {
  const empty = { name:"", phone:"", email:"", propertyId:"", salesId:"", statusId:"1", source:"" };
  const [form, setForm] = useState(empty);

  useEffect(() => {
    if (editData) {
      setForm({
        name:       editData.name       ?? "",
        phone:      editData.phone      ?? "",
        email:      editData.email      ?? "",
        propertyId: editData.property?.id ?? "",
        salesId:    editData.sales?.id    ?? "",
        statusId:   editData.status?.id   ?? "1",
        source:     editData.source     ?? "",
      });
    } else {
      setForm(empty);
    }
  }, [editData, isOpen]);

  useEffect(() => {
    if (location.state) {
      setForm((prev) => ({
        ...prev,
        leadId: location.state.leadId,
        leadName: location.state.leadName,
        phone: location.state.phone,
      }));
    }
  }, [location.state]);

  if (!isOpen) return null;

  const set = (k) => (e) => setForm(f => ({ ...f, [k]: e.target.value }));

  return (
    <div className="modal-overlay show" onClick={onClose}>
      <div className="modal" onClick={e => e.stopPropagation()}>
        <div className="modal-head">
          <div className="modal-title">{editData ? "Edit Lead" : "Tambah Lead Baru"}</div>
          <button className="modal-close" onClick={onClose}>✕</button>
        </div>
        <div className="modal-body">
          <div className="form-row">
            <div className="form-group">
              <label className="form-label">Nama Lengkap *</label>
              <input className="form-input" placeholder="Contoh: Budi Santoso" value={form.name} onChange={set("name")} />
            </div>
            <div className="form-group">
              <label className="form-label">Nomor HP *</label>
              <input className="form-input" placeholder="0812-xxxx-xxxx" value={form.phone} onChange={set("phone")} />
            </div>
          </div>
          <div className="form-row">
            <div className="form-group">
              <label className="form-label">Email</label>
              <input className="form-input" type="email" placeholder="email@contoh.com" value={form.email} onChange={set("email")} />
            </div>
            <div className="form-group">
              <label className="form-label">Sumber Lead</label>
              <select className="form-select" value={form.source} onChange={set("source")}>
                {SOURCE_OPTIONS.filter(Boolean).map(s => <option key={s} value={s}>{s}</option>)}
              </select>
            </div>
          </div>
          <div className="form-row">
            <div className="form-group">
              <label className="form-label">Status</label>
              <select className="form-select" value={form.statusId} onChange={set("statusId")}>
                {STATUS_OPTIONS.filter(o => o.val).map(o => <option key={o.val} value={o.val}>{o.lbl}</option>)}
              </select>
            </div>
            <div className="form-group">
              <label className="form-label">ID Properti</label>
              <input className="form-input" placeholder="ID properti" value={form.propertyId} onChange={set("propertyId")} />
            </div>
          </div>
          <div className="form-row full">
            <div className="form-group">
              <label className="form-label">ID Sales PIC</label>
              <input className="form-input" placeholder="ID sales" value={form.salesId} onChange={set("salesId")} />
            </div>
          </div>
        </div>
        <div className="modal-foot">
          <button className="btn-cancel" onClick={onClose}>Batal</button>
          <button className="btn-save" onClick={() => onSave(form)}>
            {editData ? "Simpan Perubahan" : "Tambah Lead"}
          </button>
        </div>
      </div>
    </div>
  );
}

// ── DETAIL MODAL ──────────────────────────────────────────────────────────────
function DetailModal({ lead, onClose }) {
  if (!lead) return null;
  const statusName = lead.status?.statusName ?? "–";
  const tag = STATUS_TAG[statusName] ?? { cls:"tag-cold", lbl: statusName };

  return (
    <div className="modal-overlay show" onClick={onClose}>
      <div className="modal" onClick={e => e.stopPropagation()}>
        <div className="modal-head">
          <div className="modal-title">Detail Lead</div>
          <button className="modal-close" onClick={onClose}>✕</button>
        </div>
        <div className="modal-body">
          <div style={{ display:"flex", alignItems:"center", gap:14, marginBottom:18 }}>
            <div className="lc-av" style={{ width:48, height:48, fontSize:16, background: COLORS[lead.id % COLORS.length] }}>
              {initials(lead.name)}
            </div>
            <div>
              <div style={{ fontWeight:700, fontSize:16 }}>{lead.name}</div>
              <div style={{ fontSize:12, color:"var(--text-muted)" }}>{lead.phone}</div>
              <span className={`tag ${tag.cls}`} style={{ marginTop:4, display:"inline-flex" }}>{tag.lbl}</span>
            </div>
          </div>
          <div className="detail-section">
            <div className="ds-title">Informasi Lead</div>
            <div className="ds-grid">
              <div className="ds-item"><div className="ds-lbl">Email</div><div className="ds-val">{lead.email ?? "–"}</div></div>
              <div className="ds-item"><div className="ds-lbl">Sumber</div><div className="ds-val">{lead.source ?? "–"}</div></div>
              <div className="ds-item"><div className="ds-lbl">Properti</div><div className="ds-val">{lead.property?.name ?? "–"}</div></div>
              <div className="ds-item"><div className="ds-lbl">Harga</div><div className="ds-val">{formatRupiah(lead.property?.price)}</div></div>
              <div className="ds-item"><div className="ds-lbl">Sales PIC</div><div className="ds-val">{lead.sales?.name ?? "–"}</div></div>
              <div className="ds-item"><div className="ds-lbl">Tgl Masuk</div><div className="ds-val">{formatDate(lead.createdAt)}</div></div>
            </div>
          </div>
        </div>
        <div className="modal-foot">
          <button className="btn-cancel" onClick={onClose}>Tutup</button>
        </div>
      </div>
    </div>
  );
}

// ── MAIN PAGE ─────────────────────────────────────────────────────────────────
export default function DataLead() {
  const navigate = useNavigate();
  const location = useLocation();
  const [leads,       setLeads]       = useState([]);
  const [loading,     setLoading]     = useState(true);
  const [searchQ,     setSearchQ]     = useState("");
  const [filterStatus,setFilterStatus]= useState("");
  const [filterSource,setFilterSource]= useState("");
  const [page,        setPage]        = useState(1);
  const [modalOpen,   setModalOpen]   = useState(false);
  const [editData,    setEditData]    = useState(null);
  const [detailLead,  setDetailLead]  = useState(null);
  const [selected,    setSelected]    = useState(new Set());
  const [view,        setView]        = useState("table"); // "table" | "card"

  // ── FETCH ──────────────────────────────────────────────────────────────────
  useEffect(() => { fetchLeads(); }, []);

  async function fetchLeads() {
    try {
      setLoading(true);

      const userId = localStorage.getItem("userId");
      const token = localStorage.getItem("token");

      const url = userId
        ? `${API}/api/leads?userId=${userId}`
        : `${API}/api/leads/raw`;

      const res = await fetch(url, {
        headers: token ? { Authorization: `Bearer ${token}` } : {},
      });

      if (!res.ok) throw new Error(`HTTP ${res.status}`);

      const data = await res.json();

      console.log("LEADS DATA =", data);

      setLeads(Array.isArray(data) ? data.sort((a, b) => b.id - a.id) : []);
    } catch (err) {
      console.error("ERROR fetchLeads:", err);
    } finally {
      setLoading(false);
    }
  }

  async function createLead(form) {
    try {
      const token = localStorage.getItem("token");
      const res = await fetch(`${API}/api/leads`, {
        method: "POST",
        headers: {
          ...(token ? { Authorization: `Bearer ${token}` } : {}),
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          name:       form.name,
          phone:      form.phone,
          email:      form.email,
          propertyId: Number(form.propertyId) || null,
          salesId:    Number(form.salesId)    || null,
          statusId:   Number(form.statusId)   || 1,
          source:     form.source,
        }),
      });
      if (!res.ok) throw new Error(`HTTP ${res.status}`);
      await fetchLeads();
      setModalOpen(false);
    } catch (err) {
      console.error("ERROR createLead:", err);
      alert("Gagal menambah lead.");
    }
  }

  async function updateLeadStatus(id, statusId) {
    try {
      const token = localStorage.getItem("token");
      await fetch(`${API}/api/leads/${id}/status`, {
        method: "PUT",
        headers: {
          ...(token ? { Authorization: `Bearer ${token}` } : {}),
          "Content-Type": "application/json",
        },
        body: JSON.stringify({ statusId }),
      });
      await fetchLeads();
    } catch (err) {
      console.error("ERROR updateStatus:", err);
    }
  }

  // ── FILTER & PAGINATION ────────────────────────────────────────────────────
  const filtered = useMemo(() => {
    return leads.filter(l => {
      const q   = searchQ.toLowerCase();
      const matchQ = !q || l.name?.toLowerCase().includes(q)
                        || l.phone?.includes(q)
                        || l.property?.name?.toLowerCase().includes(q);
      const matchStatus = !filterStatus || String(l.status?.id) === filterStatus;
      const matchSource = !filterSource || l.source === filterSource;
      return matchQ && matchStatus && matchSource;
    });
  }, [leads, searchQ, filterStatus, filterSource]);

  const totalPages = Math.max(1, Math.ceil(filtered.length / ROWS_PER_PAGE));
  const paginated  = filtered.slice((page-1)*ROWS_PER_PAGE, page*ROWS_PER_PAGE);

  // ── STATS ──────────────────────────────────────────────────────────────────
  const stats = useMemo(() => {
    const total   = leads.length;
    const newLead = leads.filter(l => l.status?.id === 1).length;
    const followUp= leads.filter(l => l.status?.id === 3).length;
    const closed  = leads.filter(l => l.status?.id === 5).length;
    const rate    = total > 0 ? Math.round((closed/total)*100) : 0;
    return { total, newLead, followUp, closed, rate };
  }, [leads]);

  // ── SELECTION ──────────────────────────────────────────────────────────────
  function toggleSelect(id) {
    setSelected(prev => {
      const next = new Set(prev);
      next.has(id) ? next.delete(id) : next.add(id);
      return next;
    });
  }
  function toggleAll(e) {
    setSelected(e.target.checked ? new Set(paginated.map(l => l.id)) : new Set());
  }

  // ── EXPORT CSV ────────────────────────────────────────────────────────────
  function exportCSV() {
    const header = "ID,Nama,Phone,Email,Properti,Status,Sumber,Sales,Tgl Masuk";
    const rows = filtered.map(l =>
      [l.id, l.name, l.phone, l.email ?? "",
       l.property?.name ?? "", l.status?.statusName ?? "",
       l.source ?? "", l.sales?.name ?? "",
       formatDate(l.createdAt)].join(",")
    );
    const blob = new Blob([[header,...rows].join("\n")], { type:"text/csv" });
    const a = document.createElement("a");
    a.href = URL.createObjectURL(blob);
    a.download = "data-lead.csv";
    a.click();
  }

  // ── RENDER ────────────────────────────────────────────────────────────────
  return (
    <div className="app-shell">
      <Sidebar />

      <main className="main">
        {/* TOPBAR */}
        <div className="topbar">
          <div className="topbar-title">Data Lead</div>
          <div className="topbar-right">
            <div className="date-chip">
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

        <div className="content">
          {/* TOOLBAR */}
          <div className="toolbar">
            <div className="search-wrap">
              <svg
                className="search-icon"
                width="14"
                height="14"
                viewBox="0 0 24 24"
                fill="currentColor"
              >
                <path d="M15.5 14h-.79l-.28-.27A6.47 6.47 0 0 0 16 9.5 6.5 6.5 0 1 0 9.5 16c1.61 0 3.09-.59 4.23-1.57l.27.28v.79l5 4.99L20.49 19l-4.99-5zm-6 0C7.01 14 5 11.99 5 9.5S7.01 5 9.5 5 14 7.01 14 9.5 11.99 14 9.5 14z" />
              </svg>
              <input
                className="search-input"
                placeholder="Cari nama, nomor, atau properti..."
                value={searchQ}
                onChange={(e) => {
                  setSearchQ(e.target.value);
                  setPage(1);
                }}
              />
            </div>
            <select
              className="filter-select"
              value={filterStatus}
              onChange={(e) => {
                setFilterStatus(e.target.value);
                setPage(1);
              }}
            >
              {STATUS_OPTIONS.map((o) => (
                <option key={o.val} value={o.val}>
                  {o.lbl}
                </option>
              ))}
            </select>
            <select
              className="filter-select"
              value={filterSource}
              onChange={(e) => {
                setFilterSource(e.target.value);
                setPage(1);
              }}
            >
              {SOURCE_OPTIONS.map((s) => (
                <option key={s} value={s}>
                  {s || "Semua Sumber"}
                </option>
              ))}
            </select>
            <button className="export-btn" onClick={exportCSV}>
              <svg
                width="13"
                height="13"
                viewBox="0 0 24 24"
                fill="currentColor"
              >
                <path d="M14 2H6c-1.1 0-2 .9-2 2v16c0 1.1.9 2 2 2h12c1.1 0 2-.9 2-2V8l-6-6zm4 18H6V4h7v5h5v11z" />
              </svg>
              Export
            </button>
            <button
              className="add-btn"
              onClick={() => {
                setEditData(null);
                setModalOpen(true);
              }}
            >
              <svg
                width="14"
                height="14"
                viewBox="0 0 24 24"
                fill="currentColor"
              >
                <path d="M19 13h-6v6h-2v-6H5v-2h6V5h2v6h6v2z" />
              </svg>
              Tambah Lead
            </button>
          </div>

          {/* STAT STRIP */}
          <div className="stat-strip">
            {[
              {
                icon: "📋",
                bg: "#fef3cd",
                val: stats.total,
                lbl: "Total Lead",
              },
              {
                icon: "🆕",
                bg: "#dbeafe",
                val: stats.newLead,
                lbl: "New Lead",
              },
              {
                icon: "🔄",
                bg: "#fef3cd",
                val: stats.followUp,
                lbl: "Follow Up",
              },
              { icon: "✅", bg: "#dcfce7", val: stats.closed, lbl: "Closing" },
              {
                icon: "📈",
                bg: "#dbeafe",
                val: `${stats.rate}%`,
                lbl: "Closing Rate",
              },
            ].map((s, i) => (
              <div key={i} className="sstrip">
                <div className="sstrip-icon" style={{ background: s.bg }}>
                  {s.icon}
                </div>
                <div>
                  <div className="sstrip-val">{s.val}</div>
                  <div className="sstrip-lbl">{s.lbl}</div>
                </div>
              </div>
            ))}
          </div>

          {/* TABLE CARD */}
          <div className="table-card">
            {/* BULK BAR */}
            {selected.size > 0 && (
              <div className="bulk-bar show">
                <span>{selected.size} lead dipilih</span>
                <button
                  className="bulk-btn gold"
                  onClick={() => alert("Assign sales...")}
                >
                  Assign Sales
                </button>
                <button
                  className="bulk-btn danger"
                  onClick={() => {
                    setSelected(new Set());
                  }}
                >
                  Batal Pilih
                </button>
                <span
                  className="bulk-close"
                  onClick={() => setSelected(new Set())}
                >
                  ✕ Batal
                </span>
              </div>
            )}

            <div className="table-header">
              <div className="th-title">
                Daftar Semua Lead{" "}
                <span
                  style={{
                    fontSize: 12,
                    color: "var(--text-muted)",
                    fontWeight: 400,
                  }}
                >
                  ({filtered.length} lead)
                </span>
              </div>
              <div className="th-right">
                <div className="view-toggle">
                  <button
                    className={`vt-btn${view === "table" ? " active" : ""}`}
                    onClick={() => setView("table")}
                  >
                    ☰ Tabel
                  </button>
                  <button
                    className={`vt-btn${view === "card" ? " active" : ""}`}
                    onClick={() => setView("card")}
                  >
                    ⊞ Kartu
                  </button>
                </div>
              </div>
            </div>

            {loading ? (
              <div
                style={{
                  padding: 40,
                  textAlign: "center",
                  color: "var(--text-muted)",
                }}
              >
                Memuat data...
              </div>
            ) : view === "table" ? (
              <>
                <div className="table-wrap">
                  <table>
                    <thead>
                      <tr>
                        <th className="td-check">
                          <input
                            type="checkbox"
                            onChange={toggleAll}
                            checked={
                              selected.size === paginated.length &&
                              paginated.length > 0
                            }
                          />
                        </th>
                        <th>ID</th>
                        <th>Nama Lead</th>
                        <th>Properti</th>
                        <th>Status</th>
                        <th>Sumber</th>
                        <th>Sales PIC</th>
                        <th>Harga Properti</th>
                        <th>Tgl Masuk</th>
                        <th>Aksi</th>
                      </tr>
                    </thead>
                    <tbody>
                      {paginated.length === 0 ? (
                        <tr>
                          <td
                            colSpan={9}
                            style={{
                              textAlign: "center",
                              padding: 32,
                              color: "var(--text-muted)",
                            }}
                          >
                            Tidak ada lead ditemukan
                          </td>
                        </tr>
                      ) : (
                        paginated.map((l, idx) => {
                          const statusName = l.status?.statusName ?? "–";
                          const tag = STATUS_TAG[statusName] ?? {
                            cls: "tag-cold",
                            lbl: statusName,
                          };
                          const color = COLORS[l.id % COLORS.length];
                          return (
                            <tr key={l.id}>
                              <td className="td-check">
                                <input
                                  type="checkbox"
                                  checked={selected.has(l.id)}
                                  onChange={() => toggleSelect(l.id)}
                                />
                              </td>

                              <td>#{l.id}</td>  
                              
                              <td>
                                <div className="lead-cell">
                                  <div
                                    className="lc-av"
                                    style={{ background: color }}
                                  >
                                    {initials(l.name)}
                                  </div>
                                  <div>
                                    <div className="lc-nm">{l.name}</div>
                                    <div className="lc-ph">{l.phone}</div>
                                  </div>
                                </div>
                              </td>
                              <td>{l.property?.name ?? "–"}</td>
                              <td>
                                <span className={`tag ${tag.cls}`}>
                                  {tag.lbl}
                                </span>
                              </td>
                              <td>{l.source ?? "–"}</td>
                              <td>{l.sales?.name ?? "–"}</td>
                              <td>{formatRupiah(l.property?.price)}</td>
                              <td>{formatDate(l.createdAt)}</td>
                              <td>
                                <div className="action-cell">
                                  <button
                                    className="ic-btn view"
                                    title="Detail"
                                    onClick={() => setDetailLead(l)}
                                  >
                                    <svg
                                      width="13"
                                      height="13"
                                      viewBox="0 0 24 24"
                                      fill="currentColor"
                                    >
                                      <path d="M12 4.5C7 4.5 2.73 7.61 1 12c1.73 4.39 6 7.5 11 7.5s9.27-3.11 11-7.5c-1.73-4.39-6-7.5-11-7.5zM12 17c-2.76 0-5-2.24-5-5s2.24-5 5-5 5 2.24 5 5-2.24 5-5 5zm0-8c-1.66 0-3 1.34-3 3s1.34 3 3 3 3-1.34 3-3-1.34-3-3-3z" />
                                    </svg>
                                  </button>
                                  <button
                                  className="ic-btn followup"
                                  title="Jadwalkan Follow Up"
                                  onClick={() => {
                                    navigate("/reminder", {
                                      state: {
                                        leadId: l.id,
                                        leadName: l.name,
                                        phone: l.phone,
                                      },
                                    });
                                  }}
                                >
                                  <svg
                                    width="13"
                                    height="13"
                                    viewBox="0 0 24 24"
                                    fill="currentColor"
                                  >
                                    <path d="M12 2a10 10 0 100 20 10 10 0 000-20zm1 11h5v-2h-4V6h-2v7z"/>
                                  </svg>
                                </button>
                                  <button
                                    className="ic-btn edit"
                                    title="Edit Status"
                                    onClick={() => {
                                      const newStatusId = prompt(
                                        "Masukkan ID status baru (1-6):",
                                        l.status?.id,
                                      );
                                      if (newStatusId)
                                        updateLeadStatus(
                                          l.id,
                                          parseInt(newStatusId),
                                        );
                                    }}
                                  >
                                    <svg
                                      width="13"
                                      height="13"
                                      viewBox="0 0 24 24"
                                      fill="currentColor"
                                    >
                                      <path d="M3 17.25V21h3.75L17.81 9.94l-3.75-3.75L3 17.25zM20.71 7.04c.39-.39.39-1.02 0-1.41l-2.34-2.34c-.39-.39-1.02-.39-1.41 0l-1.83 1.83 3.75 3.75 1.83-1.83z" />
                                    </svg>
                                  </button>
                                </div>
                              </td>
                            </tr>
                          );
                        })
                      )}
                    </tbody>
                  </table>
                </div>

                {/* PAGINATION */}
                <div className="pagination">
                  <div className="pg-info">
                    Menampilkan{" "}
                    {Math.min((page - 1) * ROWS_PER_PAGE + 1, filtered.length)}–
                    {Math.min(page * ROWS_PER_PAGE, filtered.length)} dari{" "}
                    {filtered.length} lead
                  </div>
                  <div className="pg-btns">
                    <button
                      className="pg-btn"
                      disabled={page === 1}
                      onClick={() => setPage((p) => p - 1)}
                    >
                      ‹
                    </button>
                    {Array.from({ length: totalPages }, (_, i) => i + 1)
                      .filter(
                        (p) =>
                          p === 1 ||
                          p === totalPages ||
                          Math.abs(p - page) <= 1,
                      )
                      .map((p, i, arr) => (
                        <>
                          {i > 0 && arr[i - 1] !== p - 1 && (
                            <span
                              key={`e${p}`}
                              style={{
                                padding: "0 4px",
                                color: "var(--text-muted)",
                              }}
                            >
                              …
                            </span>
                          )}
                          <button
                            key={p}
                            className={`pg-btn${page === p ? " active" : ""}`}
                            onClick={() => setPage(p)}
                          >
                            {p}
                          </button>
                        </>
                      ))}
                    <button
                      className="pg-btn"
                      disabled={page === totalPages}
                      onClick={() => setPage((p) => p + 1)}
                    >
                      ›
                    </button>
                  </div>
                </div>
              </>
            ) : (
              /* CARD VIEW */
              <div
                style={{
                  padding: 16,
                  display: "grid",
                  gridTemplateColumns: "repeat(auto-fill,minmax(240px,1fr))",
                  gap: 12,
                }}
              >
                {paginated.map((l) => {
                  const statusName = l.status?.statusName ?? "–";
                  const tag = STATUS_TAG[statusName] ?? {
                    cls: "tag-cold",
                    lbl: statusName,
                  };
                  const color = COLORS[l.id % COLORS.length];
                  return (
                    <div
                      key={l.id}
                      className="lead-kard"
                      onClick={() => setDetailLead(l)}
                    >
                      <div
                        style={{
                          display: "flex",
                          gap: 10,
                          alignItems: "center",
                          marginBottom: 10,
                        }}
                      >
                        <div
                          className="lc-av"
                          style={{
                            background: color,
                            width: 36,
                            height: 36,
                            fontSize: 13,
                          }}
                        >
                          {initials(l.name)}
                        </div>
                        <div>
                          <div style={{ fontWeight: 700, fontSize: 13 }}>
                            {l.name}
                          </div>
                          <div
                            style={{ fontSize: 11, color: "var(--text-muted)" }}
                          >
                            {l.phone}
                          </div>
                        </div>
                        <span
                          className={`tag ${tag.cls}`}
                          style={{ marginLeft: "auto" }}
                        >
                          {tag.lbl}
                        </span>
                      </div>
                      <div
                        style={{ fontSize: 12, color: "var(--text-secondary)" }}
                      >
                        🏠 {l.property?.name ?? "–"}
                      </div>
                      <div
                        style={{
                          fontSize: 12,
                          color: "var(--text-secondary)",
                          marginTop: 4,
                        }}
                      >
                        📣 {l.source ?? "–"}
                      </div>
                      <div
                        style={{
                          fontSize: 12,
                          color: "var(--text-secondary)",
                          marginTop: 4,
                        }}
                      >
                        👤 {l.sales?.name ?? "–"}
                      </div>
                    </div>
                  );
                })}
              </div>
            )}
          </div>
        </div>
      </main>

      {/* MODALS */}
      <LeadModal
        isOpen={modalOpen}
        editData={editData}
        onClose={() => setModalOpen(false)}
        onSave={createLead}
      />
      <DetailModal lead={detailLead} onClose={() => setDetailLead(null)} />
    </div>
  );
}