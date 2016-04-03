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
    private String ShapesURL;
    private HashMap<Integer, MarkerOptions> BusHashMap; //possible way to keep track of buses?
    private GoogleMap mMap;
    private boolean StopsRetrieved;
    private boolean ShapesRetrieved;

    @Override
    public void onTaskCompleted(String response){
        Gson parser = new Gson();
        if(!StopsRetrieved) { //First get the stops data and load to map
            try {
                List stops = parser.fromJson(response, List.class);
                StopsRetrieved = true;
                loadStopData(stops);

                HTTPConnection conn = new HTTPConnection(this); //Load Shapes
                conn.makeConnection(ShapesURL);
            } catch (JsonSyntaxException ex) {
                Log.v("Dirty JSON", response); //Redo
                HTTPConnection conn = new HTTPConnection(this);
                conn.makeConnection(StopsURL);
            }

        }else if(!ShapesRetrieved){
            try { //Then if the stops data is loaded load the shapes data
                List shapes = parser.fromJson(response, List.class);
                ShapesRetrieved = true;
                loadShapeData(shapes);
            } catch (JsonSyntaxException ex) {
                Log.v("Dirty JSON", response); //Redo
                HTTPConnection conn = new HTTPConnection(this);
                conn.makeConnection(ShapesURL);
            }

        }else if(StopsRetrieved&&ShapesRetrieved){
            /* Bus Code!
            try { //Then if the stops data is loaded load the shapes data
                List shapes = parser.fromJson(response, List.class);
                Shapes = response;
                loadShapeData(shapes);
            } catch (JsonSyntaxException ex) {
                Log.v("Dirty JSON", response);
                HTTPConnection conn = new HTTPConnection(this);
                conn.makeConnection(ShapesURL);
            }
            */
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        StopsRetrieved = false;
        ShapesRetrieved = false;
        BusHashMap = new HashMap<Integer, MarkerOptions>();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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
            makeUrls();
            HTTPConnection conn = new HTTPConnection(this);
            conn.makeConnection(StopsURL);
        }

    }

    public void loadStopData(List routes){

        for(int i = 0; i < routes.size();i++) {
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

            }
        }
    }

    public void loadShapeData(List shapeList) {
        for (int i = 0; i < shapeList.size(); i++) {
            Map<String, Object> shape = (Map) (shapeList).get(i);
            int id = ((Double) shape.get("routeID")).intValue();
            PolylineOptions pOps = new PolylineOptions();

            List shapesList = (List) shape.get("Shape");
            for (int j = 0; j < shapesList.size(); j++) {
                Map<String, Object> shapes = (Map) (shapesList).get(j);
                double lat = (Double) shapes.get("Lat");
                double lng = (Double) shapes.get("Lon");
                LatLng point = new LatLng(lat, lng);
                pOps.add(point);
            }

            //Random Colours for each line
            int R = (int) (Math.random() * 256);
            int G = (int) (Math.random() * 256);
            int B = (int) (Math.random() * 256);
            pOps.color(Color.rgb(R, G, B));

            //Color the bus marker to be the same colour as the line
            //if(!BusHashMap.containsKey(id) || BusHashMap.get(id) == null)
            //    BusHashMap.put(id, new MarkerOptions());
            // BusHashMap.get(id).icon(BitmapDescriptorFactory.defaultMarker(Color.rgb(R, G, B)));

            pOps.width(5);
            mMap.addPolyline(pOps);
        }
    }

    private void makeUrls(){
        StringBuilder StopsBuilder = new StringBuilder();
        StringBuilder ShapesBuilder = new StringBuilder();
        StringBuilder routeListBuilder = new StringBuilder();

        StopsBuilder.append(MainActivity.URL).append("/Home/ShowStopsJSON?");
        ShapesBuilder.append(MainActivity.URL).append("/Home/ShowShapesJSON?");
        for(int i = 0; i < routeList.size(); i++){
            routeListBuilder.append("routeIDs[").append(i).append("]=").append(routeList.get(i));
            if(i < routeList.size() -1)
                routeListBuilder.append("&");
        }
        StopsBuilder.append(routeListBuilder.toString());
        ShapesBuilder.append(routeListBuilder.toString());
        StopsURL = StopsBuilder.toString();
        ShapesURL = ShapesBuilder.toString();
    }

    public String formatTime(String Time) {
        StringBuilder build = new StringBuilder();
        int hour = Integer.valueOf(Time.substring(0,2));
        String min = Time.substring(3,5);
        if(hour > 12){
            hour -= 12;
            build.append(hour).append(":").append(min).append(" PM");
        }
        else
            build.append(hour).append(":").append(min).append(" AM");

        return build.toString();
    }

    public class MarkerInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
        public MarkerInfoWindowAdapter() {}
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
