package majicbus.gpsapp;

import android.content.Context;
import com.google.gson.Gson;

/**
 * TODO: Document the class
 */
public abstract class DataHandler {
    protected Gson parser;
    protected String url;
    protected String JSON;
    protected Context context;

    public DataHandler(String newUrl, Context newContext){
        url = newUrl;
        parser = new Gson();
        context = newContext;
    }

    public abstract void loadData();
    public void setData(String newJSON){JSON = newJSON;}
    public Context getContext(){return context;}
    public String getUrl(){return url;}
}