package com.example.heydude;

import android.content.Intent;
import android.net.Uri;
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

public class CallingActivity extends AppCompatActivity {

    TextToSpeech t1;
    TextView textView2;
    String phoneNum;
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
        setContentView(R.layout.activity_calling);

        enterNum();

        Runnable r = new Runnable() {
            @Override
            public void run(){
                getNumber(); //<-- put your code in here.



            }
        };

        Handler h = new Handler();
        h.postDelayed(r, 2000);

        int requestCode = 5; // unique code for the permission request
        ActivityCompat.requestPermissions(CallingActivity.this, new String[]{RECORD_AUDIO, INTERNET}, requestCode);



    }


    public void enterNum(){

        t1=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status !=TextToSpeech.ERROR ){
                    t1.setLanguage(Locale.UK);
                    String toSpeak="Say the Number!";
                    Toast.makeText(getApplicationContext(),toSpeak,Toast.LENGTH_SHORT).show();
                    t1.speak(toSpeak,TextToSpeech.QUEUE_FLUSH,null);
                }
            }
        });

    }

    public void getNumber(){

        Toast.makeText(getApplicationContext(),"GetNumber",Toast.LENGTH_LONG).show();

        textView2=findViewById(R.id.textViewPhoneNum);

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
                phoneNum=result.getText();
                textView2.setText(phoneNum);
            }
            else{
                textView2.setText("Error recognizing. Did you update the subscription info?"+System.lineSeparator()+result.toString() );
            }

            reco.close();

            //making a call
            Intent i = new Intent(Intent.ACTION_CALL);
            i.setData(Uri.parse("tel:"+phoneNum));
            startActivity(i);


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



/*
call_btn.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // TODO Auto-generated method stub
                Intent i = new Intent(Intent.ACTION_CALL);
                i.setData(Uri.parse("tel:"+et.getText().toString()));
                startActivity(i);
            }
        });
 */