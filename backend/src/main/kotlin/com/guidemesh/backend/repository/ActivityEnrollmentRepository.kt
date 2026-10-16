package com.guidemesh.backend.repository

import com.guidemesh.backend.entity.ActivityEnrollment
import org.springframework.data.jpa.repository.JpaRepository

interface ActivityEnrollmentRepository : JpaRepository<ActivityEnrollment, Long> {

    fun findByActivityId(activityId: Long): List<ActivityEnrollment>

    fun findByUserId(userId: Long): List<ActivityEnrollment>

    fun findByActivityIdAndUserId(
        activityId: Long,
        userId: Long
    ): ActivityEnrollment?
}