package majicbus.gpsapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import java.util.List;
import java.util.Map;

/**
 * The StopHandler class is an extension of the DataHandler Class
 * It is used to get the stop data and display it on the map
 */
public class StopHandler extends DataHandler {
    private GoogleMap mMap;
    private LayoutInflater inflater;

    public StopHandler(String url, Context cont, GoogleMap newMap) {
        super(url, cont);
        mMap = newMap;
        inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
    }

    /**
     * Parses the JSON
     * Creates the strings to be displayed on the marker
     * Displays on the map
     */
    @Override
    public void loadData() {
        List routes = parser.fromJson(JSON, List.class);

        for (int i = 0; i < routes.size(); i++) {
            Map<String, Object> route = (Map) (routes.get(i));
            int id = ((Double) route.get("routeID")).intValue();
            List stopsList = (List) route.get("routeStops");

            for (int j = 0; j < stopsList.size(); j++) {
                Map<String, Object> stopMap = (Map) (stopsList.get(j));
                LatLng point = new LatLng((Double) stopMap.get("lat"),(Double) stopMap.get("lon"));
                MarkerOptions mOps = new MarkerOptions();

                mOps.title("Stop: " + ((Double) stopMap.get("StopID")).intValue());
                StringBuilder build = new StringBuilder();
                build.append("Route: ").append(id).append("\n");
                build.append(stopMap.get("StopName"));
                List TimeList = (List) stopMap.get("Dtimes");

                if (TimeList.size() == 0)
                    build.append("");
                else
                    build.append("\nNext ").append(TimeList.size()).append(" Times:\n");

                for (int k = 0; k < TimeList.size(); k++) {
                    build.append(Utility.formatTime((String) TimeList.get(k)));
                    if (k < TimeList.size() - 1)
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
     * Private class for the InfoWindowAdapter,
     * This is required because the Snippet by default does not support
     * multiple lines and \n characters.
     */
    private class MarkerInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
        public MarkerInfoWindowAdapter() {}
        @Override
        public View getInfoWindow(Marker marker){return null;}

        @Override
        public View getInfoContents(Marker marker) {
            View v  = inflater.inflate(R.layout.infowindow_layout, null);
            TextView markerLabel = (TextView)v.findViewById(R.id.marker_label);
            markerLabel.setText(marker.getTitle() + "\n" +  marker.getSnippet());
            return v;
        }
    }
}

