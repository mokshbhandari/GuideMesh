package com.guidemesh.backend.dto

data class StudentRegistrationRequest(
    val name: String,
    val rollNo: String,
    val moodleId: String,
    val department: String,
    val division: String,
    val email: String,
    val password: String
)

data class ProfessorRegistrationRequest(
    val name: String,
    val department: String,
    val email: String,
    val password: String
)

data class LoginRequest(
    val email: String,
    val password: String
)