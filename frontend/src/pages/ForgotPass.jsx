import { useState, useRef, useEffect, useCallback } from "react";
import "../styles/Forgotpass.css";
import { useNavigate } from "react-router-dom";
import LogoPng from "../assets/leadestate-logo.png"; 
const API = import.meta.env.VITE_API_URL || "http://localhost:8080";

// ─── CONSTANTS ────────────────────────────────────────────────────────────────

const FLOW_STEPS = [
  { icon: "📧", title: "Masukkan Email",      desc: "Verifikasi identitas dengan email terdaftar"    },
  { icon: "🔢", title: "Verifikasi Kode OTP", desc: "Masukkan kode 6 digit yang dikirim ke email"    },
  { icon: "🔑", title: "Password Baru",       desc: "Buat password baru yang kuat untuk akun"        },
];

const PASS_REQS = [
  { id: "r1", label: "Minimal 8 karakter",             test: (v) => v.length >= 8             },
  { id: "r2", label: "Mengandung huruf besar (A-Z)",   test: (v) => /[A-Z]/.test(v)           },
  { id: "r3", label: "Mengandung angka (0-9)",         test: (v) => /[0-9]/.test(v)           },
  { id: "r4", label: "Mengandung karakter spesial (!@#$)", test: (v) => /[^A-Za-z0-9]/.test(v) },
];

const STR_COLORS = ["", "#ef4444", "#f59e0b", "#f59e0b", "#22c55e"];
const STR_LABELS = ["", "Lemah", "Cukup", "Kuat", "Sangat Kuat"];

// ─── ICONS ────────────────────────────────────────────────────────────────────

const IconMail = () => (
  <svg width="16" height="16" viewBox="0 0 24 24" fill="currentColor">
    <path d="M20 4H4c-1.1 0-2 .9-2 2v12c0 1.1.9 2 2 2h16c1.1 0 2-.9 2-2V6c0-1.1-.9-2-2-2zm0 4l-8 5-8-5V6l8 5 8-5v2z" />
  </svg>
);

const IconLock = () => (
  <svg width="16" height="16" viewBox="0 0 24 24" fill="currentColor">
    <path d="M18 8h-1V6c0-2.76-2.24-5-5-5S7 3.24 7 6v2H6c-1.1 0-2 .9-2 2v10c0 1.1.9 2 2 2h12c1.1 0 2-.9 2-2V10c0-1.1-.9-2-2-2zm-6 9c-1.1 0-2-.9-2-2s.9-2 2-2 2 .9 2 2-.9 2-2 2zm3.1-9H8.9V6c0-1.71 1.39-3.1 3.1-3.1s3.1 1.39 3.1 3.1v2z" />
  </svg>
);

const IconBack = () => (
  <svg width="14" height="14" viewBox="0 0 24 24" fill="currentColor">
    <path d="M20 11H7.83l5.59-5.59L12 4l-8 8 8 8 1.41-1.41L7.83 13H20v-2z" />
  </svg>
);

