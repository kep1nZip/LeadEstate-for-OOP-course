import { useState, useRef } from "react";
import "../styles/register.css";
import { useNavigate } from "react-router-dom";
import logo from "../assets/leadestate-logo.png"; 
 
// ─── CONSTANTS ────────────────────────────────────────────────────────────────
 
const STEPS = [
  { num: 1, title: "Informasi Akun",  desc: "Nama, email & password"  },
  { num: 2, title: "Detail Profil",   desc: "Role & nomor HP"          },
  { num: 3, title: "Konfirmasi",      desc: "Verifikasi & selesai"     },
];
 
const STEP_TITLES = ["", "Informasi Akun", "Detail Profil", "Konfirmasi"];
const STEP_DESCS  = [
  "",
  "Mulai dengan mengisi detail dasar akun Anda",
  "Lengkapi profil dan pilih role Anda",
  "Tinjau data dan buat akun",
];
 
// ─── ICONS ────────────────────────────────────────────────────────────────────
 
const IconUser = () => (
  <svg width="15" height="15" viewBox="0 0 24 24" fill="currentColor">
    <path d="M12 12c2.76 0 5-2.24 5-5s-2.24-5-5-5-5 2.24-5 5 2.24 5 5 5zm0 2c-3.33 0-10 1.67-10 5v2h20v-2c0-3.33-6.67-5-10-5z" />
  </svg>
);
 
const IconMail = () => (
  <svg width="15" height="15" viewBox="0 0 24 24" fill="currentColor">
    <path d="M20 4H4c-1.1 0-2 .9-2 2v12c0 1.1.9 2 2 2h16c1.1 0 2-.9 2-2V6c0-1.1-.9-2-2-2zm0 4l-8 5-8-5V6l8 5 8-5v2z" />
  </svg>
);
 
const IconLock = () => (
  <svg width="15" height="15" viewBox="0 0 24 24" fill="currentColor">
    <path d="M18 8h-1V6c0-2.76-2.24-5-5-5S7 3.24 7 6v2H6c-1.1 0-2 .9-2 2v10c0 1.1.9 2 2 2h12c1.1 0 2-.9 2-2V10c0-1.1-.9-2-2-2zm-6 9c-1.1 0-2-.9-2-2s.9-2 2-2 2 .9 2 2-.9 2-2 2zm3.1-9H8.9V6c0-1.71 1.39-3.1 3.1-3.1s3.1 1.39 3.1 3.1v2z" />
  </svg>
);
 
const IconPhone = () => (
  <svg width="15" height="15" viewBox="0 0 24 24" fill="currentColor">
    <path d="M6.62 10.79c1.44 2.83 3.76 5.14 6.59 6.59l2.2-2.2c.27-.27.67-.36 1.02-.24 1.12.37 2.33.57 3.57.57.55 0 1 .45 1 1V20c0 .55-.45 1-1 1-9.39 0-17-7.61-17-17 0-.55.45-1 1-1h3.5c.55 0 1 .45 1 1 0 1.25.2 2.45.57 3.57.11.35.03.74-.25 1.02l-2.2 2.2z" />
  </svg>
);
 
const IconBuilding = () => (
  <svg width="15" height="15" viewBox="0 0 24 24" fill="currentColor">
    <path d="M12 7V3H2v18h20V7H12zM6 19H4v-2h2v2zm0-4H4v-2h2v2zm0-4H4V9h2v2zm0-4H4V5h2v2zm4 12H8v-2h2v2zm0-4H8v-2h2v2zm0-4H8V9h2v2zm0-4H8V5h2v2zm10 12h-8v-2h2v-2h-2v-2h2v-2h-2V9h8v10zm-2-8h-2v2h2v-2zm0 4h-2v2h2v-2z" />
  </svg>
);
 
const IconChat = () => (
  <svg width="15" height="15" viewBox="0 0 24 24" fill="currentColor">
    <path d="M20 2H4c-1.1 0-2 .9-2 2v18l4-4h14c1.1 0 2-.9 2-2V4c0-1.1-.9-2-2-2z" />
  </svg>
);
 
