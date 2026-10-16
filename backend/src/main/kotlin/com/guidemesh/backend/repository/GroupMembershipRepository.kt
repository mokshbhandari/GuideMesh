package com.guidemesh.backend.repository

import com.guidemesh.backend.entity.GroupMembership
import org.springframework.data.jpa.repository.JpaRepository

interface GroupMembershipRepository : JpaRepository<GroupMembership, Long> {

    fun findByGroupId(groupId: Long): List<GroupMembership>

    fun findByStudentId(studentId: Long): List<GroupMembership>
}