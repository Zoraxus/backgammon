package com.example.backgammon;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Bundle;
import android.text.method.Touch;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.Toast;

public class OfflineGameActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offline_game);

    }
    @Override
    public boolean onTouchEvent(MotionEvent e){
        ImageView img1 = findViewById(R.id.bPiece15);
        float imgX = img1.getLeft();
        float imgY = img1.getTop();
        float x = e.getX();
        float y = e.getY();
        System.out.println("X,Y: "+x+","+y);
        System.out.println("imgX,imgY: "+imgX+","+imgY);

        return true;
    }
}