const IconError = () => (
  <svg width="14" height="14" viewBox="0 0 24 24" fill="currentColor">
    <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm1 15h-2v-2h2v2zm0-4h-2V7h2v6z" />
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

const IconCheck = ({ size = 9 }) => (
  <svg width={size} height={size} viewBox="0 0 24 24" fill="currentColor">
    <path d="M9 16.17L4.83 12l-1.42 1.41L9 19 21 7l-1.41-1.41z" />
  </svg>
);

// ─── SMALL COMPONENTS ────────────────────────────────────────────────────────

function ErrorBanner({ message }) {
  if (!message) return null;
  return (
    <div className="fp-error-msg">
      <IconError />
      {message}
    </div>
  );
}

function Greeting({ step, title, desc }) {
  return (
    <div className="fp-form-greeting">
      <div className="fp-greet-label">
        <span className="fp-greet-line" />
        Langkah {step} dari 3
      </div>
      <h2 dangerouslySetInnerHTML={{ __html: title }} />
      <p dangerouslySetInnerHTML={{ __html: desc }} />
    </div>
  );
}

function SubmitBtn({ onClick, loading, children }) {
  return (
    <button
      type="button"
      className={`fp-submit-btn${loading ? " loading" : ""}`}
      onClick={onClick}
    >
      <div className="fp-btn-inner">
        <div className="fp-spinner" />
        <span className="fp-btn-text">{children}</span>
      </div>
    </button>
  );
}

function BackLink({ onClick, label }) {
  return (
    <button type="button" className="fp-back-link" onClick={onClick}>
      <IconBack />
      {label}
    </button>
  );
}

// ─── LEFT PANEL ───────────────────────────────────────────────────────────────

function LeftPanel({ currentPanel }) {
  return (
    <div className="fp-left">
      {/* Brand */}
      <div className="fp-left-brand">
        <img src={LogoPng} alt="LeadEstate Logo" className="fp-brand-logo" />
        <div className="fp-brand-text">Lead<span>Estate</span></div>
      </div>

      <div className="fp-left-center">
        {/* Headline */}
        <div className="fp-left-headline">
          <h1>Pulihkan<br />Akses Anda<br /><em>dengan Aman</em></h1>
          <p>Ikuti tiga langkah mudah untuk mengatur ulang password akun LeadEstate Anda.</p>
        </div>

        {/* Flow steps */}
        <div className="fp-flow-visual">
          {FLOW_STEPS.map((step, i) => (
            <div key={step.title} className="fp-flow-item">
              {i < FLOW_STEPS.length - 1 && <div className="fp-flow-connector" />}
              <div className={`fp-flow-icon${currentPanel === i + 1 ? " active-step" : ""}`}>
                {step.icon}
              </div>
              <div className="fp-flow-info">
                <div className="fp-flow-title">{step.title}</div>
                <div className="fp-flow-desc">{step.desc}</div>
              </div>
            </div>
          ))}
        </div>

        {/* Security card */}
        <div className="fp-security-card">
          <div className="fp-sec-icon">🛡️</div>
          <div>
            <div className="fp-sec-title">Keamanan Terjamin</div>
            <div className="fp-sec-desc">
              Kode OTP hanya berlaku 5 menit dan hanya bisa digunakan sekali.
              Data Anda aman bersama kami.
            </div>
          </div>
        </div>
      </div>

      <div className="fp-left-footer">
        &copy; 2026 LeadEstate &nbsp;·&nbsp;
        <a href="#">Kebijakan Privasi</a> &nbsp;·&nbsp;
        <a href="#">Syarat Layanan</a>
      </div>
    </div>
  );
}

// ─── PANEL 1: EMAIL ───────────────────────────────────────────────────────────

function Panel1({ onNext, navigate }) {
  const [loading, setLoading] = useState(false);
  const [error,   setError]   = useState("");
  const emailRef              = useRef();

  const handleSend = () => {
    const em = emailRef.current?.value.trim();
    if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(em)) {
      return setError("Masukkan alamat email yang valid.");
    }
    setError("");
    setLoading(true);
    fetch(`${API}/api/auth/forgot-password`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        email: em,
      }),
    })
      .then(async (res) => {

        const data = await res.json();

        if (!res.ok) {
          throw new Error(data);
        }

        setLoading(false);

        alert(`Kode OTP kamu: ${data.token}`);

        onNext(em, data.token);
      })
      .catch((err) => {
        setLoading(false);
        setError(err.message);
      });
  };

  useEffect(() => {
    const handler = (e) => { if (e.key === "Enter") handleSend(); };
    window.addEventListener("keydown", handler);
    return () => window.removeEventListener("keydown", handler);
  }, []);

  return (
    <div className="fp-panel">
      <Greeting
        step={1}
        title="Lupa<br/>Password?"
        desc="Masukkan alamat email yang terdaftar. Kami akan mengirimkan kode verifikasi."
      />

      <ErrorBanner message={error} />

      <div className="fp-form-group">
        <label className="fp-form-label">Alamat Email</label>
        <div className="fp-input-wrap">
          <span className="fp-input-icon"><IconMail /></span>
          <input
            ref={emailRef}
            className="fp-form-input"
            type="email"
            placeholder="nama@email.com"
          />
        </div>
      </div>

      <SubmitBtn loading={loading} onClick={handleSend}>
        Kirim Kode Verifikasi
      </SubmitBtn>

      <BackLink onClick={() => navigate("/")} label="Kembali ke halaman masuk" />
    </div>
  );
}

