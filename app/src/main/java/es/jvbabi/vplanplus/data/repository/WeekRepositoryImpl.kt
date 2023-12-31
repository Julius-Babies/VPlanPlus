package es.jvbabi.vplanplus.data.repository

import es.jvbabi.vplanplus.data.source.database.dao.WeekDao
import es.jvbabi.vplanplus.domain.model.Week
import es.jvbabi.vplanplus.domain.repository.WeekRepository

class WeekRepositoryImpl(
    private val weekDao: WeekDao
) : WeekRepository {
    override suspend fun replaceWeeks(weeks: List<Week>) {
        weeks.map { it.schoolWeekRefId }.toSet().forEach {
            weekDao.deleteWeeksBySchoolId(it)
        }
        weeks.forEach {
            weekDao.insertWeek(it)
        }
    }
}