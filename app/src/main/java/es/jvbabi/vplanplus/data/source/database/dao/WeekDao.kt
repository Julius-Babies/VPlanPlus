package es.jvbabi.vplanplus.data.source.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import es.jvbabi.vplanplus.domain.model.Week

@Dao
abstract class WeekDao {
    @Upsert
    abstract suspend fun insertWeek(week: Week)

    @Query("DELETE FROM weeks WHERE schoolWeekRefId = :schoolId")
    abstract suspend fun deleteWeeksBySchoolId(schoolId: Long)
}