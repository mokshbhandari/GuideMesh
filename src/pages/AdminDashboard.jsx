import { useEffect, useState } from "react";
import DashboardLayout from "../components/DashboardLayout";
import {
  allocateMentors,
  closeActivity,
  createActivity,
  getActivityGroups,
  getAdminActivities,
} from "../api";

const emptyForm = {
  title: "",
  academicYearStart: "2026",
  year: "3",
  department: "CSE-AIML",
  division: "A",
  minGroupSize: "2",
  maxGroupSize: "4",
  registrationClosesOn: "",
};

function AdminDashboard() {
  const [activities, setActivities] = useState([]);
  const [groups, setGroups] = useState([]);
  const [selectedActivity, setSelectedActivity] =
    useState("");
  const [form, setForm] = useState(emptyForm);
  const [showForm, setShowForm] = useState(false);
  const [message, setMessage] = useState("");
  const [error, setError] = useState("");

  async function loadActivities() {
    try {
      setError("");

      const data =
        await getAdminActivities();

      setActivities(data);
    } catch (exception) {
      setError(exception.message);
    }
  }

  useEffect(() => {
    loadActivities();
  }, []);

  async function handleCreate(event) {
    event.preventDefault();
    setError("");

    try {
      await createActivity({
        ...form,
        academicYearStart:
          Number(form.academicYearStart),
        year: Number(form.year),
        minGroupSize:
          Number(form.minGroupSize),
        maxGroupSize:
          Number(form.maxGroupSize),
        registrationClosesOn:
          form.registrationClosesOn,
      });

      setForm(emptyForm);
      setShowForm(false);

      setMessage(
        "Activity created and opened for registration."
      );

      await loadActivities();
    } catch (exception) {
      setError(exception.message);
    }
  }

  async function selectActivity(
    activityId
  ) {
    setSelectedActivity(activityId);
    setGroups([]);

    if (!activityId) {
      return;
    }

    try {
      setError("");

      setGroups(
        await getActivityGroups(
          activityId
        )
      );
    } catch (exception) {
      setError(exception.message);
    }
  }

  async function handleCloseActivity(
    activityId
  ) {
    try {
      setError("");

      await closeActivity(
        activityId
      );

      setMessage(
        "Registration closed successfully."
      );

      await loadActivities();

      if (
        selectedActivity === activityId
      ) {
        await selectActivity(
          activityId
        );
      }
    } catch (exception) {
      setError(exception.message);
    }
  }

  async function handleAllocation() {
    if (!selectedActivity) {
      setError(
        "Select an activity first."
      );

      return;
    }

    try {
      setError("");

      const result =
        await allocateMentors(
          selectedActivity
        );

      setMessage(
        `${result.length} group(s) allocated successfully.`
      );

      await loadActivities();

      await selectActivity(
        selectedActivity
      );
    } catch (exception) {
      setError(exception.message);
    }
  }

  return (
    <DashboardLayout role="Admin">
      <div className="page-heading">
        <p className="eyebrow">
          Administrator dashboard
        </p>

        <h1>
          Manage GuideMesh.
        </h1>

        <p>
          Create activities, monitor groups
          and manage mentor allocation.
        </p>
      </div>

      {message && (
        <div className="success-message">
          {message}
        </div>
      )}

      {error && (
        <div className="form-error">
          {error}
        </div>
      )}

      <section
        id="activities"
        className="dashboard-section"
      >
        <div className="section-title">
          <div>
            <h2>Activities</h2>

            <p className="section-description">
              Create and manage project
              activities.
            </p>
          </div>

          <button
            className="button primary"
            onClick={() =>
              setShowForm(!showForm)
            }
          >
            {showForm
              ? "Close Form"
              : "Create Activity"}
          </button>
        </div>

        {showForm && (
          <form
            onSubmit={handleCreate}
            className="form-card compact-card"
          >
            <div className="form-grid">
              <label>
                Title

                <input
                  value={form.title}
                  onChange={(e) =>
                    setForm({
                      ...form,
                      title: e.target.value,
                    })
                  }
                  placeholder="Mini Project Activity"
                  required
                />
              </label>

              <label>
                Academic Year Start

                <input
                  type="number"
                  value={
                    form.academicYearStart
                  }
                  onChange={(e) =>
                    setForm({
                      ...form,
                      academicYearStart:
                        e.target.value,
                    })
                  }
                  required
                />
              </label>

              <label>
                Student Year

                <input
                  type="number"
                  min="1"
                  max="6"
                  value={form.year}
                  onChange={(e) =>
                    setForm({
                      ...form,
                      year: e.target.value,
                    })
                  }
                  required
                />
              </label>

              <label>
                Department

                <select
                  value={
                    form.department
                  }
                  onChange={(e) =>
                    setForm({
                      ...form,
                      department:
                        e.target.value,
                    })
                  }
                >
                  <option value="CSE-AIML">
                    CSE (AIML)
                  </option>

                  <option value="CSE">
                    Computer Engineering
                  </option>

                  <option value="IT">
                    Information Technology
                  </option>

                  <option value="DS">
                    Data Science
                  </option>

                  <option value="MECH">
                    Mechanical Engineering
                  </option>

                  <option value="CIVIL">
                    Civil Engineering
                  </option>
                </select>
              </label>

              <label>
                Division

                <select
                  value={
                    form.division
                  }
                  onChange={(e) =>
                    setForm({
                      ...form,
                      division:
                        e.target.value,
                    })
                  }
                >
                  <option>A</option>
                  <option>B</option>
                  <option>C</option>
                </select>
              </label>

              <label>
                Minimum Group Size

                <input
                  type="number"
                  min="1"
                  value={
                    form.minGroupSize
                  }
                  onChange={(e) =>
                    setForm({
                      ...form,
                      minGroupSize:
                        e.target.value,
                    })
                  }
                  required
                />
              </label>

              <label>
                Maximum Group Size

                <input
                  type="number"
                  min="1"
                  value={
                    form.maxGroupSize
                  }
                  onChange={(e) =>
                    setForm({
                      ...form,
                      maxGroupSize:
                        e.target.value,
                    })
                  }
                  required
                />
              </label>

              <label>
                Registration Closes

                <input
                  type="datetime-local"
                  value={
                    form.registrationClosesOn
                  }
                  onChange={(e) =>
                    setForm({
                      ...form,
                      registrationClosesOn:
                        e.target.value,
                    })
                  }
                  required
                />
              </label>
            </div>

            <button
              className="button primary"
              type="submit"
            >
              Create Activity
            </button>
          </form>
        )}

        {activities.length === 0 ? (
          <div className="empty-card">
            <h3>
              No activities created
            </h3>

            <p>
              Create the first project
              activity above.
            </p>
          </div>
        ) : (
          <div className="data-grid">
            {activities.map(
              (activity) => (
                <div
                  className="data-card"
                  key={activity.id}
                >
                  <h3>
                    {activity.title}
                  </h3>

                  <p>
                    Year {activity.year} ·{" "}
                    {activity.department} ·
                    Division{" "}
                    {activity.division}
                  </p>

                  <p>
                    Group size:{" "}
                    {activity.minGroupSize}–
                    {activity.maxGroupSize}
                  </p>

                  <p>
                    Closes:{" "}
                    {new Date(
                      activity.registrationClosesOn
                    ).toLocaleString()}
                  </p>

                  <p>
                    Status:{" "}
                    <strong>
                      {activity.status}
                    </strong>
                  </p>

                  <div className="card-actions">
                    <button
                      className="button secondary"
                      onClick={() =>
                        selectActivity(
                          activity.id
                        )
                      }
                    >
                      View Groups
                    </button>

                    {activity.status ===
                      "OPEN" && (
                      <button
                        className="button secondary"
                        onClick={() =>
                          handleCloseActivity(
                            activity.id
                          )
                        }
                      >
                        Close Registration
                      </button>
                    )}
                  </div>
                </div>
              )
            )}
          </div>
        )}
      </section>

      <section
        id="groups"
        className="dashboard-section"
      >
        <h2>Groups</h2>

        <p className="section-description">
          Select an activity to view its
          submitted groups and members.
        </p>

        {selectedActivity &&
        groups.length === 0 ? (
          <div className="empty-card">
            <p>
              No groups have been created
              for this activity yet.
            </p>
          </div>
        ) : selectedActivity ? (
          <div className="data-grid">
            {groups.map((group) => (
              <div
                className="data-card"
                key={group.id}
              >
                <h3>
                  {group.name}
                </h3>

                <p>
                  Code:{" "}
                  <strong>
                    {group.groupCode}
                  </strong>
                </p>

                <p>
                  Status: {group.status}
                </p>

                <p>
                  Leader:{" "}
                  {group.leaderName}
                </p>

                <div className="member-list">
                  {group.members.map(
                    (member) => (
                      <span
                        key={member.id}
                      >
                        {member.name} (
                        {member.role})
                      </span>
                    )
                  )}
                </div>

                {group.mentorName && (
                  <p className="mentor-result">
                    Guide:{" "}
                    <strong>
                      {group.mentorName}
                    </strong>
                  </p>
                )}
              </div>
            ))}
          </div>
        ) : (
          <div className="empty-card">
            <p>
              Select an activity above.
            </p>
          </div>
        )}
      </section>

      <section
        id="allocation"
        className="dashboard-section"
      >
        <div className="section-title">
          <div>
            <h2>
              Mentor Allocation
            </h2>

            <p className="section-description">
              Close registration first,
              then run mentor allocation.
            </p>
          </div>

          <button
            className="button primary"
            onClick={
              handleAllocation
            }
            disabled={
              !selectedActivity
            }
          >
            Run Allocation
          </button>
        </div>

        <div className="empty-card">
          <p>
            Select the activity, close
            registration, then run
            allocation.
          </p>
        </div>
      </section>
    </DashboardLayout>
  );
}

export default AdminDashboard;