package edu.uco.hsung.m08_sms;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {

    private Button sendButton;
    private PendingIntent sentPendingIntent;
    private PendingIntent deliveredPendingIntent;

    private String INTENT_SMS_SENT = "edu.uco.hsung.SMS_SENT";
    private String INTENT_SMS_DELIVERED = "edu.uco.hsung.SMS_DELIVERED";

    private int MY_PERMISSION_SEND_SMS = 0;
    private int MY_PERMISSION_RECEIVE_SMS = 1;
    private int MY_PERMISSION_READ_PHONE_STATE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final EditText toAddress = (EditText) findViewById(R.id.toAddress);
        final EditText message = (EditText) findViewById(R.id.message2send);

        sendButton = (Button) findViewById(R.id.sendButton);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (toAddress.getText() == null || toAddress.getText().length() == 0) {
                    Toast.makeText(MainActivity.this, "Invaid phone number", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (message.getText() == null || message.getText().length() == 0) {
                    Toast.makeText(MainActivity.this, "No message to send", Toast.LENGTH_SHORT).show();
                    return;
                }
                sendSMSMessage(
                      toAddress.getText().toString(),
                        message.getText().toString()
                );
            }
        });

        // API 23 or higher - runtime permission handling
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (getApplicationContext().checkSelfPermission(
                    android.Manifest.permission.SEND_SMS)
                    != PackageManager.PERMISSION_GRANTED) {
                this.requestPermissions(new String[]{Manifest.permission.SEND_SMS},
                        MY_PERMISSION_SEND_SMS);
            }
            if (getApplicationContext().checkSelfPermission(
                    Manifest.permission.READ_PHONE_STATE)
                    != PackageManager.PERMISSION_GRANTED) {
                this.requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE},
                        MY_PERMISSION_READ_PHONE_STATE);
            }
            if (getApplicationContext().checkSelfPermission(
                    Manifest.permission.RECEIVE_SMS)
                    != PackageManager.PERMISSION_GRANTED) {
                this.requestPermissions(new String[]{Manifest.permission.RECEIVE_SMS},
                        MY_PERMISSION_RECEIVE_SMS);
            }
        }

        sentPendingIntent =
                PendingIntent.getBroadcast(this, 0, new Intent(INTENT_SMS_SENT), 0);
        deliveredPendingIntent =
                PendingIntent.getBroadcast(this, 0, new Intent(INTENT_SMS_DELIVERED), 0);

        // register broadcast receivers FOR SMS sent and delivered
        registerReceiver(new SMSSentReceiver(), new IntentFilter(INTENT_SMS_SENT));
        registerReceiver(new SMSDeliveredReceiver(), new IntentFilter(INTENT_SMS_DELIVERED));

    }

    private void sendSMSMessage(String address, String message) {
        SmsManager smsMgr = SmsManager.getDefault();
        smsMgr.sendTextMessage(address, null, message,
                sentPendingIntent, deliveredPendingIntent);
    }
}
