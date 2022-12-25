package com.vlatrof.subscriptionsmanager.presentation.notifications

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.vlatrof.subscriptionsmanager.domain.usecases.GetAllSubscriptionsUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.time.LocalDate
import java.time.LocalTime
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.coroutineScope

class SubscriptionRenewalAlertsManager {

    fun launchAlertsWorker(context: Context) {
        // calculate initial delay in seconds to the next time point at 12.00 PM
        val currentTime = LocalTime.now().toSecondOfDay()
        val alertTime = LocalTime.of(alertTimeHours, alertTimeMinutes).toSecondOfDay()
        val initialDelay = if (currentTime < alertTime) {
            alertTime - currentTime
        } else {
            totalOfDaySeconds - currentTime + alertTime
        }

        // create work request
        val newWorkRequest = PeriodicWorkRequestBuilder<SubscriptionRenewalAlertsWorker>(
            repeatInterval = repeatIntervalHours,
            repeatIntervalTimeUnit = TimeUnit.HOURS
        ).setInitialDelay(
            initialDelay.toLong(),
            TimeUnit.SECONDS
        ).build()

        // launch periodic work with replace policy
        WorkManager.getInstance(context)
            .enqueueUniquePeriodicWork(
                "subscriptionAlerts",
                ExistingPeriodicWorkPolicy.REPLACE,
                newWorkRequest
            )
    }

    companion object {
        private const val alertTimeHours: Int = 12
        private const val alertTimeMinutes: Int = 0
        private const val repeatIntervalHours: Long = 24L
        private const val totalOfDaySeconds: Int = 86400
    }
}

@HiltWorker
class SubscriptionRenewalAlertsWorker @AssistedInject constructor(

    @Assisted appContext: Context,
    @Assisted workerParameters: WorkerParameters,
    private val getAllSubscriptionsUseCase: GetAllSubscriptionsUseCase

) : CoroutineWorker(appContext, workerParameters) {

    override suspend fun doWork(): Result {
        coroutineScope {
            val subscriptions = getAllSubscriptionsUseCase()

            if (subscriptions.isEmpty()) {
                return@coroutineScope
            }

            subscriptions.forEach { subscription ->
                if (!subscription.alertEnabled) {
                    return@forEach
                }
                if (subscription.nextRenewalDate + subscription.alertPeriod != LocalDate.now()) {
                    return@forEach
                }
                SubscriptionRenewalNotification(
                    subscription = subscription,
                    context = applicationContext
                ).show()
            }
        }
        return Result.success()
    }
}
