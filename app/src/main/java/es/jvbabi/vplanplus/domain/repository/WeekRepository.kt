package es.jvbabi.vplanplus.domain.repository

import es.jvbabi.vplanplus.domain.model.Week

interface WeekRepository {
    suspend fun replaceWeeks(weeks: List<Week>)
}