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
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * This is the first screen that a user sees. It creates the buttons and
 * Fetches the route information.
 */
public class MainActivity extends AppCompatActivity implements OnTaskCompleted{
    private RouteHandler handler;
    static String URL = "http://majicbus.azurewebsites.net";

    @Override
    public void onTaskCompleted(DataHandler handler){handler.loadData();}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        createMapButton();

        String url = URL +"/Home/showRoutesJSON";
        handler = new RouteHandler(url,this,findViewById(R.id.RouteTable));
        HTTPConnection conn = new HTTPConnection(handler);
        conn.makeConnection();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings)
            return true;
        return super.onOptionsItemSelected(item);
    }

    public void createMapButton(){
        Button btn = (Button)findViewById(R.id.MapBtn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                intent.putStringArrayListExtra("routeData", handler.getRoutes());
                startActivity(intent);
            }
        });
    }
}
