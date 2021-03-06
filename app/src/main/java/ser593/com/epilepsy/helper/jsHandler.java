package ser593.com.epilepsy.helper;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ser593.com.epilepsy.app.AppController;

/**
 * Created by aniPC on 2/10/2015.
 */
public class jsHandler {
    private static int defaultAlarmIntervalInMins=60;
    Activity activity;
    String TAG = "jsHandler";
    WebView webView;
    private String pin="";
    private boolean receivedPIN=false;
    private static int PENDING_INTENT_ID_PAINREPORTNOTIFICATIONSERVICE=2;

    public jsHandler(Activity _contxt, WebView _webView) {
        activity = _contxt;
        webView = _webView;
    }

    @JavascriptInterface
    public String getPatientPin(){
        String pin = AppController.getInstance().readPreference("patientPin");
        if(pin != null)
            return pin;
        else
            return "Not Found";
    }

    @JavascriptInterface
    public String getServerAddress(){
        String serverAdd = AppController.getInstance().readPreference("url");
        if(serverAdd != null){
            return serverAdd;
        }
        else
            return "Not Found";
    }

//    @JavascriptInterface
//    public void killApp(){
//        Log.e("Debug","Inside Kill App");
//        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(activity);
//        SharedPreferences.Editor editor=sharedPrefs.edit();
//        editor.putString("surveyInProgress", null);
//        editor.commit();
//
//        //int alarmIntervalInMilli=1*20*1000;
//        AlarmManager am = (AlarmManager) activity.getSystemService(Service.ALARM_SERVICE);
//
//        //cancel any alarms related to appInBackgroundService
//        Intent appInBackgroundService = new Intent(activity, edu.asu.healPromisV3.service.appInBackgroundService.class);
//        Intent invalidateServiceIntent=new Intent(activity, invalidateService.class);
//
//        PendingIntent piAppInBackgroundService = PendingIntent.getService(activity,0, appInBackgroundService, 0);
//        PendingIntent invalidateIntent = PendingIntent.getService(activity, 1, invalidateServiceIntent, 0);
//        am.cancel(piAppInBackgroundService);
//        am.cancel(invalidateIntent);
//
//        activity.finish();
//        System.exit(0);
//    }
//
//    @JavascriptInterface
//    public void updateSettings(String surveyAppServerSettings, String surveyAppPin, String reminderTime, String saveStateTime){
//        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(activity);
//        SharedPreferences.Editor editor=sharedPrefs.edit();
//        Log.e("Debug","Setting are updated");
//        if(surveyAppServerSettings!=null){editor.putString("surveyAppServerSettings", surveyAppServerSettings);}
//        if(surveyAppPin!=null){editor.putString("PIN", surveyAppPin);}
//        //if(reminderTime!=null){editor.putInt("reminderInterval", convertStringToInt(reminderTime));}
//        if(reminderTime!=null){editor.putInt("reminderInterval", convertStringToInt(reminderTime));}
//        if(saveStateTime!=null){editor.putInt("saveStateTime", convertStringToInt(saveStateTime));}
//        editor.commit();
//    }
//
//    @JavascriptInterface
//    public void submitError(final String surveyAnswer){
//        Log.e("Unsubmitted Answer:",surveyAnswer);
//        new AlertDialog.Builder(activity)
//                .setTitle("There seems to be a problem")
//                .setMessage("Your survey was not submitted, but we will try to submit it on your behalf. Pressing ok will exit the application")
//                .setNeutralButton(android.R.string.yes, new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(activity);
//                        SharedPreferences.Editor editor=sharedPrefs.edit();
//                       // editor.putString("surveyInProgress", null);
//                        editor.putString("pendingSurveySubmission",surveyAnswer);
//                        editor.commit();
//                        Log.d("Survey Answer",surveyAnswer);
//                        activity.finish();
//
//                    }
//                })
//                .setIcon(R.drawable.ic_launcher)
//                .show();
//    }
//
//    private boolean isJSONValid(String test) {
//        boolean result=true;
//        try {
//            new JSONObject(test);
//        } catch (JSONException ex) {
//            // edited, to include @Arthur's comment
//            // e.g. in case JSONArray is valid as well...
//            try {
//                new JSONArray(test);
//            } catch (JSONException ex1) {
//                result= false;
//            }
//        }
//        return result;
//    }
//    private int convertStringToInt(String input){
//        int number;
//        try {
//            number= new Integer(input);
//        } catch (NumberFormatException e) {
//            number= defaultAlarmIntervalInMins;
//        }
//        return number;
//    }
//    /**
//     * This function handles call from Android-Java
//     */
//    /*public void javaFnCall(String jsString) {
//
//        final String webUrl = "javascript:diplayJavaMsg()";
//        // Add this to avoid android.view.windowmanager$badtokenexception unable to add window
//        if(!activity.isFinishing())
//            // loadurl on UI main thread
//            activity.runOnUiThread(new Runnable() {
//
//                @Override
//                public void run() {
//                    webView.loadUrl(webUrl);
//                }
//            });
//    }*/

}
