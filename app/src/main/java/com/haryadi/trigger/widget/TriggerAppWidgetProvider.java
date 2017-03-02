package com.haryadi.trigger.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;

import com.haryadi.trigger.utils.ChangeSettings;


public class TriggerAppWidgetProvider extends AppWidgetProvider {


    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (intent.getAction().equals(ChangeSettings.ACTION_DATA_UPDATED)) {
            Intent serviceIntent = new Intent(context, AppWidgetUpdateService.class);
            context.startService(serviceIntent);
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Intent serviceIntent = new Intent(context, AppWidgetUpdateService.class);
        context.startService(serviceIntent);
    }
}
