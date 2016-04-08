package majicbus.gpsapp;

import android.content.Context;
import android.support.v4.graphics.ColorUtils;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.SphericalUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The BusHandler class is an extension of the DataHandler Class
 * It is used to get the bus position data and display it on the screen
 */
public class BusHandler extends DataHandler {
    private GoogleMap mMap;
    private HashMap<Integer, ArrayList<Marker>> BusHashMap;
    private HashMap<Integer, Integer[]> RouteColors;

    public BusHandler(String url, Context cont, HashMap<Integer, Integer[]> newRouteColors, GoogleMap newMap) {
        super(url, cont);
        mMap = newMap;
        BusHashMap = new HashMap<>();
        RouteColors = newRouteColors;
    }

    /**
     * Parses the JSON
     * Updates or makes the bus markers
     */
    @Override
    public void loadData() {
        List routes = parser.fromJson(JSON, List.class);

        for (int i = 0; i < routes.size(); i++) {
            Map<String, Object> busList = (Map) (routes.get(i));
            int id = ((Double) busList.get("routeID")).intValue();
            List buses = (List)busList.get("Buses");

            if(!BusHashMap.containsKey(id)) //There aren't any buses on the map
                makeBuses(id,buses);
            else { //Update the buses
                ArrayList<Marker> marks = BusHashMap.get(id);
                if(buses.size() == marks.size())//If there are the same number of buses fetched
                    updateBuses(id,buses);
                else{//Remove all the buses and remake
                    for(int j = 0; j < marks.size(); j++)
                        marks.get(j).remove();
                    makeBuses(id,buses);
                }

            }
        }
    }

    /**
     * Helper method to make new bus markers
     * @param id - Route id
     * @param buses - list of buses
     */
    private void makeBuses(int id, List buses){
        ArrayList<Marker>  marks = new ArrayList<Marker>();
        for (int j = 0; j < buses.size(); j++) {
            Map<String, Object> busMap = (Map) (buses.get(j));
            double lat = (Double) busMap.get("Lat");
            double lng = (Double) busMap.get("Lon");

            //draw markers
            MarkerOptions ops = new MarkerOptions();
            ops.position(new LatLng(lat, lng));
            ops.title(String.valueOf(id));
            ops.snippet("0%");
            Integer RGB[] = RouteColors.get(id);
            float HSL[] = new float[3];
            ColorUtils.RGBToHSL(RGB[0], RGB[1], RGB[2], HSL);

            ops.icon(BitmapDescriptorFactory.defaultMarker(HSL[0]));
            Marker mark = mMap.addMarker(ops);
            marks.add(mark);
        }
        BusHashMap.put(id, marks); //Keep track of the buses added to the map
    }

    /**
     * Helper method to update bus markers
     * @param id - Route id
     * @param buses - list of buses
     */
    private void updateBuses(int id, List buses){
        ArrayList<Marker>  marks = BusHashMap.get(id);
        for (int i = 0; i < buses.size(); i++) {
            Map<String, Object> busMap = (Map) (buses.get(i));
            LatLng newBusPos = new LatLng((Double) busMap.get("Lat"),(Double) busMap.get("Lon"));
            double min = Double.MAX_VALUE;
            Marker closestBus = null;

            //For each bus in the list find the closest bus then update the marker
            //This results in O(n^2) complexity for n buses, could be reduced but don't have the time.
            for(int j = 0; j < marks.size(); j++){
                Marker mark = marks.get(j);
                double tempMin = SphericalUtil.computeDistanceBetween(newBusPos, mark.getPosition());
                if(tempMin < min) {
                    closestBus = mark;
                    min = tempMin;
                }
            }
            Utility.animateMarker(mMap, closestBus, newBusPos, false, MapsActivity.updateFrequency);
        }
    }

}
