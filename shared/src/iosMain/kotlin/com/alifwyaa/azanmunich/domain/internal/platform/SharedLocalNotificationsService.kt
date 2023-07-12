package com.alifwyaa.azanmunich.domain.internal.platform

import com.alifwyaa.azanmunich.domain.internal.platform.SharedDispatchers.Main
import com.alifwyaa.azanmunich.domain.model.SharedNotificationModel
import com.alifwyaa.azanmunich.domain.model.SharedNotificationModel.Sound
import com.alifwyaa.azanmunich.domain.model.SharedPlatformInfo
import com.alifwyaa.azanmunich.domain.services.SharedDateTimeService
import com.alifwyaa.azanmunich.domain.services.SharedLogService
import com.alifwyaa.azanmunich.domain.services.SharedSettingsService
import com.autodesk.coroutineworker.threadSafeSuspendCallback
import kotlinx.coroutines.withContext
import platform.Foundation.NSError
import platform.UserNotifications.UNAuthorizationOptionAlert
import platform.UserNotifications.UNAuthorizationOptionSound
import platform.UserNotifications.UNAuthorizationStatusAuthorized
import platform.UserNotifications.UNMutableNotificationContent
import platform.UserNotifications.UNNotification
import platform.UserNotifications.UNNotificationPresentationOptionBanner
import platform.UserNotifications.UNNotificationPresentationOptionList
import platform.UserNotifications.UNNotificationPresentationOptionSound
import platform.UserNotifications.UNNotificationPresentationOptions
import platform.UserNotifications.UNNotificationRequest
import platform.UserNotifications.UNNotificationSettings
import platform.UserNotifications.UNNotificationSound
import platform.UserNotifications.UNTimeIntervalNotificationTrigger
import platform.UserNotifications.UNUserNotificationCenter
import platform.UserNotifications.UNUserNotificationCenterDelegateProtocol
import platform.darwin.NSObject
import kotlin.native.concurrent.freeze
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime

/**
 * @author Created by Abdullah Essa on 06.06.21.
 */
@Suppress("UndocumentedPublicFunction")
actual class SharedLocalNotificationsService actual constructor(
    platformInfo: SharedPlatformInfo,
    private val dateTimeService: SharedDateTimeService,
    settingsService: SharedSettingsService,
    logService: SharedLogService,
) {
    /**
     * UNUserNotificationCenterDelegate
     */
    private val notificationCenterDelegate = UNUserNotificationCenterDelegate(
        refreshAllWidgetsBlock = {
            (platformInfo as SharedPlatformInfo.Ios).nativeDelegate?.refreshWidgets()
        }
    )

    //region SharedLocalNotificationsService Methods

    actual suspend fun isPermissionGranted(): Boolean = withContext(Main) {
        threadSafeSuspendCallback { completion ->
            val callback: (UNNotificationSettings?) -> Unit = { settings ->
                val value = settings?.authorizationStatus == UNAuthorizationStatusAuthorized
                completion(Result.success(value))
            }

            callback.freeze()

            getNotificationCenter().getNotificationSettingsWithCompletionHandler(callback)

            return@threadSafeSuspendCallback {
                // not cancelable
            }
        }
    }

    actual suspend fun requestPermission(): Boolean = withContext(Main) {
        threadSafeSuspendCallback { completion ->
            val callback = { isGranted: Boolean, error: NSError? ->
                error?.apply { println("requestAuthorizationWithOptions Error  ${this.localizedDescription}") }
                completion(Result.success(isGranted))
            }

            callback.freeze()

            getNotificationCenter().requestAuthorizationWithOptions(
                UNAuthorizationOptionSound or UNAuthorizationOptionAlert,
                callback
            )

            return@threadSafeSuspendCallback {
                // not cancelable
            }
        }
    }

    actual suspend fun isNotificationAdded(id: String): Boolean = withContext(Main) {
        threadSafeSuspendCallback { completion ->
            val callback: (List<*>?) -> Unit = { requests: List<*>? ->
                val found = (requests as? List<UNNotificationRequest>)
                    ?.any { it.identifier == id } == true
                completion(Result.success(found))
            }

            callback.freeze()

            getNotificationCenter()
                .getPendingNotificationRequestsWithCompletionHandler(callback)

            return@threadSafeSuspendCallback {
                // not cancelable
            }
        }
    }

    @OptIn(ExperimentalTime::class)
    actual suspend fun addNotifications(models: List<SharedNotificationModel>): Boolean =
        withContext(Main) {
            threadSafeSuspendCallback { completion ->
                val callback: (NSError?) -> Unit = { error ->
                    error?.apply { println("addNotification Error ${this.localizedDescription}") }
                    completion(Result.success(error == null))
                }

                callback.freeze()

                models.forEach { model ->
                    val timeFromNowInSeconds: Double = dateTimeService.getDurationUntil(
                        dateModel = model.dateModel,
                        timeModel = model.timeModel
                    ).toDouble(DurationUnit.SECONDS)

                    if (timeFromNowInSeconds <= 0) return@forEach

                    if (!model.isEnabled) return@forEach

                    getNotificationCenter().addNotificationRequest(
                        model.toUNNotificationRequest(timeFromNowInSeconds),
                        callback
                    )
                }

                return@threadSafeSuspendCallback {
                    // not cancelable
                }
            }
        }

    actual suspend fun cancelNotifications(ids: List<String>): Boolean = withContext(Main) {
        getNotificationCenter().removePendingNotificationRequestsWithIdentifiers(ids)
        true
    }

    //endregion

    //region Helpers

    private fun getNotificationCenter(): UNUserNotificationCenter =
        UNUserNotificationCenter.currentNotificationCenter().apply {
            delegate = notificationCenterDelegate
        }

    private fun SharedNotificationModel.toUNNotificationRequest(
        timeFromNowInSeconds: Double
    ): UNNotificationRequest {
        val identifier = id
        val categoryIdentifier = categoryId
        val body = title
        val sound = when (sound) {
            Sound.DEFAULT -> UNNotificationSound.defaultSound
            Sound.SOUND1_FAJR -> UNNotificationSound.soundNamed("azan.wav")
            Sound.SOUND1_OTHERS -> UNNotificationSound.soundNamed("azan.wav")
        }
        val payload = payload

        val notificationContent = UNMutableNotificationContent().apply {
            setCategoryIdentifier(categoryIdentifier)
            setBody(body)
            setSound(sound)
            setUserInfo(payload.map { it.key as? Any to it.value as? Any }.toMap())
        }

        val trigger = UNTimeIntervalNotificationTrigger
            .triggerWithTimeInterval(timeFromNowInSeconds, false)

        return UNNotificationRequest.requestWithIdentifier(
            identifier,
            notificationContent,
            trigger
        )
    }

    //endregion

    //region UNUserNotificationCenterDelegateProtocol

    private class UNUserNotificationCenterDelegate(
        refreshAllWidgetsBlock: () -> Unit
    ) : NSObject(), UNUserNotificationCenterDelegateProtocol {

        private val safeRefreshAllWidgetsBlock = refreshAllWidgetsBlock.freeze()

        override fun userNotificationCenter(
            center: UNUserNotificationCenter,
            willPresentNotification: UNNotification,
            withCompletionHandler: (UNNotificationPresentationOptions) -> Unit
        ) {
            safeRefreshAllWidgetsBlock()
            withCompletionHandler(
                UNNotificationPresentationOptionSound
                        or UNNotificationPresentationOptionBanner
                        or UNNotificationPresentationOptionList
            )
        }
    }

    //endregion

}
