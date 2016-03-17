package majicbus.gpsapp;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by Bronson on 2016-03-11.
 */
public class MBBackendConnection {
    private ArrayList<String> RouteData;
    private ArrayList<String> StopData;
    private String JSON;
    private Context appcontext;
    private Gson GsonParser;

    public MBBackendConnection(Context newContext){
        appcontext = newContext;
        JSON = "";
        RouteData = new ArrayList<String>();
        GsonParser = new Gson();
    }

    public int makeConnection(String url){
        ConnectivityManager connMgr = (ConnectivityManager)appcontext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            new getData().execute(url);
        }
        else
            return -1;
        return 1;
    }

    private void parseJSONData(String json){
        //TODO Parse JSON data into the arraylists
        Type collectionType = new TypeToken<Collection<String>>(){}.getType();
        Collection<String> data = GsonParser.fromJson(json, collectionType);

    }


    public ArrayList<String> getRouteData(){return RouteData;}
    public ArrayList<String> getStopData() {return StopData;}

    // Uses AsyncTask to create a task away from the main UI thread. This task takes a
    // URL string and uses it to create an HttpUrlConnection. Once the connection
    // has been established, the AsyncTask downloads the contents of the webpage as
    // an InputStream. Finally, the InputStream is converted into a string, which is
    // displayed in the UI by the AsyncTask's onPostExecute method.
    private class getData extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            // params comes from the execute() call: params[0] is the url.
            try {
                downloadUrl(urls[0]);
            } catch (IOException e) {
                Log.v("foo","Unable to retrieve web page. URL may be invalid.");
            }
            return "";
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            JSON = result;
        }

        // Given a URL, establishes an HttpUrlConnection and retrieves
        // the web page content as a InputStream, which it returns as
        // a string.
        private void downloadUrl(String myUrl) throws IOException {
            InputStream is = null;
            // Only display the first 500 characters of the retrieved
            // web page content.

            try {
                URL url = new URL(myUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                // Starts the query
                conn.connect();
                int response = conn.getResponseCode();
                is = conn.getInputStream();

                // Convert the InputStream into a string
                String contentAsString = readIt(is, conn.getContentLength());
                //return contentAsString;
                MapsActivity active = ((MapsActivity) appcontext);
                class MyRunnable implements Runnable
                {
                    String _data;
                    MapsActivity _active;
                    public MyRunnable(String data, MapsActivity activity)
                    {
                        _data = data;
                        _active = activity;
                    }
                    public void run() {
                        _active.loadData(_data);
                    }
                }
                active.runOnUiThread(new MyRunnable(contentAsString, active));

                // Makes sure that the InputStream is closed after the app is
                // finished using it.
            } catch (Exception ex){
                Log.v("httpBroke", ex.getMessage());
            }
            finally {
                if (is != null) {
                    is.close();
                }
            }
        }

        //Converts the Inputstream to a string
        public String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
            Reader reader = null;
            reader = new InputStreamReader(stream, "UTF-8");
            char[] buffer = new char[len];
            reader.read(buffer);
            return new String(buffer);
        }
    }



}
