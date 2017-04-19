package com.example.daniel.androidassignment3.CallHistory;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.test.espresso.core.deps.guava.base.Charsets;
import android.support.test.espresso.core.deps.guava.io.Files;
import android.telephony.TelephonyManager;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

/**
 * Created by Daniel on 2017-01-15.
 */

public class CallListener extends BroadcastReceiver {

    File file;
    private static String save;
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle b = intent.getExtras();
        if (b == null) {
            return;
        }
        String callState = intent.getStringExtra(TelephonyManager.EXTRA_STATE);

        if (!callState.equals(save)) {
            save = callState;
            if (callState.equalsIgnoreCase(TelephonyManager.EXTRA_STATE_RINGING)) {
                String newNumber = b.getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
                file = context.getExternalFilesDir(null);
                storeNumber(newNumber);
            }
        }
    }

    private void storeNumber(String nr) {
        File f = new File(file, "callHistory");
        try {
            Files.append(nr + ",", f, Charsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Number save checker.");
    }
}
