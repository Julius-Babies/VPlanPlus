package es.jvbabi.vplanplus.data.repository

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import androidx.core.app.NotificationCompat
import es.jvbabi.vplanplus.domain.repository.LogRecordRepository
import es.jvbabi.vplanplus.domain.repository.NotificationRepository

class NotificationRepositoryImpl(
    private val appContext: Context,
    private val logRepository: LogRecordRepository
) : NotificationRepository {
    override suspend fun sendNotification(
        channelId: String,
        id: Int,
        title: String,
        message: String,
        icon: Int,
        pendingIntent: PendingIntent?
    ) {
        logRepository.log("Notification", "Sending $id to $channelId: $title")

        val builder = NotificationCompat.Builder(appContext, channelId)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(message)
            )
            .setSmallIcon(icon)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        val notificationManager =
            appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(id, builder.build())
    }
}