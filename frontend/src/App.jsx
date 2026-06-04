import './App.css'
import Login from "./pages/Login";
import { BrowserRouter, Link, Route, Routes, Navigate } from 'react-router-dom';
import Projects from "./pages/Projects";
import Register from "./pages/Register";
import Tasks from "./pages/Tasks";
import ActivityLogs from "./pages/ActivityLogs";
import { useState, useEffect } from 'react';
import api from "./api/axiosConfig";

function App() {

  const [user, setUser] = useState(null);

  useEffect(() => {
    async function fetchCurrentUser() {

        const token = localStorage.getItem("token");

        if (!token) return;

        try {
            const response = await api.get("/users/me");
            setUser(response.data);
        } catch (error) {
            console.log(error);
        }
    }

    fetchCurrentUser();
  }, []);

  function handleLogout() {
    localStorage.removeItem("token");
    window.location.href = "/login"
  }
  
  return (
    <BrowserRouter>
    <strong className="logo">Team Workspace</strong>
    <nav>
      {user && (user.role === "ADMIN" || user.role === "SUPERADMIN") && (
        <Link to="/projects">Projects</Link>
      )}

      {user && (
        <Link to="/tasks">Tasks</Link>
      )}

      {user && (user.role === "ADMIN" || user.role === "SUPERADMIN") && (
        <Link to="/activity-logs">Activity Logs</Link>
      )}

      {user && (
        <span>
          Company: {user.organizationName}
        </span>
      )}

      {user && <button onClick={handleLogout}>Logout</button>}
    </nav>
    <Routes>
      <Route path="/" element={<Navigate to="/login" />} />
      <Route path="/login" element={<Login />} />
      <Route path="/register" element={<Register />} />
      <Route path="/projects" element={<Projects />} />
      <Route path="/tasks" element={<Tasks user={user} />} />
      <Route path="/activity-logs" element={<ActivityLogs />} />
    </Routes>
    </BrowserRouter>
  )
}

export default App
