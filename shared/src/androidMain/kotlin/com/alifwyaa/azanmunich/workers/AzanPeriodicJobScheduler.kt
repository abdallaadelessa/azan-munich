package com.alifwyaa.azanmunich.workers

import android.content.Context
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.alifwyaa.azanmunich.domain.services.SharedLogService
import com.alifwyaa.azanmunich.domain.services.SharedNotificationSchedulerService
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.component.inject
import java.util.concurrent.TimeUnit

/**
 * @author Created by Abdullah Essa on 16.08.21.
 */
class AzanPeriodicJobScheduler(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams), KoinComponent {
    private val schedulerService by inject<SharedNotificationSchedulerService>()
    private val logService by inject<SharedLogService>()


    @Suppress("SwallowedException")
    override suspend fun doWork(): Result {
        return try {
            logService.m { "AzanPeriodicJobScheduler Started" }
            schedulerService.startScheduleNotificationsJob()
            logService.m { "AzanPeriodicJobScheduler Finished" }
            Result.success()
        } catch (error: Throwable) {
            logService.e(throwable = error, report = true)
            Result.failure()
        }
    }


    companion object : KoinComponent {
        private const val ID_AZAN_TIME_JOB = "ID_AZAN_TIME_JOB"
        private const val TAG_AZAN_TIME_JOB = "TAG_AZAN_TIME_JOB"

        /**
         * Schedule the periodic job
         */
        fun schedule(context: Context) {
            val workManager = WorkManager.Companion.getInstance(context)

            workManager.cancelAllWorkByTag(TAG_AZAN_TIME_JOB)

            val repeatInterval: Long =
                get<SharedNotificationSchedulerService>().scheduleTimeFromNowInSeconds.toLong()

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