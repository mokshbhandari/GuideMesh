package com.guidemesh.backend.dto

import com.guidemesh.backend.enums.ActivityStatus
import com.guidemesh.backend.enums.GroupStatus
import com.guidemesh.backend.enums.InvitationStatus
import java.time.LocalDateTime

data class ActivityResponse(
    val id: Long,
    val title: String,
    val academicYearStart: Int,
    val year: Int,
    val department: String,
    val division: String,
    val minGroupSize: Int,
    val maxGroupSize: Int,
    val registrationClosesOn: LocalDateTime,
    val status: ActivityStatus,
    val enrolled: Boolean
)

data class UserSummary(
    val id: Long,
    val name: String,
    val email: String
)

data class GroupMemberResponse(
    val id: Long,
    val name: String,
    val email: String,
    val role: String
)

data class GroupResponse(
    val id: Long,
    val name: String,
    val groupCode: String,
    val activityId: Long,
    val activityTitle: String,
    val status: GroupStatus,
    val leaderId: Long,
    val leaderName: String,
    val members: List<GroupMemberResponse>,
    val mentorName: String?
)

data class InvitationResponse(
    val id: Long,
    val groupId: Long,
    val groupName: String,
    val activityTitle: String,
    val status: InvitationStatus
)

data class MentorAssignmentResponse(
    val groupId: Long,
    val groupName: String,
    val groupCode: String,
    val activityTitle: String,
    val professorName: String
)