const IconEyeOpen = () => (
  <svg width="15" height="15" viewBox="0 0 24 24" fill="currentColor">
    <path d="M12 4.5C7 4.5 2.73 7.61 1 12c1.73 4.39 6 7.5 11 7.5s9.27-3.11 11-7.5c-1.73-4.39-6-7.5-11-7.5zM12 17c-2.76 0-5-2.24-5-5s2.24-5 5-5 5 2.24 5 5-2.24 5-5 5zm0-8c-1.66 0-3 1.34-3 3s1.34 3 3 3 3-1.34 3-3-1.34-3-3-3z" />
  </svg>
);
 
const IconEyeOff = () => (
  <svg width="15" height="15" viewBox="0 0 24 24" fill="currentColor">
    <path d="M12 7c2.76 0 5 2.24 5 5 0 .65-.13 1.26-.36 1.83l2.92 2.92c1.51-1.26 2.7-2.89 3.43-4.75-1.73-4.39-6-7.5-11-7.5-1.4 0-2.74.25-3.98.7l2.16 2.16C10.74 7.13 11.35 7 12 7zM2 4.27l2.28 2.28.46.46C3.08 8.3 1.78 10.02 1 12c1.73 4.39 6 7.5 11 7.5 1.55 0 3.03-.3 4.38-.84l.42.42L19.73 22 21 20.73 3.27 3 2 4.27zM7.53 9.8l1.55 1.55c-.05.21-.08.43-.08.65 0 1.66 1.34 3 3 3 .22 0 .44-.03.65-.08l1.55 1.55c-.67.33-1.41.53-2.2.53-2.76 0-5-2.24-5-5 0-.79.2-1.53.53-2.2zm4.31-.78l3.15 3.15.02-.16c0-1.66-1.34-3-3-3l-.17.01z" />
  </svg>
);
 
const IconCheck = ({ size = 11 }) => (
  <svg width={size} height={size} viewBox="0 0 24 24" fill="currentColor">
    <path d="M9 16.17L4.83 12l-1.42 1.41L9 19 21 7l-1.41-1.41z" />
  </svg>
);
 
const IconGoogle = () => (
  <svg width="17" height="17" viewBox="0 0 24 24">
    <path fill="#4285F4" d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92c-.26 1.37-1.04 2.53-2.21 3.31v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.09z" />
    <path fill="#34A853" d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z" />
    <path fill="#FBBC05" d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.07H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.93l2.85-2.22.81-.62z" />
    <path fill="#EA4335" d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.07l3.66 2.84c.87-2.6 3.3-4.53 6.16-4.53z" />
  </svg>
);
 
// ─── SMALL COMPONENTS ────────────────────────────────────────────────────────
 
function InputField({ label, icon, type = "text", id, placeholder, hint, onChange, rightSlot, inputRef }) {
  return (
    <div className="rg-form-group">
      {label && <label className="rg-form-label" htmlFor={id}>{label}</label>}
      <div className="rg-input-wrap">
        {icon && <span className="rg-input-icon">{icon}</span>}
        <input
          ref={inputRef}
          className={`rg-form-input${icon ? "" : " no-icon"}`}
          type={type}
          id={id}
          placeholder={placeholder}
          onChange={onChange}
        />
        {rightSlot}
      </div>
      {hint && <div className="rg-field-hint">{hint}</div>}
    </div>
  );
}
 
function EyeBtn({ visible, onToggle }) {
  return (
    <button type="button" className="rg-eye-btn" onClick={onToggle}>
      {visible ? <IconEyeOff /> : <IconEyeOpen />}
    </button>
  );
}
 
function SubmitBtn({ onClick, loading, children, fullWidth }) {
  return (
    <button
      type="button"
      className={`rg-submit-btn${loading ? " loading" : ""}${fullWidth ? " full" : ""}`}
      onClick={onClick}
    >
      <div className="rg-btn-inner">
        <div className="rg-spinner" />
        <span className="rg-btn-text">{children}</span>
      </div>
    </button>
  );
}
 
// ─── LEFT PANEL ───────────────────────────────────────────────────────────────
 
