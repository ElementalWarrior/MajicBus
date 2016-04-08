package majicbus.gpsapp;

/*
  Any activity that wants to make an HTTP request MUST implement
  the OnTaskCompleted interface in order for the HTTP request to
  call back to that activity. This brings the thread back to the activity
  that called the http connection.
 */
public interface OnTaskCompleted {
   public void onTaskCompleted(DataHandler handler);
}
