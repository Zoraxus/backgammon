package com.example.backgammon;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

public class ShowRulesActivity extends AppCompatActivity {

    // used for pages
    private int[] Pages = new int[]{R.drawable.rulespage1,R.drawable.rulespage2,R.drawable.rulespage3,
            R.drawable.rulespage4,R.drawable.rulespage5};
    private int currentPage = 1;


    private BroadcastReceiver mReceiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /*
        this function will run when the activity is created and will show results, create broadcast receiver
        and will set the layout.
        param: saveInstanceState : Bundle
        return: void
        */
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_rules);
        this.mReceiver = new ShowRulesActivity.BatteryBroadcastReceiver();
    }
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
            if(!isCharging && level <= 15 && !this.isLow) { // if the phone isn't being charged and level is less than 15
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
        /*
        this function will run when the activity starts to be shown, will start the broadcast receiver.
        param: none
        return: void
        */
        registerReceiver(this.mReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        super.onStart();
    }

    @Override
    protected void onStop() {
        /*
        this function will run when the activity stops to be shown, will stop the broadcast receiver.
        param: none
        return: void
        */
        unregisterReceiver(this.mReceiver);
        super.onStop();
    }

    public void goNext(View view) {
        /*
        this function will change the image to the next one
        param: view : View
        return: void
        */
        if(this.currentPage < 5)
        {
            ImageView img = findViewById(R.id.viewRules);
            img.setImageResource(this.Pages[currentPage]);
            this.currentPage ++;
        }
    }
    public void goPrev(View view){
        /*
        this function will change the previous one
        param: view : View
        return: void
        */
        if(this.currentPage > 1) {
            ImageView img = findViewById(R.id.viewRules);
            img.setImageResource(this.Pages[currentPage - 2]);
            this.currentPage--;
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
        inflater.inflate(R.menu.mymenu2,menu);
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
                exitActivity();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    public void exitActivity() {
        /*
        this function will finish the activity
        param: none
        return: void
        */
        finish();
    }
}