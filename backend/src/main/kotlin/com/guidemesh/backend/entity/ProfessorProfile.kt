package com.guidemesh.backend.entity

import jakarta.persistence.*

@Entity
@Table(name = "professor_profiles")
class ProfessorProfile(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    val user: User,

    @Column(nullable = false)
    var department: String
)