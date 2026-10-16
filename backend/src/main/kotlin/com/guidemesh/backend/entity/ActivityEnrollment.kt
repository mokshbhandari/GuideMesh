package com.guidemesh.backend.entity

import com.guidemesh.backend.enums.EnrollmentStatus
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(
    name = "activity_enrollments",
    uniqueConstraints = [
        UniqueConstraint(
            columnNames = ["activity_id", "user_id"]
        )
    ]
)
class ActivityEnrollment(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne
    @JoinColumn(name = "activity_id", nullable = false)
    val activity: Activity,

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: EnrollmentStatus = EnrollmentStatus.ENROLLED,

    @Column(nullable = false)
    val enrolledAt: LocalDateTime = LocalDateTime.now()
)