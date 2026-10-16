package com.guidemesh.backend.service

import com.guidemesh.backend.dto.*
import com.guidemesh.backend.entity.*
import com.guidemesh.backend.enums.*
import com.guidemesh.backend.repository.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import kotlin.random.Random

@Service
class GuideMeshService(
    private val activityRepository: ActivityRepository,
    private val activityEnrollmentRepository: ActivityEnrollmentRepository,
    private val groupRepository: ProjectGroupRepository,
    private val invitationRepository: GroupInvitationRepository,
    private val membershipRepository: GroupMembershipRepository,
    private val mentorAssignmentRepository: MentorAssignmentRepository,
    private val userRepository: UserRepository,
    private val studentProfileRepository: StudentProfileRepository,
    private val professorProfileRepository: ProfessorProfileRepository
) {

    @Transactional
    fun createActivity(
        request: ActivityRequest,
        admin: User
    ): ActivityResponse {

        require(admin.role == Role.ADMIN) {
            "Only admin can create activities"
        }

        require(request.title.isNotBlank()) {
            "Activity title is required"
        }

        require(request.academicYearStart >= 2000) {
            "Invalid academic year"
        }

        require(request.year in 1..6) {
            "Invalid student year"
        }

        require(request.minGroupSize > 0) {
            "Minimum group size must be at least 1"
        }

        require(request.minGroupSize <= request.maxGroupSize) {
            "Minimum group size cannot be greater than maximum group size"
        }

        require(
            request.registrationClosesOn.isAfter(LocalDateTime.now())
        ) {
            "Registration closing time must be in the future"
        }

        val activity = activityRepository.save(
            Activity(
                title = request.title.trim(),
                academicYearStart = request.academicYearStart,
                year = request.year,
                department = request.department,
                division = request.division,
                minGroupSize = request.minGroupSize,
                maxGroupSize = request.maxGroupSize,
                registrationClosesOn = request.registrationClosesOn,
                status = ActivityStatus.OPEN,
                createdBy = admin
            )
        )

        return toActivityResponse(activity, admin)
    }

    @Transactional
    fun closeActivity(
        admin: User,
        activityId: Long
    ): ActivityResponse {

        require(admin.role == Role.ADMIN) {
            "Only admin can close registration"
        }

        val activity = getActivity(activityId)

        require(
            activity.status == ActivityStatus.OPEN
        ) {
            "Activity is not open"
        }

        activity.status = ActivityStatus.CLOSED

        activityRepository.save(activity)

        return toActivityResponse(
            activity,
            admin
        )
    }

    fun getActivitiesForAdmin(
        admin: User
    ): List<ActivityResponse> {

        require(admin.role == Role.ADMIN) {
            "Only admin can access activities"
        }

        closeExpiredActivities()

        return activityRepository
            .findAll()
            .map {
                toActivityResponse(it, admin)
            }
    }

    fun getEligibleActivities(
        student: User
    ): List<ActivityResponse> {

        require(student.role == Role.STUDENT) {
            "Only students can access this"
        }

        val profile =
            studentProfileRepository
                .findByUserId(student.id)
                ?: throw IllegalArgumentException(
                    "Student profile not found"
                )

        return getOpenActivities()
            .filter { activity ->

                val prefix =
                    profile.moodleId
                        .take(2)
                        .toIntOrNull()
                        ?: return@filter false

                val joiningYear =
                    2000 + prefix

                val expectedYear =
                    activity.academicYearStart -
                        joiningYear +
                        1

                expectedYear == activity.year &&
                    profile.department == activity.department &&
                    profile.division == activity.division
            }
            .map {
                toActivityResponse(it, student)
            }
    }

    fun getProfessorActivities(
        professor: User
    ): List<ActivityResponse> {

        require(professor.role == Role.PROFESSOR) {
            "Only professors can access this"
        }

        val profile =
            professorProfileRepository
                .findByUserId(professor.id)
                ?: throw IllegalArgumentException(
                    "Professor profile not found"
                )

        return getOpenActivities()
            .filter {
                it.department == profile.department
            }
            .map {
                toActivityResponse(it, professor)
            }
    }

    fun getOpenActivities(): List<Activity> {

        closeExpiredActivities()

        return activityRepository
            .findByStatus(ActivityStatus.OPEN)
            .filter {
                it.registrationClosesOn.isAfter(
                    LocalDateTime.now()
                )
            }
    }

    private fun closeExpiredActivities() {

        val now = LocalDateTime.now()

        activityRepository
            .findByStatus(ActivityStatus.OPEN)
            .filter {
                !it.registrationClosesOn.isAfter(now)
            }
            .forEach {
                it.status = ActivityStatus.CLOSED
                activityRepository.save(it)
            }
    }

    private fun isStudentEnrolled(
        student: User,
        activityId: Long
    ): Boolean {

        return activityEnrollmentRepository
            .findByActivityIdAndUserId(
                activityId,
                student.id
            )
            ?.status == EnrollmentStatus.ENROLLED
    }

    private fun isProfessorEnrolled(
        professor: User,
        activityId: Long
    ): Boolean {

        return activityEnrollmentRepository
            .findByActivityIdAndUserId(
                activityId,
                professor.id
            )
            ?.status == EnrollmentStatus.ENROLLED
    }

    @Transactional
    fun enrollStudent(
        student: User,
        activityId: Long
    ): ActivityResponse {

        require(student.role == Role.STUDENT) {
            "Only students can enroll"
        }

        val activity =
            getActivity(activityId)

        ensureRegistrationOpen(activity)

        require(
            getEligibleActivities(student)
                .any {
                    it.id == activityId
                }
        ) {
            "Student is not eligible for this activity"
        }

        require(
            !isStudentEnrolled(
                student,
                activityId
            )
        ) {
            "Student is already enrolled"
        }

        activityEnrollmentRepository.save(
            ActivityEnrollment(
                activity = activity,
                user = student
            )
        )

        return toActivityResponse(
            activity,
            student
        )
    }

    @Transactional
    fun enrollProfessor(
        professor: User,
        activityId: Long
    ): ActivityResponse {

        require(professor.role == Role.PROFESSOR) {
            "Only professors can enroll"
        }

        val activity =
            getActivity(activityId)

        ensureRegistrationOpen(activity)

        val profile =
            professorProfileRepository
                .findByUserId(professor.id)
                ?: throw IllegalArgumentException(
                    "Professor profile not found"
                )

        require(
            profile.department ==
                activity.department
        ) {
            "Professor department does not match activity"
        }

        require(
            !isProfessorEnrolled(
                professor,
                activityId
            )
        ) {
            "Professor is already enrolled"
        }

        activityEnrollmentRepository.save(
            ActivityEnrollment(
                activity = activity,
                user = professor
            )
        )

        return toActivityResponse(
            activity,
            professor
        )
    }

    @Transactional
    fun createGroup(
        leader: User,
        activityId: Long,
        groupName: String
    ): GroupResponse {

        require(leader.role == Role.STUDENT) {
            "Only students can create groups"
        }

        require(groupName.isNotBlank()) {
            "Group name is required"
        }

        val activity =
            getActivity(activityId)

        ensureRegistrationOpen(activity)

        require(
            isStudentEnrolled(
                leader,
                activityId
            )
        ) {
            "Student is not enrolled in this activity"
        }

        require(
            membershipRepository
                .findByStudentId(leader.id)
                .none {
                    it.group.activity.id ==
                        activityId
                }
        ) {
            "Student already belongs to a group for this activity"
        }

        val group =
            groupRepository.save(
                ProjectGroup(
                    activity = activity,
                    name = groupName.trim(),
                    groupCode = generateGroupCode(),
                    leader = leader
                )
            )

        membershipRepository.save(
            GroupMembership(
                group = group,
                student = leader,
                role = MembershipRole.LEADER
            )
        )

        return toGroupResponse(group)
    }

    @Transactional
    fun inviteStudent(
        leader: User,
        groupId: Long,
        studentId: Long
    ): InvitationResponse {

        require(leader.role == Role.STUDENT) {
            "Only students can send invitations"
        }

        val group =
            getGroup(groupId)

        require(
            group.leader.id == leader.id
        ) {
            "Only the group leader can send invitations"
        }

        require(
            group.status == GroupStatus.DRAFT
        ) {
            "Group is already submitted"
        }

        ensureRegistrationOpen(
            group.activity
        )

        val student =
            userRepository
                .findById(studentId)
                .orElseThrow {
                    IllegalArgumentException(
                        "Student not found"
                    )
                }

        require(
            student.role == Role.STUDENT
        ) {
            "Only students can be invited"
        }

        require(
            student.id != leader.id
        ) {
            "Leader cannot invite themselves"
        }

        require(
            isStudentEnrolled(
                student,
                group.activity.id
            )
        ) {
            "Student is not enrolled in this activity"
        }

        require(
            membershipRepository
                .findByStudentId(student.id)
                .none {
                    it.group.activity.id ==
                        group.activity.id
                }
        ) {
            "Student already belongs to a group for this activity"
        }

        require(
            invitationRepository
                .findByStudentIdAndStatus(
                    student.id,
                    InvitationStatus.PENDING
                )
                .none {
                    it.group.activity.id ==
                        group.activity.id
                }
        ) {
            "Student already has a pending invitation for this activity"
        }

        val currentSize =
            membershipRepository
                .findByGroupId(groupId)
                .size

        require(
            currentSize <
                group.activity.maxGroupSize
        ) {
            "Group has reached maximum size"
        }

        val invitation =
            invitationRepository.save(
                GroupInvitation(
                    group = group,
                    student = student
                )
            )

        return toInvitationResponse(
            invitation
        )
    }

    @Transactional
    fun respondToInvitation(
        student: User,
        invitationId: Long,
        accept: Boolean
    ) {

        require(student.role == Role.STUDENT) {
            "Only students can respond to invitations"
        }

        val invitation =
            invitationRepository
                .findById(invitationId)
                .orElseThrow {
                    IllegalArgumentException(
                        "Invitation not found"
                    )
                }

        require(
            invitation.student.id ==
                student.id
        ) {
            "This invitation does not belong to you"
        }

        require(
            invitation.status ==
                InvitationStatus.PENDING
        ) {
            "Invitation has already been answered"
        }

        if (accept) {

            val group =
                invitation.group

            ensureRegistrationOpen(
                group.activity
            )

            require(
                group.status ==
                    GroupStatus.DRAFT
            ) {
                "Group is already submitted"
            }

            require(
                isStudentEnrolled(
                    student,
                    group.activity.id
                )
            ) {
                "Student is not enrolled in this activity"
            }

            require(
                membershipRepository
                    .findByStudentId(student.id)
                    .none {
                        it.group.activity.id ==
                            group.activity.id
                    }
            ) {
                "Student already belongs to a group for this activity"
            }

            require(
                membershipRepository
                    .findByGroupId(group.id)
                    .size <
                    group.activity.maxGroupSize
            ) {
                "Group has reached maximum size"
            }

            invitation.status =
                InvitationStatus.ACCEPTED

            membershipRepository.save(
                GroupMembership(
                    group = group,
                    student = student,
                    role = MembershipRole.MEMBER
                )
            )

        } else {

            invitation.status =
                InvitationStatus.REJECTED
        }

        invitation.respondedAt =
            LocalDateTime.now()

        invitationRepository.save(
            invitation
        )
    }

    @Transactional
    fun submitGroup(
        leader: User,
        groupId: Long
    ): GroupResponse {

        require(leader.role == Role.STUDENT) {
            "Only students can submit groups"
        }

        val group =
            getGroup(groupId)

        require(
            group.leader.id == leader.id
        ) {
            "Only the leader can submit the group"
        }

        require(
            group.status == GroupStatus.DRAFT
        ) {
            "Group is already submitted"
        }

        ensureRegistrationOpen(
            group.activity
        )

        val members =
            membershipRepository
                .findByGroupId(groupId)

        require(
            members.size in
                group.activity.minGroupSize..
                group.activity.maxGroupSize
        ) {
            "Group size must be between " +
                "${group.activity.minGroupSize} and " +
                "${group.activity.maxGroupSize}"
        }

        require(
            members.any {
                it.student.id == leader.id &&
                    it.role ==
                    MembershipRole.LEADER
            }
        ) {
            "Leader membership is missing"
        }

        require(
            invitationRepository
                .findByGroupId(groupId)
                .none {
                    it.status ==
                        InvitationStatus.PENDING
                }
        ) {
            "There are pending invitations. " +
                "Wait for them to be answered."
        }

        require(
            members.all {
                isStudentEnrolled(
                    it.student,
                    group.activity.id
                )
            }
        ) {
            "All group members must be enrolled in the activity"
        }

        group.status =
            GroupStatus.SUBMITTED

        groupRepository.save(group)

        return toGroupResponse(group)
    }

    fun getStudentGroups(
        student: User
    ): List<GroupResponse> {

        require(student.role == Role.STUDENT) {
            "Only students can access groups"
        }

        return membershipRepository
            .findByStudentId(student.id)
            .map { it.group }
            .distinctBy { it.id }
            .map {
                toGroupResponse(it)
            }
    }

    fun getPendingInvitations(
        student: User
    ): List<InvitationResponse> {

        require(student.role == Role.STUDENT) {
            "Only students can access invitations"
        }

        return invitationRepository
            .findByStudentIdAndStatus(
                student.id,
                InvitationStatus.PENDING
            )
            .map {
                toInvitationResponse(it)
            }
    }

    fun getEnrolledStudents(
        student: User,
        activityId: Long
    ): List<UserSummary> {

        require(student.role == Role.STUDENT) {
            "Only students can access this"
        }

        require(
            isStudentEnrolled(
                student,
                activityId
            )
        ) {
            "Student is not enrolled in this activity"
        }

        return activityEnrollmentRepository
            .findByActivityId(activityId)
            .map { it.user }
            .filter {
                it.role == Role.STUDENT &&
                    it.id != student.id
            }
            .filter { otherStudent ->

                membershipRepository
                    .findByStudentId(otherStudent.id)
                    .none {
                        it.group.activity.id ==
                            activityId
                    }
            }
            .map {
                UserSummary(
                    id = it.id,
                    name = it.name,
                    email = it.email
                )
            }
    }

    fun getProfessorAssignments(
        professor: User
    ): List<MentorAssignmentResponse> {

        require(professor.role == Role.PROFESSOR) {
            "Only professors can access assignments"
        }

        return mentorAssignmentRepository
            .findByProfessorId(professor.id)
            .map {
                MentorAssignmentResponse(
                    groupId = it.group.id,
                    groupName = it.group.name,
                    groupCode = it.group.groupCode,
                    activityTitle =
                        it.group.activity.title,
                    professorName =
                        it.professor.name
                )
            }
    }

    fun getActivityGroups(
        admin: User,
        activityId: Long
    ): List<GroupResponse> {

        require(admin.role == Role.ADMIN) {
            "Only admin can access groups"
        }

        getActivity(activityId)

        return groupRepository
            .findByActivityId(activityId)
            .map {
                toGroupResponse(it)
            }
    }

    @Transactional
    fun allocateMentors(
        admin: User,
        activityId: Long
    ): List<MentorAssignmentResponse> {

        require(admin.role == Role.ADMIN) {
            "Only admin can allocate mentors"
        }

        val activity =
            getActivity(activityId)

        require(
            activity.status !=
                ActivityStatus.ALLOCATED
        ) {
            "Mentor allocation is already complete"
        }

        require(
            activity.status == ActivityStatus.CLOSED ||
                !activity.registrationClosesOn
                    .isAfter(LocalDateTime.now())
        ) {
            "Registration is still open"
        }

        val groups =
            groupRepository
                .findByActivityId(activityId)
                .filter {
                    it.status ==
                        GroupStatus.SUBMITTED
                }
                .filter {
                    mentorAssignmentRepository
                        .findByGroupId(it.id) == null
                }

        require(
            groups.isNotEmpty()
        ) {
            "No submitted groups available for allocation"
        }

        val professors =
            activityEnrollmentRepository
                .findByActivityId(activityId)
                .map { it.user }
                .filter {
                    it.role ==
                        Role.PROFESSOR
                }
                .filter {
                    professorProfileRepository
                        .findByUserId(it.id)
                        ?.department ==
                        activity.department
                }
                .distinctBy {
                    it.id
                }
                .shuffled()

        require(
            professors.isNotEmpty()
        ) {
            "No professors available for allocation"
        }

        if (activity.status ==
            ActivityStatus.OPEN
        ) {
            activity.status =
                ActivityStatus.CLOSED
        }

        val assignments =
            groups.mapIndexed { index, group ->

                val professor =
                    professors[
                        index % professors.size
                    ]

                mentorAssignmentRepository.save(
                    MentorAssignment(
                        group = group,
                        professor = professor
                    )
                )

                group.status =
                    GroupStatus.ALLOCATED

                groupRepository.save(group)

                MentorAssignmentResponse(
                    groupId = group.id,
                    groupName = group.name,
                    groupCode = group.groupCode,
                    activityTitle =
                        group.activity.title,
                    professorName =
                        professor.name
                )
            }

        activity.status =
            ActivityStatus.ALLOCATED

        activityRepository.save(activity)

        return assignments
    }

    private fun toActivityResponse(
        activity: Activity,
        user: User
    ): ActivityResponse =
        ActivityResponse(
            id = activity.id,
            title = activity.title,
            academicYearStart =
                activity.academicYearStart,
            year = activity.year,
            department =
                activity.department,
            division =
                activity.division,
            minGroupSize =
                activity.minGroupSize,
            maxGroupSize =
                activity.maxGroupSize,
            registrationClosesOn =
                activity.registrationClosesOn,
            status =
                activity.status,
            enrolled =
                activityEnrollmentRepository
                    .findByActivityIdAndUserId(
                        activity.id,
                        user.id
                    )
                    ?.status ==
                    EnrollmentStatus.ENROLLED
        )

    private fun toGroupResponse(
        group: ProjectGroup
    ): GroupResponse {

        val members =
            membershipRepository
                .findByGroupId(group.id)
                .map {
                    GroupMemberResponse(
                        id = it.student.id,
                        name = it.student.name,
                        email = it.student.email,
                        role = it.role.name
                    )
                }

        val assignment =
            mentorAssignmentRepository
                .findByGroupId(group.id)

        return GroupResponse(
            id = group.id,
            name = group.name,
            groupCode = group.groupCode,
            activityId = group.activity.id,
            activityTitle =
                group.activity.title,
            status = group.status,
            leaderId =
                group.leader.id,
            leaderName =
                group.leader.name,
            members = members,
            mentorName =
                assignment?.professor?.name
        )
    }

    private fun toInvitationResponse(
        invitation: GroupInvitation
    ): InvitationResponse =
        InvitationResponse(
            id = invitation.id,
            groupId = invitation.group.id,
            groupName = invitation.group.name,
            activityTitle =
                invitation.group.activity.title,
            status = invitation.status
        )

    private fun getActivity(
        activityId: Long
    ): Activity =
        activityRepository
            .findById(activityId)
            .orElseThrow {
                IllegalArgumentException(
                    "Activity not found"
                )
            }

    private fun getGroup(
        groupId: Long
    ): ProjectGroup =
        groupRepository
            .findById(groupId)
            .orElseThrow {
                IllegalArgumentException(
                    "Group not found"
                )
            }

    private fun ensureRegistrationOpen(
        activity: Activity
    ) {

        require(
            activity.status ==
                ActivityStatus.OPEN
        ) {
            "Activity is not open"
        }

        require(
            activity.registrationClosesOn
                .isAfter(LocalDateTime.now())
        ) {
            "Registration is closed"
        }
    }

    private fun generateGroupCode(): String {

        var code: String

        do {
            code =
                "GM-${Random.nextInt(1000, 10000)}"
        } while (
            groupRepository
                .findAll()
                .any {
                    it.groupCode == code
                }
        )

        return code
    }
}