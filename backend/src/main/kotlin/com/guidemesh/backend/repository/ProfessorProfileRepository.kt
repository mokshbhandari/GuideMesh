package com.guidemesh.backend.repository

import com.guidemesh.backend.entity.ProfessorProfile
import org.springframework.data.jpa.repository.JpaRepository

interface ProfessorProfileRepository : JpaRepository<ProfessorProfile, Long> {

    fun findByUserId(userId: Long): ProfessorProfile?
}