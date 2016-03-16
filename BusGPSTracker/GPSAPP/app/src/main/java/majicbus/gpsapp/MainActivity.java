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

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ArrayList<String> Routes;
    private ArrayList<String> Stops;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        MBBackendConnection con = new MBBackendConnection(this);
        con.makeConnection("localhost/ShowRoutesJSON");
        Routes = con.getRouteData();
        Stops = con.getStopData();

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
        Routes.add("name: \"Route 97\", stops: {lat: , lng: }");
        Routes.add("name: \"Route 10\", stops: {lat: , lng: }");
        Routes.add("name: \"Route 8\", stops: {lat: , lng: }");
        Routes.add("name: \"Route 1\", stops: {lat: , lng: }");
        Routes.add("name: \"Route 23\", stops: {lat: , lng: }");

        //Still going to need to figure out how to get listeners on these then add them to an arraylist
        //when they're checked
        for (int i = 0; i < Routes.size(); i++) {
            TableRow row= new TableRow(this);
            TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
            row.setLayoutParams(lp);
            CheckBox routeBox = new CheckBox(this);
            String routeName = Routes.get(i);
            //Parse out route name
            //routeName = substring(after name: before rest)
            routeBox.setText(routeName);
            routeBox.setId(i);
            row.addView(routeBox);
            ll.addView(row,i + 1);
        }
        //theres a good chance none of this works.
    }
}
