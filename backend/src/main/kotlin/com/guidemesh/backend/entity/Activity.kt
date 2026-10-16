package com.guidemesh.backend.entity

import com.guidemesh.backend.enums.ActivityStatus
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "activities")
class Activity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false)
    var title: String,

    @Column(nullable = false)
    var academicYearStart: Int,

    @Column(nullable = false)
    var year: Int,

    @Column(nullable = false)
    var department: String,

    @Column(nullable = false)
    var division: String,

    @Column(nullable = false)
    var minGroupSize: Int,

    @Column(nullable = false)
    var maxGroupSize: Int,

    @Column(nullable = false)
    var registrationClosesOn: LocalDateTime,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: ActivityStatus = ActivityStatus.DRAFT,

    @ManyToOne
    @JoinColumn(name = "created_by", nullable = false)
    val createdBy: User
)