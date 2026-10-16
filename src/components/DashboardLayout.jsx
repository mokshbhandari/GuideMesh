import { Link, useNavigate } from "react-router-dom";
import { logout } from "../api";

function getUserFromToken() {
  try {
    const token = localStorage.getItem("token");

    if (!token) {
      return {
        name: null,
        role: null,
      };
    }

    const payload = JSON.parse(atob(token.split(".")[1]));

    return {
      name: payload.name || null,
      role: payload.role || null,
    };
  } catch {
    return {
      name: null,
      role: null,
    };
  }
}

function DashboardLayout({ role, children }) {
  const navigate = useNavigate();
  const user = getUserFromToken();

  const displayName = user.name || role;

  const dashboardPath =
    role === "Student" ? "/student/dashboard" :
    role === "Professor" ? "/professor/dashboard" :
    "/admin/dashboard";

  function handleLogout() {
    logout();
    navigate("/");
  }

  return (
    <div className="dashboard-layout">
      <aside className="sidebar">
        <Link to={dashboardPath} className="logo">Guide<span>Mesh</span></Link>
        <p className="sidebar-role">{role} Portal</p>

        <nav className="sidebar-nav">
          <a href={dashboardPath}>Dashboard</a>

          {role === "Student" && (
            <>
              <a href="#activities">Activities</a>
              <a href="#group">My Group</a>
              <a href="#invitations">Invitations</a>
            </>
          )}

          {role === "Professor" && (
            <>
              <a href="#activities">Activities</a>
              <a href="#mentoring">My Mentoring</a>
            </>
          )}

          {role === "Admin" && (
            <>
              <a href="#activities">Activities</a>
              <a href="#groups">Groups</a>
              <a href="#allocation">Mentor Allocation</a>
            </>
          )}
        </nav>

        <button className="logout" onClick={handleLogout}>
          Logout
        </button>
      </aside>

      <main className="dashboard-main">
        <header className="dashboard-header">
          <span>{role} Portal</span>

          <div className="user-info">
            <span className="avatar">
              {displayName ? displayName[0].toUpperCase() : role[0]}
            </span>

            <span>{displayName}</span>
          </div>
        </header>

        <div className="dashboard-content">
          {children}
        </div>
      </main>
    </div>
  );
}

export default DashboardLayout;