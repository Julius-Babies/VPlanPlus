package es.jvbabi.vplanplus.domain.repository

import es.jvbabi.vplanplus.domain.model.School
import es.jvbabi.vplanplus.domain.usecase.Response
import es.jvbabi.vplanplus.domain.usecase.SchoolIdCheckResult

interface SchoolRepository {
    suspend fun getSchools(): List<School>
    suspend fun checkSchoolId(schoolId: Long): SchoolIdCheckResult?
    suspend fun login(schoolId: Long, username: String, password: String): Response
    suspend fun createSchool(schoolId: Long, username: String, password: String, name: String, daysPerWeek: Int, fullyCompatible: Boolean)
    suspend fun updateSchoolName(schoolId: Long, name: String)
    suspend fun getSchoolNameOnline(schoolId: Long, username: String, password: String): String
    suspend fun getSchoolFromId(schoolId: Long): School?
    suspend fun deleteSchool(schoolId: Long)
    suspend fun getSchoolByName(schoolName: String): School

    fun checkSchoolIdSyntax(schoolId: String): Boolean {
        return schoolId.length == 8 && schoolId.toIntOrNull() != null
    }
}