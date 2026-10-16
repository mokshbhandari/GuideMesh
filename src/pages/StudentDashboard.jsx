import { useEffect, useState } from "react";
import DashboardLayout from "../components/DashboardLayout";
import {
  createGroup,
  enrollInActivity,
  getEnrolledStudents,
  getStudentActivities,
  getStudentGroups,
  getStudentInvitations,
  inviteStudent,
  respondToInvitation,
  submitGroup,
} from "../api";

function StudentDashboard() {
  const [activities, setActivities] = useState([]);
  const [groups, setGroups] = useState([]);
  const [invitations, setInvitations] = useState([]);
  const [students, setStudents] = useState([]);
  const [selectedActivity, setSelectedActivity] = useState(null);
  const [selectedGroup, setSelectedGroup] = useState(null);
  const [groupName, setGroupName] = useState("");
  const [selectedStudent, setSelectedStudent] = useState("");
  const [message, setMessage] = useState("");
  const [error, setError] = useState("");

  async function loadData() {
    try {
      setError("");

      const [activityData, groupData, invitationData] = await Promise.all([
        getStudentActivities(),
        getStudentGroups(),
        getStudentInvitations(),
      ]);

      setActivities(activityData);
      setGroups(groupData);
      setInvitations(invitationData);
    } catch (exception) {
      setError(exception.message);
    }
  }

  useEffect(() => {
    loadData();
  }, []);

  function hasGroupForActivity(activityId) {
    return groups.some((group) => group.activityId === activityId);
  }

  function getCurrentUserId() {
    const token = localStorage.getItem("token");

    if (!token) {
      return null;
    }

    try {
      const payload = JSON.parse(
        atob(token.split(".")[1].replace(/-/g, "+").replace(/_/g, "/"))
      );

      return Number(payload.sub);
    } catch {
      return null;
    }
  }

  function isGroupLeader(group) {
    const currentUserId = getCurrentUserId();

    if (!currentUserId) {
      return false;
    }

    return group.members.some(
      (member) =>
        member.id === currentUserId &&
        member.role === "LEADER"
    );
  }

  function isActivityOpen(activity) {
    return activity?.status === "OPEN";
  }

  async function handleEnroll(activity) {
    try {
      await enrollInActivity(activity.id);
      setMessage("You are enrolled in the activity.");
      await loadData();
    } catch (exception) {
      setError(exception.message);
    }
  }

  async function handleCreateGroup(event) {
    event.preventDefault();

    if (!selectedActivity) {
      return;
    }

    if (!isActivityOpen(selectedActivity)) {
      setError("Registration for this activity is closed.");
      setSelectedActivity(null);
      return;
    }

    if (hasGroupForActivity(selectedActivity.id)) {
      setError("You are already a member of a group for this activity.");
      setSelectedActivity(null);
      return;
    }

    try {
      await createGroup(selectedActivity.id, groupName);

      setGroupName("");
      setSelectedActivity(null);
      setMessage("Group created successfully. You are the team leader.");

      await loadData();
    } catch (exception) {
      setError(exception.message);
    }
  }

  async function selectGroup(group) {
    if (!isGroupLeader(group)) {
      return;
    }

    const activity = activities.find(
      (item) => item.id === group.activityId
    );

    if (!isActivityOpen(activity)) {
      setError("Registration for this activity is closed.");
      return;
    }

    setSelectedGroup(group);
    setSelectedStudent("");
    setStudents([]);

    try {
      const data = await getEnrolledStudents(group.activityId);
      setStudents(data);
    } catch (exception) {
      setError(exception.message);
    }
  }

  async function handleInvite() {
    if (!selectedGroup || !selectedStudent) {
      return;
    }

    const activity = activities.find(
      (item) => item.id === selectedGroup.activityId
    );

    if (
      !isGroupLeader(selectedGroup) ||
      selectedGroup.status !== "DRAFT" ||
      !isActivityOpen(activity)
    ) {
      setError("You cannot invite students for this group.");
      return;
    }

    try {
      await inviteStudent(selectedGroup.id, selectedStudent);
      setMessage("Invitation sent.");
      await selectGroup(selectedGroup);
    } catch (exception) {
      setError(exception.message);
    }
  }

  async function handleSubmitGroup(groupId) {
    const group = groups.find((item) => item.id === groupId);

    if (!group) {
      return;
    }

    const activity = activities.find(
      (item) => item.id === group.activityId
    );

    if (
      !isGroupLeader(group) ||
      group.status !== "DRAFT" ||
      !isActivityOpen(activity)
    ) {
      setError("This group cannot be submitted.");
      return;
    }

    try {
      await submitGroup(groupId);

      setMessage("Group submitted successfully.");
      setSelectedGroup(null);

      await loadData();
    } catch (exception) {
      setError(exception.message);
    }
  }

  async function handleInvitation(id, accept) {
    try {
      await respondToInvitation(id, accept);

      setMessage(
        accept
          ? "Invitation accepted."
          : "Invitation rejected."
      );

      await loadData();
    } catch (exception) {
      setError(exception.message);
    }
  }

  return (
    <DashboardLayout role="Student">
      <div className="page-heading">
        <p className="eyebrow">Student dashboard</p>
        <h1>Welcome back.</h1>
        <p>Manage your activities, group and invitations.</p>
      </div>

      {message && <div className="success-message">{message}</div>}
      {error && <div className="form-error">{error}</div>}

      <section id="activities" className="dashboard-section">
        <h2>Available Activities</h2>

        <p className="section-description">
          Only activities matching your year, department and division are shown.
        </p>

        {activities.length === 0 ? (
          <div className="empty-card">
            <h3>No activities available</h3>
            <p>No eligible project activity is currently open.</p>
          </div>
        ) : (
          <div className="data-grid">
            {activities.map((activity) => {
              const alreadyInGroup = hasGroupForActivity(activity.id);
              const activityOpen = isActivityOpen(activity);

              return (
                <div className="data-card" key={activity.id}>
                  <h3>{activity.title}</h3>

                  <p>
                    Year {activity.year} · {activity.department} · Division{" "}
                    {activity.division}
                  </p>

                  <p>
                    Group size: {activity.minGroupSize}–
                    {activity.maxGroupSize}
                  </p>

                  <p>
                    Closes:{" "}
                    {new Date(
                      activity.registrationClosesOn
                    ).toLocaleString()}
                  </p>

                  {!activityOpen && (
                    <p className="section-description">
                      Registration closed.
                    </p>
                  )}

                  {activity.enrolled ? (
                    <button
                      className="button secondary"
                      disabled
                    >
                      Enrolled
                    </button>
                  ) : (
                    <button
                      className="button primary"
                      disabled={!activityOpen}
                      onClick={() => handleEnroll(activity)}
                    >
                      {activityOpen ? "Enroll" : "Registration Closed"}
                    </button>
                  )}

                  {activity.enrolled && (
                    <button
                      className="button secondary"
                      disabled={
                        alreadyInGroup || !activityOpen
                      }
                      onClick={() => {
                        if (!alreadyInGroup && activityOpen) {
                          setSelectedActivity(activity);
                        }
                      }}
                    >
                      {alreadyInGroup
                        ? "Already in a Group"
                        : !activityOpen
                        ? "Registration Closed"
                        : "Create Group"}
                    </button>
                  )}
                </div>
              );
            })}
          </div>
        )}

        {selectedActivity && (
          <div className="inline-form">
            <h3>
              Create group for {selectedActivity.title}
            </h3>

            <form
              onSubmit={handleCreateGroup}
              className="form-row"
            >
              <input
                value={groupName}
                onChange={(event) =>
                  setGroupName(event.target.value)
                }
                placeholder="Group name"
                required
              />

              <button
                className="button primary"
                type="submit"
              >
                Create Group
              </button>

              <button
                className="button secondary"
                type="button"
                onClick={() => setSelectedActivity(null)}
              >
                Cancel
              </button>
            </form>
          </div>
        )}
      </section>

      <section id="group" className="dashboard-section">
        <h2>My Group</h2>

        <p className="section-description">
          Only the group leader can invite students and submit the group while
          registration is open.
        </p>

        {groups.length === 0 ? (
          <div className="empty-card">
            <h3>No group</h3>
            <p>Create a group after enrolling in an activity.</p>
          </div>
        ) : (
          <div className="data-grid">
            {groups.map((group) => {
              const activity = activities.find(
                (item) => item.id === group.activityId
              );

              const minimumSize = activity?.minGroupSize ?? 0;
              const leader = isGroupLeader(group);
              const activityOpen = isActivityOpen(activity);

              const canManageGroup =
                leader &&
                group.status === "DRAFT" &&
                activityOpen;

              return (
                <div className="data-card" key={group.id}>
                  <h3>{group.name}</h3>

                  <p>
                    Code: <strong>{group.groupCode}</strong>
                  </p>

                  <p>{group.activityTitle}</p>

                  <p>Status: {group.status}</p>

                  <p>Leader: {group.leaderName}</p>

                  {!activityOpen && (
                    <p className="section-description">
                      Registration for this activity is closed.
                    </p>
                  )}

                  <div className="member-list">
                    {group.members.map((member) => (
                      <span key={member.id}>
                        {member.name} ({member.role})
                      </span>
                    ))}
                  </div>

                  {canManageGroup && (
                    <div className="card-actions">
                      <button
                        className="button secondary"
                        onClick={() => selectGroup(group)}
                      >
                        Invite Student
                      </button>

                      <button
                        className="button primary"
                        disabled={
                          group.members.length < minimumSize
                        }
                        onClick={() =>
                          handleSubmitGroup(group.id)
                        }
                      >
                        Submit Group
                      </button>
                    </div>
                  )}

                  {group.status === "DRAFT" &&
                    leader &&
                    activityOpen &&
                    group.members.length < minimumSize && (
                      <p className="section-description">
                        Add{" "}
                        {minimumSize - group.members.length} more
                        member
                        {minimumSize - group.members.length === 1
                          ? ""
                          : "s"}{" "}
                        before submitting the group.
                      </p>
                    )}

                  {group.status === "DRAFT" &&
                    !leader &&
                    activityOpen && (
                      <p className="section-description">
                        You are a team member. Only the group leader can
                        invite students or submit the group.
                      </p>
                    )}

                  {group.mentorName && (
                    <p className="mentor-result">
                      Guide: <strong>{group.mentorName}</strong>
                    </p>
                  )}
                </div>
              );
            })}
          </div>
        )}

        {selectedGroup && (
          <div className="inline-form">
            <h3>Invite an enrolled student</h3>

            {students.length === 0 ? (
              <p>No other enrolled students are available.</p>
            ) : (
              <div className="form-row">
                <select
                  value={selectedStudent}
                  onChange={(event) =>
                    setSelectedStudent(event.target.value)
                  }
                >
                  <option value="">Select student</option>

                  {students.map((student) => (
                    <option
                      key={student.id}
                      value={student.id}
                    >
                      {student.name} — {student.email}
                    </option>
                  ))}
                </select>

                <button
                  className="button primary"
                  onClick={handleInvite}
                >
                  Send Invitation
                </button>

                <button
                  className="button secondary"
                  onClick={() => setSelectedGroup(null)}
                >
                  Close
                </button>
              </div>
            )}
          </div>
        )}
      </section>

      <section id="invitations" className="dashboard-section">
        <h2>Invitations</h2>

        <p className="section-description">
          Accept or reject invitations from other student group leaders.
        </p>

        {invitations.length === 0 ? (
          <div className="empty-card">
            <p>No pending invitations.</p>
          </div>
        ) : (
          <div className="data-grid">
            {invitations.map((invitation) => (
              <div className="data-card" key={invitation.id}>
                <h3>{invitation.groupName}</h3>

                <p>{invitation.activityTitle}</p>

                <div className="card-actions">
                  <button
                    className="button primary"
                    onClick={() =>
                      handleInvitation(invitation.id, true)
                    }
                  >
                    Accept
                  </button>

                  <button
                    className="button secondary"
                    onClick={() =>
                      handleInvitation(invitation.id, false)
                    }
                  >
                    Reject
                  </button>
                </div>
              </div>
            ))}
          </div>
        )}
      </section>
    </DashboardLayout>
  );
}

export default StudentDashboard;