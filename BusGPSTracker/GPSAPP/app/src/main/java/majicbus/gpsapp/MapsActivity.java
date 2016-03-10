package majicbus.gpsapp;

import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

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

import java.util.ArrayList;

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
    @Override
    public void onMapReady(GoogleMap googleMap) {
        //Get data passed from the previous page
        Intent routeData = getIntent();
        ArrayList<String> routesList = routeData.getStringArrayListExtra("routeData");
        mMap = googleMap;

        //Display each route
        for(int i = 0; i < routesList.size(); i++){
            String route = routesList.get(i);
            String stopsString = route.substring(route.indexOf("stops:"));
            String[] stopArr = stopsString.split(",");

            double lastLat = 0;
            double lastLng = 0;
            //Display all the stops on a route
            for(int j = 0; j < stopArr.length; j++){
                double lat = (double)stopArr[j].indexOf("lat:");
                double lng = (double)stopArr[j].indexOf("lng:");
                LatLng point = new LatLng(lat,lng);
                mMap.addMarker(new MarkerOptions().position(point));
                if(j > 0){
                    mMap.addPolyline((new PolylineOptions())
                            .add(new LatLng(lastLat,lastLng), new LatLng(lat,lng)).width(5).color(Color.BLUE)
                            .geodesic(true));
                }
                lastLat = lat;
                lastLng = lng;
            }
        //theres a good chance none of this works.

        }

        // Add a marker in Kelowna and move the camera
        LatLng Kelowna = new LatLng(49.887952, -119.496011);
        //mMap.addMarker(new MarkerOptions().position(Kelowna).title("Marker in Kelowna!"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(Kelowna));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(10));
    }
}
