package com.guidemesh.backend.repository

import com.guidemesh.backend.entity.StudentProfile
import org.springframework.data.jpa.repository.JpaRepository

interface StudentProfileRepository : JpaRepository<StudentProfile, Long> {

    fun findByUserId(userId: Long): StudentProfile?
}