// ─── PANEL 2: OTP ────────────────────────────────────────────────────────────

function Panel2({ email, onNext, onBack }) {
  const OTP_DEMO              = useRef("482916");
  const [otp, setOtp]         = useState(["", "", "", "", "", ""]);
  const [timer, setTimer]     = useState(300);
  const [expired, setExpired] = useState(false);
  const [loading, setLoading] = useState(false);
  const [error,   setError]   = useState("");
  const [shake,   setShake]   = useState(false);
  const inputRefs             = useRef([]);
  const timerRef              = useRef(null);

  const startTimer = useCallback((sec) => {
    clearInterval(timerRef.current);
    setTimer(sec);
    setExpired(false);
    timerRef.current = setInterval(() => {
      setTimer((t) => {
        if (t <= 1) {
          clearInterval(timerRef.current);
          setExpired(true);
          return 0;
        }
        return t - 1;
      });
    }, 1000);
  }, []);

  useEffect(() => {
    startTimer(300);
    inputRefs.current[0]?.focus();
    return () => clearInterval(timerRef.current);
  }, [startTimer]);

  const formatTimer = (t) => {
    const m = Math.floor(t / 60);
    const s = t % 60;
    return `${m}:${s < 10 ? "0" : ""}${s}`;
  };

  const handleChange = (idx, val) => {
    const digit = val.replace(/\D/g, "").slice(-1);
    const next  = [...otp];
    next[idx]   = digit;
    setOtp(next);
    if (digit && idx < 5) inputRefs.current[idx + 1]?.focus();
    // auto-verify
    const full = next.every((d) => d !== "");
    if (full) setTimeout(() => verifyCode(next.join("")), 200);
  };

  const handleKeyDown = (idx, e) => {
    if (e.key === "Backspace") {
      if (!otp[idx] && idx > 0) {
        const next = [...otp];
        next[idx - 1] = "";
        setOtp(next);
        inputRefs.current[idx - 1]?.focus();
      }
    }
    if (e.key === "ArrowLeft"  && idx > 0) inputRefs.current[idx - 1]?.focus();
    if (e.key === "ArrowRight" && idx < 5) inputRefs.current[idx + 1]?.focus();
  };

  const verifyCode = (code) => {

    setLoading(true);

    fetch(`${API}/api/auth/validate-token?token=${code}`)
      .then(async (res) => {

        const data = await res.text();

        if (!res.ok) {
          throw new Error(data);
        }

        setLoading(false);

        onNext(code);
      })
      .catch((err) => {

        setLoading(false);

        setError(err.message);

        setOtp(["", "", "", "", "", ""]);

        setShake(true);

        setTimeout(() => setShake(false), 600);

        inputRefs.current[0]?.focus();
      });
  };

  const handleVerify = () => verifyCode(otp.join(""));

  const handleResend = () => {
    fetch(`${API}/api/auth/forgot-password`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        email,
      }),
    })
      .then(async (res) => {

        const data = await res.json();

        if (!res.ok) {
          throw new Error(data);
        }

        setOtp(["", "", "", "", "", ""]);
        setError("OTP baru berhasil dikirim");
        setExpired(false);

        startTimer(300);

        inputRefs.current[0]?.focus();
      })
      .catch((err) => {
        setError(err.message);
      });
  };

  useEffect(() => {
    const handler = (e) => { if (e.key === "Enter") handleVerify(); };
    window.addEventListener("keydown", handler);
    return () => window.removeEventListener("keydown", handler);
  }, [otp]);

  return (
    <div className="fp-panel">
      <Greeting
        step={2}
        title="Verifikasi<br/>Kode OTP"
        desc={`Kode 6 digit telah dikirim ke<br/><strong>${email}</strong>`}
      />

      <ErrorBanner message={error} />

      {/* OTP boxes */}
      <div className={`fp-otp-wrap${shake ? " shake" : ""}`}>
        {otp.map((digit, i) => (
          <input
            key={i}
            ref={(el) => { inputRefs.current[i] = el; }}
            className={`fp-otp-input${digit ? " filled" : ""}`}
            maxLength={1}
            value={digit}
            onChange={(e) => handleChange(i, e.target.value)}
            onKeyDown={(e) => handleKeyDown(i, e)}
            inputMode="numeric"
          />
        ))}
      </div>

      <div className="fp-otp-hint">
        Kode berlaku selama{" "}
        <span className={`fp-timer${expired ? " expired" : ""}`}>
          {formatTimer(timer)}
        </span>
      </div>

      <SubmitBtn loading={loading} onClick={handleVerify}>
        Verifikasi Kode
      </SubmitBtn>

      <div className="fp-resend-row">
        Tidak menerima kode?&nbsp;
        <button
          type="button"
          className="fp-resend-btn"
          disabled={!expired}
          onClick={handleResend}
        >
          Kirim ulang
        </button>
      </div>

      <BackLink onClick={onBack} label="Ganti email" />
    </div>
  );
}

