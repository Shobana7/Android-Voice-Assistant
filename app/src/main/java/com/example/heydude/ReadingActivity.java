package com.example.heydude;

import android.content.Intent;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

public class ReadingActivity extends AppCompatActivity {

    TextView textview;
    TextToSpeech t1;
    DBHelper mydb;
    int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reading);

        //i.putExtra("date",nextaction);
        Intent i=getIntent();
        String date=i.getStringExtra("date");

        //int position=i.getIntExtra("position",1);

        ArrayList date_arrayList=mydb.getAllDates();

        for(int iterator=0;iterator<date_arrayList.size();iterator++){
            if(date.equalsIgnoreCase( (String) date_arrayList.get(iterator)  ) ){
                position=iterator;
            }
        }




        mydb=new DBHelper(this);
        textview=findViewById(R.id.textViewEntry);

        ArrayList array_list=mydb.getAllEntries();
        String content_of_entry= (String) array_list.get(position);
        textview.setText(content_of_entry);
        readEntry(content_of_entry);

    }

    public void readEntry(String toSpeak){

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
