package majicbus.gpsapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TableLayout;
import android.widget.TableRow;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private ArrayList<String> Routes;
    private ArrayList<String> Stops;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //this is going to handle the HTTP request
        //MBBackendConnection con = new MBBackendConnection(this);
        //con.makeConnection("localhost/ShowRoutesJSON");
        //Routes = con.getRouteData();
       // con.makeConnection("localhost/ShowStopsJSON");
        //Stops = con.getStopData();

        initTable();
        createMapButton();
        //createRouteLinks();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void createMapButton(){
        Button btn = (Button)findViewById(R.id.MapBtn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                intent.putStringArrayListExtra("routeData", Routes);
                intent.putStringArrayListExtra("stopData", Stops);
                startActivity(intent);
            }
        });
    }

    public void createRouteLinks(){
        /* THIS IS OLD CODE THAT NEEDS TO BE ADAPTED TO BE DYNAMIC
        final ArrayList<String> routeData = new ArrayList<String>();
        routeData.add("Route 10");
        final Intent intent = new Intent(MainActivity.this, MapsActivity.class);
        intent.putStringArrayListExtra("routeData", routeData);

        CompoundButton R97 = (CompoundButton)findViewById(R.id.Box10);
        btn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                routeData.add("Route 97");
            }
        });

        CompoundButton R10 = (CompoundButton)findViewById(R.id.Box10);
        btn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                routeData.add("Route 10");
            }
        });

        CompoundButton R8 = (CompoundButton)findViewById(R.id.Box10);
        btn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                routeData.add("Route 8");
            }

        });


        Button btn = (Button)findViewById(R.id.MapBtn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(intent);
            }
        });
        */
    }

    //Puts data into the table to show route information
    //Going to be used to dynamically get data from the DB and then
    //Display it dynamically. Will have to keep track of which are selected
    //Then put the data into an ArrayList or something to transfer to the map page
    public void initTable(){
        TableLayout ll = (TableLayout) findViewById(R.id.RouteTable);
        //test data
        String routesjson = "[{\"RouteID\":1,\"NameShort\":\"1\",\"NameLong\":\"Lakeshore\",\"Description\":null,\"dtCreated\":\"\\/Date(1453823501090)\\/\",\"TripCount\":177}," +
                "{\"RouteID\":2,\"NameShort\":\"2\",\"NameLong\":\"North End Shuttle\",\"Description\":null,\"dtCreated\":\"\\/Date(1453823501090)\\/\",\"TripCount\":64}," +
                "{\"RouteID\":3,\"NameShort\":\"3\",\"NameLong\":\"Dilworth Mt.\",\"Description\":null,\"dtCreated\":\"\\/Date(1453823501090)\\/\",\"TripCount\":28}," +
                "{\"RouteID\":4,\"NameShort\":\"4\",\"NameLong\":\"Pandosy / UBCO Express\",\"Description\":null,\"dtCreated\":\"\\/Date(1453823501090)\\/\",\"TripCount\":19}," +
                "{\"RouteID\":5,\"NameShort\":\"5\",\"NameLong\":\"Gordon\",\"Description\":null,\"dtCreated\":\"\\/Date(1453823501090)\\/\",\"TripCount\":133}," +
                "{\"RouteID\":6,\"NameShort\":\"6\",\"NameLong\":\"Glenmore / UBCO Express\",\"Description\":null,\"dtCreated\":\"\\/Date(1453823501090)\\/\",\"TripCount\":9}," +
                "{\"RouteID\":7,\"NameShort\":\"7\",\"NameLong\":\"Glenmore\",\"Description\":null,\"dtCreated\":\"\\/Date(1453823501090)\\/\",\"TripCount\":157}," +
                "{\"RouteID\":8,\"NameShort\":\"8\",\"NameLong\":\"University / OK College\",\"Description\":null,\"dtCreated\":\"\\/Date(1453823501090)\\/\",\"TripCount\":182}," +
                "{\"RouteID\":9,\"NameShort\":\"9\",\"NameLong\":\"Shopper Shuttle\",\"Description\":null,\"dtCreated\":\"\\/Date(1453823501090)\\/\",\"TripCount\":24}," +
                "{\"RouteID\":10,\"NameShort\":\"10\",\"NameLong\":\"North Rutland\",\"Description\":null,\"dtCreated\":\"\\/Date(1453823501090)\\/\",\"TripCount\":172}," +
                "{\"RouteID\":11,\"NameShort\":\"11\",\"NameLong\":\"Rutland\",\"Description\":null,\"dtCreated\":\"\\/Date(1453823501090)\\/\",\"TripCount\":184}," +
                "{\"RouteID\":12,\"NameShort\":\"12\",\"NameLong\":\"McCulloch\",\"Description\":null,\"dtCreated\":\"\\/Date(1453823501090)\\/\",\"TripCount\":57}," +
                "{\"RouteID\":13,\"NameShort\":\"13\",\"NameLong\":\"Quail Ridge\",\"Description\":null,\"dtCreated\":\"\\/Date(1453823501090)\\/\",\"TripCount\":15}," +
                "{\"RouteID\":14,\"NameShort\":\"14\",\"NameLong\":\"Black Mountain\",\"Description\":null,\"dtCreated\":\"\\/Date(1453823501090)\\/\",\"TripCount\":58}," +
                "{\"RouteID\":15,\"NameShort\":\"15\",\"NameLong\":\"Crawford\",\"Description\":null,\"dtCreated\":\"\\/Date(1453823501090)\\/\",\"TripCount\":24}," +
                "{\"RouteID\":16,\"NameShort\":\"16\",\"NameLong\":\"Kettle Valley\",\"Description\":null,\"dtCreated\":\"\\/Date(1453823501090)\\/\",\"TripCount\":51}," +
                "{\"RouteID\":17,\"NameShort\":\"17\",\"NameLong\":\"South Ridge\",\"Description\":null,\"dtCreated\":\"\\/Date(1453823501090)\\/\",\"TripCount\":43}," +
                "{\"RouteID\":20,\"NameShort\":\"20\",\"NameLong\":\"Lakeview\",\"Description\":null,\"dtCreated\":\"\\/Date(1453823501090)\\/\",\"TripCount\":88}," +
                "{\"RouteID\":21,\"NameShort\":\"21\",\"NameLong\":\"Glenrosa\",\"Description\":null,\"dtCreated\":\"\\/Date(1453823501090)\\/\",\"TripCount\":54}," +
                "{\"RouteID\":22,\"NameShort\":\"22\",\"NameLong\":\"Peachland\",\"Description\":null,\"dtCreated\":\"\\/Date(1453823501090)\\/\",\"TripCount\":74}," +
                "{\"RouteID\":23,\"NameShort\":\"23\",\"NameLong\":\"Lake Country\",\"Description\":null,\"dtCreated\":\"\\/Date(1453823501090)\\/\",\"TripCount\":71}," +
                "{\"RouteID\":24,\"NameShort\":\"24\",\"NameLong\":\"Shannon Lake\",\"Description\":null,\"dtCreated\":\"\\/Date(1453823501090)\\/\",\"TripCount\":86}," +
                "{\"RouteID\":25,\"NameShort\":\"25\",\"NameLong\":\"East Boundary\",\"Description\":null,\"dtCreated\":\"\\/Date(1453823501090)\\/\",\"TripCount\":39}," +
                "{\"RouteID\":27,\"NameShort\":\"27\",\"NameLong\":\"Horizon\",\"Description\":null,\"dtCreated\":\"\\/Date(1453823501090)\\/\",\"TripCount\":7}," +
                "{\"RouteID\":28,\"NameShort\":\"28\",\"NameLong\":\"Smith Creek\",\"Description\":null,\"dtCreated\":\"\\/Date(1453823501090)\\/\",\"TripCount\":7}," +
                "{\"RouteID\":29,\"NameShort\":\"29\",\"NameLong\":\"Bear Creek\",\"Description\":null,\"dtCreated\":\"\\/Date(1453823501090)\\/\",\"TripCount\":7}," +
                "{\"RouteID\":32,\"NameShort\":\"32\",\"NameLong\":\"The Lakes\",\"Description\":null,\"dtCreated\":\"\\/Date(1453823501090)\\/\",\"TripCount\":17}," +
                "{\"RouteID\":97,\"NameShort\":\"97\",\"NameLong\":\"Okanagan\",\"Description\":null,\"dtCreated\":\"\\/Date(1453823501090)\\/\",\"TripCount\":281}]";

        //All this code should work, I cant seem to be able to test it
        Gson parser = new Gson();
        List routeList = parser.fromJson(routesjson, List.class);

        for (int i = 0; i < routeList.size(); i++) {
            TableRow row= new TableRow(this);
            TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
            row.setLayoutParams(lp);
            CheckBox routeBox = new CheckBox(this);

            Map<String, Object> routeMap = (Map)(routeList).get(i);
            String result = "Route ";
            result += (String)routeMap.get("NameShort");
            result += ": ";
            result += (String)routeMap.get("NameLong");

            routeBox.setText(result);
            routeBox.setId(i);
            row.addView(routeBox);
            ll.addView(row,i + 1);
        }

    }
}
