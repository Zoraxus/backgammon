package com.example.backgammon;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClick1(View view) {
        Intent intent = new Intent(this, OfflineGameActivity.class);
        startActivity(intent);

    }

    public void onClick2(View view) {
    }

    public void onClick3(View view) {
    }

    public void onClick4(View view) {
    }
}