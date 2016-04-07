package majicbus.gpsapp;

import android.graphics.Point;
import android.os.Handler;
import android.os.SystemClock;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import java.util.ArrayList;
import java.util.HashMap;

public abstract class Utility {

    /**
     * Formats time correctly for the map markers
     * @param Time string
     * @return string formatted in 12H time
     */
    public static String formatTime(String Time) {
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
     * Generate random colours for the routes
     * @param routes - list of routes for colours to be generated
     * @return - returns a hashmap of route colours
     */
    public static HashMap<Integer, Integer[]> randomColorGen(ArrayList<String> routes){
        HashMap<Integer, Integer[]> ColourMap = new HashMap<Integer, Integer[]>();
        for(int i = 0; i < routes.size();i++) {
            Integer RGB[] = new Integer[3];
            for(int j = 0; j < 3; j++)
                RGB[j] = (int) (Math.random() * 256);

            int id = Integer.valueOf(routes.get(i));
            ColourMap.put(id, RGB);
        }
        return ColourMap;
    }

    /**
     * Makes a URL with a list of routes
     */
    public static String makeUrl(String extension, ArrayList<String> routes){
        StringBuilder build = new StringBuilder();
        StringBuilder routeListBuilder = new StringBuilder();
        build.append(MainActivity.URL).append(extension);

        for(int i = 0; i < routes.size(); i++){
            routeListBuilder.append("routeIDs[").append(i).append("]=").append(routes.get(i));
            if(i < routes.size() -1)
                routeListBuilder.append("&");
        }
        String routeList = routeListBuilder.toString();
        build.append(routeList);

        return build.toString();
    }

    /**
     * Move a Map Marker from a position to the Final Position
     * @param marker
     * @param toPosition
     * @param hideMarker
     */
    public static void animateMarker(GoogleMap mMap, final Marker marker, final LatLng toPosition, final boolean hideMarker, final int duration) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        Projection mapProjection = mMap.getProjection();
        Point startPoint = mapProjection.toScreenLocation(marker.getPosition());
        final LatLng startLatLng = mapProjection.fromScreenLocation(startPoint);
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


}
