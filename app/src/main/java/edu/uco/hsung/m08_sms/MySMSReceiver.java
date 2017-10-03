package edu.uco.hsung.m08_sms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.Toast;

public class MySMSReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // Get the SMS data bound to intent
        Bundle bundle = intent.getExtras();
        SmsMessage[] messages;
        String contents = "";

        if (bundle != null) {
            // Retrieve the SMS Messages received
            Object[] pdus = (Object[]) bundle.get("pdus");
            if (pdus == null) return;

            messages = new SmsMessage[pdus.length];
            // For every SMS message received
            for (int i=0; i < messages.length; i++) {
                // Convert Object array
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    // API 23 or higher
                    String format = bundle.getString("format");
                    messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i], format);
                } else {
                    // API 22 or lower
                    messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                }
                // Sender's phone number
                contents += "SMS from " + messages[i].getOriginatingAddress() + " : ";
                // Fetch the text message
                contents += messages[i].getMessageBody();
                contents += "\n";
            }
            // Display the entire SMS Message
            Toast.makeText(context, contents, Toast.LENGTH_SHORT).show();
        }
    }
}
