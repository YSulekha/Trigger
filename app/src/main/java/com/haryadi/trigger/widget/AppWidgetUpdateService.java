package com.haryadi.trigger.widget;

import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import com.haryadi.trigger.R;
import com.haryadi.trigger.TriggerActivity;
import com.haryadi.trigger.data.TriggerContract;
import com.haryadi.trigger.service.GeofenceTrasitionService;
import com.haryadi.trigger.utils.ChangeSettings;


public class AppWidgetUpdateService extends IntentService {

    public AppWidgetUpdateService(){
        super("Widget");
    }
    public AppWidgetUpdateService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] widgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this,TriggerAppWidgetProvider.class));
        for(int widgetId:widgetIds){
            int layoutId = R.layout.widget_trigger;
            RemoteViews views = new RemoteViews(getPackageName(), layoutId);
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            long trigger_id = prefs.getLong(GeofenceTrasitionService.SHARED_LAST_TRIGGER,-1);
            Intent launchIntent = new Intent(this,TriggerActivity.class);
            if(trigger_id == -1){
                Log.v("trigger","negative");
                views.setViewVisibility(R.id.widget_empty_text, View.VISIBLE);
                views.setViewVisibility(R.id.widget_content,View.INVISIBLE);
                views.setTextViewText(R.id.widget_empty_text,"No trigger activated");
            }
            else {
                Log.v("ElseWidget",String.valueOf(trigger_id));
                String where = TriggerContract.TriggerEntry.TABLE_NAME + "." +
                        TriggerContract.TriggerEntry._ID + " = ?";
                String[] args = new String[]{Long.toString(trigger_id)};
                Cursor cursor = getContentResolver().query(TriggerContract.TriggerEntry.CONTENT_URI, ChangeSettings.TRIGGER_COLUMNS, where, args, null);
                if (cursor == null) {
                    Log.v("CursornullWidg",String.valueOf(trigger_id));
                    views.setViewVisibility(R.id.widget_empty_text, View.VISIBLE);
                    views.setTextViewText(R.id.widget_empty_text, "No Record found");
                    //return;
                }
                if (cursor.moveToFirst()) {
                    views.setViewVisibility(R.id.widget_content,View.VISIBLE);
                    views.setViewVisibility(R.id.widget_empty_text, View.INVISIBLE);
                    views.setTextViewText(R.id.widget_triggerName, cursor.getString(ChangeSettings.INDEX_NAME));
                    views.setTextViewText(R.id.widget_connect, cursor.getString(ChangeSettings.INDEX_CONNECT));
                    String triggerPoint = cursor.getString(ChangeSettings.INDEX_TRIGGER_POINT);
                    Log.v("RecordPresent",triggerPoint);
                    if (triggerPoint.equals(getString(R.string.wifi))) {
                        views.setImageViewResource(R.id.widget_profile_image, R.drawable.ic_wifi_brown);
                    }
                    else if(triggerPoint.equals(getString(R.string.bluetooth))){
                        views.setImageViewResource(R.id.widget_profile_image, R.drawable.ic_bluetooth_brown);
                    }
                    else{
                        views.setImageViewResource(R.id.widget_profile_image, R.drawable.ic_location_on_brown);
                    }
                    launchIntent.putExtra("IdValue",trigger_id);
                    launchIntent.putExtra("triggerPoint",triggerPoint);
                } else {
                    views.setViewVisibility(R.id.widget_empty_text, View.VISIBLE);
                    views.setTextViewText(R.id.widget_empty_text, "No Record found");
                   // return;
                }
            }
            launchIntent.putExtra("Trigger","ProfileFrag");
            PendingIntent pendingIntent = PendingIntent.getActivity(this,0,launchIntent,0);
            views.setOnClickPendingIntent(R.id.widget_content, pendingIntent);
            appWidgetManager.updateAppWidget(widgetId, views);
        }
    }
}