function LeftPanel({ currentStep, isDone }) {
  return (
    <div className="rg-left">
      {/* Brand */}
      <div className="rg-left-brand">
        {/* Replace src with your logo import */}
        <img src={logo} alt="LeadEstate" className="rg-brand-logo" />
        <div className="rg-brand-text">Lead<span>Estate</span></div>
      </div>
 
      <div className="rg-left-center">
        {/* Headline */}
        <div className="rg-left-headline">
          <h1>Mulai<br />Perjalanan<br /><em>Bersama</em></h1>
          <p>Bergabung dengan tim sales properti terbaik. Setup akun hanya butuh 2 menit.</p>
        </div>
 
        {/* Step indicators */}
        <div className="rg-steps">
          {STEPS.map((s, i) => {
            const isActive = currentStep === s.num;
            const isDoneSt = isDone || currentStep > s.num;
            return (
              <div key={s.num} className="rg-step-item">
                {i > 0 && <div className="rg-step-connector" />}
                <div className={`rg-step-num${isActive ? " active" : ""}${isDoneSt ? " done" : ""}`}>
                  {isDoneSt ? <IconCheck /> : s.num}
                </div>
                <div className="rg-step-info">
                  <div className="rg-step-title">{s.title}</div>
                  <div className="rg-step-desc">{s.desc}</div>
                </div>
              </div>
            );
          })}
        </div>
 
        {/* Role showcase */}
        <div className="rg-role-showcase">
          <div className="rg-role-row">
            <div className="rg-role-icon rg-role-icon--gold">👑</div>
            <div>
              <div className="rg-role-name">Admin</div>
              <div className="rg-role-desc">Akses penuh ke semua data &amp; laporan</div>
            </div>
            <div className="rg-role-badge rg-role-badge--gold">Full access</div>
          </div>
          <div className="rg-role-row">
            <div className="rg-role-icon rg-role-icon--blue">📊</div>
            <div>
              <div className="rg-role-name">Sales</div>
              <div className="rg-role-desc">Kelola lead &amp; follow up harian</div>
            </div>
            <div className="rg-role-badge rg-role-badge--blue">Lead access</div>
          </div>
        </div>
      </div>
 
      <div className="rg-left-footer">
        &copy; 2026 LeadEstate &nbsp;·&nbsp;
        <a href="#">Kebijakan Privasi</a> &nbsp;·&nbsp;
        <a href="#">Syarat Layanan</a>
      </div>
    </div>
  );
}
 
// ─── STEP 1: INFORMASI AKUN ───────────────────────────────────────────────────
 
