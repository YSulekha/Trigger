package com.haryadi.trigger.service;

import android.app.IntentService;
import android.content.Intent;


public class ChangeSettingsIntentService extends IntentService {

    public ChangeSettingsIntentService() {
        super(ChangeSettingsIntentService.class.getName());
    }


    public ChangeSettingsIntentService(String name) {
        super(name);
    }
    @Override
    protected void onHandleIntent(Intent intent) {

    }
}
