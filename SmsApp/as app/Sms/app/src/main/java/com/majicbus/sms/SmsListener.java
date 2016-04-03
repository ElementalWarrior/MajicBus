package com.majicbus.sms;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by Charlotte on 2016-03-14.
 */
public class SmsListener extends BroadcastReceiver {
    private static Activity _activity;
    public static void sendSMS(String phoneNumber, String message) {
        //method that will actually send the message back

        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, null, null);
        MainActivity.TotalSent++;

        class ToastRunnable implements Runnable {
            Activity _active;
            String _body;
            String _address;
            public ToastRunnable(Activity active, String msgBody, String msgAddress)
            {
                _active = active;
                _body = msgBody;
                _address = msgAddress;
            }
            public void run(){
                ((MainActivity)_active).LogMessageSent(_body, _address);
            }
        }
        _activity.runOnUiThread(new ToastRunnable(_activity, message, phoneNumber));
    }
    public void setActivity(Activity active)
    {
        _activity = active;
    }
@Override
    public void onReceive(Context context, Intent intent) {
//check if action is received text

    if (Telephony.Sms.Intents.DATA_SMS_RECEIVED_ACTION.equals(intent.getAction())
        || Telephony.Sms.Intents.SMS_RECEIVED_ACTION.equals(intent.getAction())) {
        for (SmsMessage smsMessage : Telephony.Sms.Intents.getMessagesFromIntent(intent)) {
            //save msg and address

            String messageBody = smsMessage.getMessageBody();
            String messageAddress = smsMessage.getOriginatingAddress();

            class ToastRunnable implements Runnable {
                Activity _active;
                String _body;
                String _address;
                public ToastRunnable(Activity active, String msgBody, String msgAddress)
                {
                    _active = active;
                    _body = msgBody;
                    _address = msgAddress;
                }
                public void run(){
                    ((MainActivity)_active).GetStopInformation(_body, _address);
                }
            }
            _activity.runOnUiThread(new ToastRunnable(_activity, messageBody, messageAddress));
        }
    }
}
}