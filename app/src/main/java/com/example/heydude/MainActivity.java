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

import java.util.Locale;
import java.util.concurrent.Future;

import static android.Manifest.permission.INTERNET;
import static android.Manifest.permission.RECORD_AUDIO;
import static java.lang.Thread.sleep;

public class MainActivity extends AppCompatActivity {

    TextToSpeech introspeech;
    TextView textView2;
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
        setContentView(R.layout.activity_main);

        heyDude();

        Runnable r = new Runnable() {
            @Override
            public void run(){
                getWhatToDo(); //<-- put your code in here.
            }
        };

        Handler h = new Handler();
        h.postDelayed(r, 3000);

        int requestCode = 5; // unique code for the permission request
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{RECORD_AUDIO, INTERNET}, requestCode);



    }

    public void heyDude(){

        introspeech=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status !=TextToSpeech.ERROR ){
                    introspeech.setLanguage(Locale.UK);
                    String toSpeak="Hey Dude! What do you wanna do now?";
                    Toast.makeText(getApplicationContext(),toSpeak,Toast.LENGTH_SHORT).show();
                    introspeech.speak(toSpeak,TextToSpeech.QUEUE_FLUSH,null);
                }
            }
        });

    }

    public void getWhatToDo(){

        Toast.makeText(getApplicationContext(),"GetWhatToDo",Toast.LENGTH_LONG).show();

        textView2=findViewById(R.id.textview2);

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
                textView2.setText(nextaction);
            }
            else{
                textView2.setText("Error recognizing. Did you update the subscription info?"+System.lineSeparator()+result.toString() );
            }

            reco.close();

            if( nextaction.equalsIgnoreCase("I want to make a call now") ){
                //proceed to make call
                Intent i=new Intent(MainActivity.this, CallingActivity.class);
                startActivity(i);
            }
            else if( nextaction.equalsIgnoreCase("what's the battery status?") ){
                Intent i=new Intent(MainActivity.this, BatteryActivity.class);
                startActivity(i);
            }
            else if( (nextaction.equalsIgnoreCase("what's the date?")) || ( nextaction.equalsIgnoreCase("what's the time?")) || ( nextaction.equalsIgnoreCase("what's the date and time?")) ){
                Intent i=new Intent(MainActivity.this, DateTimeActivity.class);
                startActivity(i);
            }
            else if( nextaction.equalsIgnoreCase("record my day.") ){
                Intent i=new Intent(MainActivity.this,RecordOrRead.class);
                startActivity(i);
            }
            else if ( nextaction.equalsIgnoreCase("play music.") ){
                //play music
            }
            else if( nextaction.equalsIgnoreCase("take a snap.") ){
                //open camera
            }



        }
        catch (Exception ex){
            Log.e("SpeechSDKDemo","unexpected "+ex.getMessage());
            assert(false);
        }


    }

    public void onDestroy() {
        // Don't forget to shutdown tts!
        if (introspeech != null) {
            introspeech.stop();
            introspeech.shutdown();
        }
        super.onDestroy();
    }

}
