package majicbus.gpsapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;


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
        try {
            List stopsList = parser.fromJson(json, List.class);
            PolylineOptions pOps = new PolylineOptions();
            for (int j = 0; j < stopsList.size(); j++) {
                Map<String, Object> stopMap = (Map) (stopsList).get(j);
                double lat = (Double) stopMap.get("lat");
                double lng = (Double) stopMap.get("lon");

                LatLng point = new LatLng(lat, lng);
                MarkerOptions mOps = new MarkerOptions();

                mOps.title("Stop: " + ((Double) stopMap.get("StopID")).intValue());

                StringBuilder build = new StringBuilder();
                build.append(stopMap.get("StopName"));
                build.append("\nNext 5 Times:\n");
                List TimeList = (List)stopMap.get("Dtimes");
                for(int k = 0; k < TimeList.size(); k++)
                    build.append(TimeList.get(k)).append("\n");

                mOps.position(point);
                mOps.snippet(build.toString());
                mOps.icon(BitmapDescriptorFactory.fromResource(R.mipmap.stop_icon));

                mMap.addMarker(mOps);
                mMap.setInfoWindowAdapter(new MarkerInfoWindowAdapter());
                pOps.add(point);
            }
            pOps.color(Color.BLUE);
            pOps.width(5);
            mMap.addPolyline(pOps);
        }
        catch(JsonSyntaxException ex){
            Log.v("JSON", json);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(49.887952, -119.496011)));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(10));

        //Get data passed from the previous page
        Intent routeData = getIntent();
        routeList = routeData.getStringArrayListExtra("routeData");

        //If there was a route selected, do the stuff.
        if(!routeList.isEmpty()) {
            String url = MainActivity.URL + "/Home/ShowStopsJSON?routeID=" + routeList.get(0);

            HTTPConnection conn = new HTTPConnection(this);
            conn.makeConnection(url);
        }

    }





    public class MarkerInfoWindowAdapter implements GoogleMap.InfoWindowAdapter
    {
        public MarkerInfoWindowAdapter()
        {
        }

        @Override
        public View getInfoWindow(Marker marker){return null;}

        @Override
        public View getInfoContents(Marker marker) {

            View v  = getLayoutInflater().inflate(R.layout.infowindow_layout, null);

            //ImageView markerIcon = (ImageView) v.findViewById(R.mipmap.stop_icon);

            TextView markerLabel = (TextView)v.findViewById(R.id.marker_label);

            //  markerIcon.setImageResource(manageMarkerIcon(myMarker.getmIcon()));

            markerLabel.setText(marker.getTitle() + "\n" +  marker.getSnippet());
            return v;
        }
    }
}
