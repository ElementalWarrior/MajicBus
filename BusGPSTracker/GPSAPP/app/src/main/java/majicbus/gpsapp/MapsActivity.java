package majicbus.gpsapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.graphics.ColorUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.maps.android.SphericalUtil;



import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, OnTaskCompleted {
    private ArrayList<String> routeList;
    private String StopsURL;
    private String ShapesURL;
    private String BusesURL;
    private ArrayList<MarkerOptions> Stops;
    private ArrayList<PolylineOptions> Lines;
    private HashMap<Integer, ArrayList<Marker>> BusHashMap;
    private HashMap<Integer, Integer[]> RouteColors;
    private GoogleMap mMap;
    private boolean StopsRetrieved;
    private boolean ShapesRetrieved;
    private Timer timer;
    private TimerTask task;
    private Marker myPos;
    protected LocationManager locationManager;
    protected LocationListener locationListener;

    /**
     * Facilitates All the HTTP Request data handling
     * First Pass - loads Stops
     * Second Pass - loads Shapes
     * Third and following - Load Bus Data
     * @param response
     */
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
                timer.scheduleAtFixedRate(task, 0,1000); //Schedule Bus requests
            } catch (JsonSyntaxException ex) {
                Log.v("Dirty JSON", response); //Redo
                HTTPConnection conn = new HTTPConnection(this);
                conn.makeConnection(ShapesURL);
            }

        }else if(StopsRetrieved&&ShapesRetrieved){
            try { //Then if the stops data is loaded and the shape data is loaded, load bus data
                List buses = parser.fromJson(response, List.class);
                loadBusData(buses);
            } catch (JsonSyntaxException ex) {
                Log.v("Dirty JSON", response);
                HTTPConnection conn = new HTTPConnection(this);
                conn.makeConnection(BusesURL);
            }
        }
    }

    /**
     * Setup all the variables
     * @param savedInstanceState - doesn't really do much as far as I know
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        StopsRetrieved = false;
        ShapesRetrieved = false;
        BusHashMap = new HashMap<Integer, ArrayList<Marker>>();
        RouteColors = new HashMap<Integer, Integer[]>();

        initLocationListener();

        //Create timer to refresh bus locations
        final Context con = this;
        timer = new Timer();
        task = new TimerTask() {
            @Override
            public void run() {
                if(StopsRetrieved && ShapesRetrieved) {
                    HTTPConnection conn = new HTTPConnection(con);
                    conn.makeConnection(BusesURL);
                }
            }
        };

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * OnPause stop everything on the timer
     */
    @Override
    public void onPause(){
        super.onPause();
        if(timer != null) {
            timer.cancel();
            timer.purge();
        }
            try{locationManager.removeUpdates(locationListener);}
            catch(SecurityException e){Log.v("GPSClose:", "Failed to get GPS Access");}
    }

    /**
     * OnStop stop everything on the timer
     */
    @Override
    public void onStop(){
        super.onStop();
        if(timer != null) {
            timer.cancel();
            timer.purge();
        }
            try{locationManager.removeUpdates(locationListener);}
            catch(SecurityException e){Log.v("GPSClose:", "Failed to get GPS Access");}
    }

    /**
     * Sets up the map, moves the camera,
     * Gets a route list, and makes the first HTTP request
     * @param googleMap
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(49.887952, -119.496011)));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(10));

        //Get data passed from the previous page
        Intent routeData = getIntent();
        routeList = routeData.getStringArrayListExtra("routeData");

        //Create my position marker
        MarkerOptions ops = new MarkerOptions();
        ops.position(new LatLng(0, 0));
        ops.visible(false);
        ops.title("Your Position").snippet("");
        ops.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        myPos = mMap.addMarker(ops);

        //If there was a route selected, do the stuff.
        if(!routeList.isEmpty()) {
            makeUrls();
            HTTPConnection conn = new HTTPConnection(this);
            conn.makeConnection(StopsURL);
        }
    }

    /**
     * Loads stop data and displays it on the map
     * Also generates the map marker information
     * @param routes
     */
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
                List TimeList = (List) stopMap.get("Dtimes");
                if(TimeList.size() == 0)
                    build.append("");
                else
                    build.append("\nNext " + TimeList.size() +" Times:\n");

                for (int k = 0; k < TimeList.size(); k++) {
                    build.append(formatTime((String) TimeList.get(k)));
                    if(k < TimeList.size() -1 )
                        build.append("\n");
                }

                mOps.position(point);
                mOps.snippet(build.toString());
                mOps.icon(BitmapDescriptorFactory.fromResource(R.mipmap.stop_icon));

                mMap.addMarker(mOps);
                mMap.setInfoWindowAdapter(new MarkerInfoWindowAdapter());

            }
        }
    }

    /**
     * Loads route shapes on the map
     * Splits up routes that have multiple shapes
     * Randomly generates a colour and stores it for each route
     * @param shapeList list of routes of shapes
     */
    public void loadShapeData(List shapeList) {
        for (int i = 0; i < shapeList.size(); i++) {
            Map<String, Object> shape = (Map) (shapeList).get(i);
            int id = ((Double) shape.get("routeID")).intValue();

            //Random Colours for each line
            int R = (int) (Math.random() * 256);
            int G = (int) (Math.random() * 256);
            int B = (int) (Math.random() * 256);

            Integer RGB[] = new Integer[3];
            RGB[0] = R; RGB[1] = G; RGB[2] = B;

            RouteColors.put(id,RGB);
            PolylineOptions pOps = new PolylineOptions();

            int lastId = 0;
            List shapesList = (List) shape.get("Shape");
            for (int j = 0; j < shapesList.size(); j++) {
                Map<String, Object> shapes = (Map) (shapesList).get(j);
                double lat = (Double) shapes.get("Lat");
                double lng = (Double) shapes.get("Lon");

                int sid = ((Double)shapes.get("sID")).intValue();
                if(j > 0 && sid != lastId){
                    pOps.color(Color.rgb(R, G, B));
                    pOps.width(5);
                    mMap.addPolyline(pOps);
                    pOps = new PolylineOptions();
                }
                pOps.add(new LatLng(lat,lng));
                lastId = sid;
            }

            pOps.color(Color.rgb(R, G, B));
            pOps.width(5);
            mMap.addPolyline(pOps);
        }
    }

    /**
     * Loads and updates bus position data
     * @param routeList list of routes of buses to display
     */
    public void loadBusData(List routeList){
        for (int i = 0; i < routeList.size(); i++) {
            Map<String, Object> busList = (Map) (routeList).get(i);
            int id = ((Double) busList.get("routeID")).intValue();
            List buses = (List)busList.get("Buses");

            if(!BusHashMap.containsKey(id)) //There aren't any buses on the map
                makeBuses(id,buses);
            else { //Update the buses
                ArrayList<Marker> marks = BusHashMap.get(id);
                //If there are the same number of buses fetched
                if(buses.size() == marks.size())
                    updateBuses(id,buses);
                else{//Remove all the buses and remake
                    for(int j = 0; j < marks.size(); j++)
                        marks.get(j).remove();
                    makeBuses(id,buses);
                }

            }
        }
    }

    private void makeBuses(int id, List buses){
        ArrayList<Marker>  marks = new ArrayList<Marker>();
        for (int j = 0; j < buses.size(); j++) {
            Map<String, Object> busMap = (Map) (buses).get(j);
            double lat = (Double) busMap.get("Lat");
            double lng = (Double) busMap.get("Lon");

            //draw markers
            MarkerOptions ops = new MarkerOptions();
            ops.position(new LatLng(lat, lng));
            ops.title(String.valueOf(id));
            ops.snippet("0%");
            Integer RGB[] = RouteColors.get(id);
            float HSL[] = new float[3];
            ColorUtils.RGBToHSL(RGB[0],RGB[1],RGB[2],HSL);

            ops.icon(BitmapDescriptorFactory.defaultMarker(HSL[0]));
            Marker mark = mMap.addMarker(ops);
            marks.add(mark);
        }
        BusHashMap.put(id, marks); //Keep track of the buses added to the map
    }

    private void updateBuses(int id, List buses){
        ArrayList<Marker>  marks = BusHashMap.get(id);
        for (int i = 0; i < buses.size(); i++) {
            Map<String, Object> busMap = (Map) (buses).get(i);
            LatLng newBusPos = new LatLng((Double) busMap.get("Lat"),(Double) busMap.get("Lon"));
            double min = Double.MAX_VALUE;
            Marker closestBus = null;

            for(int j = 0; j < marks.size(); j++){
                Marker mark = marks.get(j);
                double tempMin = SphericalUtil.computeDistanceBetween(newBusPos,mark.getPosition());
                if(tempMin < min) {
                    closestBus = mark;
                    min = tempMin;
                }
            }
            animateMarker(closestBus, newBusPos, false);
        }
    }

    /**
     * Makes all the URLS needed by the activity
     */
    private void makeUrls(){
        StringBuilder StopsBuilder = new StringBuilder();
        StringBuilder ShapesBuilder = new StringBuilder();
        StringBuilder BusesBuilder = new StringBuilder();
        StringBuilder routeListBuilder = new StringBuilder();

        StopsBuilder.append(MainActivity.URL).append("/Home/ShowStopsJSON?");
        ShapesBuilder.append(MainActivity.URL).append("/Home/ShowShapesJSON?");
        BusesBuilder.append(MainActivity.URL).append("/Home/ShowBusPositionsJSON?");
        for(int i = 0; i < routeList.size(); i++){
            routeListBuilder.append("routeIDs[").append(i).append("]=").append(routeList.get(i));
            if(i < routeList.size() -1)
                routeListBuilder.append("&");
        }
        String routes = routeListBuilder.toString();
        StopsBuilder.append(routes);
        ShapesBuilder.append(routes);
        BusesBuilder.append(routes);

        StopsURL = StopsBuilder.toString();
        ShapesURL = ShapesBuilder.toString();
        BusesURL = BusesBuilder.toString();
    }

    /**
     * Formats time correctly for the map markers
     * @param Time string
     * @return string formatted in 12H time
     */
    public String formatTime(String Time) {
        StringBuilder build = new StringBuilder();
        int hour = Integer.valueOf(Time.substring(0,2));
        String min = Time.substring(3,5);
        if(hour > 12){
            hour -= 12;
            if(hour > 12)
                hour -=11;
            if(hour == 12)
                build.append(hour).append(":").append(min).append(" AM");
            else
                build.append(hour).append(":").append(min).append(" PM");
        }
        else
            build.append(hour).append(":").append(min).append(" AM");

        return build.toString();
    }

    /**
     * Move a Map Marker from a position to the Final Position
     * @param marker
     * @param toPosition
     * @param hideMarker
     */
    public void animateMarker(final Marker marker, final LatLng toPosition, final boolean hideMarker) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        Projection proj = mMap.getProjection();
        Point startPoint = proj.toScreenLocation(marker.getPosition());
        final LatLng startLatLng = proj.fromScreenLocation(startPoint);
        final long duration = 1000;
        final Interpolator interpolator = new LinearInterpolator();

        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed / duration);
                double lng = t * toPosition.longitude + (1 - t) * startLatLng.longitude;
                double lat = t * toPosition.latitude + (1 - t) * startLatLng.latitude;
                marker.setPosition(new LatLng(lat, lng));

                if (t < 1.0)  // Post again 16ms later.
                    handler.postDelayed(this, 16);
                 else {
                    if (hideMarker)
                        marker.setVisible(false);
                     else
                        marker.setVisible(true);
                }
            }
        });
    }

    private void initLocationListener(){
        //Location Listener code
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                LatLng point = new LatLng(location.getLatitude(),location.getLongitude());
                if(myPos.isVisible())
                    animateMarker(myPos,point,false);
                else {
                    myPos.setPosition(point);
                    myPos.setVisible(true);
                }
            }
            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {}
            @Override
            public void onProviderEnabled(String provider) {
                //create marker
                MarkerOptions ops = new MarkerOptions();
                ops.position(new LatLng(0,0));
                ops.visible(false);
                ops.title("Your Position").snippet("");
                ops.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                myPos = mMap.addMarker(ops);
            }
            @Override
            public void onProviderDisabled(String provider) {if(myPos != null)myPos.remove();}
        };

        //User Location code
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        try {locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);}
        catch(SecurityException e){Log.v("GPSOpen:", "Failed to get GPS Access");}
    }

    /**
     * Class for the message box for each map marker
     */
    public class MarkerInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
        public MarkerInfoWindowAdapter() {}
        @Override
        public View getInfoWindow(Marker marker){return null;}
        @Override
        public View getInfoContents(Marker marker) {
            View v  = getLayoutInflater().inflate(R.layout.infowindow_layout, null);
            TextView markerLabel = (TextView)v.findViewById(R.id.marker_label);
            markerLabel.setText(marker.getTitle() + "\n" +  marker.getSnippet());
            return v;
        }
    }
}
