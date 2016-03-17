package com.majicbus.sms;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Telephony;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;

/**
 * Created by Charlotte on 2016-03-14.
 */
public class SmsListener extends BroadcastReceiver {
    private void sendSMS(String phoneNumber, String message) {


        SmsManager sms = SmsManager.getDefault();
       // sms.sendTextMessage("17789961163", null, "test msg", pi, null);

        sms.sendTextMessage(phoneNumber, null, message, null, null);
    }
@Override
    public void onReceive(Context context, Intent intent) {
//check if action is received text
        if (Telephony.Sms.Intents.SMS_RECEIVED_ACTION.equals(intent.getAction())) {

            for (SmsMessage smsMessage : Telephony.Sms.Intents.getMessagesFromIntent(intent)) {
                //save msg
                String messageBody = smsMessage.getMessageBody();
            }
            sendSMS("17789961163", "test msg");

        }
    }
}