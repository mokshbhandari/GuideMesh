package com.guidemesh.backend

import com.guidemesh.backend.entity.User
import com.guidemesh.backend.enums.Role
import com.guidemesh.backend.repository.UserRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.security.crypto.password.PasswordEncoder

@SpringBootApplication
class BackendApplication {

    @Bean
    fun createAdmin(
        userRepository: UserRepository,
        passwordEncoder: PasswordEncoder,
        @Value("\${app.admin.password:}") adminPassword: String
    ): CommandLineRunner {
        return CommandLineRunner {
            if (userRepository.findByEmail("admin@guidemesh.com") == null) {

                require(adminPassword.isNotBlank()) {
                    "Set app.admin.password in backend/application-local.properties before creating the initial admin account."
                }

                val encodedPassword = requireNotNull(
                    passwordEncoder.encode(adminPassword)
                ) {
                    "Password encoding failed."
                }

                val admin = User(
                    name = "GuideMesh Admin",
                    email = "admin@guidemesh.com",
                    password = encodedPassword,
                    role = Role.ADMIN
                )

                userRepository.save(admin)
            }
        }
    }
}

fun main(args: Array<String>) {
    runApplication<BackendApplication>(*args)
}