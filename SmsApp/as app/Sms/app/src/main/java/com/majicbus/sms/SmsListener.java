package com.majicbus.sms;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Telephony;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by Charlotte on 2016-03-14.
 */
public class SmsListener extends BroadcastReceiver {
    int msgrec=0;
    int msgsent=0;
    private void sendSMS(String phoneNumber, String message) {
        //method that will actually send the message back
        msgsent++;
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, null, null);
    }
@Override
    public void onReceive(Context context, Intent intent) {
//check if action is received text
        if (Telephony.Sms.Intents.SMS_RECEIVED_ACTION.equals(intent.getAction())) {
            for (SmsMessage smsMessage : Telephony.Sms.Intents.getMessagesFromIntent(intent)) {
                //save msg and address
                msgrec++;
                String messageBody = smsMessage.getMessageBody();
                String messageAddress = smsMessage.getOriginatingAddress();

                String url = "tcp:mbdev01.database.windows.net,1433;";
                String uid = "majicbus";
                String pw = "AsBrBrChJa5";
                Connection con = null;
                String sendBack=null;
                try {
                    //convert string message to int
                    int i = Integer.parseInt(messageBody.trim());
                    con = DriverManager.getConnection(url, uid, pw);
                    Statement stmt = con.createStatement();
                    String sql = "SELECT dtarrival "+
                            " FROM stoptimes "+
                            " WHERE stopid ='" + i +"'" ;
                    ResultSet rst = stmt.executeQuery(sql);
                    while (rst.next())
                    {
                        sendBack=rst.getString("dtarrival");
                        //prep to send stuff back
                    }

                }
                catch (SQLException ex){}
                finally
                {
                    if (con != null)
                    {
                        try {
                            con.close();
                        }
                        catch (SQLException ex) {}
                    }
if (sendBack==null){
    sendSMS(messageAddress,"Couldn't find bus route.");
}
                    else{
                sendSMS(messageAddress,sendBack);}
            }
        }
    }}
    public int getMsgrec(){
        //getter for messages sent
        return this.msgrec;
    }
    public int getMsgsent(){
        //getter for messages received
        return this.msgsent;
    }
}