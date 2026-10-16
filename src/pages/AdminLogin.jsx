import { useNavigate } from "react-router-dom";
import { useState } from "react";
import Navbar from "../components/Navbar";
import { login, logout } from "../api";

function getRoleFromToken(token) {
  try {
    const payload = JSON.parse(atob(token.split(".")[1]));
    return payload.role;
  } catch {
    return null;
  }
}

function AdminLogin() {
  const navigate = useNavigate();
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  async function handleSubmit(event) {
    event.preventDefault();
    setError("");
    setLoading(true);

    try {
      const token = await login({ email, password });
      const role = getRoleFromToken(token);

      if (role !== "ADMIN") {
        logout();
        throw new Error("This account does not have administrator access");
      }

      navigate("/admin/dashboard");
    } catch (exception) {
      setError(exception.message);
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="auth-page">
      <Navbar />

      <main className="auth-container">
        <div className="form-card">
          <div className="form-heading">
            <p className="eyebrow">Administrator access</p>
            <h1>Admin Login</h1>
            <p>Manage activities, groups and mentor allocation.</p>
          </div>

          <form onSubmit={handleSubmit} className="form">
            <label>
              Email
              <input
                type="email"
                value={email}
                onChange={(event) => setEmail(event.target.value)}
                placeholder="Admin email"
                required
              />
            </label>

            <label>
              Password
              <input
                type="password"
                value={password}
                onChange={(event) => setPassword(event.target.value)}
                placeholder="Admin password"
                required
              />
            </label>

            {error && <p className="form-error">{error}</p>}

            <button className="button primary" type="submit" disabled={loading}>
              {loading ? "Logging in..." : "Login as Admin"}
            </button>
          </form>
        </div>
      </main>
    </div>
  );
}

export default AdminLogin;
