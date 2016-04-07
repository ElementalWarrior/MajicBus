package com.majicbus.sms;

import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
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
import android.widget.ImageView;
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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
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
import java.util.Map;

public class MainActivity extends AppCompatActivity implements OnTaskCompleted {
//    public static final String URL = "http://majicbus.azurewebsites.net";
    public static final String URL = "http://majicbus.azurewebsites.net";
    public static int TotalReceived;
    public static int TotalSent;
    private SmsListener intentreceiver;

    public void onTaskCompleted(String response, String type) {
        if(type == "MsgReceived") {
            JsonParser jp;
            JsonElement jelement;
            JsonObject base;
            String phone;
            try
            {
                jp = new JsonParser();
                jelement = jp.parse(response);
                base = jelement.getAsJsonObject();

                phone = base.get("Phone").getAsString();
            }
            catch(Exception ex)
            {
                return;
            }
            try {

                JsonArray jarray = null;
                HashMap<Integer, ArrayList> routes = new HashMap<Integer, ArrayList>();
                jarray = base.get("Data").getAsJsonArray();
                if (jarray == null || jarray.size() == 0)
                {

                    String body = "There was no data associated with that stop number.";
                    SmsListener.sendSMS(phone, body);
                    try {
                        body = URLEncoder.encode(body, "UTF-8");
                    } catch (UnsupportedEncodingException ex) {
                        ex.printStackTrace();
                    }
                    LogMessageSent(body, phone);
                    return;
                }
                for (int i = 0; i < jarray.size(); i++) {
                    JsonObject obj;
                    try {
                        obj = (JsonObject) jarray.get(i);
                        int RouteID = obj.get("RouteID").getAsInt();
                        JsonArray times = obj.get("Dtimes").getAsJsonArray();
                        ArrayList sTimes = new ArrayList();
                        Iterator<JsonElement> iter = times.iterator();
                        int j = 0;
                        while (iter.hasNext()) {
                            String time = iter.next().getAsString();
                            sTimes.add(time);
                            j++;
                        }
                        routes.put(RouteID, sTimes);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                Object[] keys = routes.keySet().toArray();

                int currentKey = (int) keys[0];
                int i = 0;
                int timeIndexToAppend = 0;
                String smsResponse = "";
                String[] routetimes = new String[keys.length];
                while (smsResponse.length() < 128 && timeIndexToAppend < 5) {
                    currentKey = (int) keys[i];
                    if (timeIndexToAppend == 0) {
                        routetimes[i] = "{" + currentKey + "} ";
                    }
                    try {
                        String t = routes.get(currentKey).get(timeIndexToAppend).toString();
                        int lastColon = t.lastIndexOf(":");
                        t = t.substring(0, lastColon);
                        if ((smsResponse + t + ",").length() > 128) {
                            break;
                        } else {
                            routetimes[i] += t + ",";
                        }
                    } catch (IndexOutOfBoundsException ex) {
                    }
                    i++;

                    if (i > keys.length - 1) {
                        i = 0;
                        timeIndexToAppend++;
                    }
                }
                for (i = 0; i < keys.length; i++) {
                    smsResponse += routetimes[i].substring(0, routetimes[i].length() - 1) + " ";
                }
                SmsListener.sendSMS(phone, smsResponse);
                try {
                    smsResponse = URLEncoder.encode(smsResponse, "UTF-8");
                } catch (UnsupportedEncodingException ex) {
                    ex.printStackTrace();
                }
                LogMessageSent(smsResponse, phone);
            } catch (Exception e) {
                String body = "There was a problem contacting the server. Please try again.";
                SmsListener.sendSMS(phone, body);
                try {
                    body = URLEncoder.encode(body, "UTF-8");
                } catch (UnsupportedEncodingException ex) {
                    ex.printStackTrace();
                }
                LogMessageSent(body, phone);
                e.printStackTrace();
            }
        }
    }

    protected void onCreate(Bundle savedInstanceState) {


        String a = Integer.toString(TotalReceived);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView bg = ((ImageView) findViewById(R.id.backgroundImage));
        bg.setImageResource(R.drawable.busapp);
        Point p = new Point();
        getWindowManager().getDefaultDisplay().getSize(p);

        BitmapFactory.Options dimensions = new BitmapFactory.Options();
        dimensions.inJustDecodeBounds = true;
        Bitmap mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.busapp, dimensions);
        int height = dimensions.outHeight;
        int width =  dimensions.outWidth;

        double imageScale = p.x / width;
        bg.setMaxWidth(p.x);
        bg.setMinimumWidth(p.x);
        bg.setMinimumHeight((int) (height * imageScale));
        bg.setMaxHeight((int) (height * imageScale));


    }

    @Override
    protected void onResume() {
        super.onResume();

        intentreceiver = new SmsListener();
        intentreceiver.setActivity(this);
    }
    public void GetStopInformation(String body, String address)
    {
        String number = ((TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE)).getLine1Number();
        HTTPConnection conn = new HTTPConnection(this, "MsgReceived", address);
        conn.makeConnection(URL + "/Sms/Receive?from=" + address.trim() + "&to=" + number.trim() + "&body=" + body);
    }
    public void LogMessageSent(String body, String address)
    {
        HTTPConnection conn = new HTTPConnection(this, "LogSent");
        String number = ((TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE)).getLine1Number();
        conn.makeConnection(URL + "/Sms/LogMessageSent?from=" + number.trim() + "&to=" + address.trim()+ "&body=" + body);
    }


    @Override
    protected void onPause() {

        super.onPause();
    }


}