function Step1({ onNext, navigate }) {
  const [pass, setPass]           = useState("");
  const [showPass, setShowPass]   = useState(false);
  const [showConf, setShowConf]   = useState(false);
  const [confHint, setConfHint]   = useState({ text: "", ok: false });
  const [strength, setStrength]   = useState({ score: 0, label: "", color: "" });
  const [error, setError]         = useState("");
 
  const firstRef  = useRef();
  const lastRef   = useRef();
  const emailRef  = useRef();
  const passRef   = useRef();
  const confRef   = useRef();
 
  const STRENGTH_COLORS = ["", "#ef4444", "#f59e0b", "#f59e0b", "#22c55e"];
  const STRENGTH_LABELS = ["", "Lemah", "Cukup", "Kuat", "Sangat Kuat"];
 
  const handlePassChange = (e) => {
    const v = e.target.value;
    setPass(v);
    let score = 0;
    if (v.length >= 8) score++;
    if (/[A-Z]/.test(v)) score++;
    if (/[0-9]/.test(v)) score++;
    if (/[^A-Za-z0-9]/.test(v)) score++;
    setStrength({ score, label: STRENGTH_LABELS[score] || "", color: STRENGTH_COLORS[score] || "" });
    if (confRef.current?.value) checkConfirmAgainst(confRef.current.value, v);
  };
 
  const checkConfirmAgainst = (val, pw) => {
    if (!val) { setConfHint({ text: "", ok: false }); return; }
    setConfHint(val === pw
      ? { text: "✓ Password cocok",      ok: true  }
      : { text: "✗ Password tidak cocok", ok: false }
    );
  };
 
  const handleConfChange = (e) => checkConfirmAgainst(e.target.value, pass);
 
  const handleNext = () => {
    const fn = firstRef.current?.value.trim();
    const ln = lastRef.current?.value.trim();
    const em = emailRef.current?.value.trim();
    const pw = passRef.current?.value;
    const cp = confRef.current?.value;
 
    if (!fn || !ln)                                   return setError("Nama depan dan belakang wajib diisi.");
    if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(em))      return setError("Format email tidak valid.");
    if (pw.length < 8)                                return setError("Password minimal 8 karakter.");
    if (pw !== cp)                                    return setError("Konfirmasi password tidak cocok.");
 
    setError("");
    onNext({ firstName: fn, lastName: ln, email: em, password: pw });
  };
 
  return (
    <div className="rg-step-panel">
      {error && (
        <div className="rg-error-msg">
          <svg width="14" height="14" viewBox="0 0 24 24" fill="currentColor">
            <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm1 15h-2v-2h2v2zm0-4h-2V7h2v6z" />
          </svg>
          {error}
        </div>
      )}
 
      <div className="rg-row2">
        <InputField label="Nama Depan *" icon={<IconUser />} id="firstName" placeholder="John"  inputRef={firstRef} />
        <InputField label="Nama Belakang *" icon={<IconUser />} id="lastName"  placeholder="Doe"   inputRef={lastRef}  />
      </div>
 
      <InputField
        label="Email *"
        icon={<IconMail />}
        type="email"
        id="email"
        placeholder="nama@email.com"
        inputRef={emailRef}
      />
 
      {/* Password */}
      <div className="rg-form-group">
        <label className="rg-form-label" htmlFor="password">Password *</label>
        <div className="rg-input-wrap">
          <span className="rg-input-icon"><IconLock /></span>
          <input
            ref={passRef}
            className="rg-form-input"
            type={showPass ? "text" : "password"}
            id="password"
            placeholder="Min. 8 karakter"
            onChange={handlePassChange}
          />
          <EyeBtn visible={showPass} onToggle={() => setShowPass((v) => !v)} />
        </div>
        {pass.length > 0 && (
          <div className="rg-pass-strength">
            <div className="rg-strength-bars">
              {[1, 2, 3, 4].map((n) => (
                <div
                  key={n}
                  className="rg-sbar"
                  style={{ background: n <= strength.score ? strength.color : "" }}
                />
              ))}
            </div>
            <span className="rg-strength-label" style={{ color: strength.color }}>
              {strength.label}
            </span>
          </div>
        )}
      </div>
 
      {/* Confirm password */}
      <div className="rg-form-group">
        <label className="rg-form-label" htmlFor="confirmPass">Konfirmasi Password *</label>
        <div className="rg-input-wrap">
          <span className="rg-input-icon"><IconLock /></span>
          <input
            ref={confRef}
            className="rg-form-input"
            type={showConf ? "text" : "password"}
            id="confirmPass"
            placeholder="Ulangi password"
            onChange={handleConfChange}
          />
          <EyeBtn visible={showConf} onToggle={() => setShowConf((v) => !v)} />
        </div>
        {confHint.text && (
          <div className={`rg-field-hint ${confHint.ok ? "rg-hint--ok" : "rg-hint--err"}`}>
            {confHint.text}
          </div>
        )}
      </div>
 
      <SubmitBtn fullWidth onClick={handleNext}>Lanjutkan →</SubmitBtn>
 
      <div className="rg-divider"><span>atau daftar dengan</span></div>
 
      <button type="button" className="rg-sso-btn" onClick={() => alert("SSO Google")}>
        <IconGoogle /> Daftar dengan Google
      </button>
 
      <div className="rg-login-link">
        Sudah punya akun?{" "}
        <a onClick={() => navigate("/")}>
            Masuk di sini
        </a>
      </div>
    </div>
  );
}
 
// ─── STEP 2: DETAIL PROFIL ────────────────────────────────────────────────────
 
