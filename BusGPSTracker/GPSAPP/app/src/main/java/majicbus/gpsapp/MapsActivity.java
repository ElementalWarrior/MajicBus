package majicbus.gpsapp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.graphics.ColorUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;
import android.widget.Toast;

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
    private BusHandler busHandler;
    private ShapeHandler shapeHandler;
    private StopHandler stopHandler;
    private HashMap<Integer, Integer[]> RouteColors;
    private GoogleMap mMap;
    private Timer timer;
    private TimerTask task;
    private Marker myPos;
    protected LocationManager locationManager;
    protected LocationListener locationListener;
    public static int updateFrequency = 2000;

    @Override
    public void onTaskCompleted(DataHandler handler){handler.loadData();}

    /**
     * Setup all the variables
     * @param savedInstanceState - doesn't really do much as far as I know
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        //Get data passed from the previous activity
        Intent routeData = getIntent();
        routeList = routeData.getStringArrayListExtra("routeData");
        RouteColors = Utility.randomColorGen(routeList);


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] granted) {
        if (requestCode == 0) {
            initLocationListener();
        }
        InitTimer();
    }
    private void InitTimer()
    {
        timer = new Timer();
        task = new TimerTask() {
            @Override
            public void run() {
                HTTPConnection conn = new HTTPConnection(busHandler);
                conn.makeConnection();
            }
        };
        //Get Bus Positions
        timer.scheduleAtFixedRate(task, 0, updateFrequency);
    }

    @Override
    public void onResume(){
        super.onResume();

        if(mMap != null) {
            InitTimer();
        }
    }

    /**
     * OnPause stop everything on the timer
     */
    @Override
    public void onPause(){
        super.onPause();
        destroy();
    }

    /**
     * OnStop stop everything on the timer
     */
    @Override
    public void onStop(){
        super.onStop();
        destroy();
    }

    private void destroy(){
        if(timer != null) {
            timer.cancel();
            timer.purge();
        }
        if(locationManager != null) {
            try {
                locationManager.removeUpdates(locationListener);
            } catch (SecurityException e) {
                Log.v("GPSClose:", "Failed to get GPS Access");
                Toast.makeText(this, "Unable to access location", Toast.LENGTH_LONG).show();
            }
        }
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

        //Bus Data Handler
        String url = Utility.makeUrl("/Home/ShowBusPositionsJSON?", routeList);
        busHandler = new BusHandler(url,this,RouteColors,mMap);

        //Create my position marker
        MarkerOptions ops = new MarkerOptions();
        ops.position(new LatLng(0, 0));
        ops.visible(false);
        ops.title("Your Position").snippet("");
        ops.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        myPos = mMap.addMarker(ops);

        //If there was a route selected, do the fetch the stops.
        if(!routeList.isEmpty()) {
            //Get Stops
            url = Utility.makeUrl("/Home/ShowStopsJSON?", routeList);
            stopHandler = new StopHandler(url, this,mMap);
            HTTPConnection conn = new HTTPConnection(stopHandler);
            conn.makeConnection();

            //Get Route Shapes
            url = Utility.makeUrl("/Home/ShowShapesJSON?", routeList);
            shapeHandler = new ShapeHandler(url, this,RouteColors, mMap);
            conn = new HTTPConnection(shapeHandler);
            conn.makeConnection();

        }

        if (Build.VERSION.SDK_INT < 23) {
            initLocationListener();

            InitTimer();
        } else {
            int reqFine = 0;
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION
            }, reqFine);
        }
    }

    private void initLocationListener(){
        //Location Listener code
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                LatLng point = new LatLng(location.getLatitude(),location.getLongitude());
                if(myPos.isVisible())
                    Utility.animateMarker(mMap, myPos, point, false, updateFrequency);
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
                if(myPos != null)
                {
                    myPos.remove();
                }
                myPos = mMap.addMarker(ops);
            }
            @Override
            public void onProviderDisabled(String provider) {if(myPos != null)myPos.remove();}
        };

        //User Location code
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        try {

        MarkerOptions ops = new MarkerOptions();
        Location loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        ops.position(new LatLng(loc.getLatitude(), loc.getLongitude()));
        ops.title("Your Position").snippet("");
        ops.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        myPos = mMap.addMarker(ops);

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);}
        catch(SecurityException e){
            Log.v("GPSOpen:", "Failed to get GPS Access");
            Toast.makeText(this, "Unable to access location", Toast.LENGTH_LONG).show();
        }
    }
}
