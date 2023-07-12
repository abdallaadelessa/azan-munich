package com.alifwyaa.azanmunich.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.RemoteViews
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.text.bold
import androidx.core.text.buildSpannedString
import com.alifwyaa.azanmunich.R
import com.alifwyaa.azanmunich.data.internal.model.SharedAzanType
import com.alifwyaa.azanmunich.domain.SharedApp
import com.alifwyaa.azanmunich.domain.SharedStrings
import com.alifwyaa.azanmunich.domain.services.SharedAppScope
import com.alifwyaa.azanmunich.domain.internal.notification.NotificationUtils
import com.alifwyaa.azanmunich.domain.internal.platform.SharedDispatchers
import com.alifwyaa.azanmunich.domain.model.SharedResult
import com.alifwyaa.azanmunich.domain.model.widgets.SharedAndroidDayWidgetData
import com.alifwyaa.azanmunich.domain.services.SharedWidgetsDataService
import com.alifwyaa.azanmunich.extensions.sharedApp
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


/**
 * Implementation of App Widget functionality.
 */
class DayWidget : AppWidgetProvider() {

    private var loadJob: Job? = null

    //region AppWidgetProvider

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {

        // Show progress view
        showProgressView(
            context = context,
            appWidgetManager = appWidgetManager,
            appWidgetIds = appWidgetIds
        )

        val sharedApp: SharedApp = context.sharedApp
        val widgetsDataService: SharedWidgetsDataService = sharedApp.widgetsDataService
        val appScope: SharedAppScope = sharedApp.appScope
        val sharedStrings = sharedApp.localizationService.strings

        loadJob?.cancel()
        loadJob = appScope.launch(SharedDispatchers.Main) {

            when (val sharedResult: SharedResult<SharedAndroidDayWidgetData> = widgetsDataService.getDayWidgetData()) {
                is SharedResult.Success -> {
                    val data: List<SharedAndroidDayWidgetData.Item> = sharedResult.data.items
                    if (data.isNotEmpty()) {
                        showContentView(
                            context = context,
                            appWidgetManager = appWidgetManager,
                            appWidgetIds = appWidgetIds,
                            data = data
                        )
                    } else {
                        showErrorView(
                            context = context,
                            appWidgetManager = appWidgetManager,
                            appWidgetIds = appWidgetIds,
                            sharedStrings = sharedStrings,
                            message = sharedStrings.errorNoResult
                        )
                    }
                }
                is SharedResult.Error -> showErrorView(
                    context = context,
                    appWidgetManager = appWidgetManager,
                    appWidgetIds = appWidgetIds,
                    sharedStrings = sharedStrings,
                    message = sharedResult.message
                )
            }
        }
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    //endregion

    //region States

    private fun showProgressView(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray,
    ) {
        for (appWidgetId in appWidgetIds) {
            val remoteViews = RemoteViews(context.packageName, R.layout.day_widget).apply {
                populateLoading(context)
            }
            appWidgetManager.updateAppWidget(appWidgetId, remoteViews)
        }
    }

    private fun showContentView(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray,
        data: List<SharedAndroidDayWidgetData.Item>
    ) {
        for (appWidgetId in appWidgetIds) {
            val remoteViews = RemoteViews(context.packageName, R.layout.day_widget).apply {
                populateData(context, data)
            }
            appWidgetManager.updateAppWidget(appWidgetId, remoteViews)
        }
    }

    private fun showErrorView(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray,
        sharedStrings: SharedStrings,
        message: String
    ) {
        for (appWidgetId in appWidgetIds) {
            val remoteViews = RemoteViews(context.packageName, R.layout.day_widget).apply {
                populateError(context, message, sharedStrings.retry)
            }
            appWidgetManager.updateAppWidget(appWidgetId, remoteViews)
        }
    }

    //endregion

    //region RemoteView Extensions

    private fun RemoteViews.populateLoading(context: Context) {
        setOnClickPendingIntent(
            android.R.id.background,
            NotificationUtils.getPendingIntent(context)
        )
        setViewVisibility(R.id.vgLoading, View.VISIBLE)
        setViewVisibility(R.id.vgContent, View.INVISIBLE)
        setViewVisibility(R.id.vgError, View.INVISIBLE)
    }

    private fun RemoteViews.populateData(
        context: Context,
        dayPrayers: List<SharedAndroidDayWidgetData.Item>
    ) {
        setOnClickPendingIntent(
            android.R.id.background,
            NotificationUtils.getPendingIntent(context)
        )
        @Suppress("MagicNumber")
        dayPrayers.getOrNull(0)?.apply {
            setBackgroundResource(R.id.vgPrayer1, backgroundColor)
            setTextViewText(R.id.tvPrayerName1, name)
            setImageViewResource(R.id.ivPrayer1, iconResId)
            setTextViewText(R.id.tvPrayerTime1, time)
        }
        @Suppress("MagicNumber")
        dayPrayers.getOrNull(1)?.apply {
            setBackgroundResource(R.id.vgPrayer2, backgroundColor)
            setTextViewText(R.id.tvPrayerName2, name)
            setImageViewResource(R.id.ivPrayer2, iconResId)
            setTextViewText(R.id.tvPrayerTime2, time)
        }
        @Suppress("MagicNumber")
        dayPrayers.getOrNull(2)?.apply {
            setBackgroundResource(R.id.vgPrayer3, backgroundColor)
            setTextViewText(R.id.tvPrayerName3, name)
            setImageViewResource(R.id.ivPrayer3, iconResId)
            setTextViewText(R.id.tvPrayerTime3, time)
        }
        @Suppress("MagicNumber")
        dayPrayers.getOrNull(3)?.apply {
            setBackgroundResource(R.id.vgPrayer4, backgroundColor)
            setTextViewText(R.id.tvPrayerName4, name)
            setImageViewResource(R.id.ivPrayer4, iconResId)
            setTextViewText(R.id.tvPrayerTime4, time)
        }
        @Suppress("MagicNumber")
        dayPrayers.getOrNull(4)?.apply {
            setBackgroundResource(R.id.vgPrayer5, backgroundColor)
            setTextViewText(R.id.tvPrayerName5, name)
            setImageViewResource(R.id.ivPrayer5, iconResId)
            setTextViewText(R.id.tvPrayerTime5, time)
        }
        setViewVisibility(R.id.vgLoading, View.INVISIBLE)
        setViewVisibility(R.id.vgContent, View.VISIBLE)
        setViewVisibility(R.id.vgError, View.INVISIBLE)
    }

    private fun RemoteViews.populateError(
        context: Context,
        message: String,
        retryText: String
    ) {
        setOnClickPendingIntent(
            android.R.id.background,
            NotificationUtils.getPendingIntent(context)
        )
        setOnClickPendingIntent(
            R.id.btnRetry,
            PendingIntent.getBroadcast(
                context,
                1,
                getRefreshIntent(context),
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
        )
        setTextViewText(R.id.tvError, message)
        setTextViewText(R.id.btnRetry, retryText)

        setViewVisibility(R.id.vgLoading, View.INVISIBLE)
        setViewVisibility(R.id.vgContent, View.INVISIBLE)
        setViewVisibility(R.id.vgError, View.VISIBLE)
    }

    private fun RemoteViews.setBackgroundResource(viewId: Int, resId: Int) {
        setInt(viewId, "setBackgroundResource", resId)
    }

    //endregion

    //region SharedAndroidDayWidgetData Extensions

    private val SharedAndroidDayWidgetData.Item.name: CharSequence
        get() = if (isNextAzan) buildSpannedString { bold { append(displayName) } } else displayName

    private val SharedAndroidDayWidgetData.Item.time: CharSequence
        get() = if (isNextAzan) buildSpannedString { bold { append(displayTime) } } else displayTime

    @get:ColorRes
    private val SharedAndroidDayWidgetData.Item.backgroundColor: Int
        get() = when {
            isNextAzan -> when (azanType) {
                SharedAzanType.FAJR -> R.drawable.widget_day_highlight_start
                SharedAzanType.ISHA -> R.drawable.widget_day_highlight_end
                else -> R.drawable.widget_day_highlight
            }
            else -> android.R.color.transparent
        }

    @get:DrawableRes
    private val SharedAndroidDayWidgetData.Item.iconResId: Int
        get() = if (isAzanEnabled) R.drawable.ic_notification_on else R.drawable.ic_notification_off

    //endregion

    companion object {

        /**
         * trigger update for the [DayWidget]
         */
        fun triggerUpdate(context: Context) {
            val intent = getRefreshIntent(context)
            context.sendBroadcast(intent)
        }

        private fun getRefreshIntent(context: Context): Intent {
            val widgetManager = AppWidgetManager.getInstance(context)
            val ids = widgetManager.getAppWidgetIds(ComponentName(context, DayWidget::class.java))
            val intent = Intent(context, DayWidget::class.java)
            intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
            return intent
        }
    }

}

