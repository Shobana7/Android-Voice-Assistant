package com.example.heydude;

import android.content.Intent;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.microsoft.cognitiveservices.speech.ResultReason;
import com.microsoft.cognitiveservices.speech.SpeechConfig;
import com.microsoft.cognitiveservices.speech.SpeechRecognitionResult;
import com.microsoft.cognitiveservices.speech.SpeechRecognizer;

import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.Future;

import static android.Manifest.permission.INTERNET;
import static android.Manifest.permission.RECORD_AUDIO;

public class RecordOrRead extends AppCompatActivity {

    DBHelper mydb;
    ListView listView;

    TextToSpeech t1;
    String nextaction;
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
        setContentView(R.layout.activity_record_or_read);

        listView=(ListView)findViewById(R.id.listView);

        //ArrayList array_list=mydb.getAllDates();
        //ArrayAdapter arrayAdapter=new ArrayAdapter(this,android.R.layout.simple_list_item_1, array_list);
        //listView.setAdapter(arrayAdapter);

        String toSpeak="Record new entry or Read Previous";
        say(toSpeak);

        Runnable r = new Runnable() {
            @Override
            public void run(){
                getWhatToDo(); //<-- put your code in here.
            }
        };

        Handler h = new Handler();
        h.postDelayed(r, 2300);

        int requestCode = 5; // unique code for the permission request
        ActivityCompat.requestPermissions(RecordOrRead.this, new String[]{RECORD_AUDIO, INTERNET}, requestCode);

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

    public void getWhatToDo(){

        Toast.makeText(getApplicationContext(),"GetWhatToDo",Toast.LENGTH_LONG).show();

        //textView2=findViewById(R.id.textview2);

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
                nextaction=result.getText();
                Toast.makeText(getApplicationContext(),nextaction,Toast.LENGTH_LONG).show();
                //textView2.setText(nextaction);
            }
            else{
                Toast.makeText(getApplicationContext(),"Error recognizing.",Toast.LENGTH_LONG).show();
                //textView2.setText("Error recognizing. Did you update the subscription info?"+System.lineSeparator()+result.toString() );
            }

            reco.close();

            if( nextaction.equalsIgnoreCase("Record New.") ){
                //proceed to record new entry
                Intent i=new Intent(RecordOrRead.this, RecordingActivity.class);
                startActivity(i);
            }
            else if( nextaction.equalsIgnoreCase("Read Previous.") ){
                //proceed to read an entry
                Intent i=new Intent(RecordOrRead.this, GettingRecordDate.class);
                startActivity(i);
            }
        }
        catch (Exception ex){
            Log.e("SpeechSDKDemo","unexpected "+ex.getMessage());
            assert(false);
        }
    }

    public void onDestroy() {
        // Don't forget to shutdown tts!
        if (t1 != null) {
            t1.stop();
            t1.shutdown();
        }
        super.onDestroy();
    }

}
