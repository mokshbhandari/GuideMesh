package com.guidemesh.backend.repository

import com.guidemesh.backend.entity.ProjectGroup
import org.springframework.data.jpa.repository.JpaRepository

interface ProjectGroupRepository : JpaRepository<ProjectGroup, Long> {

    fun findByActivityId(activityId: Long): List<ProjectGroup>

    fun findByLeaderId(leaderId: Long): List<ProjectGroup>
}