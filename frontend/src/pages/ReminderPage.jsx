import { useState, useEffect, useRef } from "react";
import "../styles/ReminderPage.css";
import { useNavigate, useLocation } from "react-router-dom";
import logo from "../assets/leadestate-logo.png";

const API = import.meta.env.VITE_API_URL;

// ── CONSTANTS ─────────────────────────────────────────────────────────────────
const TEMPLATES = {
  "📅 Reminder Jadwal": (l) =>
    `Halo ${l.name.split(" ")[0]}, saya ${l.sales} dari LeadEstate. Ingin mengingatkan bahwa hari ini kita ada jadwal diskusi mengenai ${l.prop}. Apakah Bapak/Ibu masih bisa? 🙏`,
  "🏠 Info Properti": (l) =>
    `Halo ${l.name.split(" ")[0]}, ada informasi terbaru mengenai ${l.prop} yang mungkin menarik bagi Bapak/Ibu. Boleh saya share detailnya? 😊`,
  "💰 Penawaran Harga": (l) =>
    `Halo ${l.name.split(" ")[0]}, kami memiliki penawaran spesial untuk ${l.prop} dengan budget sekitar ${l.budget}. Tertarik untuk kita diskusikan? 🎉`,
};

const FILTER_TABS = [
  { key: "semua",    label: "Semua"    },
  { key: "hari-ini", label: "Hari Ini" },
  { key: "tertunda", label: "Tertunda" },
  { key: "selesai",  label: "Selesai"  },
];

// ── HELPERS ───────────────────────────────────────────────────────────────────
function getTagClass(tag) {
  if (tag === "today") return "h-today";
  if (tag === "soon")  return "h-soon";
  if (tag === "late")  return "h-late";
  return "h-done";
}

function getStatusPill(s) {
  if (s === "belum")  return "sp-belum";
  if (s === "proses") return "sp-proses";
  return "sp-selesai";
}

function getStatusLabel(s) {
  if (s === "belum")  return "Belum Direspons";
  if (s === "proses") return "Sedang Diproses";
  return "Selesai";
}

