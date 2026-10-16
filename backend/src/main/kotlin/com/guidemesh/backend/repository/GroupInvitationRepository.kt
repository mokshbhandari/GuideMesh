package com.guidemesh.backend.repository

import com.guidemesh.backend.entity.GroupInvitation
import com.guidemesh.backend.enums.InvitationStatus
import org.springframework.data.jpa.repository.JpaRepository

interface GroupInvitationRepository : JpaRepository<GroupInvitation, Long> {

    fun findByStudentIdAndStatus(
        studentId: Long,
        status: InvitationStatus
    ): List<GroupInvitation>

    fun findByGroupId(groupId: Long): List<GroupInvitation>
}