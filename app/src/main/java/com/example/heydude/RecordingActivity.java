package com.example.heydude;

import android.content.Intent;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.microsoft.cognitiveservices.speech.ResultReason;
import com.microsoft.cognitiveservices.speech.SpeechConfig;
import com.microsoft.cognitiveservices.speech.SpeechRecognitionResult;
import com.microsoft.cognitiveservices.speech.SpeechRecognizer;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.Future;

import static android.Manifest.permission.INTERNET;
import static android.Manifest.permission.RECORD_AUDIO;

public class RecordingActivity extends AppCompatActivity {

    DBHelper mydb;

    Calendar calander;
    SimpleDateFormat simpledateformat;

    TextView textview;
    TextToSpeech t1;
    String contents_of_entry;

    private static String speechSubscriptionKey;
    static {
        speechSubscriptionKey = "766efb08d3664e5c8abb160632e52019";
    }

    private static String serviceRegion; //could be westus...not sure
    static {
        serviceRegion = "westus";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recording);

        say("How was your day?");

        Runnable r = new Runnable() {
            @Override
            public void run(){
                getWhatToDo(); //<-- put your code in here.
            }
        };

        Handler h = new Handler();
        h.postDelayed(r, 3000);

        int requestCode = 5; // unique code for the permission request
        ActivityCompat.requestPermissions(RecordingActivity.this, new String[]{RECORD_AUDIO, INTERNET}, requestCode);


    }

    public void say(String toSpeak){

        t1=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status !=TextToSpeech.ERROR ){
                    t1.setLanguage(Locale.UK);
                    //String toSpeak="Say the Number!";
                    Toast.makeText(getApplicationContext(),toSpeak,Toast.LENGTH_SHORT).show();
                    t1.speak(toSpeak,TextToSpeech.QUEUE_FLUSH,null);
                }
            }
        });

    }

    public void onDestroy() {
        // Don't forget to shutdown tts!
        if (t1 != null) {
            t1.stop();
            t1.shutdown();
        }
        super.onDestroy();
    }

    public void getWhatToDo(){

        Toast.makeText(getApplicationContext(),"GetWhatToDo",Toast.LENGTH_LONG).show();

        textview=findViewById(R.id.textView_recording);

        try{
            SpeechConfig config = SpeechConfig.fromSubscription(speechSubscriptionKey,serviceRegion);
            assert(config!=null);

            SpeechRecognizer reco=new SpeechRecognizer(config);
            assert(reco!=null);

            Future<SpeechRecognitionResult> task=reco.recognizeOnceAsync();
            assert(task!=null);

            SpeechRecognitionResult result=task.get();
            assert(result!=null);

            if(result.getReason()== ResultReason.RecognizedSpeech){
                contents_of_entry=result.getText();
                textview.setText(contents_of_entry);
            }
            else{
                textview.setText("Error recognizing. Did you update the subscription info?"+System.lineSeparator()+result.toString() );
            }

            reco.close();

            add(contents_of_entry);


        }
        catch (Exception ex){
            Log.e("SpeechSDKDemo","unexpected "+ex.getMessage());
            assert(false);
        }


    }

    private void add(String contents_of_entry) {
        String addDate=getDate();
        String addEntry=contents_of_entry;
        mydb.insertEntry(addDate,addEntry);

    }

    private String getDate() {
        calander = Calendar.getInstance();
        simpledateformat = new SimpleDateFormat("dd MMM yyyy");
        String date= simpledateformat.format(calander.getTime());

        return date;
    }


}
