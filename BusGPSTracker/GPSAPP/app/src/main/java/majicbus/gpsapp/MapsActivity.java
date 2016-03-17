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

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    public void loadData(String json)
    {
        Gson parser = new Gson();
        List stopslist = parser.fromJson(json, List.class);
        PolylineOptions ops = new PolylineOptions();
        for (int j = 0; j < stopslist.size(); j++) {
            Map<String, Object> stopMap = (Map)(stopslist).get(j);
            double lat = (Double)stopMap.get("lat");
            double lng = (Double)stopMap.get("lon");
            String title = String.valueOf(stopMap.get("StopID"));
            LatLng point = new LatLng(lat,lng);
            mMap.addMarker(new MarkerOptions().position(point).title(title));
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
        ArrayList<String> routesList = routeData.getStringArrayListExtra("stopData");
        ArrayList<String> stopsList = routeData.getStringArrayListExtra("stopData");
        mMap = googleMap;

//        String stopsjson = "[{\"StopID\":140073,\"lat\":49.8641830000000000,\"lon\":-119.5629160000000000}," +
//                "{\"StopID\":140071,\"lat\":49.8370550000000000,\"lon\":-119.6142810000000000}," +
//                "{\"StopID\":140049,\"lat\":49.8835080000000000,\"lon\":-119.4888160000000000}," +
//                "{\"StopID\":140007,\"lat\":49.8620850000000000,\"lon\":-119.5852810000000000}," +
//                "{\"StopID\":140004,\"lat\":49.8285650000000000,\"lon\":-119.6310950000000000}," +
//                "{\"StopID\":140003,\"lat\":49.8833670000000000,\"lon\":-119.4764410000000000}," +
//                "{\"StopID\":103745,\"lat\":49.8808960000000000,\"lon\":-119.5346980000000000}," +
//                "{\"StopID\":103455,\"lat\":49.9394320000000000,\"lon\":-119.3948930000000000}," +
//                "{\"StopID\":103263,\"lat\":49.9018450000000000,\"lon\":-119.4081140000000000}," +
//                "{\"StopID\":103174,\"lat\":49.8866540000000000,\"lon\":-119.4243860000000000}," +
//                "{\"StopID\":103067,\"lat\":49.8817380000000000,\"lon\":-119.4421570000000000}," +
//                "{\"StopID\":102985,\"lat\":49.8818080000000000,\"lon\":-119.4591870000000000}," +
//                "{\"StopID\":102853,\"lat\":49.8874220000000000,\"lon\":-119.4953990000000000}]";
//
//        Gson parser = new Gson();
//        List stopslist = parser.fromJson(stopsjson, List.class);
//
//        //Display each route
//        //All this code should work, I cant seem to be able to test it
//        for(int i = 0; i < 1; i++){
//            //Get each stoplist from the arraylist passed over
//            double lastLat = 0;
//            double lastLng = 0;
//            //Display all the stops on a route
//            List stopList = parser.fromJson(stopsjson, List.class);
//            for (int j = 0; j < stopList.size(); j++) {
//                Map<String, Object> stopMap = (Map)(stopList).get(j);
//                double lat = (Double)stopMap.get("lat");
//                double lng = (Double)stopMap.get("lon");
//                String title = String.valueOf(stopMap.get("StopID"));
//                LatLng point = new LatLng(lat,lng);
//                mMap.addMarker(new MarkerOptions().position(point).title(title));
//                if(j > 0){
//                    mMap.addPolyline((new PolylineOptions())
//                            .add(new LatLng(lastLat,lastLng), new LatLng(lat,lng)).width(5).color(Color.BLUE)
//                            .geodesic(true));
//                }
//                lastLat = lat;
//                lastLng = lng;
//            }
//
//        }
//
        // Move the camera to Kelowna
        LatLng Kelowna = new LatLng(49.887952, -119.496011);
        //mMap.addMarker(new MarkerOptions().position(Kelowna).title("Marker in Kelowna!"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(Kelowna));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(10));

        MBBackendConnection conn = new MBBackendConnection(this);
        conn.makeConnection("http://206.87.18.178/Home/ShowStopsJSON");
    }
}
