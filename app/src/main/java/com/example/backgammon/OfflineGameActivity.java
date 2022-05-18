package com.example.backgammon;
import java.security.SecureRandom;
import java.util.Map;
import java.util.Random;
import java.util.Map;
import java.util.HashMap;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import java.io.*;
import static java.lang.Math.E;
import static java.lang.Math.abs;
import static java.lang.Math.round;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Bundle;
import android.os.Environment;
import android.text.method.Touch;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.webkit.WebHistoryItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.backgammon.Classes.Column;
import com.example.backgammon.Classes.Game;
import com.example.backgammon.Classes.Piece;

import org.w3c.dom.Text;

import java.util.LinkedList;


public class OfflineGameActivity extends Activity {
    SecureRandom srandom = new SecureRandom();
    final private String FileName = "gameResults.txt";
    public String name_player_1 = "";
    public String name_player_2 = "";
    private int turn = 1;
    private int press = 0;
    private int col_pressed = -1;
    private int[] piecesPosBottom = new int[] {960,880,800,720,640,920,840,760,680};
    private int[] piecesPosTop = new int[] {37,117,197,277,357,77,157,237,317};
    private int[][][] columnsPos = new int[][][] {
            {{1869,1040},{1740,600}},{{1739,1040},{1610,600}},{{1609,1040},{1480,600}},{{1479,1040},{1350,600}},{{1349,1040},{1220,600}},{{1219,1040},{1090,600}},
            {{999,1040},{870,600}},{{869,1040},{740,600}},{{739,1040},{610,600}},{{609,1040},{480,600}},{{479,1040},{350,600}},{{349,1040},{220,600}},
            {{221,38},{350,480}},{{351,38},{480,480}},{{481,38},{610,480}},{{611,38},{740,480}},{{741,38},{870,480}},{{871,38},{1000,480}},
            {{1091,38},{1220,480}},{{1221,38},{1350,480}},{{1351,38},{1480,480}},{{1481,38},{1610,480}},{{1611,38},{1740,480}},{{1741,38},{1870,480}}};
    private int[][] blueEaten = new int[][]{{1007,535},{1007,615},{1007,695},{1007,775},{1007,855},{1007,935}};
    private int[][] redEaten = new int[][]{{1007,455},{1007,375},{1007,295},{1007,215},{1007,135},{1007,55}};
    private int[] bluePieceOutID = new int[]{R.id.outpieceblue1,R.id.outpieceblue2,R.id.outpieceblue3,R.id.outpieceblue4,R.id.outpieceblue5,
            R.id.outpieceblue6,R.id.outpieceblue7,R.id.outpieceblue8,R.id.outpieceblue9,R.id.outpieceblue10,
            R.id.outpieceblue11,R.id.outpieceblue12,R.id.outpieceblue13,R.id.outpieceblue14,R.id.outpieceblue15};
    private int[] redPieceOutID = new int[]{R.id.outpiecered1,R.id.outpiecered2,R.id.outpiecered3,R.id.outpiecered4,R.id.outpiecered5,
            R.id.outpiecered6,R.id.outpiecered7,R.id.outpiecered8,R.id.outpiecered9,R.id.outpiecered10,
            R.id.outpiecered11,R.id.outpiecered12,R.id.outpiecered13,R.id.outpiecered14,R.id.outpiecered15};
    private Game game;
    private boolean rolled = false;
    private Map<Integer,Integer> diceMap = new HashMap<Integer, Integer>() {};
    private int NumOfMoves = 0;
    private int MovePower1 = 0;
    private int MovePower2 = 0;
    private boolean is_red_eaten = false;
    private boolean is_blue_eaten = false;
    private LinkedList<Piece> blueOut = new LinkedList<>();
    private LinkedList<Piece> redOut = new LinkedList<>();
    private int whoWon = -1;
    private boolean didGameEnd = false;
    private BroadcastReceiver mReceiver;
    private SensorManager sensorManager;
    private Sensor sensorLight;
    private boolean isDark = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offline_game);
        LinkedList<Integer> ll = getIds();
        this.game = new Game(this.columnsPos, ll);
        this.diceMap.put(1, R.drawable.dice1);
        this.diceMap.put(2, R.drawable.dice2);
        this.diceMap.put(3, R.drawable.dice3);
        this.diceMap.put(4, R.drawable.dice4);
        this.diceMap.put(5, R.drawable.dice5);
        this.diceMap.put(6, R.drawable.dice6);
        this.mReceiver = new OfflineGameActivity.BatteryBroadcastReceiver();
        showStartDialog();
        this.sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        this.sensorLight = this.sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

        SensorEventListener sensorEventListenerLight = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                float lux = sensorEvent.values[0];
                if (lux < 10 && !isDark) {
                    Context context = getApplicationContext();
                    Toast toast = Toast.makeText(context, "It's Dark, Lower your phone brightness", Toast.LENGTH_SHORT);
                    toast.show();
                    isDark = true;
                } else {
                    isDark = false;
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {

            }
        };
        this.sensorManager.registerListener(sensorEventListenerLight, this.sensorLight, sensorManager.SENSOR_DELAY_NORMAL);
    }
    public void showStartDialog(){
        final Dialog dialog = new Dialog(OfflineGameActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.get_names_dialog);
        final EditText name1Et = dialog.findViewById(R.id.name1);
        final EditText name2Et = dialog.findViewById(R.id.name2);

        Button startButton = dialog.findViewById(R.id.start_button);
        Button cancelButton = dialog.findViewById(R.id.cancel_button);

        startButton.setOnClickListener(new View.OnClickListener() {
           @Override
            public void onClick(View view)
           {
               name_player_1 = name1Et.getText().toString();
               name_player_2 = name2Et.getText().toString();
               if(name_player_1.length() > 6)
               {
                   Context context = getApplicationContext();
                   Toast toast = Toast.makeText(context, "Max Name Length 6.", Toast.LENGTH_SHORT);
                   toast.show();
               }
               else {
                   dialog.dismiss();
               }
           }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                dialog.dismiss();
                finish();
            }
        });
        dialog.show();
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
    public LinkedList<Integer> getIds(){
        LinkedList<Integer> ll = new LinkedList<Integer>();
        ll.add(R.id.rPiece1);
        ll.add(R.id.rPiece2);
        ll.add(R.id.rPiece3);
        ll.add(R.id.rPiece4);
        ll.add(R.id.rPiece5);
        ll.add(R.id.rPiece6);
        ll.add(R.id.rPiece7);
        ll.add(R.id.rPiece8);
        ll.add(R.id.rPiece9);
        ll.add(R.id.rPiece10);
        ll.add(R.id.rPiece11);
        ll.add(R.id.rPiece12);
        ll.add(R.id.rPiece13);
        ll.add(R.id.rPiece14);
        ll.add(R.id.rPiece15);
        ll.add(R.id.bPiece1);
        ll.add(R.id.bPiece2);
        ll.add(R.id.bPiece3);
        ll.add(R.id.bPiece4);
        ll.add(R.id.bPiece5);
        ll.add(R.id.bPiece6);
        ll.add(R.id.bPiece7);
        ll.add(R.id.bPiece8);
        ll.add(R.id.bPiece9);
        ll.add(R.id.bPiece10);
        ll.add(R.id.bPiece11);
        ll.add(R.id.bPiece12);
        ll.add(R.id.bPiece13);
        ll.add(R.id.bPiece14);
        ll.add(R.id.bPiece15);
        return ll;
    }
    public int findColumn(int x,int y) {
        int columnNum = -1;
        for (int i=0;i<this.columnsPos.length;i++){
            if (i < this.columnsPos.length/2){
                if (x <= this.columnsPos[i][0][0] && x >= this.columnsPos[i][1][0]){
                    if (y <= this.columnsPos[i][0][1] && y >= this.columnsPos[i][1][1]){
                        columnNum = i+1;
                        break;
                    }
                }
            }
            else{
                if (x >= this.columnsPos[i][0][0] && x <= this.columnsPos[i][1][0]){
                    if (y >= this.columnsPos[i][0][1] && y <= this.columnsPos[i][1][1]){
                        columnNum = i+1;
                        break;
                    }
                }
            }
        }
        return columnNum;
    }
    public void highlightColumn(int colNum, boolean flag){
        Column column = this.game.getAcolumn(colNum);
        LinkedList<Piece> ll = column.getList();
        if (ll.size() >= 1){
            int type = ll.get(0).getType();
            for (int i=0;i<ll.size();i++) {
                ImageView imgview = findViewById(ll.get(i).getId());
                if (type == 1){
                    if (flag){
                        imgview.setImageResource(R.drawable.bluepieceglow);
                    }
                    else{
                        imgview.setImageResource(R.drawable.bluepiece);
                    }
                }
                else{
                    if (flag){
                        imgview.setImageResource(R.drawable.redpieceglow);
                    }
                    else {
                        imgview.setImageResource(R.drawable.redpiece);
                    }
                }
            }
        }
    }
    public boolean CheckIfColumnIsEmpty(int colNum){
        return this.game.checkIfColumnIsEmpty(colNum);
    }
    public void move(int colNum1, int colNum2){
        Piece p = this.game.move(colNum1,colNum2);
        int place = this.game.getAcolumn(colNum2).getList().size();
        if (place == 2) {
            int type1 = this.game.getAcolumn(colNum2).getList().get(1).getType();
            int type2 = this.game.getAcolumn(colNum2).getList().get(0).getType();
            if (type1 != type2) {
                if (type2 == 1) {
                    Piece EatenPiece = this.game.getAcolumn(colNum2).getList().getFirst();
                    this.game.getAcolumn(colNum2).getList().removeFirst();
                    ImageView img = findViewById(EatenPiece.getId());
                    this.game.getBLueEaten().add(EatenPiece);
                    img.setX(this.blueEaten[this.game.getBLueEaten().size() - 1][0]);
                    img.setY(this.blueEaten[this.game.getBLueEaten().size() - 1][1]);
                    this.is_blue_eaten = true;

                } else {
                    Piece EatenPiece = this.game.getAcolumn(colNum2).getList().getFirst();
                    this.game.getAcolumn(colNum2).getList().removeFirst();
                    ImageView img = findViewById(EatenPiece.getId());
                    this.game.getRedEaten().add(EatenPiece);
                    img.setX(this.redEaten[this.game.getRedEaten().size() - 1][0]);
                    img.setY(this.redEaten[this.game.getRedEaten().size() - 1][1]);
                    this.is_red_eaten = true;
                }
                place = place - 1;
            }
        }
        ImageView imgview = findViewById(p.getId());
        if (colNum2 <= 12) {
            imgview.setY(this.piecesPosBottom[place - 1]);
            imgview.setX(this.columnsPos[colNum2 - 1][1][0] + 27);
        } else {
            imgview.setY(this.piecesPosTop[place - 1]);
            imgview.setX(this.columnsPos[Math.abs(colNum2 - 24)][1][0] + 27);
        }
        if (p.getType() == 1) {
            imgview.setImageResource(R.drawable.bluepiece);
        } else {
            imgview.setImageResource(R.drawable.redpiece);
        }
    }
    public boolean IsValidMove(int col1, int col2){
        if (col1 == -1){
            if (this.game.getAcolumn(col2).getList().size() <= 1 || this.turn == this.game.getAcolumn(col2).getList().getFirst().getType()){
                if (col2 == 25-this.MovePower1){
                    if (this.NumOfMoves <= 2){
                        this.MovePower1 = 0;
                        ImageView img5 = findViewById(R.id.dice5);
                        ImageView img7 = findViewById(R.id.dice7);
                        img5.setVisibility(View.INVISIBLE);
                        img7.setVisibility(View.INVISIBLE);
                    }
                    return true;
                }
                else if(col2 == 25-this.MovePower2){
                    if (this.NumOfMoves <= 2){
                        this.MovePower2 = 0;
                        ImageView img6 = findViewById(R.id.dice6);
                        ImageView img8 = findViewById(R.id.dice8);
                        img6.setVisibility(View.INVISIBLE);
                        img8.setVisibility(View.INVISIBLE);
                    }
                    return true;
                }
                else{
                    return false;
                }
            }
            else
            {
                return false;
            }
        }
        else if(col1 == -2){
            if (this.game.getAcolumn(col2).getList().size() <= 1 || this.turn == this.game.getAcolumn(col2).getList().getFirst().getType()){
                if (col2 == this.MovePower1){
                    if (this.NumOfMoves <= 2){
                        this.MovePower1 = 0;
                        ImageView img5 = findViewById(R.id.dice5);
                        ImageView img7 = findViewById(R.id.dice7);
                        img5.setVisibility(View.INVISIBLE);
                        img7.setVisibility(View.INVISIBLE);
                    }
                    return true;
                }
                else if(col2 == this.MovePower2){
                    if (this.NumOfMoves <= 2){
                        this.MovePower2 = 0;
                        ImageView img6 = findViewById(R.id.dice6);
                        ImageView img8 = findViewById(R.id.dice8);
                        img6.setVisibility(View.INVISIBLE);
                        img8.setVisibility(View.INVISIBLE);
                    }
                    return true;
                }
                else{
                    return false;
                }
            }
            else
            {
                return false;
            }
        }
        int type = this.game.getAcolumn(col1).getList().get(0).getType();
        if (this.game.getAcolumn(col2).getList().size() > 0){
            if (this.game.getAcolumn(col2).getList().get(0).getType() != type && this.game.getAcolumn(col2).getList().size() > 1){
                return false;
            }
        }

        if (type == 1)
        {
            if (col1 - this.MovePower1 == col2 && this.MovePower1 != 0)
            {
                if (this.NumOfMoves <= 2) {
                    this.MovePower1 = 0;
                    ImageView img5 = findViewById(R.id.dice5);
                    ImageView img7 = findViewById(R.id.dice7);
                    img5.setVisibility(View.INVISIBLE);
                    img7.setVisibility(View.INVISIBLE);
                }
                return true;
            }
            else if (col1 - this.MovePower2 == col2 && this.MovePower2 != 0)
            {
                if (this.NumOfMoves <= 2) {
                    ImageView img6 = findViewById(R.id.dice6);
                    ImageView img8 = findViewById(R.id.dice8);
                    img6.setVisibility(View.INVISIBLE);
                    img8.setVisibility(View.INVISIBLE);
                    this.MovePower2 = 0;
                }
                return true;
            }
            else
            {
                return false;
            }
        }
        else{
            if (col1 + this.MovePower1 == col2 && this.MovePower1 != 0)
            {
                if (this.NumOfMoves <= 2) {
                    ImageView img5 = findViewById(R.id.dice5);
                    ImageView img7 = findViewById(R.id.dice7);
                    img5.setVisibility(View.INVISIBLE);
                    img7.setVisibility(View.INVISIBLE);
                    this.MovePower1 = 0;
                }
                return true;
            }
            else if(col1 + this.MovePower2 == col2 && this.MovePower2 != 0)
            {
                if (this.NumOfMoves <= 2) {
                    ImageView img6 = findViewById(R.id.dice6);
                    ImageView img8 = findViewById(R.id.dice8);
                    img6.setVisibility(View.INVISIBLE);
                    img8.setVisibility(View.INVISIBLE);
                    this.MovePower2 = 0;
                }
                return true;
            }
            else
            {
                return false;
            }
        }
    }
    public boolean AreThereAnyPossibleMoves(int type) {
        //reminder for myself
        //there is a problem for end game when i roll numbers that i cant move but can get out.
        if(checkForEndGame(type))
        {
            return true;
        }
        int mult;
        if(type == 1){
            mult = -1;
        }
        else
        {
            mult = 1;
        }
        int count = 0;
        if (this.is_blue_eaten && this.turn == 1)
        {
            if(this.MovePower1 != 0)
            {
                if(this.game.getAcolumn(25-this.MovePower1).getList().size() <= 1)
                {
                    return true;
                }
                else if(this.game.getAcolumn(25-this.MovePower1).getList().getFirst().getType() == 1){
                    return true;
                }
                else
                {
                    count++;
                }
            }
            if(this.MovePower2 != 0)
            {
                if(this.game.getAcolumn(25-this.MovePower2).getList().size() <= 1)
                {
                    return true;
                }
                else if( this.game.getAcolumn(25-this.MovePower2).getList().getFirst().getType() == 1)
                {
                    return true;
                }
                else
                {
                    count++;
                }
            }
        }
        else if (this.is_red_eaten && this.turn == 0)
        {
            if(this.MovePower1 != 0)
            {
                if(this.game.getAcolumn(this.MovePower1).getList().size() <= 1)
                {
                    return true;
                }
                else if(this.game.getAcolumn(this.MovePower1).getList().getFirst().getType() == 0){
                    return true;
                }
                else
                {
                    count++;
                }
            }
            if(this.MovePower2 != 0)
            {
                if(this.game.getAcolumn(this.MovePower2).getList().size() <= 1)
                {
                    return true;
                }
                else if( this.game.getAcolumn(this.MovePower2).getList().getFirst().getType() == 0)
                {
                    return true;
                }
                else
                {
                    count++;
                }
            }
        }
        if(count > 0)
        {
            return false;
        }
        int col1,col2;
        for (int i=0;i<24;i++){
            if(this.game.getAcolumn(i+1).getList().size() > 0)
            {
                if (this.game.getAcolumn(i+1).getList().getFirst().getType() == type)
                {
                    col1 = i + this.MovePower1*mult + 1;
                    col2 = i + this.MovePower2*mult + 1;
                    if(col1 > 0 && col1 <= 24)
                    {
                        if(this.game.getAcolumn(col1).getList().size() <= 1)
                        {
                            System.out.println("wtf:" + col1);
                            return true;
                        }
                        else if(this.game.getAcolumn(col1).getList().getFirst().getType() == type)
                        {
                            System.out.println("wtf:" + col1);
                            return true;
                        }
                    }
                    if(col2 > 0 && col2 <= 24)
                    {
                        if(this.game.getAcolumn(col2).getList().size() <= 1)
                        {
                            System.out.println("wtf:" + col2);
                            return true;
                        }
                        else if(this.game.getAcolumn(col2).getList().getFirst().getType() == type)
                        {
                            System.out.println("wtf:" + col2);
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
    public void RollDice(View view){
        if (this.rolled == false){
            int dice1 = Math.abs(srandom.nextInt()%6)+1;
            int dice2 = Math.abs(srandom.nextInt()%6)+1;
            System.out.println("dice1: "+dice1);
            System.out.println("dice2: "+dice2);
            if (dice1 == dice2){
                this.NumOfMoves = 4;
                this.MovePower1 = dice1;
                this.MovePower2 = dice2;
            }
            else
            {
                this.NumOfMoves = 2;
                this.MovePower1 = dice1;
                this.MovePower2 = dice2;
            }
            if(!AreThereAnyPossibleMoves(this.turn)){
                this.turn = Math.abs(this.turn-1);
                this.MovePower1 = 0;
                this.MovePower2 = 0;
                this.NumOfMoves = 0;
            }
            else
            {
                Button button1 = findViewById(R.id.roll1);
                button1.setBackgroundResource(R.drawable.reddiceglow);
                ImageView imgV5 = findViewById(R.id.dice5);
                ImageView imgV7 = findViewById(R.id.dice7);
                imgV5.setImageResource(this.diceMap.get(dice1));
                imgV5.setVisibility(View.VISIBLE);
                imgV7.setImageResource(this.diceMap.get(dice1));
                imgV7.setVisibility(View.VISIBLE);
                ImageView imgV6 = findViewById(R.id.dice6);
                ImageView imgV8 = findViewById(R.id.dice8);
                imgV6.setImageResource(this.diceMap.get(dice2));
                imgV6.setVisibility(View.VISIBLE);
                imgV8.setImageResource(this.diceMap.get(dice2));
                imgV8.setVisibility(View.VISIBLE);
                if(this.turn == 1 && this.is_blue_eaten)
                {
                    highlightPossibleColumns(-1);
                }
                else if(this.turn == 0 && this.is_red_eaten)
                {
                    highlightPossibleColumns(-2);
                }
                this.rolled = true;
            }
            ImageView arrow = findViewById(R.id.turnArrow);
            if(this.turn == 1){
                arrow.setImageResource(R.drawable.bluearrowdown);
            }
            else
            {
                arrow.setImageResource(R.drawable.redarrowup);
            }
        }
    }
    public void GetOutOfEat(int midType, int col){
        if(midType == -1){
            if (this.game.getAcolumn(col).getList().size() == 0 || this.game.getAcolumn(col).getList().getFirst().getType() == 1){
                Piece p = this.game.getBLueEaten().getLast();
                this.game.getBLueEaten().removeLast();
                this.game.getAcolumn(col).getList().add(p);
                ImageView imageview = findViewById(p.getId());
                imageview.setY(this.piecesPosTop[this.game.getAcolumn(col).getList().size() - 1]);
                imageview.setX(this.columnsPos[Math.abs(col - 24)][1][0] + 27);
                if(this.game.getBLueEaten().size() == 0){
                    this.is_blue_eaten = false;
                }

            }
            else{
                Piece p = this.game.getBLueEaten().getLast();
                this.game.getBLueEaten().removeLast();
                Piece p2 = this.game.getAcolumn(col).getList().getFirst();
                this.game.getAcolumn(col).getList().removeFirst();
                this.game.getRedEaten().add(p2);
                ImageView img1 = findViewById(p2.getId());
                img1.setX(this.redEaten[this.game.getRedEaten().size() - 1][0]);
                img1.setY(this.redEaten[this.game.getRedEaten().size() - 1][1]);
                this.is_red_eaten = true;
                this.game.getAcolumn(col).getList().add(p);
                ImageView imageview = findViewById(p.getId());
                imageview.setY(this.piecesPosTop[this.game.getAcolumn(col).getList().size() - 1]);
                imageview.setX(this.columnsPos[Math.abs(col - 24)][1][0] + 27);
                if(this.game.getBLueEaten().size() == 0){
                    this.is_blue_eaten = false;
                }
            }
        }
        else {
            if (this.game.getAcolumn(col).getList().size() == 0 || this.game.getAcolumn(col).getList().getFirst().getType() == 0){
                Piece p = this.game.getRedEaten().getLast();
                this.game.getRedEaten().removeLast();
                this.game.getAcolumn(col).getList().add(p);
                ImageView imageview = findViewById(p.getId());
                imageview.setY(this.piecesPosBottom[this.game.getAcolumn(col).getList().size() - 1]);
                imageview.setX(this.columnsPos[col-1][1][0] + 27);
                if(this.game.getRedEaten().size() == 0){
                    this.is_red_eaten = false;
                }

            }
            else{
                Piece p = this.game.getRedEaten().getLast();
                this.game.getRedEaten().removeLast();
                Piece p2 = this.game.getAcolumn(col).getList().getFirst();
                this.game.getAcolumn(col).getList().removeFirst();
                this.game.getBLueEaten().add(p2);
                ImageView img1 = findViewById(p2.getId());
                img1.setX(this.blueEaten[this.game.getBLueEaten().size() - 1][0]);
                img1.setY(this.blueEaten[this.game.getBLueEaten().size() - 1][1]);
                this.is_blue_eaten = true;
                this.game.getAcolumn(col).getList().add(p);
                ImageView imageview = findViewById(p.getId());
                imageview.setY(this.piecesPosBottom[this.game.getAcolumn(col).getList().size() - 1]);
                imageview.setX(this.columnsPos[col - 1][1][0] + 27);
                if(this.game.getRedEaten().size() == 0){
                    this.is_red_eaten = false;
                }
            }
        }
    }
    public void highlightPossibleColumns(int col) {
        int col1, col2;
        boolean valid1 = false;
        boolean valid2 = false;
        if(this.turn == 1){
            if(checkForEndGame(this.turn)) {
                if(col == this.MovePower1 || col == this.MovePower2)
                {
                    ImageView border = findViewById(R.id.outHighlight1);
                    border.setVisibility(View.VISIBLE);
                }
                if (col < this.MovePower1 || col < this.MovePower2) {
                    boolean flag = true;
                    for (int i = col + 1; i <= 6; i++) {
                        if (this.game.getAcolumn(i).getList().size() != 0) {
                            flag = false;
                        }
                    }
                    if (flag) {
                        ImageView border = findViewById(R.id.outHighlight1);
                        border.setVisibility(View.VISIBLE);
                    }
                }
            }
        }else{
            if(checkForEndGame(this.turn)) {
                if (col == 25 - this.MovePower1 || col == 25 - this.MovePower2) {
                    ImageView border = findViewById(R.id.outHighlight2);
                    border.setVisibility(View.VISIBLE);
                }
                if (col > 25 - this.MovePower1 || col > 25 - this.MovePower2) {
                    boolean flag = true;
                    for (int i = col - 1; i >= 19; i--) {
                        if (this.game.getAcolumn(i).getList().size() != 0) {
                            flag = false;
                        }
                    }
                    if (flag) {
                        ImageView border = findViewById(R.id.outHighlight2);
                        border.setVisibility(View.VISIBLE);
                    }
                }
            }
        }
        if (col == -1)
        {
            col1 = 25-this.MovePower1;
            if(this.game.getAcolumn(col1).getList().size() > 1){
                if (this.game.getAcolumn(col1).getList().getFirst().getType() != this.turn){
                    col1=0;
                }
            }
            col2 = 25-this.MovePower2;
            if(this.game.getAcolumn(col2).getList().size() > 1){
                if (this.game.getAcolumn(col2).getList().getFirst().getType() != this.turn){
                    col2=0;
                }
            }
        }
        else if (col == -2)
        {
            col1 = this.MovePower1;
            if(this.game.getAcolumn(col1).getList().size() > 1){
                if (this.game.getAcolumn(col1).getList().getFirst().getType() != this.turn){
                    col1=0;
                }
            }
            col2 = this.MovePower2;
            if(this.game.getAcolumn(col2).getList().size() > 1){
                if (this.game.getAcolumn(col2).getList().getFirst().getType() != this.turn){
                    col2=0;
                }
            }
        }
        else
        {
            int mult = 0;
            int type = this.game.getAcolumn(col).getList().get(0).getType();
            if (type == 1) {
                mult = -1;
            }
            else{
                mult = 1;
            }
            col1 = col + mult*this.MovePower1;
            if (col1 > 0 && col1 < 25) {
                if (this.game.getAcolumn(col1).getList().size() > 1) {
                    if (this.game.getAcolumn(col1).getList().getFirst().getType() != this.turn) {
                        col1 = 0;
                    }
                }
            }
            col2 = col + mult*this.MovePower2;
            if (col2 > 0 && col2 < 25) {
                if (this.game.getAcolumn(col2).getList().size() > 1) {
                    if (this.game.getAcolumn(col2).getList().getFirst().getType() != this.turn) {
                        col2 = 0;
                    }
                }
            }
        }
        if (col1 > 0 && col1 != col && col1 <= 24) {
            if (col1 > 12) {
                ImageView highlight = findViewById(R.id.ColumnHighlihgt3);
                highlight.setX(this.columnsPos[col1-1][0][0] + 7);
                highlight.setVisibility(View.VISIBLE);
            } else {
                ImageView highlight = findViewById(R.id.ColumnHighlihgt1);
                if(col1 == 6)
                {
                    highlight.setX(1096); // because i know
                }
                else
                {
                    highlight.setX(this.columnsPos[col1][0][0] + 7);
                }
                highlight.setVisibility(View.VISIBLE);
            }
        }
        if (col2 > 0 && col2 != col && col2 <= 24) {
            if (col2 > 12) {
                ImageView highlight = findViewById(R.id.ColumnHighlihgt4);
                highlight.setX(this.columnsPos[col2-1][0][0] + 7);
                highlight.setVisibility(View.VISIBLE);
            } else {
                ImageView highlight = findViewById(R.id.ColumnHighlihgt2);
                if(col2 == 6)
                {
                    highlight.setX(1096); // because i know
                }
                else
                {
                    highlight.setX(this.columnsPos[col2][0][0] + 7);
                }
                highlight.setVisibility(View.VISIBLE);
            }
        }
    }
    public void deHighlightColumns() {
        ImageView border = findViewById(R.id.outHighlight1);
        border.setVisibility(View.INVISIBLE);
        border = findViewById(R.id.outHighlight2);
        border.setVisibility(View.INVISIBLE);
        ImageView highlight = findViewById(R.id.ColumnHighlihgt1);
        highlight.setVisibility(View.INVISIBLE);
        highlight = findViewById(R.id.ColumnHighlihgt2);
        highlight.setVisibility(View.INVISIBLE);
        highlight = findViewById(R.id.ColumnHighlihgt3);
        highlight.setVisibility(View.INVISIBLE);
        highlight = findViewById(R.id.ColumnHighlihgt4);
        highlight.setVisibility(View.INVISIBLE);
    }
    public boolean checkForEndGame(int type){
        if (type == 1){
            if (this.is_blue_eaten == true)
            {
                return false;
            }
            for (int i=24;i>6;i--)
            {
                if(this.game.getAcolumn(i).getList().size() != 0)
                {
                    if(this.game.getAcolumn(i).getList().getFirst().getType() == 1)
                    {
                        return false;
                    }
                }
            }
            return true;
        }
        else
        {
            if (this.is_red_eaten == true)
            {
                return false;
            }
            for (int i=1;i<19;i++)
            {
                if(this.game.getAcolumn(i).getList().size() != 0)
                {
                    if(this.game.getAcolumn(i).getList().getFirst().getType() == 0)
                    {
                        return false;
                    }
                }
            }
            return true;
        }

    }
    public boolean endGameMove(int col){
        if(this.game.getAcolumn(col).getList().getFirst().getType() == 1)
        {
            System.out.println("please2");
            if(col == this.MovePower1 || col == this.MovePower2)
            {
                System.out.println("please3");
                Piece p = this.game.getAcolumn(col).getList().getLast();
                this.blueOut.add(p);
                this.game.getAcolumn(col).getList().removeLast();
                ImageView bluepieceout = findViewById(this.bluePieceOutID[this.blueOut.size()-1]);
                bluepieceout.setVisibility(View.VISIBLE);
                ImageView img = findViewById(p.getId());
                img.setVisibility(View.INVISIBLE);
                if(col == this.MovePower1) {
                    if (this.NumOfMoves <= 2) {
                        ImageView img5 = findViewById(R.id.dice5);
                        ImageView img7 = findViewById(R.id.dice7);
                        img5.setVisibility(View.INVISIBLE);
                        img7.setVisibility(View.INVISIBLE);
                        this.MovePower1 = 0;
                    }
                }
                else {
                    if (this.NumOfMoves <= 2) {
                        ImageView img6 = findViewById(R.id.dice6);
                        ImageView img8 = findViewById(R.id.dice8);
                        img6.setVisibility(View.INVISIBLE);
                        img8.setVisibility(View.INVISIBLE);
                        this.MovePower2 = 0;
                    }
                }
                return true;
            }
            else if (col < this.MovePower1 || col < this.MovePower2)
            {
                boolean flag = true;
                for (int i=col+1;i<=6;i++){
                    if(this.game.getAcolumn(i).getList().size() != 0)
                    {
                        flag = false;
                    }
                }
                if(flag)
                {
                    System.out.println("please3");
                    Piece p = this.game.getAcolumn(col).getList().getLast();
                    this.blueOut.add(p);
                    this.game.getAcolumn(col).getList().removeLast();
                    ImageView bluepieceout = findViewById(this.bluePieceOutID[this.blueOut.size()-1]);
                    bluepieceout.setVisibility(View.VISIBLE);
                    ImageView img = findViewById(p.getId());
                    img.setVisibility(View.INVISIBLE);
                    int move;
                    boolean flag1 = false;
                    if(this.MovePower1 > this.MovePower2)
                    {
                        move = this.MovePower1;
                        flag1 = true;
                    }
                    else
                    {
                        flag1 = false;
                        move = this.MovePower2;
                    }
                    if(col < move) {
                        if (this.NumOfMoves <= 2) {
                            if(flag1) {
                                ImageView img5 = findViewById(R.id.dice5);
                                ImageView img7 = findViewById(R.id.dice7);
                                img5.setVisibility(View.INVISIBLE);
                                img7.setVisibility(View.INVISIBLE);
                                this.MovePower1 = 0;
                            }
                            else
                            {
                                ImageView img6 = findViewById(R.id.dice6);
                                ImageView img8 = findViewById(R.id.dice8);
                                img6.setVisibility(View.INVISIBLE);
                                img8.setVisibility(View.INVISIBLE);
                                this.MovePower2 = 0;
                            }
                        }
                    }
                    return true;
                }
            }
        }
        else
        {
            if(col == 25-this.MovePower1 || col == 25-this.MovePower2)
            {
                System.out.println("please3");
                Piece p = this.game.getAcolumn(col).getList().getLast();
                this.redOut.add(p);
                this.game.getAcolumn(col).getList().removeLast();
                ImageView redpieceout = findViewById(this.redPieceOutID[this.redOut.size()-1]);
                redpieceout.setVisibility(View.VISIBLE);
                ImageView img = findViewById(p.getId());
                img.setVisibility(View.INVISIBLE);
                if(col == 25-this.MovePower1) {
                    if (this.NumOfMoves <= 2) {
                        ImageView img5 = findViewById(R.id.dice5);
                        ImageView img7 = findViewById(R.id.dice7);
                        img5.setVisibility(View.INVISIBLE);
                        img7.setVisibility(View.INVISIBLE);
                        this.MovePower1 = 0;
                    }
                }
                else {
                    if (this.NumOfMoves <= 2) {
                        ImageView img6 = findViewById(R.id.dice6);
                        ImageView img8 = findViewById(R.id.dice8);
                        img6.setVisibility(View.INVISIBLE);
                        img8.setVisibility(View.INVISIBLE);
                        this.MovePower2 = 0;
                    }
                }
                return true;
            }
            else if (col > 25-this.MovePower1 || col > 25-this.MovePower2)
            {
                boolean flag = true;
                for (int i=col-1;i>=19;i--){
                    if(this.game.getAcolumn(i).getList().size() != 0)
                    {
                        flag = false;
                    }
                }
                if(flag)
                {
                    System.out.println("please3");
                    Piece p = this.game.getAcolumn(col).getList().getLast();
                    this.redOut.add(p);
                    this.game.getAcolumn(col).getList().removeLast();
                    ImageView redpieceout = findViewById(this.redPieceOutID[this.redOut.size()-1]);
                    redpieceout.setVisibility(View.VISIBLE);
                    ImageView img = findViewById(p.getId());
                    img.setVisibility(View.INVISIBLE);
                    int move;
                    boolean flag1 = false;
                    if(this.MovePower1 > this.MovePower2)
                    {
                        move = this.MovePower1;
                        flag1 = true;
                    }
                    else
                    {
                        flag1 = false;
                        move = this.MovePower2;
                    }
                    if(col > 25-move) {
                        if (this.NumOfMoves <= 2) {
                            if(flag1) {
                                ImageView img5 = findViewById(R.id.dice5);
                                ImageView img7 = findViewById(R.id.dice7);
                                img5.setVisibility(View.INVISIBLE);
                                img7.setVisibility(View.INVISIBLE);
                                this.MovePower1 = 0;
                            }
                            else
                            {
                                ImageView img6 = findViewById(R.id.dice6);
                                ImageView img8 = findViewById(R.id.dice8);
                                img6.setVisibility(View.INVISIBLE);
                                img8.setVisibility(View.INVISIBLE);
                                this.MovePower2 = 0;
                            }
                        }
                    }
                    return true;
                }
            }
        }
        return false;
    }
    public boolean checkIfGameEnded(int type) {
        System.out.println("blabla: "+type);
        if (type == 1){
            System.out.println(blueOut.size());
            if (this.blueOut.size() == 15)
            {
                this.whoWon = 1;
                return true;
            }
            else
            {
                return false;
            }
        }
        else{
            System.out.println(redOut.size());
            if (this.redOut.size() == 15)
            {
                this.whoWon = 0;
                return true;
            }
            else
            {
                return false;
            }
        }
    }
    public boolean IsValidOut(int col,int type){
        System.out.println(col);
        System.out.println(type);
        System.out.println(MovePower1);
        System.out.println(MovePower2);
        if (type == 1)
        {
            if (col == this.MovePower1 || col == this.MovePower2) {
                return true;
            }
            else if(col < this.MovePower1 || col < this.MovePower2) {
                return true;
            }
            else{
                return false;
            }
        }
        else
        {
            if (col == 25-this.MovePower1 || col == 25-this.MovePower2) {
                return true;
            }
            else if(col > 25-this.MovePower1 || col > 25-this.MovePower2) {
                return true;
            }
            else {
                return false;
            }
        }
    }
    public void EndTheGame(int type){
        TextView txtview = findViewById(R.id.winsign);
        if (type == 1){
            txtview.setText("BLUE WON!");
            txtview.setTextColor(Color.BLUE);
        }
        else{
            txtview.setText("RED WON!");
            txtview.setTextColor(Color.RED);
        }
        Button button1 = findViewById(R.id.savebutton);
        Button button2 = findViewById(R.id.exitbutton);
        txtview.setVisibility(View.VISIBLE);
        button1.setVisibility(View.VISIBLE);
        button2.setVisibility(View.VISIBLE);
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        // Vibrate for 500 milliseconds
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            //deprecated in API 26
            v.vibrate(300);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 0:
                writeFile();
        }
    }
    public String readFile() {
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
                text.append(line);
                text.append("\n");
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("here: ");
        System.out.println(text.toString());
        return text.toString();
    }
    public void saveResult(View view) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            writeFile();
        } else {
            // Request permission from the user
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        }
        finish();
    }
    public void writeFile(){

        String sBody = readFile();
        if (this.whoWon == 1) {
            sBody += name_player_1;
            sBody += "#";
            sBody += name_player_2;
        } else {
            sBody += name_player_2;
            sBody += "#";
            sBody += name_player_1;
        }
        sBody += "#";
        if(this.whoWon == 1){
            sBody += Integer.toString(15-this.redOut.size());
        }
        else {
            sBody += Integer.toString(15-this.blueOut.size());
        }
        sBody += "\n";
        try{
            File root = new File(Environment.getExternalStorageDirectory(), "Files");
            if (!root.exists()) {
                root.mkdir();
            }
            File gpxfile = new File(root, this.FileName);
            FileWriter writer = new FileWriter(gpxfile);
            writer.append(sBody);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
    public void exitActivity(View view) {
        finish();
    }
    @Override
    public boolean onTouchEvent(MotionEvent e){
        if(!didGameEnd) {
            switch (e.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (this.NumOfMoves > 0) {
                        int x = round(e.getX());
                        int y = round(e.getY());
                        int col = findColumn(x, y);
                        if (this.press == 0) {
                            if (col != -1) {
                                if (this.is_blue_eaten && this.turn == 1) {
                                    this.press = 1;
                                    this.col_pressed = -1;
                                } else if (this.is_red_eaten && this.turn == 0) {
                                    this.press = 1;
                                    this.col_pressed = -2;
                                } else if (!CheckIfColumnIsEmpty(col)) {
                                    if (this.game.getAcolumn(col).getList().get(0).getType() == this.turn) {
                                        this.press = 1;
                                        this.col_pressed = col;
                                        highlightColumn(col, true);
                                        highlightPossibleColumns(col);
                                    }
                                }
                            }
                        } else {
                            if (col == -1) {
                                if (checkForEndGame(this.turn)) {
                                    System.out.println("please0");
                                    if (IsValidOut(this.col_pressed, this.turn)) {
                                        System.out.println("please1");
                                        boolean flag = endGameMove(this.col_pressed);
                                        if (flag){
                                            this.NumOfMoves--;
                                        }
                                        if (checkIfGameEnded(this.turn)) {
                                            this.didGameEnd = true;
                                            EndTheGame(this.turn);
                                        }
                                    }
                                    if (this.NumOfMoves == 0) {
                                        this.turn = Math.abs(this.turn - 1);
                                        System.out.println(this.turn);
                                    }
                                }
                            } else {
                                if (IsValidMove(this.col_pressed, col)) {
                                    move(this.col_pressed, col);
                                    this.NumOfMoves--;
                                    if (this.NumOfMoves == 0) {
                                        this.turn = Math.abs(this.turn - 1);
                                        System.out.println(this.turn);
                                    }
                                }
                            }
                            highlightColumn(this.col_pressed, false);
                            deHighlightColumns();
                            this.press = 0;
                        }
                        if ((this.col_pressed == -1 || this.col_pressed == -2) && this.press == 1) {
                            if (col != -1) {
                                if (IsValidMove(this.col_pressed, col)) {
                                    GetOutOfEat(this.col_pressed, col);
                                    this.NumOfMoves--;
                                    deHighlightColumns();
                                    if (this.NumOfMoves == 0) {
                                        this.turn = Math.abs(this.turn - 1);
                                        System.out.println(this.turn);
                                    }
                                }
                                this.press = 0;
                            }
                        }
                    }
                case MotionEvent.ACTION_UP:
                    if (this.NumOfMoves == 0) {
                        this.rolled = false;
                        Button button1 = findViewById(R.id.roll1);
                        button1.setBackgroundResource(R.drawable.reddice);
                    }
                    else {
                        System.out.println("oops0");
                        if (!AreThereAnyPossibleMoves(this.turn)) {
                            System.out.println("oops1");
                            this.turn = Math.abs(this.turn - 1);
                            this.NumOfMoves = 0;
                        }
                    }
            }
        }
        return true;
    }
}