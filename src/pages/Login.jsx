import { Link, useNavigate } from "react-router-dom";
import { useState } from "react";
import Navbar from "../components/Navbar";
import { login } from "../api";

function getRoleFromToken(token) {
  try {
    const payload = JSON.parse(atob(token.split(".")[1]));
    return payload.role;
  } catch {
    return null;
  }
}

function Login() {
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

      if (role === "STUDENT") navigate("/student/dashboard");
      else if (role === "PROFESSOR") navigate("/professor/dashboard");
      else if (role === "ADMIN") navigate("/admin/dashboard");
      else throw new Error("Invalid account role");
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
            <p className="eyebrow">Welcome back</p>
            <h1>Login to GuideMesh</h1>
            <p>Access your academic project workspace.</p>
          </div>

          <form onSubmit={handleSubmit} className="form">
            <label>
              Email
              <input
                type="email"
                value={email}
                onChange={(event) => setEmail(event.target.value)}
                placeholder="Enter your email"
                required
              />
            </label>

            <label>
              Password
              <input
                type="password"
                value={password}
                onChange={(event) => setPassword(event.target.value)}
                placeholder="Enter your password"
                required
              />
            </label>

            {error && <p className="form-error">{error}</p>}

            <button className="button primary" type="submit" disabled={loading}>
              {loading ? "Logging in..." : "Login"}
            </button>
          </form>

          <div className="register-message">
            <span>New to GuideMesh?</span>
            <Link to="/register">Register first</Link>
          </div>
        </div>
      </main>
    </div>
  );
}

export default Login;
