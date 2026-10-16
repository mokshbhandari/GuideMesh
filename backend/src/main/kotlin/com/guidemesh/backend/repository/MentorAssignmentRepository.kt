package com.guidemesh.backend.repository

import com.guidemesh.backend.entity.MentorAssignment
import org.springframework.data.jpa.repository.JpaRepository

interface MentorAssignmentRepository : JpaRepository<MentorAssignment, Long> {

    fun findByGroupId(groupId: Long): MentorAssignment?

    fun findByProfessorId(professorId: Long): List<MentorAssignment>
}