package com.guidemesh.backend.repository

import com.guidemesh.backend.entity.Activity
import com.guidemesh.backend.enums.ActivityStatus
import org.springframework.data.jpa.repository.JpaRepository

interface ActivityRepository : JpaRepository<Activity, Long> {

    fun findByStatus(status: ActivityStatus): List<Activity>
}