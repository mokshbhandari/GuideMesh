package com.guidemesh.backend.entity

import jakarta.persistence.*

@Entity
@Table(name = "student_profiles")
class StudentProfile(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    val user: User,

    @Column(nullable = false, unique = true)
    var rollNo: String,

    @Column(nullable = false, unique = true)
    var moodleId: String,

    @Column(nullable = false)
    var department: String,

    @Column(nullable = false)
    var division: String
)