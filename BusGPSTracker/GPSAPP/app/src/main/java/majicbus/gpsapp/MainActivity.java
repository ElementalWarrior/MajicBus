package majicbus.gpsapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements OnTaskCompleted{
    private ArrayList<String> Routes;

    @Override
    public void onTaskCompleted(String response){
        loadData(response);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Routes = new ArrayList<String>();
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        createMapButton();

        //Make HTTP Request, will callback to the loadData function
        HTTPConnection conn = new HTTPConnection(this);
        conn.makeConnection("http://192.168.1.19/Home/showRoutesJSON");
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
                startActivity(intent);
            }
        });
    }

    /*
      Puts data into the table to show route information
      Creates event listeners to keep track of which are selected
      Then selected routes are put into an ArrayList to transfer to the map
    */
    public void loadData(String JSON){
        TableLayout ll = (TableLayout) findViewById(R.id.RouteTable);
        Gson parser = new Gson();

        try {
             List routeList = parser.fromJson(JSON, List.class);

            for (int i = 0; i < routeList.size(); i++) {
                StringBuilder build = new StringBuilder();

                TableRow row= new TableRow(this);
                TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
                row.setLayoutParams(lp);
                CheckBox routeBox = new CheckBox(this);

                Map<String, Object> routeMap = (Map)(routeList).get(i);
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
                ll.addView(row, i + 1);
            }
        }catch(JsonSyntaxException e){
            //Retry connection
            HTTPConnection conn = new HTTPConnection(this);
            conn.makeConnection("http://192.168.1.19/Home/showRoutesJSON");

            /*Log.v("foo",JSON);
            TableRow row= new TableRow(this);
            TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
            row.setLayoutParams(lp);
            TextView text = new TextView(this);
            text.setText("Error fetching route data.");
            row.addView(text);
            ll.addView(row, 1);
            */
        }
    }
}
