package com.guidemesh.backend.entity

import com.guidemesh.backend.enums.GroupStatus
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(
    name = "project_groups",
    uniqueConstraints = [
        UniqueConstraint(columnNames = ["group_code"])
    ]
)
class ProjectGroup(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne
    @JoinColumn(name = "activity_id", nullable = false)
    val activity: Activity,

    @Column(nullable = false)
    var name: String,

    @Column(name = "group_code", nullable = false, unique = true)
    var groupCode: String,

    @ManyToOne
    @JoinColumn(name = "leader_id", nullable = false)
    val leader: User,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: GroupStatus = GroupStatus.DRAFT,

    @Column(nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now()
)