function Step2({ onNext, onBack }) {
  const [role, setRole]   = useState("admin");
  const [error, setError] = useState("");
  const phoneRef          = useRef();
  const companyRef        = useRef();
  const referralRef       = useRef();
 
  const handlePhone = (e) => {
    let v = e.target.value.replace(/\D/g, "");
    if (v.length > 4 && v.length <= 8) v = v.slice(0, 4) + "-" + v.slice(4);
    else if (v.length > 8) v = v.slice(0, 4) + "-" + v.slice(4, 8) + "-" + v.slice(8, 12);
    e.target.value = v;
  };
 
  const handleNext = () => {
    const ph = phoneRef.current?.value.replace(/\D/g, "");
    if (!ph || ph.length < 10) return setError("Nomor HP tidak valid.");
    setError("");
    onNext({
      role,
      phone:    phoneRef.current.value,
      company:  companyRef.current?.value || "—",
      referral: referralRef.current?.value || "",
    });
  };
 
  return (
    <div className="rg-step-panel">
      {error && (
        <div className="rg-error-msg">
          <svg width="14" height="14" viewBox="0 0 24 24" fill="currentColor">
            <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm1 15h-2v-2h2v2zm0-4h-2V7h2v6z" />
          </svg>
          {error}
        </div>
      )}
 
      {/* Role selector */}
      <div className="rg-form-group">
        <label className="rg-form-label">Pilih Role *</label>
        <div className="rg-role-select-group">
          {[
            { id: "admin", icon: "👑", name: "Admin",  desc: "Akses penuh" },
            { id: "sales", icon: "📊", name: "Sales",  desc: "Kelola lead" },
          ].map((r) => (
            <div
              key={r.id}
              className={`rg-role-card${role === r.id ? " selected" : ""}`}
              onClick={() => setRole(r.id)}
            >
              <span className="rg-rc-icon">{r.icon}</span>
              <div>
                <div className="rg-rc-name">{r.name}</div>
                <div className="rg-rc-desc">{r.desc}</div>
              </div>
              <div className="rg-rc-dot" />
            </div>
          ))}
        </div>
      </div>
 
      <div className="rg-form-group">
        <label className="rg-form-label" htmlFor="phone">Nomor HP *</label>
        <div className="rg-input-wrap">
          <span className="rg-input-icon"><IconPhone /></span>
          <input
            ref={phoneRef}
            className="rg-form-input"
            id="phone"
            placeholder="0812-xxxx-xxxx"
            onChange={handlePhone}
          />
        </div>
      </div>
 
      <div className="rg-form-group">
        <label className="rg-form-label" htmlFor="company">Nama Perusahaan / Tim</label>
        <div className="rg-input-wrap">
          <span className="rg-input-icon"><IconBuilding /></span>
          <input ref={companyRef} className="rg-form-input" id="company" placeholder="Nama perusahaan (opsional)" />
        </div>
      </div>
 
      <div className="rg-form-group">
        <label className="rg-form-label" htmlFor="referral">Kode Referral</label>
        <div className="rg-input-wrap">
          <span className="rg-input-icon"><IconChat /></span>
          <input
            ref={referralRef}
            className="rg-form-input"
            id="referral"
            placeholder="Masukkan kode (opsional)"
            onChange={(e) => { e.target.value = e.target.value.toUpperCase(); }}
          />
        </div>
        <div className="rg-field-hint">Punya kode dari rekan? Masukkan untuk bonus eksklusif</div>
      </div>
 
      <div className="rg-btn-row">
        <button type="button" className="rg-back-btn" onClick={onBack}>← Kembali</button>
        <SubmitBtn onClick={handleNext}>Lanjutkan →</SubmitBtn>
      </div>
    </div>
  );
}
 
// ─── STEP 3: KONFIRMASI ───────────────────────────────────────────────────────
 
function Step3({ data, onBack, onSubmit }) {
  const [terms,   setTerms]   = useState(false);
  const [loading, setLoading] = useState(false);
  const [error,   setError]   = useState("");
 
  const { step1, step2 } = data;
  const fullName = `${step1.firstName} ${step1.lastName}`;
 
  const handleSubmit = () => {
    if (!terms) return setError("Anda harus menyetujui syarat & ketentuan.");
    setError("");
    onSubmit();
  };
 
  return (
    <div className="rg-step-panel">
      {error && (
        <div className="rg-error-msg">
          <svg width="14" height="14" viewBox="0 0 24 24" fill="currentColor">
            <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm1 15h-2v-2h2v2zm0-4h-2V7h2v6z" />
          </svg>
          {error}
        </div>
      )}
 
      {/* Review card */}
      <div className="rg-review-card">
        <div className="rg-review-header">
          <div className="rg-review-avatar">{step1.firstName?.[0] || "U"}</div>
          <div>
            <div className="rg-review-name">{fullName}</div>
            <div className="rg-review-role">
              {step2.role === "admin" ? "👑 Admin" : "📊 Sales"}
            </div>
          </div>
          <div className="rg-review-badge">Baru</div>
        </div>
        <div className="rg-review-body">
          {[
            { label: "Email",      value: step1.email },
            { label: "Nomor HP",   value: step2.phone },
            { label: "Perusahaan", value: step2.company || "—" },
          ].map((row) => (
            <div key={row.label} className="rg-review-row">
              <span className="rg-review-lbl">{row.label}</span>
              <span className="rg-review-val">{row.value}</span>
            </div>
          ))}
        </div>
      </div>
 
      {/* Terms */}
      <div className="rg-terms-row">
        <div
          className={`rg-custom-cb${terms ? " checked" : ""}`}
          onClick={() => setTerms((v) => !v)}
        >
          {terms && <IconCheck />}
        </div>
        <div className="rg-terms-text">
          Dengan mendaftar, saya menyetujui{" "}
          <a href="#">Syarat &amp; Ketentuan</a> dan{" "}
          <a href="#">Kebijakan Privasi</a> LeadEstate.
        </div>
      </div>
 
      <div className="rg-btn-row">
        <button type="button" className="rg-back-btn" onClick={onBack}>← Kembali</button>
        <SubmitBtn loading={loading} onClick={handleSubmit}>Buat Akun</SubmitBtn>
      </div>
    </div>
  );
}
 
