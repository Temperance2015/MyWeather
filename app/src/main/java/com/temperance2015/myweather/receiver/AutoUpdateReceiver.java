package com.temperance2015.myweather.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Isabel on 2015/10/13.
 */
public class AutoUpdateReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context,Intent intent){
        Intent i = new Intent(context,AutoUpdateReceiver.class);
        context.startService(i);
    }
}
