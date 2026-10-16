import { Link, useNavigate } from "react-router-dom";
import { useState } from "react";
import Navbar from "../components/Navbar";
import { registerStudent } from "../api";

function StudentRegistration() {
  const navigate = useNavigate();

  const [form, setForm] = useState({
    name: "",
    rollNo: "",
    moodleId: "",
    department: "",
    division: "",
    email: "",
    password: "",
    confirmPassword: "",
  });

  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  function handleChange(event) {
    setForm({
      ...form,
      [event.target.name]: event.target.value,
    });
  }

  async function handleSubmit(event) {
    event.preventDefault();
    setError("");

    if (form.password !== form.confirmPassword) {
      setError("Passwords do not match.");
      return;
    }

    setLoading(true);

    try {
      await registerStudent({
        name: form.name,
        rollNo: form.rollNo,
        moodleId: form.moodleId,
        department: form.department,
        division: form.division,
        email: form.email,
        password: form.password,
      });

      navigate("/login");
    } catch (exception) {
      setError(exception.message);
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="auth-page">
      <Navbar />

      <main className="auth-container wide">
        <div className="form-card">
          <div className="form-heading">
            <p className="eyebrow">Student registration</p>

            <h1>Create your student account</h1>

            <p>
              Enter your academic and account information.
            </p>
          </div>

          <form onSubmit={handleSubmit} className="form">
            <div className="form-grid">

              <label>
                Full Name
                <input
                  name="name"
                  value={form.name}
                  onChange={handleChange}
                  placeholder="Full name"
                  required
                />
              </label>

              <label>
                Roll No
                <input
                  name="rollNo"
                  value={form.rollNo}
                  onChange={handleChange}
                  placeholder="Roll number"
                  required
                />
              </label>

              <label>
                Moodle ID
                <input
                  name="moodleId"
                  value={form.moodleId}
                  onChange={handleChange}
                  placeholder="Moodle ID"
                  required
                />
              </label>

              <label>
                Department

                <select
                  name="department"
                  value={form.department}
                  onChange={handleChange}
                  required
                >
                  <option value="">Select department</option>
                  <option value="CSE-AIML">CSE (AIML)</option>
                  <option value="CSE">Computer Engineering</option>
                  <option value="IT">Information Technology</option>
                  <option value="DS">Data Science</option>
                  <option value="MECH">Mechanical Engineering</option>
                  <option value="CIVIL">Civil Engineering</option>
                </select>
              </label>

              <label>
                Division

                <select
                  name="division"
                  value={form.division}
                  onChange={handleChange}
                  required
                >
                  <option value="">Select division</option>
                  <option value="A">A</option>
                  <option value="B">B</option>
                  <option value="C">C</option>
                </select>
              </label>

              <label>
                Email
                <input
                  type="email"
                  name="email"
                  value={form.email}
                  onChange={handleChange}
                  placeholder="Email address"
                  required
                />
              </label>

              <label>
                Password
                <input
                  type="password"
                  name="password"
                  value={form.password}
                  onChange={handleChange}
                  placeholder="Password"
                  required
                />
              </label>

              <label>
                Confirm Password
                <input
                  type="password"
                  name="confirmPassword"
                  value={form.confirmPassword}
                  onChange={handleChange}
                  placeholder="Confirm password"
                  required
                />
              </label>

            </div>

            {error && (
              <p className="form-error">
                {error}
              </p>
            )}

            <button
              className="button primary"
              type="submit"
              disabled={loading}
            >
              {loading
                ? "Creating account..."
                : "Create Student Account"}
            </button>
          </form>

          <Link to="/register" className="back-link">
            ← Choose another account type
          </Link>
        </div>
      </main>
    </div>
  );
}

export default StudentRegistration;