// ─── PANEL 3: NEW PASSWORD ────────────────────────────────────────────────────

function Panel3({ token, onNext }) {
  const [showNew,   setShowNew]   = useState(false);
  const [showConf,  setShowConf]  = useState(false);
  const [passVal,   setPassVal]   = useState("");
  const [confHint,  setConfHint]  = useState({ text: "", ok: false });
  const [loading,   setLoading]   = useState(false);
  const [error,     setError]     = useState("");
  const confRef                   = useRef();

  const reqs  = PASS_REQS.map((r) => ({ ...r, ok: r.test(passVal) }));
  const score = reqs.filter((r) => r.ok).length;

  const handlePassChange = (e) => {
    const v = e.target.value;
    setPassVal(v);
    if (confRef.current?.value) checkConf(confRef.current.value, v);
  };

  const checkConf = (val, pw) => {
    if (!val) return setConfHint({ text: "", ok: false });
    setConfHint(
      val === pw
        ? { text: "✓ Password cocok",      ok: true  }
        : { text: "✗ Password tidak cocok", ok: false }
    );
  };

  const handleConfChange = (e) => checkConf(e.target.value, passVal);

  const handleReset = () => {
    const pw  = passVal;
    const cpw = confRef.current?.value;
    if (pw.length < 8)  
      return setError("Password minimal 8 karakter.");

    if (pw !== cpw)     
      return setError("Konfirmasi password tidak cocok.");

    setError("");

    setLoading(true);

    fetch(`${API}/api/auth/reset-password`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        token,
        newPassword: pw,
      }),
    })
      .then(async (res) => {

        const data = await res.text();

        if (!res.ok) {
          throw new Error(data);
        }

        setLoading(false);

        onNext();
      })
      .catch((err) => {

        setLoading(false);

        setError(err.message);
      });
  };

  useEffect(() => {
    const handler = (e) => { if (e.key === "Enter") handleReset(); };
    window.addEventListener("keydown", handler);
    return () => window.removeEventListener("keydown", handler);
  }, [passVal]);

  return (
    <div className="fp-panel">
      <Greeting
        step={3}
        title="Password<br/>Baru"
        desc="Buat password baru yang kuat dan belum pernah digunakan sebelumnya."
      />

      <ErrorBanner message={error} />

      {/* New password */}
      <div className="fp-form-group">
        <label className="fp-form-label">Password Baru</label>
        <div className="fp-input-wrap">
          <span className="fp-input-icon"><IconLock /></span>
          <input
            className="fp-form-input"
            type={showNew ? "text" : "password"}
            placeholder="Min. 8 karakter"
            value={passVal}
            onChange={handlePassChange}
          />
          <button type="button" className="fp-eye-btn" onClick={() => setShowNew((v) => !v)}>
            {showNew ? <IconEyeOff /> : <IconEyeOpen />}
          </button>
        </div>

        {/* Strength bars */}
        {passVal.length > 0 && (
          <div className="fp-pass-strength">
            <div className="fp-strength-bars">
              {[1, 2, 3, 4].map((n) => (
                <div
                  key={n}
                  className="fp-sbar"
                  style={{ background: n <= score ? STR_COLORS[score] : "" }}
                />
              ))}
            </div>
            <span className="fp-strength-label" style={{ color: STR_COLORS[score] }}>
              {STR_LABELS[score]}
            </span>
          </div>
        )}

        {/* Requirements */}
        <div className="fp-req-list">
          {reqs.map((r) => (
            <div key={r.id} className={`fp-req-item${r.ok ? " ok" : ""}`}>
              <div className="fp-req-dot">
                {r.ok && <IconCheck />}
              </div>
              <span>{r.label}</span>
            </div>
          ))}
        </div>
      </div>

      {/* Confirm password */}
      <div className="fp-form-group" style={{ marginTop: 8 }}>
        <label className="fp-form-label">Konfirmasi Password</label>
        <div className="fp-input-wrap">
          <span className="fp-input-icon"><IconLock /></span>
          <input
            ref={confRef}
            className="fp-form-input"
            type={showConf ? "text" : "password"}
            placeholder="Ulangi password baru"
            onChange={handleConfChange}
          />
          <button type="button" className="fp-eye-btn" onClick={() => setShowConf((v) => !v)}>
            {showConf ? <IconEyeOff /> : <IconEyeOpen />}
          </button>
        </div>
        {confHint.text && (
          <div className={`fp-conf-hint${confHint.ok ? " ok" : " err"}`}>
            {confHint.text}
          </div>
        )}
      </div>

      <SubmitBtn loading={loading} onClick={handleReset}>
        Simpan Password Baru
      </SubmitBtn>
    </div>
  );
}

