package com.guidemesh.backend.controller

import com.guidemesh.backend.dto.ActivityRequest
import com.guidemesh.backend.entity.User
import com.guidemesh.backend.service.GuideMeshService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api")
class GuideMeshController(
    private val guideMeshService: GuideMeshService
) {

    private fun currentUser(): User {

        val authentication =
            SecurityContextHolder
                .getContext()
                .authentication
                ?: throw IllegalStateException(
                    "User is not authenticated"
                )

        return authentication.principal as? User
            ?: throw IllegalStateException(
                "Authenticated user not found"
            )
    }

    private fun ok(
        body: Any
    ): ResponseEntity<Any> =
        ResponseEntity.ok(body)

    private fun created(
        body: Any
    ): ResponseEntity<Any> =
        ResponseEntity
            .status(HttpStatus.CREATED)
            .body(body)

    private fun badRequest(
        message: String
    ): ResponseEntity<Any> =
        ResponseEntity
            .badRequest()
            .body(
                mapOf(
                    "error" to message
                )
            )

    @PostMapping("/activities")
    fun createActivity(
        @RequestBody request: ActivityRequest
    ): ResponseEntity<Any> = try {

        created(
            guideMeshService.createActivity(
                request,
                currentUser()
            )
        )

    } catch (exception: IllegalArgumentException) {

        badRequest(
            exception.message
                ?: "Invalid request"
        )
    }

    @GetMapping("/activities")
    fun getActivities(): ResponseEntity<Any> = try {

        ok(
            guideMeshService
                .getOpenActivities()
                .map { activity ->

                    mapOf(
                        "id" to activity.id,
                        "title" to activity.title,
                        "academicYearStart" to
                            activity.academicYearStart,
                        "year" to activity.year,
                        "department" to
                            activity.department,
                        "division" to
                            activity.division,
                        "minGroupSize" to
                            activity.minGroupSize,
                        "maxGroupSize" to
                            activity.maxGroupSize,
                        "registrationClosesOn" to
                            activity.registrationClosesOn,
                        "status" to activity.status
                    )
                }
        )

    } catch (exception: IllegalArgumentException) {

        badRequest(
            exception.message
                ?: "Could not load activities"
        )
    }

    @GetMapping("/admin/activities")
    fun getAdminActivities(): ResponseEntity<Any> = try {

        ok(
            guideMeshService
                .getActivitiesForAdmin(
                    currentUser()
                )
        )

    } catch (exception: IllegalArgumentException) {

        badRequest(
            exception.message
                ?: "Could not load activities"
        )
    }

    @GetMapping("/student/activities")
    fun getEligibleActivities(): ResponseEntity<Any> = try {

        ok(
            guideMeshService
                .getEligibleActivities(
                    currentUser()
                )
        )

    } catch (exception: IllegalArgumentException) {

        badRequest(
            exception.message
                ?: "Could not load activities"
        )
    }

    @GetMapping("/professor/activities")
    fun getProfessorActivities(): ResponseEntity<Any> = try {

        ok(
            guideMeshService
                .getProfessorActivities(
                    currentUser()
                )
        )

    } catch (exception: IllegalArgumentException) {

        badRequest(
            exception.message
                ?: "Could not load activities"
        )
    }

    @GetMapping("/student/groups")
    fun getStudentGroups(): ResponseEntity<Any> = try {

        ok(
            guideMeshService
                .getStudentGroups(
                    currentUser()
                )
        )

    } catch (exception: IllegalArgumentException) {

        badRequest(
            exception.message
                ?: "Could not load groups"
        )
    }

    @GetMapping("/student/invitations")
    fun getStudentInvitations(): ResponseEntity<Any> = try {

        ok(
            guideMeshService
                .getPendingInvitations(
                    currentUser()
                )
        )

    } catch (exception: IllegalArgumentException) {

        badRequest(
            exception.message
                ?: "Could not load invitations"
        )
    }

    @GetMapping("/activities/{activityId}/students")
    fun getEnrolledStudents(
        @PathVariable activityId: Long
    ): ResponseEntity<Any> = try {

        ok(
            guideMeshService.getEnrolledStudents(
                currentUser(),
                activityId
            )
        )

    } catch (exception: IllegalArgumentException) {

        badRequest(
            exception.message
                ?: "Could not load students"
        )
    }

    @GetMapping("/professor/assignments")
    fun getProfessorAssignments(): ResponseEntity<Any> = try {

        ok(
            guideMeshService
                .getProfessorAssignments(
                    currentUser()
                )
        )

    } catch (exception: IllegalArgumentException) {

        badRequest(
            exception.message
                ?: "Could not load assignments"
        )
    }

    @GetMapping("/activities/{activityId}/groups")
    fun getActivityGroups(
        @PathVariable activityId: Long
    ): ResponseEntity<Any> = try {

        ok(
            guideMeshService.getActivityGroups(
                currentUser(),
                activityId
            )
        )

    } catch (exception: IllegalArgumentException) {

        badRequest(
            exception.message
                ?: "Could not load groups"
        )
    }

    @PostMapping("/activities/{activityId}/enroll")
    fun enrollStudent(
        @PathVariable activityId: Long
    ): ResponseEntity<Any> = try {

        ok(
            guideMeshService.enrollStudent(
                currentUser(),
                activityId
            )
        )

    } catch (exception: IllegalArgumentException) {

        badRequest(
            exception.message
                ?: "Enrollment failed"
        )
    }

    @PostMapping("/activities/{activityId}/mentor-enroll")
    fun enrollProfessor(
        @PathVariable activityId: Long
    ): ResponseEntity<Any> = try {

        ok(
            guideMeshService.enrollProfessor(
                currentUser(),
                activityId
            )
        )

    } catch (exception: IllegalArgumentException) {

        badRequest(
            exception.message
                ?: "Professor enrollment failed"
        )
    }

    @PostMapping("/activities/{activityId}/groups")
    fun createGroup(
        @PathVariable activityId: Long,
        @RequestParam(name = "groupName")
        groupName: String
    ): ResponseEntity<Any> = try {

        created(
            guideMeshService.createGroup(
                currentUser(),
                activityId,
                groupName
            )
        )

    } catch (exception: IllegalArgumentException) {

        badRequest(
            exception.message
                ?: "Group creation failed"
        )
    }

    @PostMapping("/groups/{groupId}/invite/{studentId}")
    fun inviteStudent(
        @PathVariable groupId: Long,
        @PathVariable studentId: Long
    ): ResponseEntity<Any> = try {

        ok(
            guideMeshService.inviteStudent(
                currentUser(),
                groupId,
                studentId
            )
        )

    } catch (exception: IllegalArgumentException) {

        badRequest(
            exception.message
                ?: "Invitation failed"
        )
    }

    @PostMapping("/invitations/{invitationId}/respond")
    fun respondToInvitation(
        @PathVariable invitationId: Long,
        @RequestParam(name = "accept")
        accept: Boolean
    ): ResponseEntity<Any> = try {

        guideMeshService.respondToInvitation(
            currentUser(),
            invitationId,
            accept
        )

        ok(
            mapOf(
                "message" to
                    "Invitation response recorded"
            )
        )

    } catch (exception: IllegalArgumentException) {

        badRequest(
            exception.message
                ?: "Invitation response failed"
        )
    }

    @PostMapping("/groups/{groupId}/submit")
    fun submitGroup(
        @PathVariable groupId: Long
    ): ResponseEntity<Any> = try {

        ok(
            guideMeshService.submitGroup(
                currentUser(),
                groupId
            )
        )

    } catch (exception: IllegalArgumentException) {

        badRequest(
            exception.message
                ?: "Group submission failed"
        )
    }

    @PostMapping("/admin/activities/{activityId}/close")
    fun closeActivity(
        @PathVariable activityId: Long
    ): ResponseEntity<Any> = try {

        ok(
            guideMeshService.closeActivity(
                currentUser(),
                activityId
            )
        )

    } catch (exception: IllegalArgumentException) {

        badRequest(
            exception.message
                ?: "Could not close registration"
        )
    }

    @PostMapping("/activities/{activityId}/allocate")
    fun allocateMentors(
        @PathVariable activityId: Long
    ): ResponseEntity<Any> = try {

        ok(
            guideMeshService.allocateMentors(
                currentUser(),
                activityId
            )
        )

    } catch (exception: IllegalArgumentException) {

        badRequest(
            exception.message
                ?: "Mentor allocation failed"
        )
    }
}