package com.alifwyaa.azanmunich.android

import android.content.Context
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.alifwyaa.azanmunich.domain.SharedApp
import com.alifwyaa.azanmunich.domain.services.SharedNotificationSchedulerService
import com.alifwyaa.azanmunich.extensions.sharedApp
import java.util.concurrent.TimeUnit

/**
 * @author Created by Abdullah Essa on 16.08.21.
 */
class AzanPeriodicJobScheduler(
    private val appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    @Suppress("SwallowedException")
    override suspend fun doWork(): Result {
        val sharedApp: SharedApp = appContext.sharedApp
        return try {
            sharedApp.logService.m { "AzanPeriodicJobScheduler Started" }
            sharedApp.notificationSchedulerService.startScheduleNotificationsJob()
            sharedApp.logService.m { "AzanPeriodicJobScheduler Finished" }
            Result.success()
        } catch (error: Throwable) {
            sharedApp.logService.e(throwable = error, report = true)
            Result.failure()
        }
    }


    companion object {
        private const val ID_AZAN_TIME_JOB = "ID_AZAN_TIME_JOB"
        private const val TAG_AZAN_TIME_JOB = "TAG_AZAN_TIME_JOB"

        /**
         * Schedule the periodic job
         */
        fun schedule(context: Context) {

            val notificationSchedulerService: SharedNotificationSchedulerService =
                context.sharedApp.notificationSchedulerService

            //========>

            //notificationSchedulerService.startScheduleNotificationsJob()

            //========>

            val workManager = WorkManager.getInstance(context)

            workManager.cancelAllWorkByTag(TAG_AZAN_TIME_JOB)

            val repeatInterval: Long =
                notificationSchedulerService.scheduleTimeFromNowInSeconds.toLong()

            val constraints: Constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val workRequest: PeriodicWorkRequest =
                PeriodicWorkRequestBuilder<AzanPeriodicJobScheduler>(
                    repeatInterval,
                    TimeUnit.SECONDS
                )
                    .setConstraints(constraints)
                    .addTag(TAG_AZAN_TIME_JOB)
                    .build()

            workManager.enqueueUniquePeriodicWork(
                ID_AZAN_TIME_JOB,
                ExistingPeriodicWorkPolicy.REPLACE,
                workRequest,
            )
        }
    }
}