// ─── PANEL 4: SUCCESS ────────────────────────────────────────────────────────

function Panel4({ navigate }) {
  return (
    <div className="fp-panel">
      <div className="fp-success-wrap">
        <div className="fp-success-icon-wrap">
          <IconCheck size={36} />
        </div>
        <h3>Password Berhasil<br />Diperbarui!</h3>
        <p>
          Password akun Anda telah berhasil diganti.<br />
          Silakan masuk menggunakan password baru Anda.
        </p>
        <button
          type="button"
          className="fp-goto-login"
          onClick={() => navigate("/")}
        >
          Masuk Sekarang →
        </button>
        <div className="fp-report-row">
          Bukan Anda yang mengubah password?{" "}
          <a href="#" className="fp-report-link">Laporkan</a>
        </div>
      </div>
    </div>
  );
}

// ─── MAIN COMPONENT ──────────────────────────────────────────────────────────

export default function ForgotPassword() {
  const [panel, setPanel] = useState(1);
  const [email, setEmail] = useState("");
  const [token, setToken] = useState("");
  const navigate = useNavigate();

  const handleEmailNext = (em, tk) => {
    setEmail(em);
    setToken(tk);
    setPanel(2);
  };

  return (
    <div className="fp-page">
      <LeftPanel currentPanel={panel} />

      <div className="fp-right">
        <div className="fp-form-box">

          {panel === 1 && (
            <Panel1
              onNext={handleEmailNext}
              navigate={navigate}
            />
          )}

          {panel === 2 && (
            <Panel2
              email={email}
              onNext={(verifiedToken) => {
                setToken(verifiedToken);
                setPanel(3);
              }}
              onBack={() => setPanel(1)}
            />
          )}

          {panel === 3 && (
            <Panel3
              token={token}
              onNext={() => setPanel(4)}
            />
          )}

          {panel === 4 && (
            <Panel4 navigate={navigate} />
          )}

        </div>
      </div>
    </div>
  );
}