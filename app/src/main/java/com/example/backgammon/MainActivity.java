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
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startService(new Intent(this, MyService.class));

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            Log.d("READ PREMITION","success");
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
                float lux = sensorEvent.values[0];
                if(lux < 10 && !isDark)
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
            public void onAccuracyChanged(Sensor sensor, int i) {

            }
        };
        this.sensorManager.registerListener(sensorEventListenerLight,this.sensorLight,sensorManager.SENSOR_DELAY_NORMAL);

    }
    @Override
    protected void onStart() {
        registerReceiver(this.mReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        startService(new Intent(this, MyService.class));
        super.onStart();
    }

    @Override
    protected void onStop() {
        unregisterReceiver(this.mReceiver);
        stopService(new Intent(this, MyService.class));
        super.onStop();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 0:
                Log.d("READ PREMITION", "success");
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mymenu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.item1:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage("Do you want to exit ?");
        builder.setCancelable(false);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                finish();
            }});
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.cancel();
            }});
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    public void onClick1(View view) {
        stopService(new Intent(this, MyService.class));
        Intent intent = new Intent(this, OfflineGameActivity.class);
        startActivity(intent);

    }
    public void matchLogShow(View view) {
        stopService(new Intent(this, MyService.class));
        Intent intent = new Intent(this, ShowResultsActivity.class);
        startActivity(intent);
    }
    public void ShowRules(View view) {
        stopService(new Intent(this, MyService.class));
        Intent intent = new Intent(this, ShowRulesActivity.class);
        startActivity(intent);
    }
    public void changeAudioState(View view)
    {
        if(this.isAudioOn)
        {
            this.isAudioOn = false;
            stopService(new Intent(this, MyService.class));
            Button button = findViewById(R.id.musicState);
            button.setBackgroundResource(R.drawable.volumeoff);
        }
        else{
            this.isAudioOn = true;
            startService(new Intent(this, MyService.class));
            Button button = findViewById(R.id.musicState);
            button.setBackgroundResource(R.drawable.volumeon);
        }
    }
}