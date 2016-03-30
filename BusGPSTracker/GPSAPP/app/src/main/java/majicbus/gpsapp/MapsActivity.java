package majicbus.gpsapp;

import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;


import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, OnTaskCompleted {
    private ArrayList<String> routeList;
    private GoogleMap mMap;

    @Override
    public void onTaskCompleted(String response){
        loadData(response);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    public void loadData(String json){
        Gson parser = new Gson();
        List stopsList = parser.fromJson(json, List.class);
        PolylineOptions ops = new PolylineOptions();
        for (int j = 0; j < stopsList.size(); j++) {
            Map<String, Object> stopMap = (Map)(stopsList).get(j);
            double lat = (Double)stopMap.get("lat");
            double lng = (Double)stopMap.get("lon");

            //Need to add stop times and route number
            StringBuilder build = new StringBuilder();

            build.append("Stop: ");
            build.append(((Double)stopMap.get("StopID")).intValue());
            LatLng point = new LatLng(lat,lng);
            mMap.addMarker(new MarkerOptions().position(point).title(build.toString()));
            ops.add(point);
        }
        ops.color(Color.BLUE);
        ops.width(5);
        mMap.addPolyline(ops);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        //Get data passed from the previous page
        Intent routeData = getIntent();
        routeList = routeData.getStringArrayListExtra("routeData");

        //For testing purposes, tests the listeners.
        for(int i = 0; i < routeList.size(); i++)
            Log.v("Route", routeList.get(i));


        mMap = googleMap;
//
        // Move the camera to Kelowna
        LatLng Kelowna = new LatLng(49.887952, -119.496011);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(Kelowna));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(10));

        HTTPConnection conn = new HTTPConnection(this);
        conn.makeConnection("http://192.168.1.19/Home/ShowStopsJSON");

    }
}
