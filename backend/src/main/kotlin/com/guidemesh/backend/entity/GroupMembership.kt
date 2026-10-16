package com.guidemesh.backend.entity

import com.guidemesh.backend.enums.MembershipRole
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(
    name = "group_memberships",
    uniqueConstraints = [
        UniqueConstraint(
            columnNames = ["group_id", "student_id"]
        )
    ]
)
class GroupMembership(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne
    @JoinColumn(name = "group_id", nullable = false)
    val group: ProjectGroup,

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    val student: User,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val role: MembershipRole,

    @Column(nullable = false)
    val joinedAt: LocalDateTime = LocalDateTime.now()
)