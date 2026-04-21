import { useEffect, useState } from "react";
import { BrowserRouter as Router, Routes, Route, Navigate } from "react-router-dom";
import Login from "./pages/Login";
import Register from "./pages/Register";
import Dashboard from "./pages/Dashboard";
import Settings from "./pages/Settings";
import ForgotPass from "./pages/ForgotPass";

function App() {
  const [user, setUser] = useState(null);

  useEffect(() => {
    const storedUser = localStorage.getItem("user");
    if (storedUser) {
      setUser(storedUser);
    }
  }, []);

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
          path="/settings" 
          element={
            user ? <Settings /> : <Navigate to="/" />
          } 
        />

      </Routes>
    </Router>
  );
}

export default App;