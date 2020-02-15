package com.example.signlanguagetranslator;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            context.startForegroundService(new Intent(context, BluetoothService.class));
            Log.d("BootReceiver", "부트 리시버 -> 블루투스 서비스");
        }
    }
}
