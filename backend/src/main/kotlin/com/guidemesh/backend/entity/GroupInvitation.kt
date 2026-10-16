package com.guidemesh.backend.entity

import com.guidemesh.backend.enums.InvitationStatus
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "group_invitations")
class GroupInvitation(

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
    var status: InvitationStatus = InvitationStatus.PENDING,

    @Column(nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    var respondedAt: LocalDateTime? = null
)