package com.guidemesh.backend.enums

enum class Role {
    STUDENT,
    PROFESSOR,
    ADMIN
}

enum class ActivityStatus {
    DRAFT,
    OPEN,
    CLOSED,
    ALLOCATED
}

enum class EnrollmentStatus {
    ENROLLED,
    CANCELLED
}

enum class GroupStatus {
    DRAFT,
    SUBMITTED,
    ALLOCATED
}

enum class InvitationStatus {
    PENDING,
    ACCEPTED,
    REJECTED
}

enum class MembershipRole {
    LEADER,
    MEMBER
}