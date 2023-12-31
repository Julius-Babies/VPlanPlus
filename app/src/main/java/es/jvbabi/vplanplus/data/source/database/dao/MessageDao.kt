package es.jvbabi.vplanplus.data.source.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import es.jvbabi.vplanplus.domain.model.Message
import kotlinx.coroutines.flow.Flow

@Dao
abstract class MessageDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun insert(messages: Message)

    @Query("SELECT * FROM messages WHERE NOT isRead AND fromVersion <= :appVersion AND toVersion >= :appVersion ORDER BY date DESC")
    abstract fun getUnreadMessages(appVersion: Int): Flow<List<Message>>

    @Query("SELECT * FROM messages WHERE fromVersion <= :appVersion AND toVersion >= :appVersion ORDER BY date DESC")
    abstract fun getMessages(appVersion: Int): Flow<List<Message>>

    @Query("SELECT * FROM messages WHERE id = :messageId")
    abstract fun getMessage(messageId: String): Flow<Message>

    @Transaction
    open suspend fun insertMessages(messages: List<Message>) {
        messages.forEach { message ->
            insert(message)
        }
    }

    @Query("UPDATE messages SET isRead = 1 WHERE id = :messageId")
    abstract suspend fun markMessageAsRead(messageId: String)

    @Query("UPDATE messages SET notificationSent = 1 WHERE id = :messageId")
    abstract suspend fun markMessageAsSent(messageId: String)
}
