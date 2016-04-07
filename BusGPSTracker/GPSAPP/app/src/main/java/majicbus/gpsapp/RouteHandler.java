package majicbus.gpsapp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.StringBufferInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RouteHandler extends DataHandler {
    private ArrayList<String> Routes;
    private TableLayout routeTable;

    public RouteHandler(String url, Context con, View newView){
        super(url, con);
        Routes = new ArrayList<String>();
        routeTable = (TableLayout)newView;
    }

    public ArrayList<String> getRoutes(){return Routes;}

    @Override
    public void loadData() {
        Gson parser = new Gson();
        try {
            List routeList = parser.fromJson(JSON, List.class);

            for (int i = 0; i < routeList.size(); i++) {
                StringBuilder build = new StringBuilder();

                TableRow row = new TableRow(context);
                TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
                row.setLayoutParams(lp);
                CheckBox routeBox = new CheckBox(context);

                Map<String, Object> routeMap = (Map)(routeList.get(i));
                build.append("Route ");
                build.append((String)routeMap.get("NameShort")).append(": ");
                build.append((String) routeMap.get("NameLong"));
                String result = build.toString();
                routeBox.setText(result);
                routeBox.setId(((Double)routeMap.get("RouteID")).intValue());

                //Create listener
                CompoundButton Btn = (CompoundButton)routeBox;
                Btn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        String id = String.valueOf(buttonView.getId());
                        if (isChecked)
                            Routes.add(id);
                        else
                            Routes.remove(id);
                    }
                });

                row.addView(routeBox);
                routeTable.addView(row, i + 1);
            }
        }catch(Exception e){
            Toast.makeText(context, "Unable to fetch Route List", Toast.LENGTH_LONG).show();
        }
    }
}
