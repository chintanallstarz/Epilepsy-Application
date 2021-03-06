package ser593.com.epilepsy.Results;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.yarolegovich.lovelydialog.LovelyStandardDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import ser593.com.epilepsy.Main.MainActivity;
import ser593.com.epilepsy.R;
import ser593.com.epilepsy.apiCall.CheckForInternet;
import ser593.com.epilepsy.apiCall.ServiceCall;
import ser593.com.epilepsy.app.AppController;


public class ResultActivity extends AppCompatActivity {
    String LOG_TAG = ResultActivity.class.getSimpleName();
    private RelativeLayout resultsLayout = null;

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        resultsLayout = (RelativeLayout) findViewById(R.id.resultsContainerLayout);
        addListenerOnReturnButton();

        Intent intent =this.getIntent();

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;

        //String jsonStr = "{\"task\":\"Finger Tapping\",\"timer_length\":10,\"answer\":[{\"side\":\"left\",\"tap_count\":15},{\"side\":\"right\",\"tap_count\":21}]}";
        //String jsonStr = "{\"task\":\"Pattern Comparison Processing\",\"answer\":[{\"question_index\":1,\"correct\":true,\"elapsed_time\":15},{\"question_index\":2,\"correct\":false,\"elapsed_time\":25},{\"question_index\":3,\"correct\":true,\"elapsed_time\":35},{\"question_index\":4,\"correct\":true,\"elapsed_time\":45},{\"question_index\":5,\"correct\":false,\"elapsed_time\":55}]}";
        String jsonStr = intent.getStringExtra(getString(R.string.task_result));
        Log.d("ResultJSON", jsonStr);
        try {
            //create json obj for api
            JSONObject jsonForApi = new JSONObject();
            jsonForApi.put("activityInstanceID", getIntent().getExtras().getString("activityInstanceID"));
            jsonForApi.put("timeStamp", System.currentTimeMillis());

            JSONObject jsonObj = new JSONObject(jsonStr);
            String task = jsonObj.getString(getString(R.string.task));

            // Find Tablelayout
            TableLayout tl = (TableLayout) findViewById(R.id.tlResult);

            if (task.equals(getString(R.string.task_finger_tapping)))
            {
                JSONArray answers = jsonObj.getJSONArray(getString(R.string.task_answer));
                JSONArray answersArr = new JSONArray();

                TextView txtAccuracyLabel = (TextView)findViewById(R.id.txtAccuracyLabel);
                txtAccuracyLabel.setText("");
                TextView txtAvgLabel = (TextView)findViewById(R.id.txtAvgLabel);
                txtAvgLabel.setText("");

                // Create header row, format with padding
                TableRow trHeader = new TableRow(this);
                TextView tHeader1 = new TextView(this);
                tHeader1.setText(String.format("%1$-20s", "Side"));
                trHeader.addView(tHeader1);
                TextView tHeader2 = new TextView(this);
                tHeader2.setText(String.format("%1$-20s", "Number of Taps"));
                trHeader.addView(tHeader2);
                tl.addView(trHeader);

                // Loop through the list and add row to table

                for(int i = 0; i < answers.length(); i++)
                {
                    //add to answersArr
                    JSONObject ans = new JSONObject();
                    ans.put("operatingHand", answers.getJSONObject(i).getString(getString(R.string.finger_tapping_json_side)).toLowerCase());
                    ans.put("tapNumber", Integer.parseInt(answers.getJSONObject(i).getString(getString(R.string.finger_tapping_json_tap_count))));
                    answersArr.put(ans);

                    //Log.v("looping", answers.getJSONObject(i).getString(getString(R.string.finger_tapping_json_side)) + ", " + answers.getJSONObject(i).getString(getString(R.string.finger_tapping_json_tap_count)));
                    TableRow tr = new TableRow(this);

                    TextView t = new TextView(this);
                    t.setText(answers.getJSONObject(i).getString(getString(R.string.finger_tapping_json_side)));
                    tr.addView(t);

                    TextView t1 = new TextView(this);
                    t1.setText(answers.getJSONObject(i).getString(getString(R.string.finger_tapping_json_tap_count)));
                    tr.addView(t1);

                    tl.addView(tr);
                }

                //continue building json obj for api
                JSONArray activityResultsArray = new JSONArray();
                JSONObject activityResults = new JSONObject();
                activityResults.put("activityBlockId", "FINGERTAPPING");
                activityResults.put("timeToTap", Integer.parseInt(jsonObj.getString("timer_length")));
                activityResults.put("screenWidth", width);
                activityResults.put("screenHeight", height);
                activityResults.put("timeTakenToComplete", Integer.parseInt(jsonObj.get("elapseTime").toString()));
                activityResults.put("answers", answersArr);
                activityResultsArray.put(activityResults);
                jsonForApi.put("activityResults", activityResultsArray);

                Log.d(LOG_TAG, jsonForApi.toString());
            }
            else if (task.equals(getString(R.string.task_flanker))) //flanker
            {
                int numAnswer = jsonObj.getJSONArray(getString(R.string.task_answer)).length();
                long totalTime = 0;
                int correct = 0;

                // Create header row, format with padding
                TableRow trHeader = new TableRow(this);
                TextView tHeader1 = new TextView(this);
                tHeader1.setText(String.format("%1$-20s", "Question Index"));
                trHeader.addView(tHeader1);
                TextView tHeader2 = new TextView(this);
                tHeader2.setText(String.format("%1$-20s", "Correct"));
                trHeader.addView(tHeader2);
                TextView tHeader3 = new TextView(this);
                tHeader3.setText(String.format("%1$-20s", "Elapsed Time"));
                trHeader.addView(tHeader3);
                tl.addView(trHeader);

                // Loop through the list and add row to table
                JSONArray answers = jsonObj.getJSONArray(getString(R.string.task_answer));
                JSONArray answersArr = new JSONArray();
                for(int i = 0; i < numAnswer; i++)//for(Answer ans: record)
                {
                    //add to answersArr
                    JSONObject ans = new JSONObject();
                    ans.put("pattern", answers.getJSONObject(i).getString("pattern"));
                    ans.put("result", answers.getJSONObject(i).getBoolean("correct"));
                    ans.put("timeTaken", answers.getJSONObject(i).getInt("elapsed_time"));
                    ans.put("questionIndex", i+1);
                    answersArr.put(ans);

                    TableRow tr = new TableRow(this);

                    TextView t = new TextView(this);
                    t.setText(answers.getJSONObject(i).getString("question_index"));
                    tr.addView(t);

                    if (answers.getJSONObject(i).getBoolean("correct")) correct++;
                    TextView t1 = new TextView(this);
                    t1.setText(String.valueOf(answers.getJSONObject(i).getBoolean("correct")));
                    tr.addView(t1);

                    totalTime += answers.getJSONObject(i).getInt("elapsed_time");
                    TextView t2 = new TextView(this);
                    t2.setText(Integer.toString(answers.getJSONObject(i).getInt("elapsed_time")));
                    tr.addView(t2);

                    tl.addView(tr);
                }

                // print out stats
                TextView txtAccuracy = (TextView)findViewById(R.id.txtAccuracy);
                txtAccuracy.setText(String.format("%1$d/%2$d", correct, numAnswer));

                TextView txtAccuracyPercentage = (TextView)findViewById(R.id.txtAccuracyPercentage);
                txtAccuracyPercentage.setText(String.format("%.2f%%", ((double)correct)/numAnswer*100));

                TextView txtAvgTIme = (TextView)findViewById(R.id.txtAvgTime);
                txtAvgTIme.setText(String.format("%1$dms", totalTime/numAnswer));

                //continue building json obj for api
                JSONArray activityResultsArray = new JSONArray();
                JSONObject activityResults = new JSONObject();
                activityResults.put("activityBlockId", "FLANKER");
                activityResults.put("screenWidth", width);
                activityResults.put("screenHeight", height);
                activityResults.put("timeTakenToComplete", Integer.parseInt(jsonObj.get("elapseTime").toString()));
                activityResults.put("answers", answersArr);
                activityResultsArray.put(activityResults);
                jsonForApi.put("activityResults", activityResultsArray);

                Log.d(LOG_TAG, jsonForApi.toString());
            }
            else if (task.equals(getString(R.string.task_pattern_comparison_processing))) //pattern comparison
            {
                int numAnswer = jsonObj.getJSONArray(getString(R.string.task_answer)).length();
                long totalTime = 0;
                int correct = 0;

                // Create header row, format with padding
                TableRow trHeader = new TableRow(this);
                TextView tHeader1 = new TextView(this);
                tHeader1.setText(String.format("%1$-20s", "Question Index"));
                trHeader.addView(tHeader1);
                TextView tHeader2 = new TextView(this);
                tHeader2.setText(String.format("%1$-20s", "Correct"));
                trHeader.addView(tHeader2);
                TextView tHeader3 = new TextView(this);
                tHeader3.setText(String.format("%1$-20s", "Elapsed Time"));
                trHeader.addView(tHeader3);
                tl.addView(trHeader);

                // Loop through the list and add row to table
                JSONArray answers = jsonObj.getJSONArray(getString(R.string.task_answer));
                JSONArray answersArr = new JSONArray();
                for(int i = 0; i < numAnswer; i++)//for(Answer ans: record)
                {
                    //add to answersArr
                    JSONObject ans = new JSONObject();
                    ans.put("pattern", answers.getJSONObject(i).getString("pattern"));
                    ans.put("result", answers.getJSONObject(i).getBoolean("correct"));
                    ans.put("timeTaken", answers.getJSONObject(i).getInt("elapsed_time"));
                    ans.put("questionIndex", i+1);
                    answersArr.put(ans);

                    TableRow tr = new TableRow(this);

                    TextView t = new TextView(this);
                    t.setText(answers.getJSONObject(i).getString("question_index"));
                    tr.addView(t);

                    if (answers.getJSONObject(i).getBoolean("correct")) correct++;
                    TextView t1 = new TextView(this);
                    t1.setText(String.valueOf(answers.getJSONObject(i).getBoolean("correct")));
                    tr.addView(t1);

                    totalTime += answers.getJSONObject(i).getInt("elapsed_time");
                    TextView t2 = new TextView(this);
                    t2.setText(Integer.toString(answers.getJSONObject(i).getInt("elapsed_time")));
                    tr.addView(t2);

                    tl.addView(tr);
                }

                // print out stats
                TextView txtAccuracy = (TextView)findViewById(R.id.txtAccuracy);
                txtAccuracy.setText(String.format("%1$d/%2$d", correct, numAnswer));

                TextView txtAccuracyPercentage = (TextView)findViewById(R.id.txtAccuracyPercentage);
                txtAccuracyPercentage.setText(String.format("%.2f%%", ((double)correct)/numAnswer*100));

                TextView txtAvgTIme = (TextView)findViewById(R.id.txtAvgTime);
                txtAvgTIme.setText(String.format("%1$dms", totalTime/numAnswer));

                //continue building json obj for api
                JSONArray activityResultsArray = new JSONArray();
                JSONObject activityResults = new JSONObject();
                activityResults.put("activityBlockId", "PATTERNCOMPARISON");
                activityResults.put("screenWidth", width);
                activityResults.put("screenHeight", height);
                activityResults.put("timeTakenToComplete", Integer.parseInt(jsonObj.get("elapseTime").toString()));
                activityResults.put("answers", answersArr);
                activityResultsArray.put(activityResults);
                jsonForApi.put("activityResults", activityResultsArray);

                Log.d(LOG_TAG, jsonForApi.toString());
            }
            else if(task.equals(getString(R.string.spatial_span))){

                int numAnswer = jsonObj.getJSONArray(getString(R.string.task_answer)).length();
                int correct = 0;

                TableRow trHeader = new TableRow(this);

                TextView tHeader1 = new TextView(this);
                tHeader1.setText(String.format("%1$-20s", "Pattern Index"));
                trHeader.addView(tHeader1);

                TextView tHeader2 = new TextView(this);
                tHeader2.setText(String.format("%1$-20s", "Result"));
                trHeader.addView(tHeader2);

                TextView tHeader3 = new TextView(this);
                tHeader3.setText(String.format("%1$-20s", "Difficulty"));
                trHeader.addView(tHeader3);

                TextView tHeader4 = new TextView(this);
                tHeader4.setText(String.format("%1$-20s", "Time"));
                trHeader.addView(tHeader4);

                tl.addView(trHeader);

                // Loop through the list and add row to table
                JSONArray answers = jsonObj.getJSONArray(getString(R.string.task_answer));
                JSONArray answersArr = new JSONArray();

                for(int i = 0; i < numAnswer; i++)//for(Answer ans: record)
                {
                    JSONObject ans = new JSONObject();
                    ans.put("questionIndex", Integer.parseInt(answers.getJSONObject(i).getString("pattern_id")));
                    ans.put("result", answers.getJSONObject(i).getBoolean("user_answer"));
                    ans.put("timeTaken", Integer.parseInt(answers.getJSONObject(i).getString("timeTaken")));
                    ans.put("difficulty", Integer.parseInt(answers.getJSONObject(i).getString("difficulty")));
                    answersArr.put(ans);

                    TableRow tr = new TableRow(this);

                    TextView t = new TextView(this);
                    t.setText(answers.getJSONObject(i).getString("pattern_id"));
                    tr.addView(t);

                    if (answers.getJSONObject(i).getBoolean("user_answer")) correct++;
                    TextView t1 = new TextView(this);
                    t1.setText(String.valueOf(answers.getJSONObject(i).getBoolean("user_answer")));
                    tr.addView(t1);

                    //totalTime += answers.getJSONObject(i).getInt("elapsed_time");
                    TextView t2 = new TextView(this);
                    t2.setText((answers.getJSONObject(i).getString("difficulty")));
                    tr.addView(t2);

                    TextView t3 = new TextView(this);
                    t3.setText((answers.getJSONObject(i).getString("timeTaken")));
                    tr.addView(t3);

                    tl.addView(tr);
                }
                // print out stats
                TextView txtAccuracy = (TextView)findViewById(R.id.txtAccuracy);
                txtAccuracy.setText(String.format("%1$d/%2$d", correct, numAnswer));

                TextView txtAccuracyPercentage = (TextView)findViewById(R.id.txtAccuracyPercentage);
                txtAccuracyPercentage.setText(String.format("%.2f%%", ((double)correct)/numAnswer*100));

//                TextView txtAvgTIme = (TextView)findViewById(R.id.txtAvgTime);
//                txtAvgTIme.setText(String.format("%1$dms", totalTime/numAnswer));
                //continue building json obj for api
                JSONArray activityResultsArray = new JSONArray();
                JSONObject activityResults = new JSONObject();
                activityResults.put("activityBlockId", "SPATIALSPAN");
                activityResults.put("screenWidth", width);
                activityResults.put("screenHeight", height);
                activityResults.put("timeTakenToComplete", Integer.parseInt(jsonObj.get("timeTakenToComplete").toString()));
                activityResults.put("answers", answersArr);
                activityResultsArray.put(activityResults);
                jsonForApi.put("activityResults", activityResultsArray);

                Log.d(LOG_TAG, jsonForApi.toString());

            }

            if(CheckForInternet.isNetworkAvailable(this)){
                ServiceCall serviceCall = new ServiceCall(getApplicationContext(),resultsLayout);
                serviceCall.submitActivityInstance(jsonForApi);
            }else{
                //Alert User to get internet
                new LovelyStandardDialog(this)
                        .setTopColorRes(R.color.indigo)
                        .setButtonsColorRes(R.color.darkDeepOrange)
                        .setTitle("No Network")
                        .setMessage("Your results have been recorded successfully.\n\nThe application will try to submit the results when the wifi will be made available.")
                        .setPositiveButton("Ok", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            }
                        })
                        .show();
                String pendingSurveys = AppController.getInstance().readPreference("pendingSurveys");
                ArrayList<String> pendingSurveysList = new ArrayList<>();
                if(pendingSurveys == null){
                    pendingSurveysList.add(jsonForApi.toString());
                    AppController.getInstance().writePreference("pendingSurveys",pendingSurveysList.toString());
                    Log.d(LOG_TAG, "onCreate: " + pendingSurveysList.toString());
                }else{
                    JSONArray arr = new JSONArray(pendingSurveys);
                    JSONObject obj;
                    for(int i=0; i<arr.length();i++){
                        obj = (JSONObject) arr.get(i);
                        pendingSurveysList.add(obj.toString());
                    }
                    pendingSurveysList.add(jsonForApi.toString());
                    AppController.getInstance().writePreference("pendingSurveys",pendingSurveysList.toString());
                }

            }
        }
        catch (JSONException e)
        {
            Log.e(LOG_TAG, "Json parsing error: " + e.getMessage());
        }
    }

    private void addListenerOnReturnButton() {
        Button btnReturn = (Button) findViewById(R.id.btnReturn);
        btnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(getApplication(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}