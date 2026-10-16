import { Link } from "react-router-dom";
import Navbar from "../components/Navbar";

function Register() {
  return (
    <div className="auth-page">
      <Navbar />

      <main className="auth-container">
        <div className="form-card">
          <div className="form-heading">
            <p className="eyebrow">Create an account</p>
            <h1>Choose your role</h1>
            <p>Select the account type you want to create.</p>
          </div>

          <div className="role-cards">
            <Link to="/register/student" className="role-card">
              <strong>Student</strong>
              <p>
                Enroll in activities, create groups and manage invitations.
              </p>
              <span>Continue →</span>
            </Link>

            <Link to="/register/professor" className="role-card">
              <strong>Professor</strong>
              <p>
                Participate as a mentor and guide assigned project groups.
              </p>
              <span>Continue →</span>
            </Link>
          </div>

          <Link to="/login" className="back-link">
            ← Back to Login
          </Link>
        </div>
      </main>
    </div>
  );
}

export default Register;