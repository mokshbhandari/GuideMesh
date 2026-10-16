import { Link } from "react-router-dom";
import Navbar from "../components/Navbar";

function Home() {
  return (
    <div>
      <Navbar />

      <main>
        <section className="hero">
          <div>
            <p className="eyebrow">Academic Project Management</p>

            <h1>
              Project collaboration
              <br />
              made simpler.
            </h1>

            <p className="hero-text">
              GuideMesh provides a structured platform for students,
              professors and administrators to manage academic project
              activities and team formation.
            </p>

            <div className="buttons">
              <Link to="/login" className="button primary">
                Get Started
              </Link>

              <a href="#how-it-works" className="button secondary">
                Learn More
              </a>
            </div>
          </div>

          <div className="hero-box">
            <h3>How GuideMesh works</h3>

            <div className="step">
              <strong>01</strong>
              <div>
                <b>Enroll</b>
                <p>Join an eligible project activity.</p>
              </div>
            </div>

            <div className="step">
              <strong>02</strong>
              <div>
                <b>Form a group</b>
                <p>Invite teammates and confirm membership.</p>
              </div>
            </div>

            <div className="step">
              <strong>03</strong>
              <div>
                <b>Get a guide</b>
                <p>Receive a faculty mentor through allocation.</p>
              </div>
            </div>
          </div>
        </section>

        <section id="how-it-works" className="section">
          <div className="section-heading">
            <p className="eyebrow">How it works</p>
            <h2>A simple workflow for academic projects.</h2>
          </div>

          <div className="feature-grid">
            <div className="card">
              <span>01</span>
              <h3>Register</h3>
              <p>
                Students and professors create accounts according to their
                roles.
              </p>
            </div>

            <div className="card">
              <span>02</span>
              <h3>Participate</h3>
              <p>
                Students and professors enroll in activities relevant to them.
              </p>
            </div>

            <div className="card">
              <span>03</span>
              <h3>Collaborate</h3>
              <p>
                Students form groups and individually confirm their
                participation.
              </p>
            </div>

            <div className="card">
              <span>04</span>
              <h3>Get a Guide</h3>
              <p>
                Eligible project groups are assigned faculty mentors.
              </p>
            </div>
          </div>
        </section>

        <section className="role-section">
          <div className="section role-content">
            <div>
              <p className="eyebrow">One platform</p>
              <h2>Three roles, one workflow.</h2>
            </div>

            <div className="roles">
              <div>
                <b>Students</b>
                <span>Enroll, form groups and manage invitations.</span>
              </div>

              <div>
                <b>Professors</b>
                <span>Enroll as mentors and guide assigned groups.</span>
              </div>

              <div>
                <b>Administrators</b>
                <span>Manage activities and mentor allocation.</span>
              </div>
            </div>
          </div>
        </section>
      </main>

      <footer>
        <b>GuideMesh</b>
        <span>Academic Project Management Platform</span>
      </footer>
    </div>
  );
}

export default Home;