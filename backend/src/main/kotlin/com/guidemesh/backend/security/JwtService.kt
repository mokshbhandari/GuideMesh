package com.guidemesh.backend.security

import com.guidemesh.backend.entity.User
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.nio.charset.StandardCharsets
import java.util.Date

@Service
class JwtService(
    @Value("\${app.security.jwt-secret:}") secret: String
) {

    private val key = run {
        val secretBytes = secret.toByteArray(StandardCharsets.UTF_8)

        require(secretBytes.size >= 32) {
            "Set app.security.jwt-secret in backend/application-local.properties. Use a random secret of at least 32 bytes."
        }

        Keys.hmacShaKeyFor(secretBytes)
    }

    fun generateToken(user: User): String {
        return Jwts.builder()
            .subject(user.id.toString())
            .claim("role", user.role.name)
            .claim("email", user.email)
            .claim("name", user.name)
            .issuedAt(Date())
            .expiration(
                Date(System.currentTimeMillis() + 86400000)
            )
            .signWith(key)
            .compact()
    }

    fun extractUserId(token: String): Long {
        return Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token)
            .payload
            .subject
            .toLong()
    }
}