package com.alifwyaa.azanmunich.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.RemoteViews
import com.alifwyaa.azanmunich.domain.internal.platform.SharedDispatchers
import com.alifwyaa.azanmunich.domain.model.SharedResult
import com.alifwyaa.azanmunich.domain.model.widgets.SharedAndroidNewPrayerWidgetData
import com.alifwyaa.azanmunich.domain.services.SharedAppScope
import com.alifwyaa.azanmunich.domain.services.SharedWidgetsService
import com.alifwyaa.azanmunich.extensions.getLaunchPendingIntent
import com.alifwyaa.azanmunich.shared.R
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

/**
 * @author Created by Abdullah Essa on 29.10.21.
 */
class NextPrayerWidget : AppWidgetProvider(), KoinComponent {

    private val widgetsDataService by inject<SharedWidgetsService>()
    private val appScope by inject<SharedAppScope>()

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

        loadJob?.cancel()
        loadJob = appScope.launch(SharedDispatchers.Main) {
            when (val sharedResult: SharedResult<SharedAndroidNewPrayerWidgetData> =
                widgetsDataService.getNextPrayerWidgetData()) {
                is SharedResult.Success -> showContentView(
                    context = context,
                    appWidgetManager = appWidgetManager,
                    appWidgetIds = appWidgetIds,
                    data = sharedResult.data
                )

                is SharedResult.Error -> showErrorView(
                    context = context,
                    appWidgetManager = appWidgetManager,
                    appWidgetIds = appWidgetIds,
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
            val remoteViews = RemoteViews(context.packageName, R.layout.next_prayer_widget).apply {
                populateLoading(context)
            }
            appWidgetManager.updateAppWidget(appWidgetId, remoteViews)
        }
    }

    private fun showContentView(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray,
        data: SharedAndroidNewPrayerWidgetData
    ) {
        for (appWidgetId in appWidgetIds) {
            val remoteViews = RemoteViews(context.packageName, R.layout.next_prayer_widget).apply {
                populateData(context, data)
            }
            appWidgetManager.updateAppWidget(appWidgetId, remoteViews)
        }
    }

    private fun showErrorView(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            val remoteViews = RemoteViews(context.packageName, R.layout.next_prayer_widget).apply {
                populateError(context)
            }
            appWidgetManager.updateAppWidget(appWidgetId, remoteViews)
        }
    }

    //endregion

    //region Helpers


    private fun RemoteViews.populateLoading(context: Context) {
        setOnClickPendingIntent(
            android.R.id.background,
            getLaunchPendingIntent(context)
        )
        setViewVisibility(R.id.vgLoading, View.VISIBLE)
        setViewVisibility(R.id.vgContent, View.INVISIBLE)
        setViewVisibility(R.id.vgError, View.INVISIBLE)
    }

    private fun RemoteViews.populateData(
        context: Context,
        dayPrayer: SharedAndroidNewPrayerWidgetData
    ) {
        setOnClickPendingIntent(
            android.R.id.background,
            getLaunchPendingIntent(context)
        )
        dayPrayer.apply {
            setTextViewText(R.id.tvPrayerName, displayName)
            setTextViewText(R.id.tvPrayerTime, displayTime)
        }
        setViewVisibility(R.id.vgLoading, View.INVISIBLE)
        setViewVisibility(R.id.vgContent, View.VISIBLE)
        setViewVisibility(R.id.vgError, View.INVISIBLE)
    }

    private fun RemoteViews.populateError(context: Context) {
        setOnClickPendingIntent(
            android.R.id.background,
            getLaunchPendingIntent(context)
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
        setViewVisibility(R.id.vgLoading, View.INVISIBLE)
        setViewVisibility(R.id.vgContent, View.INVISIBLE)
        setViewVisibility(R.id.vgError, View.VISIBLE)
    }

    //endregion

    companion object {
        /**
         * trigger update for the [NextPrayerWidget]
         */
        fun triggerUpdate(context: Context) {
            val intent = getRefreshIntent(context)
            context.sendBroadcast(intent)
        }

        private fun getRefreshIntent(context: Context): Intent {
            val widgetManager = AppWidgetManager.getInstance(context)
            val ids =
                widgetManager.getAppWidgetIds(ComponentName(context, NextPrayerWidget::class.java))
            val intent = Intent(context, NextPrayerWidget::class.java)
            intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
            return intent
        }
    }
}
