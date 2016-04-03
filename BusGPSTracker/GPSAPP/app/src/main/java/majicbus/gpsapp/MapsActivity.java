package majicbus.gpsapp;

import android.content.Intent;

import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, OnTaskCompleted {
    private ArrayList<String> routeList;
    private String StopsURL;
    private HashMap<Integer, MarkerOptions> BusHashMap; //possible way to keep track of buses?
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
            List routes = parser.fromJson(json, List.class);

            for(int i = 0; i < routes.size();i++) {
                PolylineOptions pOps = new PolylineOptions();
                Map<String, Object> route = (Map) (routes).get(i);

                int id = ((Double)route.get("routeID")).intValue();
                List stopsList = (List)route.get("routeStops");
                for (int j = 0; j < stopsList.size(); j++) {
                    Map<String, Object> stopMap = (Map) (stopsList).get(j);
                    double lat = (Double) stopMap.get("lat");
                    double lng = (Double) stopMap.get("lon");

                    LatLng point = new LatLng(lat, lng);
                    MarkerOptions mOps = new MarkerOptions();

                    mOps.title("Stop: " + ((Double) stopMap.get("StopID")).intValue());

                    StringBuilder build = new StringBuilder();
                    build.append("Route: ").append(id).append("\n");
                    build.append(stopMap.get("StopName"));
                    build.append("\nNext 5 Times:\n");
                    List TimeList = (List) stopMap.get("Dtimes");
                    for (int k = 0; k < TimeList.size(); k++)
                        build.append(formatTime((String) TimeList.get(k))).append("\n");

                    mOps.position(point);
                    mOps.snippet(build.toString());
                    mOps.icon(BitmapDescriptorFactory.fromResource(R.mipmap.stop_icon));

                    mMap.addMarker(mOps);
                    mMap.setInfoWindowAdapter(new MarkerInfoWindowAdapter());
                    pOps.add(point);
                }

                //Random Colours for each line
                int R = (int)(Math.random()*256);
                int G = (int)(Math.random()*256);
                int B = (int)(Math.random()*256);
                pOps.color(Color.rgb(R, G, B));

                //pOps.color(Color.BLUE); old code
                pOps.width(5);
                mMap.addPolyline(pOps);
            }

        }
        catch(JsonSyntaxException ex){
            Log.v("Dirty JSON", json);
            HTTPConnection conn = new HTTPConnection(this);
            conn.makeConnection(StopsURL);

        }
    }

    public String formatTime(String Time) {
        StringBuilder build = new StringBuilder();
        int hour = Integer.valueOf(Time.substring(0,2));
        int min = Integer.valueOf(Time.substring(3,5));
        if(hour > 12){
            hour -= 12;
            build.append(hour).append(":").append(min).append(" PM");
        }
        else
            build.append(hour).append(":").append(min).append(" AM");

        return build.toString();
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
            StringBuilder build = new StringBuilder();
            build.append(MainActivity.URL).append("/Home/ShowStopsJSON?");
            for(int i = 0; i < routeList.size(); i++){
                build.append("routeIDs[").append(i).append("]=").append(routeList.get(i));
                if(i < routeList.size() -1)
                    build.append("&");
            }
            StopsURL = build.toString();
            HTTPConnection conn = new HTTPConnection(this);
            conn.makeConnection(StopsURL);
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
