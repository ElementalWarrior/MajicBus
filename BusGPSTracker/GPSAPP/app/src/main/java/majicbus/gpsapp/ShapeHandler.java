package majicbus.gpsapp;

import android.content.Context;
import android.graphics.Color;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShapeHandler extends DataHandler {
    private GoogleMap mMap;
    private HashMap<Integer, Integer[]> RouteColors;
    private final int width = 7;

    public ShapeHandler(String url, Context cont, HashMap<Integer, Integer[]> newRouteColors, GoogleMap newMap) {
        super(url, cont);
        mMap = newMap;
        RouteColors = newRouteColors;
    }

    /**
     * Loads route shapes on the map
     * Splits up routes that have multiple shapes
     * Randomly generates a colour and stores it for each route
     */
    @Override
    public void loadData() {
        List routes = parser.fromJson(JSON, List.class);

        for (int i = 0; i < routes.size(); i++) {
            Map<String, Object> shape = (Map) (routes.get(i));
            int id = ((Double) shape.get("routeID")).intValue();

            Integer RGB[] = RouteColors.get(id);
            PolylineOptions pOps = new PolylineOptions();

            int lastId = 0;
            List shapesList = (List) shape.get("Shape");
            for (int j = 0; j < shapesList.size(); j++) {
                Map<String, Object> shapes = (Map) (shapesList.get(j));
                double lat = (Double) shapes.get("Lat");
                double lng = (Double) shapes.get("Lon");

                int sid = ((Double)shapes.get("sID")).intValue();
                if(j > 0 && sid != lastId){
                    pOps.color(Color.rgb(RGB[0], RGB[1], RGB[2]));
                    pOps.width(width);
                    mMap.addPolyline(pOps);
                    pOps = new PolylineOptions();
                }
                pOps.add(new LatLng(lat,lng));
                lastId = sid;
            }

            pOps.color(Color.rgb(RGB[0], RGB[1], RGB[2]));
            pOps.width(width);
            mMap.addPolyline(pOps);
        }
    }


}
