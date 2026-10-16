package com.guidemesh.backend.controller

import com.guidemesh.backend.dto.LoginRequest
import com.guidemesh.backend.dto.ProfessorRegistrationRequest
import com.guidemesh.backend.dto.StudentRegistrationRequest
import com.guidemesh.backend.service.AuthService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val authService: AuthService
) {

    @PostMapping("/register/student")
    fun registerStudent(@RequestBody request: StudentRegistrationRequest): ResponseEntity<Any> = try {
        authService.registerStudent(request)
        ResponseEntity.status(HttpStatus.CREATED)
            .body(mapOf("message" to "Student registered successfully"))
    } catch (exception: IllegalArgumentException) {
        ResponseEntity.badRequest().body(mapOf("error" to (exception.message ?: "Registration failed")))
    }

    @PostMapping("/register/professor")
    fun registerProfessor(@RequestBody request: ProfessorRegistrationRequest): ResponseEntity<Any> = try {
        authService.registerProfessor(request)
        ResponseEntity.status(HttpStatus.CREATED)
            .body(mapOf("message" to "Professor registered successfully"))
    } catch (exception: IllegalArgumentException) {
        ResponseEntity.badRequest().body(mapOf("error" to (exception.message ?: "Registration failed")))
    }

    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest): ResponseEntity<Any> = try {
        ResponseEntity.ok(mapOf("token" to authService.login(request)))
    } catch (exception: IllegalArgumentException) {
        ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(mapOf("error" to (exception.message ?: "Invalid email or password")))
    }
}
