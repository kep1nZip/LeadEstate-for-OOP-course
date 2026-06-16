import { useEffect, useState } from "react";
import { BrowserRouter as Router, Routes, Route, Navigate } from "react-router-dom";
import Login from "./pages/Login";
import Register from "./pages/Register";
import Dashboard from "./pages/Dashboard";
import Settings from "./pages/Settings";
import ForgotPass from "./pages/ForgotPass";
import Laporan from "./pages/Laporan";
import DataLead from "./pages/dataLead";
import ManajemenSalesPage from "./pages/ManajemenSalesPage";
import ReminderPage from "./pages/ReminderPage";


function App() {
  const [user, setUser] = useState(null);

  useEffect(() => {
    const storedUser = localStorage.getItem("user");

    if (storedUser) {
      try {
        const parsedUser = JSON.parse(storedUser);

        console.log("USER DARI LOCALSTORAGE:", parsedUser);

        setUser(parsedUser);

      } catch (err) {
        console.error("Gagal parse user:", err);

        localStorage.removeItem("user");
      }
    }
  }, []);

  const isAdmin =
    user?.role?.toLowerCase() === "admin";

  return (
    <Router>
      <Routes>

        <Route 
          path="/" 
          element={
            user ? <Navigate to="/dashboard" /> : <Login setUser={setUser} />
          } 
        />

        <Route 
          path="/register" 
          element={
            user ? <Navigate to="/dashboard" /> : <Register />
          } 
        />

          <Route 
          path="/forgot-password" 
          element={
            user ? <Navigate to="/dashboard" /> : <ForgotPass />
          } 
        />

        <Route 
          path="/dashboard" 
          element={
            user ? <Dashboard /> : <Navigate to="/" />
          } 
        />

        <Route
          path="/laporan"
          element={
            user && isAdmin
              ? <Laporan />
              : <Navigate to="/dashboard" />
          }
        />

        <Route 
          path="/settings" 
          element={
            user ? <Settings /> : <Navigate to="/" />
          } 
        />

          <Route
            path="/Manajemen_sales"
            element={
              user && isAdmin
                ? <ManajemenSalesPage />
                : <Navigate to="/dashboard" />
            }
          />

          <Route 
          path="/reminder" 
          element={
            user ? <ReminderPage /> : <Navigate to="/" />
          } 
        />

        <Route
          path="/dataLeads"
          element={user ? <DataLead /> : <Navigate to="/" />}
        />
        {/* Alias route untuk konsistensi navigasi dari halaman lain */}
        <Route path="/data-lead"      element={user ? <DataLead />            : <Navigate to="/" />} />
        
        <Route
          path="/manajemen-sales"
          element={
            user && isAdmin
              ? <ManajemenSalesPage />
              : <Navigate to="/dashboard" />
          }
        />

        <Route path="/reminderPage"   element={user ? <ReminderPage />        : <Navigate to="/" />} />

      </Routes>
    </Router>
  );
}

export default App;