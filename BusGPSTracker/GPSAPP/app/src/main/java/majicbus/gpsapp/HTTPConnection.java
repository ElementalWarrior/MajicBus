package majicbus.gpsapp;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;

public class HTTPConnection extends AsyncTask<String, Void, String> {
    protected String JSON;
    protected Context appContext;

    public HTTPConnection(Context newContext){
        appContext = newContext;
        JSON = "";
    }

    public int makeConnection(String url){
        ConnectivityManager connMgr = (ConnectivityManager)appContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            this.execute(url);
        else
            return -1;
        return 1;
    }

    @Override
    protected String doInBackground(String... urls) {
        try { // params comes from the execute() call: params[0] is the url.

            InputStream is = null;
            try {
                URL url = new URL(urls[0]);
                //Proxy prx = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("192.168.1.37", 8888));
                //HttpURLConnection conn = (HttpURLConnection) url.openConnection(prx);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("GET");
                conn.setRequestProperty("charset", "UTF-8");
                conn.setDoInput(true);
                conn.connect(); // Starts the query

                InputStream input = conn.getInputStream();
                return readIt(input, conn.getContentLength());
            } catch (Exception ex){
                Log.v("httpBroke", "" + ex.getMessage());
            }
            finally {
                if (is != null)
                    is.close();
            }
        } catch (IOException e) {
            Log.v("foo", "Unable to retrieve web page. URL may be invalid.");
        }
        return null;
    }

    @Override // onPostExecute displays the results of the AsyncTask.
    protected void onPostExecute(String response) {
        ((OnTaskCompleted)appContext).onTaskCompleted(response);
    }


    //Converts the Input stream to a string
    public String readIt(InputStream stream, int len) throws IOException {
        StringBuilder sb = new StringBuilder();

        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

        String line;
        while((line = reader.readLine()) != null)
        {
            sb.append(line);
        }
        return sb.toString();
    }
}
