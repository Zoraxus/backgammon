package com.example.backgammon;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
public class MainActivity extends AppCompatActivity {
    private boolean isAudioOn = true;
    private SensorManager sensorManager;
    private Sensor sensorLight;
    private boolean isDark = false;
    private BroadcastReceiver mReceiver;
    private class BatteryBroadcastReceiver extends BroadcastReceiver {
        private boolean isLow = false;
        private final static String BATTERY_LEVEL = "level";

        @Override
        public void onReceive(Context context, Intent intent) {
            /*
            this function will run when the phone sends a broadcast and will show user an alert
            param: context : Context , intent : Intent
            return: void
            */
            IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
            Intent batteryStatus = context.registerReceiver(null, ifilter);
            int level = intent.getIntExtra(BATTERY_LEVEL, 0);
            int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
            boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                    status == BatteryManager.BATTERY_STATUS_FULL;
            if(!isCharging && level <= 15 && !this.isLow) {
                this.isLow = true;
                Toast toast = Toast.makeText(context, "Low Battery Alert!", Toast.LENGTH_LONG);
                toast.show();
            }
            else{
                this.isLow = false;
            }
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /*
        this function will run when the activity is created and will create broadcastReceiver, sensor,
        ask for permission.
        param: saveInstanceState : Bundle
        return: void
        */
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            Log.d("WRITE PERMISSION","success");
        } else {
            // Request permission from the user
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        }
        this.mReceiver = new BatteryBroadcastReceiver();

        this.sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        this.sensorLight = this.sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

        SensorEventListener sensorEventListenerLight = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                /*
                this function will run when the lightSensor level changed
                param: sensorEvent : SensorEvent
                return:void
                */
                float lux = sensorEvent.values[0];
                if(lux < 2 && !isDark)
                {
                    Context context = getApplicationContext();
                    Toast toast = Toast.makeText(context, "It's Dark, Lower your phone brightness", Toast.LENGTH_SHORT);
                    toast.show();
                    isDark = true;
                }
                else
                {
                    isDark = false;
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {} // i don't use this one.
        };
        this.sensorManager.registerListener(sensorEventListenerLight,this.sensorLight,sensorManager.SENSOR_DELAY_NORMAL);

    }
    @Override
    protected void onStart() {
        /*
        this function will run when the activity is the one shown, will start the broadcast receiver,
        the music Service and will set the layout.
        param: none
        return: void
        */
        registerReceiver(this.mReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        if(isAudioOn) {
            startService(new Intent(getApplicationContext(), MyService.class));
        }
        super.onStart();
    }

    @Override
    protected void onStop() {
        /*
        this function will run when the activity stops to be shown, will stop the broadcast receiver
        and the music Service.
        param: none
        return: void
        */
        unregisterReceiver(this.mReceiver);
        stopService(new Intent(getApplicationContext(), MyService.class));
        super.onStop();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        /*
        this function will run after permission has been requested
        param: requestCode : int , permissions : String[] , grantResults : int[]
        return: void
        */
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 0:
                Log.d("WRITE PERMISSION", "success");
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /*
        this function will start the menu on the activity
        param: menu : Menu
        return: boolean
        */
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mymenu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        /*
        this function will handle menu options selected
        param: item : MenuItem
        return: boolean
        */
        switch (item.getItemId()){
            case R.id.item1:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {
        /*
        this function will handle back button pressed
        param: none
        return: void
        */
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage("Do you want to exit ?");
        builder.setCancelable(false);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                /*
                this function will handle if the user pressed exit->yes.
                param: dialog : DialogInterface , which : int
                return: void
                */
                finish();
            }});
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                /*
                this function will handle if the user pressed exit->no.
                param: dialog : DialogInterface , which : int
                return: void
                */
                dialog.cancel();
            }});
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    public void startGame(View view) {
        /*
        this function will handle what happens if the user pressed new offline game button
        param: view : View
        return: void
        */
        Intent intent = new Intent(this, OfflineGameActivity.class);
        startActivity(intent);
        if (isAudioOn) {
            stopService(new Intent(getApplicationContext(), MyService.class));
        }

    }
    public void matchLogShow(View view) {
        /*
        this function will handle what happens if the user pressed the show mach log button
        param: view : View
        return: void
        */
        Intent intent = new Intent(this, ShowResultsActivity.class);
        startActivity(intent);
        if (isAudioOn) {
            stopService(new Intent(getApplicationContext(), MyService.class));
        }
    }
    public void ShowRules(View view) {
        /*
        this function will handle what happens if the user pressed show rules button
        param: view : View
        return: void
        */
        Intent intent = new Intent(this, ShowRulesActivity.class);
        startActivity(intent);
        if (isAudioOn) {
            stopService(new Intent(getApplicationContext(), MyService.class));
        }
    }
    public void changeAudioState(View view) {
        /*
        this function will handle what happens if the user pressed the audio button
        param: view : View
        return: void
        */
        System.out.println(isAudioOn);
        if(this.isAudioOn)
        {
            this.isAudioOn = false;
            Button button = findViewById(R.id.musicState);
            button.setBackgroundResource(R.drawable.volumeoff);
            stopService(new Intent(getApplicationContext(), MyService.class));
        }
        else{
            this.isAudioOn = true;
            startService(new Intent(getApplicationContext(), MyService.class));
            Button button = findViewById(R.id.musicState);
            button.setBackgroundResource(R.drawable.volumeon);
        }
    }
}