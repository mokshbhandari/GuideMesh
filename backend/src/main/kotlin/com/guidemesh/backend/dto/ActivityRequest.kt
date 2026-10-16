package com.guidemesh.backend.dto

import java.time.LocalDateTime

data class ActivityRequest(
    val title: String,
    val academicYearStart: Int,
    val year: Int,
    val department: String,
    val division: String,
    val minGroupSize: Int,
    val maxGroupSize: Int,
    val registrationClosesOn: LocalDateTime
)