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


  // useEffect(() => {
  //   const user = localStorage.getItem("user");
  //   if (user) {
  //     navigate("/dashboard");
  //   }
  // }, []);

  
const handleLogin = async (e) => {
  if (e) e.preventDefault();
  setError("");

  if (!email) return setError("Email tidak boleh kosong.");
  if (!password) return setError("Password tidak boleh kosong.");

  try {
    setLoading(true);
    const res = await axios.post("http://localhost:8080/api/auth/login", {
      email,
      password,
    });

    console.log("LOGIN SUCCESS:", res.data);

    // simpan user
    localStorage.setItem("user", JSON.stringify(res.data));
    
    setUser(res.data);
    
    // redirect ke dashboard
    navigate("/dashboard");

  } catch (err) {
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

          <div className="divider"><span>atau masuk dengan</span></div>

          <button className="sso-btn" type="button">
            <svg width="18" height="18" viewBox="0 0 24 24">
              <path fill="#4285F4" d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92c-.26 1.37-1.04 2.53-2.21 3.31v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.09z"/>
              <path fill="#34A853" d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z"/>
              <path fill="#FBBC05" d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.07H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.93l2.85-2.22.81-.62z"/>
              <path fill="#EA4335" d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.07l3.66 2.84c.87-2.6 3.3-4.53 6.16-4.53z"/>
            </svg>
            Masuk dengan Google
          </button>

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