package com.majicbus.sms;

/*
  Any activity that wants to make an HTTP request MUST implement
  the OnTaskCompleted interface in order for the HTTP request to
  call back to that activity.
 */
public interface OnTaskCompleted {
   public void onTaskCompleted(String response);
}
