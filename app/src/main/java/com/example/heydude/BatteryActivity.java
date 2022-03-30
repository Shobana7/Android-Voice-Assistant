package com.example.heydude;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

public class BatteryActivity extends AppCompatActivity {

    TextView textview;
    TextToSpeech t1;
    IntentFilter intentfilter;
    int deviceStatus;
    String currentBatteryStatus="Battery Info";
    int batteryLevel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_battery);

        textview=findViewById(R.id.textViewBattery);

        intentfilter=new IntentFilter(Intent.ACTION_BATTERY_CHANGED);

        //done on button press
        BatteryActivity.this.registerReceiver(broadcastreceiver,intentfilter);

    }

    private BroadcastReceiver broadcastreceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            deviceStatus = intent.getIntExtra(BatteryManager.EXTRA_STATUS,-1);
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            int batteryLevel=(int)(((float)level / (float)scale) * 100.0f);

            if(deviceStatus == BatteryManager.BATTERY_STATUS_CHARGING){

                textview.setText(currentBatteryStatus+" = Charging at "+batteryLevel+" %");

            }

            if(deviceStatus == BatteryManager.BATTERY_STATUS_DISCHARGING){

                textview.setText(currentBatteryStatus+" = Discharging at "+batteryLevel+" %");

            }

            if (deviceStatus == BatteryManager.BATTERY_STATUS_FULL){

                textview.setText(currentBatteryStatus+"= Battery Full at "+batteryLevel+" %");

            }

            if(deviceStatus == BatteryManager.BATTERY_STATUS_UNKNOWN){

                textview.setText(currentBatteryStatus+" = Unknown at "+batteryLevel+" %");
            }


            if (deviceStatus == BatteryManager.BATTERY_STATUS_NOT_CHARGING){

                textview.setText(currentBatteryStatus+" = Not Charging at "+batteryLevel+" %");

            }

            String toSpeak=textview.getText().toString();
            sayBattery(toSpeak);

        }
    };

    public void sayBattery(String toSpeak){

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
