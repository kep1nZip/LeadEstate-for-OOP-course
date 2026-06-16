import { useState, useEffect } from "react";
import axios from "axios";
import "../styles/login.css";
import { useNavigate } from "react-router-dom";
import LogoPng from "../assets/leadestate-logo.png"; 

export default function Login( { setUser } ) {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);
  const [passVisible, setPassVisible] = useState(false);
  const navigate = useNavigate();

const API = import.meta.env.VITE_API_URL || "";

const handleLogin = async (e) => {
  console.log("HANDLE LOGIN KEJALAN");

  if (e) e.preventDefault();

  console.log("SETELAH PREVENT");

  setError("");

  console.log("EMAIL:", email);
  console.log("PASSWORD:", password);

  if (!email) return setError("Email tidak boleh kosong.");
  if (!password) return setError("Password tidak boleh kosong.");

  try {
    console.log("MASUK TRY");

    setLoading(true);

    console.log("SEBELUM AXIOS");

    const res = await axios.post(`${API}/api/auth/login`, {
      email,
      password,
    });

    // TAMBAHKAN BARIS INI:
    console.log("ROLE:", res.data.role);
console.log("CEK DATA DARI SERVER:", res.data); 
// alert("Isi data server: " + JSON.stringify(res.data)); // Ini akan muncul kotak pesan di layar

localStorage.setItem("user", JSON.stringify(res.data));
setUser(res.data);
navigate("/dashboard");

    localStorage.setItem("user", JSON.stringify(res.data));

    setUser(res.data);

    navigate("/dashboard");

  } catch (err) {
    console.error("LOGIN ERROR:", err);

    setError("Email atau password salah");
  } finally {
    setLoading(false);
  }
};

  return (
    <div className="login-container">
      {/* LEFT PANEL */}
      <div className="left">
        {/* Update Brand dengan Logo PNG sesuai gambar */}
        <div className="left-brand">
          <img src={LogoPng} alt="LeadEstate Logo" className="brand-logo-img" />
          <div className="brand-text">Lead<span>Estate</span></div>
        </div>

        <div className="left-center">
          <div className="left-headline">
            <h1>Kelola<br />Lead dengan<br /><em>Presisi</em></h1>
            <p>
              Platform CRM (<em>Customer Relationship Management</em>) properti 
              yang membantu tim sales menutup lebih banyak deal, lebih cepat.
            </p>
          </div>
          
          {/* Bagian Stats & Card disembunyikan atau dihapus sesuai gambar referensi terbaru */}
          {/* Jika ingin tetap ada, biarkan kodenya. Jika ingin persis gambar 2, hapus bagian stats-strip & prop-card */}
        </div>

        <div className="left-footer">
          &copy; 2026 LeadEstate
        </div>
      </div>

      {/* RIGHT PANEL */}
      <div className="right">
        <div className="form-box">
          <div className="form-greeting">
            <div className="greet-label">
              <span className="greet-line"></span>
              SELAMAT DATANG
            </div>
            <h2>Masuk ke<br />Akun Anda</h2>
          </div>

          {/* Error Message */}
          <div className={`error-msg ${error ? "show" : ""}`}>
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
              <circle cx="12" cy="12" r="10"></circle>
              <line x1="12" y1="8" x2="12" y2="12"></line>
              <line x1="12" y1="16" x2="12.01" y2="16"></line>
            </svg>
            <span>{error}</span>
          </div>

          <form onSubmit={handleLogin}>
            <div className="form-group">
              <label className="form-label">EMAIL</label>
              <div className="input-wrap">
                <input
                  className="form-input"
                  type="email"
                  placeholder="Masukkan Email"
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                />
              </div>
            </div>

            <div className="form-group">
              <label className="form-label">PASSWORD</label>
              <div className="input-wrap">
                <input
                  className="form-input"
                  type={passVisible ? "text" : "password"}
                  placeholder="Masukkan Password"
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                />
                <button 
                  className="eye-btn" 
                  type="button" 
                  onClick={() => setPassVisible(!passVisible)}
                >
                  <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                    <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"></path>
                    <circle cx="12" cy="12" r="3"></circle>
                  </svg>
                </button>
              </div>
            </div>

            <div className="forgot-row">
              <a onClick={() => navigate("/forgot-password")} 
                className="forgot-link">Lupa Password?</a>
            </div>

            <button 
              className={`submit-btn ${loading ? "loading" : ""}`} 
              type="submit"
              disabled={loading}
            >
              <div className="btn-inner">
                {loading && <div className="spinner"></div>}
                <span className="btn-text">{loading ? "MEMPROSES..." : "MASUK"}</span>
              </div>
            </button>
          </form>

          <div className="register-link">
            Belum punya akun?{" "}
            <a onClick={() => navigate("/register")}>
              Daftar sekarang
            </a>
          </div>
        </div>
      </div>
    </div>
  );
}