package com.vlatrof.subscriptionsmanager.presentation.notifications

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.res.ResourcesCompat
import com.vlatrof.subscriptionsmanager.R
import com.vlatrof.subscriptionsmanager.domain.models.Subscription
import com.vlatrof.subscriptionsmanager.presentation.screens.common.MainActivity
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class SubscriptionRenewalNotification(
    
    private val subscription: Subscription,
    private val context: Context
    
) {

    fun show() {
        submitChannel(
            channelId = channelId,
            channelName = context.getString(channelNameResourceStringId)
        )
        val notification = create()
        NotificationManagerCompat.from(context).notify(subscription.id, notification)
    }

    private fun submitChannel(channelId: String, channelName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
                .createNotificationChannel(channel)
        }
    }

    private fun create(): Notification {
        // prepare title 
        val title = context.getString(R.string.renewal_notification_title, subscription.name)

        // prepare date
        val dateStr = when (subscription.nextRenewalDate) {
            LocalDate.now() -> { context.getString(R.string.today).lowercase() }
            LocalDate.now().plusDays(1) -> { context.getString(R.string.tomorrow).lowercase() }
            else -> {
                subscription.nextRenewalDate.format(DateTimeFormatter.ofPattern("dd MMMM"))
            }
        }

        // completed message str
        val message = context.getString(
            R.string.renewal_notification_message,
            subscription.name,
            dateStr,
            subscription.paymentCost.toString(),
            subscription.paymentCurrency.currencyCode
        )

        // create pending intent to open app by click on notification
        val intent = Intent(context, MainActivity::class.java)
            .apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            }
        val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
        } else {
            PendingIntent.getActivity(context, 0, intent, 0)
        }

        // build notification
        return NotificationCompat.Builder(context, channelId)
            .setContentTitle(title)
            .setContentText(message)
            .setTicker(message)
            .setSmallIcon(R.drawable.ic_notification_icon)
            .setColor(ResourcesCompat.getColor(context.resources, R.color.green, null))
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()
    }
    
    companion object {
        private const val channelId = "SUBSCRIPTIONS_RENEWAL_ALERTS"
        private const val channelNameResourceStringId = R.string.renewal_notification_channel_name
    }
}
