package com.luminaos.launcher.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.widget.RemoteViews
import com.luminaos.launcher.R
import java.text.SimpleDateFormat
import java.util.*

class LuminaWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    private fun updateAppWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        val views = RemoteViews(context.packageName, R.layout.widget_clock)
        
        // Update time
        val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val dateFormat2 = SimpleDateFormat("EEEE, MMM d", Locale.getDefault())
        
        views.setTextViewText(R.id.widget_time, dateFormat.format(Date()))
        views.setTextViewText(R.id.widget_date, dateFormat2.format(Date()))
        
        appWidgetManager.updateAppWidget(appWidgetId, views)
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for your first widget
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for your last widget
    }
}
