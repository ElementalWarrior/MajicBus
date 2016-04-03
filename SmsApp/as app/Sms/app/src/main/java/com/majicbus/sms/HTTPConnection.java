package com.majicbus.sms;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

public class HTTPConnection {
    protected String JSON;
    protected Context appContext;

    public HTTPConnection(Context newContext){
        appContext = newContext;
        JSON = "";
    }

    public int makeConnection(String url, String json){
        try {
            ConnectivityManager connMgr = (ConnectivityManager) appContext.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected())
                new getData().execute(url, json);
            else
                return -1;
        } catch (Exception ex)
        {
            Log.v("connectivityManager", ex.getCause() + " " + ex.getMessage() + " \n" + ex.getStackTrace());
        }
        return 1;
    }

    /* Uses AsyncTask to create a task away from the main UI thread. This task takes a
       URL string and uses it to create an HttpUrlConnection. Once the connection
       has been established, the AsyncTask downloads the contents of the webpage as
       an InputStream. Finally, the InputStream is converted into a string, which is
       displayed in the UI by the AsyncTask's onPostExecute method.
    */
    private class getData extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            try { // params comes from the execute() call: params[0] is the url.
                downloadUrl(urls[0], urls[1]);
            } catch (IOException e) {
                Log.v("foo", "Unable to retrieve web page. URL may be invalid.");
            }
            return "";
        }

        @Override // onPostExecute displays the results of the AsyncTask.
        protected void onPostExecute(String result) {
            JSON = result;
        }

        /* Given a URL, establishes an HttpUrlConnection and retrieves
           the web page content as a InputStream, which it returns as
           a string.
        */
        private void downloadUrl(String myUrl, String json) throws IOException {
            InputStream is = null;
            try {
                URL url = new URL(myUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestProperty("charset", "UTF-8");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setRequestMethod("GET");
                conn.connect(); // Starts the query
                int response = conn.getResponseCode();
                is = conn.getInputStream();

                // Convert the InputStream into a string
                String contentAsString = readIt(is, conn.getContentLength());

                Activity active = (Activity) appContext;

                class MyRunnable implements Runnable {
                    String data;
                    Activity active;
                    public MyRunnable(String newData, Activity newActivity) {
                        data = newData;
                        active = newActivity;
                    }
                    public void run() {
                        ((OnTaskCompleted)active).onTaskCompleted(data);
                    }
                }

                active.runOnUiThread(new MyRunnable(contentAsString, active));

                // Makes sure that the InputStream is closed after the app finished using it.
            } catch (Exception ex){
                Log.v("httpBroke", ex.getMessage());
            }
            finally {
                if (is != null)
                    is.close();
            }
        }

        //Converts the Input stream to a string
        public String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
            Reader reader = null;
            reader = new InputStreamReader(stream, "UTF-8");
            char[] buffer = new char[len];
            reader.read(buffer);
            return new String(buffer);
        }
    }
}
