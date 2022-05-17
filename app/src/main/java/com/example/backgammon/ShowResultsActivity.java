package com.example.backgammon;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;

public class ShowResultsActivity extends AppCompatActivity {
    final private String FileName = "gameResults.txt";
    private LinkedList<String> results = new LinkedList<>();
    private int[] TextIDs = new int[]{R.id.result1,R.id.result2,R.id.result3,R.id.result4,R.id.result5};
    private BroadcastReceiver mReceiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_results);
        this.mReceiver = new ShowResultsActivity.BatteryBroadcastReceiver();
        readFile();
        if(this.results.size() >= 5) {
            for (int j=0, i = this.results.size() - 1; i > this.results.size() - 6; i--) {
                String text = "";
                TextView txtview = findViewById(TextIDs[j]);
                String [] parts = this.results.get(i).split("#");
                text += parts[0] + " beat " + parts[1] + ", " + parts[1] + " had " + parts[2] + " pieces left.";
                txtview.setText(text);
                j++;
            }
        }
        else{
            for (int j = 0, i = this.results.size() - 1; i > -1; i--) {
                String text = "";
                TextView txtview = findViewById(TextIDs[j]);
                String [] parts = this.results.get(i).split("#");
                text += parts[0] + " beat " + parts[1] + ", " + parts[1] + " had " + parts[2] + " pieces left.";
                txtview.setText(text);
                j++;
            }
        }
    }
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
    protected void onStart() {
        registerReceiver(this.mReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        super.onStart();
    }
    @Override
    protected void onStop() {
        unregisterReceiver(this.mReceiver);
        super.onStop();
    }
    public void readFile() {
        StringBuilder text = new StringBuilder();
        try{
            File root = new File(Environment.getExternalStorageDirectory(), "Files");
            if (!root.exists()) {
                root.mkdir();
            }
            File file = new File(root,this.FileName);
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while((line = br.readLine()) != null)
            {
                this.results.add(line);
                text.append(line);
                text.append("\n");
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("here: ");
        System.out.println(text.toString());
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mymenu2,menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.item1:
                exitActivity();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    public void exitActivity() { finish(); }
}