// ── SIDEBAR ───────────────────────────────────────────────────────────────────
function Sidebar({ todayFollowups }) {
  const navigate = useNavigate();

  const currentUser = (() => {
    try {
      return JSON.parse(localStorage.getItem("user") || "{}");
    } catch {
      return {};
    }
  })();

  const isAdmin = currentUser?.role?.toLowerCase() === "admin";

  const userInitials = (currentUser.name || "U")
    .split(" ")
    .slice(0, 2)
    .map((w) => w[0])
    .join("")
    .toUpperCase();

  const userRole =
    currentUser.role === "Admin"       ? "Administrator" :
    currentUser.role === "Supervisor"  ? "Supervisor"    :
    currentUser.role === "Sales"       ? "Sales"         :
    currentUser.role || "Administrator";

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

        <div
          className="le-nav-item"
          onClick={() => navigate("/dashboard")}
        >
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

        <div className="le-nav-item active">
          <span className="le-nav-icon">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.8">
              <circle cx="12" cy="12" r="10" />
              <polygon points="10,8 16,12 10,16" fill="currentColor" />
            </svg>
          </span>
          Reminder &amp; Follow-Up
          <span className="le-nav-badge">{todayFollowups}</span>
        </div>

        <div
          className="le-nav-item"
          onClick={() => navigate("/dataLeads")}
        >
          <span className="le-nav-icon">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.8">
              <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2" />
              <circle cx="9" cy="7" r="4" />
            </svg>
          </span>
          Data Lead
        </div>

        {isAdmin && (
          <>
            <div
              className="le-nav-item"
              onClick={() => navigate("/Manajemen_sales")}
            >
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

            <div
              className="le-nav-item"
              onClick={() => navigate("/laporan")}
            >
              <span className="le-nav-icon">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.8">
                  <line x1="18" y1="20" x2="18" y2="10" />
                  <line x1="12" y1="20" x2="12" y2="4" />
                  <line x1="6"  y1="20" x2="6"  y2="14" />
                </svg>
              </span>
              Laporan &amp; Statistik
            </div>
          </>
        )}

        <div
          className="le-nav-item"
          onClick={() => navigate("/settings")}
        >
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

// ── LEAD CARD ─────────────────────────────────────────────────────────────────
function LeadCard({ lead, selected, onClick }) {
  return (
    <div
      className={`lead-card${selected ? " selected" : ""}`}
      onClick={onClick}
    >
      <div className="lc-avatar" style={{ background: lead.color }}>
        {lead.initials}
      </div>
      <div className="lc-info">
        <div className="lc-name">{lead.name}</div>
        <div className="lc-phone">{lead.phone}</div>
        <div className="lc-prop">🏠 {lead.prop}</div>
      </div>
      <div className="lc-right">
        <div className="lc-time">{lead.time}</div>
        <div className={`lc-h ${getTagClass(lead.tag)}`}>{lead.hday}</div>
      </div>
    </div>
  );
}

// ── CONTACT CARD ──────────────────────────────────────────────────────────────
function ContactCard({ lead }) {
  return (
    <div className="contact-card">
      <div className="contact-header">
        <div className="contact-avatar" style={{ background: lead.color }}>
          {lead.initials}
        </div>
        <div>
          <div className="contact-name">{lead.name}</div>
          <div className="contact-phone">📞 {lead.phone}</div>
          <div className="contact-actions" style={{ marginTop: 8 }}>
            <button
              className="ca-btn wa"
              onClick={() =>
                window.open(
                  `https://wa.me/${lead.phone.replace(/\D/g, "")}`,
                  "__blank"
                )
              }
            >
              💬 WhatsApp
            </button>
            <button
              className="ca-btn call"
              onClick={() => alert(`Telepon ${lead.phone}`)}
            >
              📞 Telepon
            </button>
          </div>
        </div>
        <div style={{ marginLeft: "auto", textAlign: "right" }}>
          <div className={`status-pill ${getStatusPill(lead.status)}`}>
            {getStatusLabel(lead.status)}
          </div>
          <div style={{ fontSize: 11, color: "var(--text-muted)", marginTop: 6 }}>
            Sales: {lead.sales}
          </div>
        </div>
      </div>

      <div className="info-grid">
        <div className="info-item">
          <div className="info-label">Properti Diminati</div>
          <div className="info-val">🏠 {lead.prop}</div>
        </div>
        <div className="info-item">
          <div className="info-label">Sumber Lead</div>
          <div className="info-val">{lead.source}</div>
        </div>
        <div className="info-item">
          <div className="info-label">Budget</div>
          <div className="info-val">{lead.budget}</div>
        </div>
        <div className="info-item">
          <div className="info-label">Jadwal Reminder</div>
          <div className="info-val">{lead.hday} · Hari ini {lead.time}</div>
        </div>
      </div>
    </div>
  );
}

// ── HISTORY TIMELINE ──────────────────────────────────────────────────────────
function HistoryTimeline({ history = [] }) {
  return (
    <div className="reminder-card">
      <div className="rc-title">🕐 Riwayat Follow Up</div>
      <div className="timeline">
        {history.map((h, i) => (
          <div className="tl-item" key={i}>
            <div className="tl-line">
              <div
                className="tl-dot"
                style={{ background: h.done ? "var(--success)" : "var(--brand-gold)" }}
              />
              {i < history.length - 1 && (
                <div className={`tl-seg ${h.done ? "done" : "current"}`} />
              )}
            </div>
            <div className="tl-content">
              <div className="tl-date">{h.date}</div>
              <div className="tl-note">{h.note}</div>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}

// ── ACTION CARD ───────────────────────────────────────────────────────────────
function ActionCard({ lead, onTandaiSelesai, onCatatAktivitas }) {
  const [selectedTemplate, setSelectedTemplate] = useState(null);
  const [notes, setNotes] = useState("");

  const preview = selectedTemplate
    ? TEMPLATES[selectedTemplate](lead)
    : "Pilih template pesan di atas...";

  function handleSendWA() {
    if (!selectedTemplate) {
      alert("Pilih template terlebih dahulu");
      return;
    }

    const cleanPhone = lead.phone.replace(/\D/g, "");
    const encodedMessage = encodeURIComponent(preview);

    window.open(
      `https://wa.me/${cleanPhone}?text=${encodedMessage}`,
      "__blank"
    );
  }

  function handleCatat() {
    if (!notes.trim()) {
      alert("Tulis catatan terlebih dahulu");
      return;
    }
    onCatatAktivitas(notes.trim());
    setNotes("");
  }

  return (
    <div className="action-card">
      <div className="action-title">⚡ Aksi Follow Up</div>

      <div className="wa-box">
        <div className="wa-label">💬 Kirim via WhatsApp</div>
        <div className="wa-template">
          {Object.keys(TEMPLATES).map((key) => (
            <div
              key={key}
              className={`wa-chip${selectedTemplate === key ? " sel" : ""}`}
              onClick={() => setSelectedTemplate(key)}
            >
              {key}
            </div>
          ))}
        </div>
        <div className="wa-preview">{preview}</div>
        <button className="send-btn" style={{ marginTop: 10 }} onClick={handleSendWA}>
          <svg width="16" height="16" viewBox="0 0 24 24" fill="currentColor">
            <path d="M2.01 21L23 12 2.01 3 2 10l15 2-15 2z" />
          </svg>
          Kirim WhatsApp
        </button>
      </div>

      <div className="divider-or">atau catat aktivitas</div>

      <textarea
        className="notes-area"
        placeholder="Tulis catatan hasil follow up... (misal: Lead sudah dihubungi, respon positif, minta brosur)"
        value={notes}
        onChange={(e) => setNotes(e.target.value)}
      />

      <div className="action-buttons">
        <button className="act-btn primary" onClick={onTandaiSelesai}>
          ✅ Tandai Selesai
        </button>
        <button className="act-btn secondary" onClick={handleCatat}>
          📝 Catat Aktivitas
        </button>
      </div>
    </div>
  );
}

// ── DETAIL PANEL ──────────────────────────────────────────────────────────────
function DetailPanel({ lead, onTandaiSelesai, onCatatAktivitas }) {
  if (!lead) {
    return (
      <div className="detail-wrap">
        <div className="empty-state">
          <svg viewBox="0 0 24 24" fill="currentColor">
            <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-2 14.5v-9l6 4.5-6 4.5z" />
          </svg>
          <p>
            Pilih lead dari daftar
            <br />
            untuk melihat detail follow-up
          </p>
        </div>
      </div>
    );
  }

  return (
    <div className="detail-wrap">
      <ContactCard lead={lead} />
      <HistoryTimeline history={lead.history} />
      <ActionCard
        lead={lead}
        onTandaiSelesai={onTandaiSelesai}
        onCatatAktivitas={onCatatAktivitas}
      />
    </div>
  );
}

// ── MAIN PAGE ─────────────────────────────────────────────────────────────────
export default function ReminderPage() {
  const [leads, setLeads]             = useState([]);
  const [todayFollowups, setTodayFollowups] = useState(0);
  const [selectedId, setSelectedId]   = useState(null);
  const [followupHistory, setFollowupHistory] = useState([]);
  const [activeFilter, setActiveFilter] = useState("semua");
  const [searchQ, setSearchQ]         = useState("");
  const location = useLocation();

  // ── FIX 2B: Menggunakan useRef guard untuk menangani StrictMode double-invoke ──
  const reminderCreatedRef = useRef(false);

  useEffect(() => {
    if (location.state?.leadId && !reminderCreatedRef.current) {
      reminderCreatedRef.current = true;
      createReminderFromState(location.state);
    }
  }, [location.state]); 

  useEffect(() => {
    fetchLeads();
  }, []);

  // ── FIX 2A & 2B: Modifikasi fungsi createReminderFromState ──
  async function createReminderFromState(state) {
    try {
      const token = localStorage.getItem("token");
      const user = JSON.parse(localStorage.getItem("user") || "{}");

      const currentLeads = await fetchLeads();
      const alreadyExists = currentLeads?.some(l => l.leadId === state.leadId);
      
      if (alreadyExists) {
        console.log("Reminder untuk lead ini sudah ada, membatalkan auto-create.");
        return;
      }

      await fetch(`${API}/api/reminders`, {
        method: "POST",
        headers: {
          ...(token ? { Authorization: `Bearer ${token}` } : {}),
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          leadId: state.leadId,
          salesId: user?.id,
          reminderDate: new Date().toISOString(), // FIX 2A: Mengirim ISO string yang valid
          status: "pending",
        }),
      });

      await new Promise(r => setTimeout(r, 300));
      await fetchLeads();

    } catch (err) {
      console.error("Gagal create reminder:", err);
    }
  }

  async function fetchLeads() {
    try {

      const token = localStorage.getItem("token");
      const user = JSON.parse(localStorage.getItem("user") || "{}");
      const res = await fetch(
        `${API}/api/reminders?userId=${user.id}&role=${user.role}`
      );

      console.log("STATUS:", res.status);

      const json = await res.json();

      const rawReminders = json.data || [];
      const uniqueMap = new Map();

      rawReminders
        .sort((a, b) => new Date(b.reminderDate) - new Date(a.reminderDate))
        .forEach(r => {
          if (!uniqueMap.has(r.leadId)) {
            uniqueMap.set(r.leadId, r);
          }
        });

      const reminders = Array.from(uniqueMap.values());

      setTodayFollowups(
        reminders.filter(
          r => r.status?.toLowerCase() !== "done"
        ).length
      );

      const mapped = reminders.map((item, index) => {
        const reminderStatus = item.status?.toLowerCase();
        let tag = "soon";

        if (reminderStatus === "done") {
          tag = "done";
        } else {
          const reminderDate = new Date(item.reminderDate);
          const now = new Date();
          const diffHours = (reminderDate - now) / (1000 * 60 * 60);

          if (diffHours < 0) tag = "late";
          else if (diffHours <= 24) tag = "today";
          else tag = "soon";
        }

        const leadId = item.leadId;
        const leadName = item.leadName;
        const phone = item.phone;
        const propertyName = item.propertyName;
        const salesName = item.salesName;
        const source = item.source;

        let hdayLabel = "Segera";
        if (tag === "today") hdayLabel = "Hari ini";
        if (tag === "late") hdayLabel = "Tertunda";
        if (tag === "done") hdayLabel = "Selesai";

        return {
          id: item.id,
          leadId,
          name: leadName || "Unknown",
          phone: phone || "-",
          prop: propertyName || "Properti",
          sales: salesName || "Sales",
          source: source || "-",
          budget: "Rp -",
          initials: (leadName || "U")
            .split(" ")
            .map((n) => n[0])
            .join("")
            .substring(0, 2)
            .toUpperCase(),
          color: [
            "#f59e0b",
            "#6366f1",
            "#10b981",
            "#ef4444",
            "#8b5cf6",
          ][index % 5],
          tag,
          hday: hdayLabel,
          time: new Date(item.reminderDate).toLocaleTimeString("id-ID", {
            hour: "2-digit",
            minute: "2-digit",
          }),
          status:
            reminderStatus === "done"
              ? "selesai"
              : tag === "late"
              ? "belum"
              : "proses",
          history: [],
        };
      });

      console.log("HASIL MAPPING:", mapped);
      setLeads(mapped);

      if (mapped.length > 0) {
        setSelectedId(mapped[0].id);
      }
      
      return mapped; 
    } catch (err) {
      console.error("ERROR FETCH:", err);
    }
  }

  async function fetchFollowupHistory(leadId) {
    try {
      const res = await fetch(
        `${API}/followups/lead/${leadId}`
      );

      const data = await res.json();

      const mappedHistory = data.map((f) => ({
        date: new Date(f.createdAt).toLocaleString("id-ID"),
        note: f.notes,
        done: f.status?.toLowerCase() === "done",
      }));

      setFollowupHistory(mappedHistory);
    } catch (err) {
      console.error("Gagal ambil history:", err);
      setFollowupHistory([]);
    }
  }

  const filtered = leads.filter((l) => {
    const matchFilter =
      activeFilter === "semua"    ||
      (activeFilter === "hari-ini" && l.tag === "today") ||
      (activeFilter === "tertunda" && l.tag === "late")  ||
      (activeFilter === "selesai"  && l.tag === "done");
    const q = searchQ.toLowerCase();
    const matchSearch =
      l.name.toLowerCase().includes(q) ||
      (l.prop ?? "").toLowerCase().includes(q);
    return matchFilter && matchSearch;
  });

  const selectedLead = leads.find((l) => l.id === selectedId) || null;

  useEffect(() => {
    if (selectedLead?.leadId) {
      fetchFollowupHistory(selectedLead.leadId);
    }
  }, [selectedLead]);

  // ── FIX 2C: Ambil data user dari localStorage dan passing query param userId & role ──
  async function handleTandaiSelesai() {
    try {
      const token = localStorage.getItem("token");
      const user = JSON.parse(localStorage.getItem("user") || "{}");

      await fetch(`${API}/api/reminders/${selectedId}/done?userId=${user.id}&role=${user.role}`, {
        method: "PUT",
        headers: {
          ...(token ? { Authorization: `Bearer ${token}` } : {}),
          "Content-Type": "application/json",
        },
      });
      
      await new Promise(r => setTimeout(r, 300));
      await fetchLeads();
      
      if (selectedLead?.leadId) {
        await fetchFollowupHistory(selectedLead.leadId);
      }
    } catch (err) {
      console.error(err);
    }
  }

  async function handleCatatAktivitas(notes) {
    try {
      const token = localStorage.getItem("token");
      const user = JSON.parse(localStorage.getItem("user") || "{}");
      const res = await fetch(`${API}/followups`, {
        method: "POST",
        headers: {
          ...(token ? { Authorization: `Bearer ${token}` } : {}),
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          leadId: selectedLead?.leadId,
          salesId: user?.id,
          notes,
          followupDate: new Date(),
          status: "done",
        }),
      });

      console.log("STATUS:", res.status);

      const data = await res.text();
      console.log("RESPONSE:", data);

      if (!res.ok) {
        throw new Error(data);
      }

      await new Promise(r => setTimeout(r, 300));
      await fetchLeads();

      if (selectedLead?.leadId) {
        await fetchFollowupHistory(selectedLead.leadId);
      }

      alert("✅ Aktivitas berhasil dicatat!");
    } catch (err) {
      console.error(err);
    }
  }

  return (
    <div className="le-root">
      <Sidebar todayFollowups={todayFollowups} />

      <main className="le-main">
        {/* TOPBAR */}
        <div className="le-topbar">
          <div className="le-topbar-title">Reminder &amp; Follow Up</div>
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

        <div className="body-split">
          {/* LEFT PANEL */}
          <div className="left-panel">
            <div className="filter-tabs">
              {FILTER_TABS.map((tab) => (
                <div
                  key={tab.key}
                  className={`ftab${activeFilter === tab.key ? " active" : ""}`}
                  onClick={() => setActiveFilter(tab.key)}
                >
                  {tab.label}
                </div>
              ))}
            </div>

            <div className="search-bar">
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
                  placeholder="Cari nama atau properti..."
                  value={searchQ}
                  onChange={(e) => setSearchQ(e.target.value)}
                />
              </div>
            </div>

            <div className="list-header">
              <div className="list-title">Daftar Reminder</div>
              <div className="list-count">{filtered.length} lead</div>
            </div>

            <div className="lead-list">
              {filtered.length === 0 ? (
                <div className="empty-state">
                  <svg viewBox="0 0 24 24" fill="currentColor">
                    <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm1 15h-2v-2h2v2zm0-4h-2V7h2v6z" />
                  </svg>
                  <p>Tidak ada lead ditemukan</p>
                </div>
              ) : (
                filtered.map((lead) => (
                  <LeadCard
                    key={lead.id}
                    lead={lead}
                    selected={selectedId === lead.id}
                    onClick={() => setSelectedId(lead.id)}
                  />
                ))
              )}
            </div>
          </div>

          {/* RIGHT PANEL */}
          <div className="right-panel">
            <DetailPanel
              lead={selectedLead}
              onTandaiSelesai={handleTandaiSelesai}
              onCatatAktivitas={handleCatatAktivitas}
            />
          </div>
        </div>
      </main>
    </div>
  );
}