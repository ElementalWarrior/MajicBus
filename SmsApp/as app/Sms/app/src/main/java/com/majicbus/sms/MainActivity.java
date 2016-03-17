package com.majicbus.sms;
import android.telephony.TelephonyManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.provider.Telephony;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MainActivity extends AppCompatActivity {

  //  @Override
int smssent=0;
    int smsrec=0;
    TextView textView1;
    TextView textView2;

    protected void onCreate(Bundle savedInstanceState) {
        textView1 = (TextView) findViewById(R.id.textView4);
        textView2 = (TextView) findViewById(R.id.textView5);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
public void testClick(View v)
    {
       smssent++;
       // textView1.setText(smssent);
        PendingIntent pi = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class), 0);
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage("17789961163", null, "test msg", null, null);
    }
    /*
    private void sendSMS(String phoneNumber, String message) {
        smssent++;
        textView1.setText(smssent);
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, null, null);
    }
    /*
    public void onReceive(Context context, Intent intent) {
//check if action is received text
        if (Telephony.Sms.Intents.SMS_RECEIVED_ACTION.equals(intent.getAction())) {
            smsrec++;
            textView1.setText(smsrec);
            for (SmsMessage smsMessage : Telephony.Sms.Intents.getMessagesFromIntent(intent)) {
                //save msg
                String messageBody = smsMessage.getMessageBody();
            }
            sendSMS("12508696381", "test msg");

        }

        // this will be done by db later
        /*
        String url = "tcp:mbdev01.database.windows.net,1433;";
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
    }*/


    }