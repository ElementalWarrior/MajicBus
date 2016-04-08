package majicbus.gpsapp;

import android.content.Context;
import com.google.gson.Gson;

/**
 * Data handler class is an abstract class that is used to
 * abstract away the processing of different sets of data that the HTTP requests
 * can fetch. The load data method is the primary function of the Data Handler class
 */
public abstract class DataHandler {
    protected Gson parser;
    protected String url;
    protected String JSON;
    protected Context context;

    /**
     * Constructor initializes the context of the Datahandler and the url  to connect to
     */
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