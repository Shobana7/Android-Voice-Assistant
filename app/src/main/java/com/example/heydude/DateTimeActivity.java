package com.example.heydude;

import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class DateTimeActivity extends AppCompatActivity {

    Calendar calander;
    SimpleDateFormat simpledateformat;
    String Date, Day;
    TextView DisplayDateTime;
    TextToSpeech t1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_datetime);

        DisplayDateTime=findViewById(R.id.textViewDateTime);

        calander = Calendar.getInstance();
        simpledateformat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        Date = simpledateformat.format(calander.getTime());

        DisplayDateTime.setText(Date);
        String toSpeak=DisplayDateTime.getText().toString();
        sayDateTime(toSpeak);

    }

    public void sayDateTime(String toSpeak){

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
}
