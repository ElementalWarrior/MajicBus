package com.majicbus.sms;

import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.telephony.TelephonyManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.provider.Telephony;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.JsonReader;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnTaskCompleted {
    public static int TotalReceived;
    public static int TotalSent;
    private SmsListener intentreceiver;

    //  @Override
    TextView textView4;
    TextView textView5;

    public void onTaskCompleted(String response) {

        //TODO: parse json for message body and phone number


        String body = "";
        //TODO: parse the stop times from the response and turn into string max 128 characters long

        //TODO: send to phone number the times

        Gson parser = new Gson();
        JsonParser jp = new JsonParser();
        JsonElement jelement = jp.parse(response);
        JsonObject base = jelement.getAsJsonObject();
        try {
            String phone = base.get("Phone").getAsString();

            JsonArray jarray = null;
            HashMap<Integer, ArrayList> routes = new HashMap<Integer, ArrayList>();
            try {
                jarray = base.get("Data").getAsJsonArray();
            } catch (Exception e) {
                e.printStackTrace();
            }
            for(int i = 0; i < jarray.size(); i++)
            {
                JsonObject obj;
                try {
                    obj  = (JsonObject) jarray.get(i);
                    int RouteID = obj.get("RouteID").getAsInt();
                    JsonArray times = obj.get("Dtimes").getAsJsonArray();
                    ArrayList sTimes = new ArrayList();
                    Iterator<JsonElement> iter = times.iterator();
                    int j = 0;
                    while(iter.hasNext())
                    {
                        String time = iter.next().getAsString();
                        sTimes.add(time);
                        j++;
                    }
                    routes.put(RouteID, sTimes);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            SmsListener.sendSMS(phone, "hihi");
        } catch (Exception e) {
            e.printStackTrace();
        }
        //String pNum = jobject.get("Phone").toString();


    }

    protected void onCreate(Bundle savedInstanceState) {


        String a = Integer.toString(TotalReceived);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView4 = (TextView) findViewById(R.id.textView4);
        textView5 = (TextView) findViewById(R.id.textView5);
        textView4.setText("sent");
        textView5.setText("rec");


    }

    @Override
    protected void onResume() {
        super.onResume();

        intentreceiver = new SmsListener();
        intentreceiver.setActivity(this);
    }
    public void GetStopInformation(String body, String address)
    {
        HTTPConnection conn = new HTTPConnection(this);
        String number = ((TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE)).getLine1Number();
        conn.makeConnection("http://192.168.1.11/Sms/Receive?from=" + address + "&to=" + number + "&body=" + body, "");
    }


    @Override
    protected void onPause() {

        super.onPause();
    }


}