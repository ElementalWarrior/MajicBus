package com.majicbus.sms;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.provider.Telephony;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MainActivity extends AppCompatActivity {

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    private void sendSMS(String phoneNumber, String message) {
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, null, null);
    }
    public void onReceive(Context context, Intent intent) {
//check if action is received text
            if (Telephony.Sms.Intents.SMS_RECEIVED_ACTION.equals(intent.getAction())) {
                for (SmsMessage smsMessage : Telephony.Sms.Intents.getMessagesFromIntent(intent)) {
                    //save msg
                    String messageBody = smsMessage.getMessageBody();
                }
            }

        // TODO Auto-generated method stub
        String url = "tcp:mbdev01.database.windows.net;databaseName=1433;";
        String uid = "majicbus";
        String pw = "asbrbrchja5";
        Connection con = null;
        try {
            con = DriverManager.getConnection(url, uid, pw);
            Statement stmt = con.createStatement();
            String sql = "SELECT  "+
                    " FROM  "+
                    " WHERE  =  ";

            ResultSet rst = stmt.executeQuery(sql);


            while (rst.next())
            {
               //prep to send stuff back
            }
            //send text
            sendSMS("test","test");
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
    }

    }
}