// ─── SUCCESS ──────────────────────────────────────────────────────────────────
 
function SuccessState() {
  return (
    <div className="rg-success-state">
      <div className="rg-success-circle">
        <IconCheck size={32} />
      </div>
      <h3>Akun Berhasil Dibuat!</h3>
      <p>Selamat bergabung di LeadEstate.<br />Silakan masuk untuk mulai mengelola lead Anda.</p>
      <a href="/" className="rg-login-link-btn">Masuk Sekarang →</a>
    </div>
  );
}
 
// ─── MAIN COMPONENT ──────────────────────────────────────────────────────────
 
export default function Register() {
    const [currentStep, setCurrentStep] = useState(1);
    const [isDone,      setIsDone]      = useState(false);
    const [formData,    setFormData]    = useState({ step1: {}, step2: {} });
    const navigate = useNavigate();
 
const handleStep1 = async (data) => {
  try {
    const res = await fetch(`http://localhost:8080/api/auth/check-email?email=${data.email}`);
    
    if (res.ok) {
      setFormData((prev) => ({ ...prev, step1: data }));
      setCurrentStep(2);
    } else {
      const msg = await res.text();
      alert(msg); 
    }
  } catch (err) {
    alert("Koneksi gagal! Pastikan aplikasi Spring Boot kamu sudah jalan.");
  }
};
 
  const handleStep2 = (data) => {
    setFormData((prev) => ({ ...prev, step2: data }));
    setCurrentStep(3);
  };
 
const handleSubmit = async () => {
  const payload = {
    name: `${formData.step1.firstName} ${formData.step1.lastName}`,
    email: formData.step1.email,
    password: formData.step1.password,
    roleId: formData.step2.role === "admin" ? 1 : 2
  };

  try {
    const res = await fetch('http://localhost:8080/api/auth/register', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload)
    });

    if (res.ok) setIsDone(true);
  } catch (err) {
    alert("Gagal simpan data.");
  }
};
 
  const rightScrollRef = useRef();
 
  const scrollTop = () => {
    if (rightScrollRef.current) rightScrollRef.current.scrollTop = 0;
  };
 
  return (
    <div className="rg-page">
 
      {/* LEFT */}
      <LeftPanel currentStep={currentStep} isDone={isDone} />
 
      {/* RIGHT */}
      <div className="rg-right" ref={rightScrollRef}>
        <div className="rg-form-box">
 
          {!isDone && (
            <>
              {/* Greeting */}
              <div className="rg-form-greeting">
                <div className="rg-greet-label">
                  <span className="rg-greet-line" />
                  Buat Akun Baru
                </div>
                <h2>{STEP_TITLES[currentStep]}</h2>
                <p>{STEP_DESCS[currentStep]}</p>
              </div>
 
              {/* Progress tabs */}
              <div className="rg-step-tabs">
                {[1, 2, 3].map((n) => (
                  <div
                    key={n}
                    className={`rg-stab${currentStep === n ? " active" : ""}${currentStep > n ? " done" : ""}`}
                  />
                ))}
              </div>
            </>
          )}
 
          {isDone ? (
            <SuccessState />
          ) : currentStep === 1 ? (
            <Step1 
                onNext={(d) => { handleStep1(d); scrollTop(); }}
                navigate={navigate}
            />
          ) : currentStep === 2 ? (
            <Step2
              onNext={(d) => { handleStep2(d); scrollTop(); }}
              onBack={() => { setCurrentStep(1); scrollTop(); }}
            />
          ) : (
            <Step3
              data={formData}
              onBack={() => { setCurrentStep(2); scrollTop(); }}
              onSubmit={handleSubmit}
            />
          )}
 
        </div>
      </div>
    </div>
  );
}