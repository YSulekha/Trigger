package com.haryadi.trigger.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.haryadi.trigger.TriggerActivity;


public class TriggerAppWidgetProvider extends AppWidgetProvider {


    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (intent.getAction().equals(TriggerActivity.ACTION_DATA_UPDATED)) {
            Log.v("adad","Inside OnReceive");
            Intent serviceIntent = new Intent(context,AppWidgetUpdateService.class);
            context.startService(serviceIntent);
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
      /*  for (int widgetId : appWidgetIds) {
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_trigger);
            Intent intent = new Intent(context, TriggerActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            remoteViews.setOnClickPendingIntent(R.id.widget, pendingIntent);
            appWidgetManager.updateAppWidget(widgetId, remoteViews);
        }*/
        Intent serviceIntent = new Intent(context,AppWidgetUpdateService.class);
        context.startService(serviceIntent);
    }
}
