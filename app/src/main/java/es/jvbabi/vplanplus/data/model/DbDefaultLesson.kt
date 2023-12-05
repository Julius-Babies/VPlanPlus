package es.jvbabi.vplanplus.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import java.util.UUID

@Entity(
    tableName = "default_lesson",
    primaryKeys = ["defaultLessonId"],
    indices = [
        Index(value = ["defaultLessonId"], unique = true),
        Index(value = ["vpId"]),
    ],
    foreignKeys = [
        ForeignKey(
            entity = DbClass::class,
            parentColumns = ["classId"],
            childColumns = ["classId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = DbTeacher::class,
            parentColumns = ["teacherId"],
            childColumns = ["teacherId"],
            onDelete = ForeignKey.SET_NULL
        ),
    ]
)
data class DbDefaultLesson(
    val defaultLessonId: UUID,
    val vpId: Long,
    val subject: String,
    val teacherId: UUID?,
    val classId: UUID,
)