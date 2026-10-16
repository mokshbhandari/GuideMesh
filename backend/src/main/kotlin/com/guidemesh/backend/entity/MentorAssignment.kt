package com.guidemesh.backend.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(
    name = "mentor_assignments",
    uniqueConstraints = [
        UniqueConstraint(
            columnNames = ["group_id"]
        )
    ]
)
class MentorAssignment(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @OneToOne
    @JoinColumn(name = "group_id", nullable = false, unique = true)
    val group: ProjectGroup,

    @ManyToOne
    @JoinColumn(name = "professor_id", nullable = false)
    val professor: User,

    @Column(nullable = false)
    val assignedAt: LocalDateTime = LocalDateTime.now()
)