import { useEffect, useState } from "react";
import DashboardLayout from "../components/DashboardLayout";
import { enrollProfessor, getProfessorActivities, getProfessorAssignments } from "../api";

function ProfessorDashboard() {
  const [activities, setActivities] = useState([]);
  const [assignments, setAssignments] = useState([]);
  const [error, setError] = useState("");
  const [message, setMessage] = useState("");

  async function loadData() {
    try {
      const [activityData, assignmentData] = await Promise.all([
        getProfessorActivities(),
        getProfessorAssignments(),
      ]);
      setActivities(activityData);
      setAssignments(assignmentData);
    } catch (exception) {
      setError(exception.message);
    }
  }

  useEffect(() => { loadData(); }, []);

  async function handleEnroll(activityId) {
    try {
      await enrollProfessor(activityId);
      setMessage("You are now available as a guide for this activity.");
      await loadData();
    } catch (exception) {
      setError(exception.message);
    }
  }

  return (
    <DashboardLayout role="Professor">
      <div className="page-heading">
        <p className="eyebrow">Professor dashboard</p>
        <h1>Welcome back.</h1>
        <p>Manage activities and your mentoring assignments.</p>
      </div>

      {message && <div className="success-message">{message}</div>}
      {error && <div className="form-error">{error}</div>}

      <section id="activities" className="dashboard-section">
        <h2>Available Activities</h2>
        <p className="section-description">Activities from your department are shown here across all student years.</p>

        {activities.length === 0 ? (
          <div className="empty-card"><h3>No activities available</h3><p>No open activity is currently available for your department.</p></div>
        ) : (
          <div className="data-grid">
            {activities.map((activity) => (
              <div className="data-card" key={activity.id}>
                <h3>{activity.title}</h3>
                <p>Year {activity.year} · Division {activity.division}</p>
                <p>Closes: {new Date(activity.registrationClosesOn).toLocaleString()}</p>
                {activity.enrolled ? (
                  <button className="button secondary" disabled>Enrolled as Guide</button>
                ) : (
                  <button className="button primary" onClick={() => handleEnroll(activity.id)}>Enroll as Guide</button>
                )}
              </div>
            ))}
          </div>
        )}
      </section>

      <section id="mentoring" className="dashboard-section">
        <h2>My Mentoring</h2>
        <p className="section-description">Groups assigned to you after administrator allocation.</p>

        {assignments.length === 0 ? (
          <div className="empty-card"><h3>No groups assigned</h3><p>Your mentoring assignments will appear here after allocation.</p></div>
        ) : (
          <div className="data-grid">
            {assignments.map((assignment) => (
              <div className="data-card" key={assignment.groupId}>
                <h3>{assignment.groupName}</h3>
                <p>Code: <strong>{assignment.groupCode}</strong></p>
                <p>{assignment.activityTitle}</p>
                <p>Guide: {assignment.professorName}</p>
              </div>
            ))}
          </div>
        )}
      </section>
    </DashboardLayout>
  );
}

export default ProfessorDashboard;
