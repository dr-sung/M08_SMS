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
    private int MY_PERMISSION_READ_SMS = 2;

    private boolean receiveSMS = false;
    private boolean readSMS = false;
    private boolean sendSMS = false;

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
                if (!sendSMS) {
                    remindPermission();
                    return;
                }
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
                    Manifest.permission.SEND_SMS)
                    != PackageManager.PERMISSION_GRANTED) {
                this.requestPermissions(new String[]{Manifest.permission.SEND_SMS},
                        MY_PERMISSION_SEND_SMS);
            }
            if (getApplicationContext().checkSelfPermission(
                    Manifest.permission.RECEIVE_SMS)
                    != PackageManager.PERMISSION_GRANTED) {
                this.requestPermissions(new String[]{Manifest.permission.RECEIVE_SMS},
                        MY_PERMISSION_RECEIVE_SMS);
            }
            if (getApplicationContext().checkSelfPermission(
                    Manifest.permission.READ_SMS)
                    != PackageManager.PERMISSION_GRANTED) {
                this.requestPermissions(new String[]{Manifest.permission.READ_SMS},
                        MY_PERMISSION_READ_SMS);
            }
        } else {
            // API 22 or lower: register receivers
            sendSMS = readSMS = receiveSMS = true;
            registerReceiver(new SMSSentReceiver(), new IntentFilter(INTENT_SMS_SENT));
            registerReceiver(new SMSDeliveredReceiver(), new IntentFilter(INTENT_SMS_DELIVERED));
        }

        sentPendingIntent =
                PendingIntent.getBroadcast(this, 0, new Intent(INTENT_SMS_SENT), 0);
        deliveredPendingIntent =
                PendingIntent.getBroadcast(this, 0, new Intent(INTENT_SMS_DELIVERED), 0);

    }

    private void sendSMSMessage(String address, String message) {
        SmsManager smsMgr = SmsManager.getDefault();
        smsMgr.sendTextMessage(address, null, message,
                sentPendingIntent, deliveredPendingIntent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        if (grantResults == null || grantResults.length == 0) {
            remindPermission();
            return;
        }

        if (requestCode == MY_PERMISSION_SEND_SMS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                sendSMS = true;
                registerReceiver(new SMSSentReceiver(), new IntentFilter(INTENT_SMS_SENT));
                registerReceiver(new SMSDeliveredReceiver(), new IntentFilter(INTENT_SMS_DELIVERED));
            } else {
                remindPermission();
            }
        } else if (requestCode == MY_PERMISSION_RECEIVE_SMS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                receiveSMS = true;
            } else {
                remindPermission();
            }
        } else if (requestCode == MY_PERMISSION_READ_SMS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                readSMS = true;
            } else {
                remindPermission();
            }
        }
    }

    private void remindPermission() {
        Toast.makeText(MainActivity.this, "SMS SEND/RECEIVE Permissions required", Toast.LENGTH_SHORT).show();
    }
}
