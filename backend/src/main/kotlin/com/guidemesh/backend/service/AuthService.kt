package com.guidemesh.backend.service

import com.guidemesh.backend.dto.LoginRequest
import com.guidemesh.backend.dto.ProfessorRegistrationRequest
import com.guidemesh.backend.dto.StudentRegistrationRequest
import com.guidemesh.backend.entity.ProfessorProfile
import com.guidemesh.backend.entity.StudentProfile
import com.guidemesh.backend.entity.User
import com.guidemesh.backend.enums.Role
import com.guidemesh.backend.repository.ProfessorProfileRepository
import com.guidemesh.backend.repository.StudentProfileRepository
import com.guidemesh.backend.repository.UserRepository
import com.guidemesh.backend.security.JwtService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val studentProfileRepository: StudentProfileRepository,
    private val professorProfileRepository: ProfessorProfileRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtService: JwtService
) {

    @Transactional
    fun registerStudent(request: StudentRegistrationRequest): User {
        if (userRepository.findByEmail(request.email) != null) {
            throw IllegalArgumentException("Email is already registered")
        }

        val encodedPassword = passwordEncoder.encode(request.password)
            ?: throw IllegalStateException("Could not encode password")

        val user = User(
            name = request.name,
            email = request.email,
            password = encodedPassword,
            role = Role.STUDENT
        )

        val savedUser = userRepository.save(user)

        val studentProfile = StudentProfile(
            user = savedUser,
            rollNo = request.rollNo,
            moodleId = request.moodleId,
            department = request.department,
            division = request.division
        )

        studentProfileRepository.save(studentProfile)

        return savedUser
    }

    @Transactional
    fun registerProfessor(request: ProfessorRegistrationRequest): User {
        if (userRepository.findByEmail(request.email) != null) {
            throw IllegalArgumentException("Email is already registered")
        }

        val encodedPassword = passwordEncoder.encode(request.password)
            ?: throw IllegalStateException("Could not encode password")

        val user = User(
            name = request.name,
            email = request.email,
            password = encodedPassword,
            role = Role.PROFESSOR
        )

        val savedUser = userRepository.save(user)

        val professorProfile = ProfessorProfile(
            user = savedUser,
            department = request.department
        )

        professorProfileRepository.save(professorProfile)

        return savedUser
    }

    fun login(request: LoginRequest): String {
        val user = userRepository.findByEmail(request.email)
            ?: throw IllegalArgumentException("Invalid email or password")

        if (!passwordEncoder.matches(request.password, user.password)) {
            throw IllegalArgumentException("Invalid email or password")
        }

        return jwtService.generateToken(user)
    }
}