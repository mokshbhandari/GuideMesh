const API_URL = "http://localhost:8080/api";

async function request(path, options = {}) {
  const token = localStorage.getItem("token");

  const headers = {
    ...(options.body
      ? { "Content-Type": "application/json" }
      : {}),
    ...(token
      ? { Authorization: `Bearer ${token}` }
      : {}),
    ...(options.headers || {}),
  };

  const response = await fetch(
    `${API_URL}${path}`,
    {
      ...options,
      headers,
    }
  );

  const text = await response.text();

  let result = text;

  try {
    result = text
      ? JSON.parse(text)
      : null;
  } catch {
    // Keep plain-text response.
  }

  if (!response.ok) {

    const message =
      result?.error ||
      result?.message ||
      (typeof result === "string" && result) ||
      "Request failed";

    throw new Error(
      `${message}`
    );
  }

  return result;
}

export function registerStudent(data) {
  return request(
    "/auth/register/student",
    {
      method: "POST",
      body: JSON.stringify(data),
    }
  );
}

export function registerProfessor(data) {
  return request(
    "/auth/register/professor",
    {
      method: "POST",
      body: JSON.stringify(data),
    }
  );
}

export async function login(data) {

  const result =
    await request(
      "/auth/login",
      {
        method: "POST",
        body: JSON.stringify(data),
      }
    );

  localStorage.setItem(
    "token",
    result.token
  );

  return result.token;
}

export function logout() {
  localStorage.removeItem("token");
}

export function getStudentActivities() {
  return request(
    "/student/activities"
  );
}

export function enrollInActivity(
  activityId
) {
  return request(
    `/activities/${activityId}/enroll`,
    {
      method: "POST",
    }
  );
}

export function getProfessorActivities() {
  return request(
    "/professor/activities"
  );
}

export function enrollProfessor(
  activityId
) {
  return request(
    `/activities/${activityId}/mentor-enroll`,
    {
      method: "POST",
    }
  );
}

export function getStudentGroups() {
  return request(
    "/student/groups"
  );
}

export function getStudentInvitations() {
  return request(
    "/student/invitations"
  );
}

export function getEnrolledStudents(
  activityId
) {
  return request(
    `/activities/${activityId}/students`
  );
}

export function createGroup(
  activityId,
  groupName
) {
  return request(
    `/activities/${activityId}/groups?groupName=${encodeURIComponent(
      groupName
    )}`,
    {
      method: "POST",
    }
  );
}

export function inviteStudent(
  groupId,
  studentId
) {
  return request(
    `/groups/${groupId}/invite/${studentId}`,
    {
      method: "POST",
    }
  );
}

export function respondToInvitation(
  invitationId,
  accept
) {
  return request(
    `/invitations/${invitationId}/respond?accept=${accept}`,
    {
      method: "POST",
    }
  );
}

export function submitGroup(
  groupId
) {
  return request(
    `/groups/${groupId}/submit`,
    {
      method: "POST",
    }
  );
}

export function getProfessorAssignments() {
  return request(
    "/professor/assignments"
  );
}

export function getAdminActivities() {
  return request(
    "/admin/activities"
  );
}

export function createActivity(data) {
  return request(
    "/activities",
    {
      method: "POST",
      body: JSON.stringify(data),
    }
  );
}

export function getActivityGroups(
  activityId
) {
  return request(
    `/activities/${activityId}/groups`
  );
}

export function allocateMentors(
  activityId
) {
  return request(
    `/activities/${activityId}/allocate`,
    {
      method: "POST",
    }
  );
}
export function closeActivity(activityId) {
  return request(
    `/admin/activities/${activityId}/close`,
    {
      method: "POST",
    }
  );
}