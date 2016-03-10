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

/* THIS DOESN'T WANT TO WORK
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
*/

public class MainActivity extends AppCompatActivity {
    private ArrayList<String> RouteData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        RouteData = new ArrayList<String>();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //makeGetRequest(); THIS DOESN'T WANT TO WORK
        if(RouteData.isEmpty()){
            //Display a message cannot to connect to servers
        }else{
            //initTable
        }
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
                intent.putStringArrayListExtra("routeData", RouteData);
                startActivity(intent);
            }
        });
    }

/* THIS DOESN'T WANT TO WORK
    private void makeGetRequest() {
        HttpClient client = new DefaultHttpClient();
        HttpGet request = new HttpGet("http://localhost...");
        // replace with the url

        HttpResponse response;
        try {
            response = client.execute(request);
            Log.d("Response of GET request", response.toString());
            String[] data = response.toString().split(","); //Split the JSON into individual routes
            for(int i = 0; i < data.length; i++){
                RouteData.add(data[i]);
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
*/

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
        RouteData.add("name: \"Route 97\", stops: {lat: , lng: }");
        RouteData.add("name: \"Route 10\", stops: {lat: , lng: }");
        RouteData.add("name: \"Route 8\", stops: {lat: , lng: }");
        RouteData.add("name: \"Route 1\", stops: {lat: , lng: }");
        RouteData.add("name: \"Route 23\", stops: {lat: , lng: }");

        //Still going to need to figure out how to get listeners on these then add them to an arraylist
        //when they're checked
        for (int i = 0; i < RouteData.size(); i++) {
            TableRow row= new TableRow(this);
            TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
            row.setLayoutParams(lp);
            CheckBox routeBox = new CheckBox(this);
            String routeName = RouteData.get(i);
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
