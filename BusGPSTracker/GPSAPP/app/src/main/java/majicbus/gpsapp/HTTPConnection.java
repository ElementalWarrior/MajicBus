package majicbus.gpsapp;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * TODO: Document the class
 */
public class HTTPConnection extends AsyncTask<String, Void, String> {
    protected Context appContext;
    protected DataHandler handler;
    protected String url;

    public HTTPConnection(DataHandler newHandler){
        handler = newHandler;
        appContext = handler.getContext();
        url = handler.getUrl();
    }

    public int makeConnection(){
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
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("GET");
                conn.setRequestProperty("charset", "UTF-8");
                conn.setDoInput(true);
                conn.connect(); // Starts the query

                InputStream input = conn.getInputStream();
                return readIt(input, conn.getContentLength());
            } catch (Exception ex){Log.v("httpBroke", "" + ex.getMessage());}
            finally {if (is != null) is.close();}
        } catch (IOException e) {Log.v("foo", "Unable to retrieve web page. URL may be invalid.");}
        return null;
    }

    @Override // onPostExecute displays the results of the AsyncTask.
    protected void onPostExecute(String response) {
        handler.setData(response);
        ((OnTaskCompleted)appContext).onTaskCompleted(handler);
    }

    //Converts the Input stream to a string
    public String readIt(InputStream stream, int len) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        String line;
        while((line = reader.readLine()) != null)
            sb.append(line);

        return sb.toString();
    }
}
