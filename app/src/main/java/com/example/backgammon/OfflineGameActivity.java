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

    // used for the results
    final private String FileName = "gameResults.txt";
    public String name_player_1 = "";
    public String name_player_2 = "";

    // used for the turn based game and to remember the presses
    private int turn = 1;
    private int press = 0;
    private int col_pressed = -1;

    // used to check which column to unHighlight
    private boolean dHighlight1 = true;
    private boolean dHighlight2 = true;
    private boolean dHighlight3 = true;
    private boolean dHighlight4 = true;

    // used to position the pieces in the columns and to find where the user has pressed
    private int[] piecesPosBottom = new int[] {960,880,800,720,640,930,850,770,690,610};
    private int[] piecesPosTop = new int[] {37,117,197,277,357,67,147,227,307,387};
    private int[][][] columnsPos = new int[][][] {
            {{1869,1040},{1740,600}},{{1739,1040},{1610,600}},{{1609,1040},{1480,600}},{{1479,1040},{1350,600}},{{1349,1040},{1220,600}},{{1219,1040},{1090,600}},
            {{999,1040},{870,600}},{{869,1040},{740,600}},{{739,1040},{610,600}},{{609,1040},{480,600}},{{479,1040},{350,600}},{{349,1040},{220,600}},
            {{221,38},{350,480}},{{351,38},{480,480}},{{481,38},{610,480}},{{611,38},{740,480}},{{741,38},{870,480}},{{871,38},{1000,480}},
            {{1091,38},{1220,480}},{{1221,38},{1350,480}},{{1351,38},{1480,480}},{{1481,38},{1610,480}},{{1611,38},{1740,480}},{{1741,38},{1870,480}}};
    private int[][] blueEaten = new int[][]{{1007,455},{1007,375},{1007,295},{1007,215},{1007,135},{1007,55}};
    private int[][] redEaten = new int[][]{{1007,535},{1007,615},{1007,695},{1007,775},{1007,855},{1007,935}};

    // used to save the ids of the pieces that are out
    private int[] bluePieceOutID = new int[]{R.id.outpieceblue1,R.id.outpieceblue2,R.id.outpieceblue3,R.id.outpieceblue4,R.id.outpieceblue5,
            R.id.outpieceblue6,R.id.outpieceblue7,R.id.outpieceblue8,R.id.outpieceblue9,R.id.outpieceblue10,
            R.id.outpieceblue11,R.id.outpieceblue12,R.id.outpieceblue13,R.id.outpieceblue14,R.id.outpieceblue15};
    private int[] redPieceOutID = new int[]{R.id.outpiecered1,R.id.outpiecered2,R.id.outpiecered3,R.id.outpiecered4,R.id.outpiecered5,
            R.id.outpiecered6,R.id.outpiecered7,R.id.outpiecered8,R.id.outpiecered9,R.id.outpiecered10,
            R.id.outpiecered11,R.id.outpiecered12,R.id.outpiecered13,R.id.outpiecered14,R.id.outpiecered15};

    // used as an Object of the game class
    private Game game;

    // used for the roll function
    private boolean rolled = false;
    private Map<Integer,Integer> diceMap = new HashMap<Integer, Integer>() {};

    // used for following the moves the player wants to make
    private int NumOfMoves = 0;
    private int MovePower1 = 0;
    private int MovePower2 = 0;

    // used to distinguish between a regular move to a special move of getting out of eat
    private boolean is_red_eaten = false;
    private boolean is_blue_eaten = false;

    // used to store the red and blue pieces that are out of the game
    private LinkedList<Piece> blueOut = new LinkedList<>();
    private LinkedList<Piece> redOut = new LinkedList<>();

    // used for end end game
    private int whoWon = -1;
    private boolean didGameEnd = false;

    // used  for the broadcast and the light sensor
    private BroadcastReceiver mReceiver;
    private SensorManager sensorManager;
    private Sensor sensorLight;
    private boolean isDark = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /*
        this function will set up basic things for the activity
        param: savedInstanceState : Bundle
        return:void
         */

        // set the activity
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offline_game);
        // set up the board
        LinkedList<Integer> ll = getIds();
        this.game = new Game(this.columnsPos, ll);
        // set up the diceMap
        this.diceMap.put(1, R.drawable.dice1);
        this.diceMap.put(2, R.drawable.dice2);
        this.diceMap.put(3, R.drawable.dice3);
        this.diceMap.put(4, R.drawable.dice4);
        this.diceMap.put(5, R.drawable.dice5);
        this.diceMap.put(6, R.drawable.dice6);

        // set up broadcast receiver, light sensor and the player names dialog
        this.mReceiver = new OfflineGameActivity.BatteryBroadcastReceiver();
        showStartDialog();
        this.sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        this.sensorLight = this.sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

        SensorEventListener sensorEventListenerLight = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                float lux = sensorEvent.values[0];
                if (lux < 1 && !isDark) { // if lux is less than 1 then its too dark
                    Context context = getApplicationContext();
                    Toast toast = Toast.makeText(context, "It's Dark, Lower your phone brightness", Toast.LENGTH_SHORT);
                    toast.show();
                    isDark = true;
                }
                else if (lux > 200)
                {
                    isDark = false;
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) { } // i don't use this
        };
        this.sensorManager.registerListener(sensorEventListenerLight, this.sensorLight, sensorManager.SENSOR_DELAY_NORMAL);
    }
    public void showStartDialog(){
        /*
        this function will manage a dialog in which the user will input the names of the players
        param: none
        return:void
         */
        final Dialog dialog = new Dialog(OfflineGameActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // setting the window
        dialog.setCancelable(false); // setting to not being able to cancel by pressing outside of the window
        dialog.setContentView(R.layout.get_names_dialog); // set the content
        // get the 2 names
        final EditText name1Et = dialog.findViewById(R.id.name1);
        final EditText name2Et = dialog.findViewById(R.id.name2);
        // get the 2 buttons
        Button startButton = dialog.findViewById(R.id.start_button);
        Button cancelButton = dialog.findViewById(R.id.cancel_button);
        // listen to start button pressed
        startButton.setOnClickListener(new View.OnClickListener() {
           @Override
            public void onClick(View view)
           {
               name_player_1 = name1Et.getText().toString();// first name
               name_player_2 = name2Et.getText().toString();// second name
               if(name_player_1.length() > 6) // make sure the name isn't too long
               {
                   Context context = getApplicationContext();
                   Toast toast = Toast.makeText(context, "Max Name Length 6.", Toast.LENGTH_SHORT);
                   toast.show();
               }
               else {
                   // set the names of the players and close the dialog
                   TextView tv = findViewById(R.id.blueName);
                   tv.setText(name_player_1);
                   tv.setVisibility(View.VISIBLE);
                   TextView tv1 = findViewById(R.id.redName);
                   tv1.setText(name_player_2);
                   tv1.setVisibility(View.VISIBLE);
                   dialog.dismiss();
               }
           }
        });
        // listen to cancel button pressed
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                // close the dialog window and finish the activity
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
            /*
        this function will run when the phone sends a broadcast and will alert the user in case the
        battery is low.
        param: context : Context , intent : Intent
        return: void
         */
            IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
            Intent batteryStatus = context.registerReceiver(null, ifilter);
            int level = intent.getIntExtra(BATTERY_LEVEL, 0);
            int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
            boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                    status == BatteryManager.BATTERY_STATUS_FULL;
            if(!isCharging && level <= 15 && !this.isLow) { // if phone isn't charging and teh battery level is less than 15
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
        this function will run when the activity starts and will start the broadcast receiver
        param: none
        return: void
         */
        registerReceiver(this.mReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        super.onStart();
    }
    @Override
    protected void onStop() {
        /*
        this function will run when the activity stops and will start the broadcast receiver
        param: none
        return: void
         */
        unregisterReceiver(this.mReceiver);
        super.onStop();
    }
    public LinkedList<Integer> getIds(){
        /*
        this function will return a list with all the ids of all the pieces
        param: none
        return: LinkedList<Integer>
         */
        LinkedList<Integer> ll = new LinkedList<Integer>();
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
        return ll;
    }
    public int findColumn(int x,int y) {
        /*
        this function will search for a column depends on the x,y given
        param: x : int, y : int
        return: int
         */
        int columnNum = -1;
        for (int i=0;i<this.columnsPos.length;i++){
            if (i < this.columnsPos.length/2){ // check for columns at the bottom
                if (x <= this.columnsPos[i][0][0] && x >= this.columnsPos[i][1][0]){
                    if (y <= this.columnsPos[i][0][1] && y >= this.columnsPos[i][1][1]){
                        columnNum = i+1;
                        break;
                    }
                }
            }
            else{ // check for the columns at the top
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
        /*
        this function will highlight/unhighlight the pieces in a column given (flag is true = highlight / flag is false = unhighlight)
        param: colNum : int, flag : boolean
        return: void
         */
        Column column = this.game.getAcolumn(colNum);
        LinkedList<Piece> ll = column.getList();
        if (ll.size() >= 1){ // make sure the column isn't empty
            int type = ll.get(0).getType(); // find the type of the column
            for (int i=0;i<ll.size();i++) { // goes through all the pieces in the column
                ImageView imgview = findViewById(ll.get(i).getId()); // get the image view for each piece
                if (type == 1){ // if the column is blue
                    if (flag){// highlight
                        imgview.setImageResource(R.drawable.bluepieceglow);
                    }
                    else{// unhighlight
                        imgview.setImageResource(R.drawable.bluepiece);
                    }
                }
                else{ // else the column is red
                    if (flag){// highlight
                        imgview.setImageResource(R.drawable.redpieceglow);
                    }
                    else {// unhighlight
                        imgview.setImageResource(R.drawable.redpiece);
                    }
                }
            }
        }
    }
    public boolean CheckIfColumnIsEmpty(int colNum){
        /*
        this function will call a function from the game class and will return if a column is empy or not
        param: colNum : int
        return: boolean
         */
        return this.game.checkIfColumnIsEmpty(colNum);
    }
    public void move(int colNum1, int colNum2){
        /*
        this function will move a piece from colNum1 to colNum2
        param: colNum1 : int , colNum2 : int
        return: void
         */
        Piece p = this.game.move(colNum1,colNum2); // move and get the piece moved
        int place = this.game.getAcolumn(colNum2).getList().size(); // get how many pieces are in the dest column
        if (place == 2) { // if the dest column had only one piece
            int type1 = this.game.getAcolumn(colNum2).getList().get(1).getType(); // get the type moved
            int type2 = this.game.getAcolumn(colNum2).getList().get(0).getType(); // ge the type moved to
            if (type1 != type2) { // if a eat move happened
                if (type2 == 1) {// if the piece eaten is blue

                    // put the piece eaten in the eaten list
                    Piece EatenPiece = this.game.getAcolumn(colNum2).getList().getFirst();
                    this.game.getAcolumn(colNum2).getList().removeFirst();
                    ImageView img = findViewById(EatenPiece.getId());
                    this.game.getBLueEaten().add(EatenPiece);

                    // if there is place to show in the middle
                    if (this.game.getBLueEaten().size() <= 6){
                        img.setX(this.blueEaten[this.game.getBLueEaten().size() - 1][0]);
                        img.setY(this.blueEaten[this.game.getBLueEaten().size() - 1][1]);
                        img.setVisibility(View.VISIBLE);
                    }
                    else // if there isn't place to show in the middle
                    {
                        img.setVisibility(View.INVISIBLE);
                    }
                    this.is_blue_eaten = true;

                } else {// if the piece eaten is red

                    // put the piece eaten in the eaten list
                    Piece EatenPiece = this.game.getAcolumn(colNum2).getList().getFirst();
                    this.game.getAcolumn(colNum2).getList().removeFirst();
                    ImageView img = findViewById(EatenPiece.getId());
                    this.game.getRedEaten().add(EatenPiece);

                    // if there is place to show in the middle
                    if (this.game.getRedEaten().size() <= 6) {
                        img.setX(this.redEaten[this.game.getRedEaten().size() - 1][0]);
                        img.setY(this.redEaten[this.game.getRedEaten().size() - 1][1]);
                        img.setVisibility(View.VISIBLE);
                    }
                    else// if there isn't place to show in the middle
                    {
                        img.setVisibility(View.INVISIBLE);
                    }
                    this.is_red_eaten = true;
                }
                place = place - 1;
            }
        }
        // if a piece isn't eaten
        ImageView imgview = findViewById(p.getId());
        if (place <= 10) { // if there is place to show the piece in the column
            if (colNum2 <= 12) { // if the column is in the bottom
                imgview.setY(this.piecesPosBottom[place - 1]);
                imgview.setX(this.columnsPos[colNum2 - 1][1][0] + 27);
                imgview.setVisibility(View.VISIBLE);
            } else { // if the column is in the top
                imgview.setY(this.piecesPosTop[place - 1]);
                imgview.setX(this.columnsPos[Math.abs(colNum2 - 24)][1][0] + 27);
                imgview.setVisibility(View.VISIBLE);
            }
            if (p.getType() == 1) {
                imgview.setImageResource(R.drawable.bluepiece);
            } else {
                imgview.setImageResource(R.drawable.redpiece);
            }
        }
        else
        {
            imgview.setVisibility(View.INVISIBLE);
        }
    }
    public boolean IsValidMove(int col1, int col2){
        /*
        this function will return if a move form col1 to col2 is a valid and legal move
        param: col1 : int , col2 : int
        return: boolean
         */
        if (col1 == -1){ // if blue wants to get out of being eaten
            // if the place he want to get out to is a valid place
            if (this.game.getAcolumn(col2).getList().size() <= 1 || this.turn == this.game.getAcolumn(col2).getList().getFirst().getType()){
                if (col2 == this.MovePower1){ // is the move dice 1 ?
                    if (this.NumOfMoves <= 2){
                        this.MovePower1 = 0;
                        ImageView img5 = findViewById(R.id.dice5);
                        ImageView img7 = findViewById(R.id.dice7);
                        img5.setVisibility(View.INVISIBLE);
                        img7.setVisibility(View.INVISIBLE);
                    }
                    return true;
                }
                else if(col2 == this.MovePower2){// is the move dice 2 ?
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
        else if(col1 == -2){// if blue wants to get out of being eaten
            // if the place he want to get out to is a valid place
            if (this.game.getAcolumn(col2).getList().size() <= 1 || this.turn == this.game.getAcolumn(col2).getList().getFirst().getType()){
                if (col2 == 25-this.MovePower1){ // is the move dice 1 ?
                    if (this.NumOfMoves <= 2){
                        this.MovePower1 = 0;
                        ImageView img5 = findViewById(R.id.dice5);
                        ImageView img7 = findViewById(R.id.dice7);
                        img5.setVisibility(View.INVISIBLE);
                        img7.setVisibility(View.INVISIBLE);
                    }
                    return true;
                }
                else if(col2 == 25-this.MovePower2){// is the move dice 2 ?
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
        // if there isn't a and eating situation and the move is to a column of another type
        int type = this.game.getAcolumn(col1).getList().get(0).getType();
        if (this.game.getAcolumn(col2).getList().size() > 0){
            if (this.game.getAcolumn(col2).getList().get(0).getType() != type && this.game.getAcolumn(col2).getList().size() > 1){
                return false;
            }
        }

        if (type == 1) // if blue want to move
        {
            // if the dice used is dice 1
            if (col1 + this.MovePower1 == col2 && this.MovePower1 != 0)
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
            // if the dice used is dice 2
            else if (col1 + this.MovePower2 == col2 && this.MovePower2 != 0)
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
        else{// if red want to move
            // if the dice used is dice 1
            if (col1 - this.MovePower1 == col2 && this.MovePower1 != 0)
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
            // if the dice used is dice 2
            else if(col1 - this.MovePower2 == col2 && this.MovePower2 != 0)
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
        /*
        this function will return if a certain type has any possible move with any piece
        param: type : int
        return: boolean
         */
        if(checkForEndGame(type))
        {
            return true;
        }
        int mult; // mult differentiate between the moves of red and blue as they move in different directions
        if(type == 1){
            mult = 1;
        }
        else
        {
            mult = -1;
        }
        int count = 0;
        // if blue is eaten and its his turn to get out
        if (this.is_blue_eaten && this.turn == 1)
        {
            if(this.MovePower1 != 0)
            {
                // if there is a possible column with move1

                if(this.game.getAcolumn(this.MovePower1).getList().size() <= 1)
                {
                    return true;
                }
                else if(this.game.getAcolumn(this.MovePower1).getList().getFirst().getType() == 1){
                    return true;
                }
                else
                {
                    count++;
                }
            }
            if(this.MovePower2 != 0)
            {
                // if there is a possible column with move2

                if(this.game.getAcolumn(this.MovePower2).getList().size() <= 1)
                {
                    return true;
                }
                else if( this.game.getAcolumn(this.MovePower2).getList().getFirst().getType() == 1)
                {
                    return true;
                }
                else
                {
                    count++;
                }
            }
        }
        // if blue is eaten and its his turn to get out
        else if (this.is_red_eaten && this.turn == 0)
        {
            if(this.MovePower1 != 0)
            {
                // if there is a possible column with move1

                if(this.game.getAcolumn(25-this.MovePower1).getList().size() <= 1)
                {
                    return true;
                }
                else if(this.game.getAcolumn(25-this.MovePower1).getList().getFirst().getType() == 0){
                    return true;
                }
                else
                {
                    count++;
                }
            }
            if(this.MovePower2 != 0)
            {
                // if there is a possible column with move2

                if(this.game.getAcolumn(25-this.MovePower2).getList().size() <= 1)
                {
                    return true;
                }
                else if( this.game.getAcolumn(25-this.MovePower2).getList().getFirst().getType() == 0)
                {
                    return true;
                }
                else
                {
                    count++;
                }
            }
        }
        if(count > 0) // if there is a piece eaten but no moves possible return false
        {
            return false;
        }
        int col1,col2;
        // got through all the possible columns
        for (int i=0;i<24;i++){
            if(this.game.getAcolumn(i+1).getList().size() > 0)
            {
                if (this.game.getAcolumn(i+1).getList().getFirst().getType() == type)
                {
                    col1 = i + this.MovePower1*mult + 1;
                    col2 = i + this.MovePower2*mult + 1;
                    if(col1 > 0 && col1 <= 24) // if the column is on the board
                    {
                        if(this.game.getAcolumn(col1).getList().size() <= 1)
                        {
                            return true;
                        }
                        else if(this.game.getAcolumn(col1).getList().getFirst().getType() == type)
                        {
                            return true;
                        }
                    }
                    if(col2 > 0 && col2 <= 24) // if the column is on the board
                    {
                        if(this.game.getAcolumn(col2).getList().size() <= 1)
                        {
                            return true;
                        }
                        else if(this.game.getAcolumn(col2).getList().getFirst().getType() == type)
                        {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
    public void RollDice(View view){
        /*
        this function will roll 2 random numbers and will set the number of moves left and the move powers
        param: view : View
        return: void
         */
        if (!this.rolled){
            // roll dice
            int dice1 = Math.abs(srandom.nextInt()%6)+1;
            int dice2 = Math.abs(srandom.nextInt()%6)+1;

            if (dice1 == dice2){ // if there is a a double
                this.NumOfMoves = 4;
                this.MovePower1 = dice1;
                this.MovePower2 = dice2;
            }
            else // if there isn't a double
            {
                this.NumOfMoves = 2;
                this.MovePower1 = dice1;
                this.MovePower2 = dice2;
            }
            if(!AreThereAnyPossibleMoves(this.turn)){ // check if there aren't any possible moves
                this.turn = Math.abs(this.turn-1);
                // set the turn arrow
                ImageView arrow = findViewById(R.id.turnArrow);
                if(this.turn == 1){
                    arrow.setImageResource(R.drawable.bluearrow);
                }
                else
                {
                    arrow.setImageResource(R.drawable.redarrow);
                }
                this.MovePower1 = 0;
                this.MovePower2 = 0;
                this.NumOfMoves = 0;
                // set the dice
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
            }
            else
            {
                // set the dice
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
                // highlight the possible columns depends on the turn
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
        }
    }
    public void GetOutOfEat(int midType, int col){
        /*
        this function will handle a situation when a piece is going from a state of being eaten to a certain column
        param: midType : int , col : int
        return: void
         */
        if(midType == -1){ // if blue is moving out of eat
            // if the column is empty or in the same type
            if (this.game.getAcolumn(col).getList().size() == 0 || this.game.getAcolumn(col).getList().getFirst().getType() == 1){
                // move the piece out of eat
                Piece p = this.game.getBLueEaten().getLast();
                this.game.getBLueEaten().removeLast();
                this.game.getAcolumn(col).getList().add(p);
                ImageView imageview = findViewById(p.getId());

                if (this.game.getAcolumn(col).getList().size() <= 10) { // if there is place left to show in the column
                    imageview.setY(this.piecesPosBottom[this.game.getAcolumn(col).getList().size() - 1]);
                    imageview.setX(this.columnsPos[Math.abs(col - 1)][1][0] + 27);
                    imageview.setVisibility(View.VISIBLE);
                }
                else// if there isn't place left to show in the column
                {
                    imageview.setVisibility(View.INVISIBLE);
                }
                if(this.game.getBLueEaten().size() == 0){
                    this.is_blue_eaten = false;
                }

            }
            // if the column isn't empty or in the same type
            else{
                Piece p = this.game.getBLueEaten().getLast();
                // eat the piece eaten
                this.game.getBLueEaten().removeLast();
                Piece p2 = this.game.getAcolumn(col).getList().getFirst();
                this.game.getAcolumn(col).getList().removeFirst();
                this.game.getRedEaten().add(p2);
                ImageView img1 = findViewById(p2.getId());
                // if there is a place in the middle to show the piece
                if (this.game.getRedEaten().size() <= 6) {
                    img1.setX(this.redEaten[this.game.getRedEaten().size() - 1][0]);
                    img1.setY(this.redEaten[this.game.getRedEaten().size() - 1][1]);
                    img1.setVisibility(View.VISIBLE);
                }
                else // if there isn't a place in the middle to show the piece
                {
                    img1.setVisibility(View.INVISIBLE);
                }
                // move the piece from the middle to a column
                this.is_red_eaten = true;
                this.game.getAcolumn(col).getList().add(p);
                ImageView imageview = findViewById(p.getId());
                // if there is a place left to show the piece in the column
                if (this.game.getAcolumn(col).getList().size() <= 10) {
                    imageview.setY(this.piecesPosBottom[this.game.getAcolumn(col).getList().size() - 1]);
                    imageview.setX(this.columnsPos[Math.abs(col - 1)][1][0] + 27);
                    imageview.setVisibility(View.VISIBLE);
                }
                else
                {
                    imageview.setVisibility(View.INVISIBLE);
                }
                if(this.game.getBLueEaten().size() == 0){
                    this.is_blue_eaten = false;
                }
            }
        }
        else {// if red is moving out of eat
            // if the column is empty or in the same type
            if (this.game.getAcolumn(col).getList().size() == 0 || this.game.getAcolumn(col).getList().getFirst().getType() == 0){
                Piece p = this.game.getRedEaten().getLast();
                // move the piece out of eat
                this.game.getRedEaten().removeLast();
                this.game.getAcolumn(col).getList().add(p);
                ImageView imageview = findViewById(p.getId());
                if (this.game.getAcolumn(col).getList().size() <= 10) {// if there is place left to show in the column
                    imageview.setY(this.piecesPosTop[this.game.getAcolumn(col).getList().size() - 1]);
                    imageview.setX(this.columnsPos[Math.abs(col - 24)][1][0] + 27);
                    imageview.setVisibility(View.VISIBLE);
                }
                else// if there isn't place left to show in the column
                {
                    imageview.setVisibility(View.INVISIBLE);
                }
                if(this.game.getRedEaten().size() == 0){
                    this.is_red_eaten = false;
                }

            }
            // if the column isn't empty or in the same type
            else{
                // eat the piece eaten
                Piece p = this.game.getRedEaten().getLast();
                this.game.getRedEaten().removeLast();
                Piece p2 = this.game.getAcolumn(col).getList().getFirst();
                this.game.getAcolumn(col).getList().removeFirst();
                this.game.getBLueEaten().add(p2);
                ImageView img1 = findViewById(p2.getId());
                // if there is a place in the middle to show the piece
                if (this.game.getBLueEaten().size() <= 6) {
                    img1.setX(this.blueEaten[this.game.getBLueEaten().size() - 1][0]);
                    img1.setY(this.blueEaten[this.game.getBLueEaten().size() - 1][1]);
                    img1.setVisibility(View.VISIBLE);
                }
                else
                {
                    img1.setVisibility(View.INVISIBLE);
                }
                // move the piece out of eat
                this.is_blue_eaten = true;
                this.game.getAcolumn(col).getList().add(p);
                ImageView imageview = findViewById(p.getId());
                if (this.game.getAcolumn(col).getList().size() <= 10) {// if there is place left to show in the column
                    imageview.setY(this.piecesPosTop[this.game.getAcolumn(col).getList().size() - 1]);
                    imageview.setX(this.columnsPos[Math.abs(col - 24)][1][0] + 27);
                    imageview.setVisibility(View.VISIBLE);
                }
                else// if there isn't place left to show in the column
                {
                    imageview.setVisibility(View.INVISIBLE);
                }
                if(this.game.getRedEaten().size() == 0){
                    this.is_red_eaten = false;
                }
            }
        }
    }
    public void highlightPossibleColumns(int col) {
        /*
        this function will highlight possible moves from the player after he pressed a piece
        param: col : int
        return: void
         */
        int col1 = -1, col2 = -1;
        if(this.turn == 1){ // if its blue's turn
            if(checkForEndGame(this.turn)) {
                if(col == 25-this.MovePower1 || col == 25-this.MovePower2) // if blue can remove a piece with a dice
                {
                    ImageView border = findViewById(R.id.outHighlight1);
                    border.setVisibility(View.VISIBLE);
                }
                // check if the the highest column is smaller than the dice
                else if (col != 19) {
                    if (col > 25 - this.MovePower1 || col > 25 - this.MovePower2) {
                        boolean flag = true;
                        for (int i = col - 1; i >= 19; i--) {
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
            }
        }else{ // if its red's turn
            if(checkForEndGame(this.turn)) {
                if (col == this.MovePower1 || col == this.MovePower2) { // if blue can remove a piece with a dice
                    ImageView border = findViewById(R.id.outHighlight2);
                    border.setVisibility(View.VISIBLE);
                }
                // check if the the highest column is smaller than the dice
                else if(col != 6) {
                    if (col < this.MovePower1 || col < this.MovePower2) {
                        boolean flag = true;
                        for (int i = col + 1; i <= 6; i++) {
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
        }
        // if blue is eaten
        if (col == -1)
        {
            if (this.MovePower1 > 0) { // check is move1 is valid
                col1 = this.MovePower1;
                if(this.game.getAcolumn(col1).getList().size() > 1) {
                    if (this.game.getAcolumn(col1).getList().getFirst().getType() != this.turn) {
                        col1 = 0;
                    }
                }
            }
            if (this.MovePower2 > 0) {// check is move2 is valid
                col2 = this.MovePower2;
                if (this.game.getAcolumn(col2).getList().size() > 1) {
                    if (this.game.getAcolumn(col2).getList().getFirst().getType() != this.turn) {
                        col2 = 0;
                    }
                }
            }
        }
        // if red is eaten
        else if (col == -2)
        {
            if (this.MovePower1 > 0) {// check is move1 is valid
                col1 = 25 - this.MovePower1;
                if (this.game.getAcolumn(col1).getList().size() > 1) {
                    if (this.game.getAcolumn(col1).getList().getFirst().getType() != this.turn) {
                        col1 = 0;
                    }
                }
            }
            if (this.MovePower2 > 0) {// check is move2 is valid
                col2 = 25 - this.MovePower2;
                if (this.game.getAcolumn(col2).getList().size() > 1) {
                    if (this.game.getAcolumn(col2).getList().getFirst().getType() != this.turn) {
                        col2 = 0;
                    }
                }
            }
        }
        else
        {
            int mult; // mult differentiate between the moves of red and blue as they move in different directions
            int type = this.game.getAcolumn(col).getList().get(0).getType();
            if (type == 1) {
                mult = 1;
            }
            else{
                mult = -1;
            }
            // check if the moves are legal moves and if not than change the dest columns to 0 because then they won't be highlighted
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
        // highlight column 1 if every test says that he is a legal column to move to
        if (col1 > 0 && col1 != col && col1 <= 24) {
            if (col1 > 12) { // bottom column
                ImageView highlight = findViewById(R.id.ColumnHighlihgt3);
                highlight.setX(this.columnsPos[col1-1][0][0] + 7);
                highlight.setVisibility(View.VISIBLE);
            } else {// top column
                ImageView highlight = findViewById(R.id.ColumnHighlihgt1);
                if(col1 == 6)
                {
                    highlight.setX(1096); // there is a problem with column 6 because of the middle part so i just set it manually
                }
                else
                {
                    highlight.setX(this.columnsPos[col1][0][0] + 7);
                }
                highlight.setVisibility(View.VISIBLE);
            }
        }
        // highlight column 2 if every test says that he is a legal column to move to
        if (col2 > 0 && col2 != col && col2 <= 24) {
            if (col2 > 12) {// bottom column
                ImageView highlight = findViewById(R.id.ColumnHighlihgt4);
                highlight.setX(this.columnsPos[col2-1][0][0] + 7);
                highlight.setVisibility(View.VISIBLE);
            } else {// top column
                ImageView highlight = findViewById(R.id.ColumnHighlihgt2);
                if(col2 == 6)
                {
                    highlight.setX(1096); // there is a problem with column 6 because of the middle part so i just set it manually
                }
                else
                {
                    highlight.setX(this.columnsPos[col2][0][0] + 7);
                }
                highlight.setVisibility(View.VISIBLE);
            }
        }
    }
    public void unHighlightColumns() {
        /*
        this function will unHighlight possible columns
        param: none
        return: void
         */
        ImageView border = findViewById(R.id.outHighlight1);
        border.setVisibility(View.INVISIBLE);
        border = findViewById(R.id.outHighlight2);
        border.setVisibility(View.INVISIBLE);
        ImageView highlight;
        if (this.dHighlight1) {
            highlight = findViewById(R.id.ColumnHighlihgt1);
            highlight.setVisibility(View.INVISIBLE);
        }
        if (this.dHighlight2) {
            highlight = findViewById(R.id.ColumnHighlihgt2);
            highlight.setVisibility(View.INVISIBLE);
        }
        if (this.dHighlight3) {
            highlight = findViewById(R.id.ColumnHighlihgt3);
            highlight.setVisibility(View.INVISIBLE);
        }
        if (this.dHighlight4) {
            highlight = findViewById(R.id.ColumnHighlihgt4);
            highlight.setVisibility(View.INVISIBLE);
        }
    }
    public boolean checkForEndGame(int type){
        /*
        this function will check if a player has all his pieces in his quarter
        param: type : int
        return: boolean
         */
        if (type == 1){ // if checking for blue
            if (this.is_blue_eaten == true)
            {
                return false;
            }
            for (int i=1;i<19;i++) // go through all the columns in the blue quarter
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
        else // if checking for red
        {
            if (this.is_red_eaten == true)
            {
                return false;
            }
            for (int i=24;i>6;i--)// go through all the columns in the red quarter
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
    public boolean endGameMove(int col) {
        /*
        this function will handle when a player wants to out a piece in the end game
        param: col : int
        return: boolean
         */
        if(this.game.getAcolumn(col).getList().getFirst().getType() == 1) // if the piece moving out is blue
        {
            if(col == 25-this.MovePower1 || col == 25-this.MovePower2) // checking if the column moving is equal to one move power
            {
                // take piece out

                Piece p = this.game.getAcolumn(col).getList().getLast();
                this.blueOut.add(p);
                this.game.getAcolumn(col).getList().removeLast();
                ImageView bluepieceout = findViewById(this.bluePieceOutID[this.blueOut.size()-1]);
                bluepieceout.setVisibility(View.VISIBLE);
                ImageView img = findViewById(p.getId());
                img.setVisibility(View.INVISIBLE);

                // use the move power

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
            else if (col > 25-this.MovePower1 || col > 25-this.MovePower2)// checking if the column smaller than each move powers
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
                    // take piece out
                    Piece p = this.game.getAcolumn(col).getList().getLast();
                    this.blueOut.add(p);
                    this.game.getAcolumn(col).getList().removeLast();
                    ImageView bluepieceout = findViewById(this.bluePieceOutID[this.blueOut.size()-1]);
                    bluepieceout.setVisibility(View.VISIBLE);
                    ImageView img = findViewById(p.getId());
                    img.setVisibility(View.INVISIBLE);

                    int move;
                    boolean flag1 = false;
                    // find which move was used
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
                    // use the move power
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
        else // if the piece moving out is red
        {
            if(col == this.MovePower1 || col == this.MovePower2) // checking if the column moving is equal to one move power
            {
                // take piece out
                Piece p = this.game.getAcolumn(col).getList().getLast();
                this.redOut.add(p);
                this.game.getAcolumn(col).getList().removeLast();
                ImageView redpieceout = findViewById(this.redPieceOutID[this.redOut.size()-1]);
                redpieceout.setVisibility(View.VISIBLE);
                ImageView img = findViewById(p.getId());
                img.setVisibility(View.INVISIBLE);
                // use the move power

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
            else if (col < this.MovePower1 || col < this.MovePower2) // checking if the column smaller than each move powers
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
                    // take piece out
                    Piece p = this.game.getAcolumn(col).getList().getLast();
                    this.redOut.add(p);
                    this.game.getAcolumn(col).getList().removeLast();
                    ImageView redpieceout = findViewById(this.redPieceOutID[this.redOut.size()-1]);
                    redpieceout.setVisibility(View.VISIBLE);
                    ImageView img = findViewById(p.getId());
                    img.setVisibility(View.INVISIBLE);

                    int move;
                    boolean flag1 = false;
                    // find which move was used
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
                    // use the move power
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
        return false;
    }
    public boolean checkIfGameEnded(int type) {
        /*
        this function will check if a player got all his pieces out
        param: type : int
        return: boolean
         */
        if (type == 1){
            if (this.blueOut.size() == 15) // check if all 15 pieces are out
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
            if (this.redOut.size() == 15)// check if all 15 pieces are out
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
        /*
        this function will check if in the end game a col pressed has a legal move to remove a piece
        param: col : int , type : int
        return: boolean
         */
        if (type == 1)
        {
            if (col == 25-this.MovePower1 || col == 25-this.MovePower2) { // checking if the column moving is equal to one move power
                return true;
            }
            else if(col > 25-this.MovePower1 || col > 25-this.MovePower2) { // checking if the column smaller than each move powers
                return true;
            }
            else {
                return false;
            }

        }
        else
        {
            if (col == this.MovePower1 || col == this.MovePower2) {// checking if the column moving is equal to one move power
                return true;
            }
            else if(col < this.MovePower1 || col < this.MovePower2) { // checking if the column smaller than each move powers
                return true;
            }
            else{
                return false;
            }
        }
    }
    public void EndTheGame(int type){
        /*
        this function will end the game buy showing who won
        param: type : int
        return: void
         */
        // show the winning text
        TextView txtview = findViewById(R.id.winsign);
        if (type == 1){
            txtview.setText(name_player_1 + " WON!");
            txtview.setTextColor(Color.BLUE);
        }
        else{
            txtview.setText(name_player_2 + " WON!");
            txtview.setTextColor(Color.RED);
        }
        // show the save and exit buttons
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
            v.vibrate(500);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        /*
        this function will run after requesting writing permissions inorder to write the result into the results file
        param: requestCode : int , permissions : String[] , grantResults : int[]
        return: void
         */
        switch (requestCode) {
            case 0:
                writeFile();
        }
    }
    public String readFile() {
        /*
        this function will read the contents from a file and will return them inorder to be rewritten in the file with the new result
        param: none
        return: String
         */
        StringBuilder text = new StringBuilder();
        try{
            File root = new File(Environment.getExternalStorageDirectory(), "Files");
            if (!root.exists()) {
                root.mkdir();
            }
            File file = new File(root,this.FileName);
            BufferedReader br = new BufferedReader(new FileReader(file)); // create reader
            String line;
            while((line = br.readLine()) != null) // read file line by line
            {
                // append text builder
                text.append(line);
                text.append("\n");
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return text.toString(); // return the text
    }
    public void saveResult(View view) {
        /*
        this function handles what happens when the user wants to save the result
        param: view : View
        return: void
         */
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
        /*
        this function will write into a file the new result of the game
        param: none
        return: void
         */
        // read body from file
        String sBody = readFile();
        // add new result to body
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
        // open the file write and close
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
    }
    public void exitActivity(View view) {
        /*
        this function will finish the activity
        param: view : View
        return: void
          */
        finish();
    }
    @Override
    public boolean onTouchEvent(MotionEvent e){
        /*
        this function will handle most of the game, of when the player wants to move a piece
        param: e : MotionEvent
        return: boolean
         */
        if(!didGameEnd) {
            switch (e.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (this.NumOfMoves > 0) {
                        // get the touch location rounded
                        int x = round(e.getX());
                        int y = round(e.getY());
                        // find the column pressed
                        int col = findColumn(x, y);
                        if (this.press == 0) { // if its first press for the turn
                            if (col != -1) { // if the column is a valid column
                                if (this.is_blue_eaten && this.turn == 1) { // is blue is eaten piece and its his turn
                                    this.press = 1;// set as pressed
                                    this.col_pressed = -1; // set column pressed as -1
                                } else if (this.is_red_eaten && this.turn == 0) { // is red is eaten piece and its his turn
                                    this.press = 1;// set as pressed
                                    this.col_pressed = -2;// set column pressed as -2
                                } else if (!CheckIfColumnIsEmpty(col)) {// check if the column is not empty
                                    if (this.game.getAcolumn(col).getList().get(0).getType() == this.turn) { // check if the column pressed match the turn player
                                        this.press = 1; // set as pressed
                                        this.col_pressed = col; // set column pressed to the column pressed
                                        highlightColumn(col, true); // highlight the column
                                        highlightPossibleColumns(col); // highlight possible move columns
                                    }
                                }
                            }
                        } else { // if its second press
                            if (col == -1) { // if second press is not on a column
                                if (checkForEndGame(this.turn)) { // check if its end game for the player
                                    if (IsValidOut(this.col_pressed, this.turn)) { // check if the out move is valid
                                        boolean flag = endGameMove(this.col_pressed); // move the piece
                                        if (flag){ // if the piece moved minus one move
                                            this.NumOfMoves--;
                                        }
                                        if (checkIfGameEnded(this.turn)) { // check if the game ended
                                            this.didGameEnd = true;
                                            EndTheGame(this.turn);
                                        }
                                    }
                                    if (this.NumOfMoves == 0) {// check for end of turn
                                        this.turn = Math.abs(this.turn - 1);
                                    }
                                }
                            } else { // if its a normal move
                                if (IsValidMove(this.col_pressed, col)) { // if move is valid
                                    move(this.col_pressed, col); // move piece
                                    this.NumOfMoves--;
                                    if (this.NumOfMoves == 0) { // check for end of turn
                                        this.turn = Math.abs(this.turn - 1);
                                    }
                                }
                            }
                            highlightColumn(this.col_pressed, false); // unhighlight column
                            unHighlightColumns(); // unhighlight possible columns
                            this.press = 0;
                        }
                        if ((this.col_pressed == -1 || this.col_pressed == -2) && this.press == 1) { // if there is a piece eaten and the press is 1 (meaning it passed the first check at the start of this function)
                            if (col != -1) { // if the press is a valid column
                                if (IsValidMove(this.col_pressed, col)) { // if the move is valid
                                    GetOutOfEat(this.col_pressed, col); // get the piece out of eat
                                    this.NumOfMoves--;
                                    unHighlightColumns(); // unhighlight possible columns
                                    if (this.NumOfMoves == 0) { // check for end of turn
                                        this.turn = Math.abs(this.turn - 1);
                                    }
                                    else
                                    {
                                        // if there are still pieces eaten and the turn isn't over highlight possible columns again
                                        if(this.turn == 1 && this.is_blue_eaten)
                                        {
                                            if(AreThereAnyPossibleMoves(1)) {
                                                highlightPossibleColumns(-1);
                                            }
                                        }
                                        else if(this.turn == 0 && this.is_red_eaten)
                                        {
                                            if(AreThereAnyPossibleMoves(0)) {
                                                highlightPossibleColumns(-2);
                                            }
                                        }
                                    }
                                }
                                // set press to 0
                                this.press = 0;
                            }
                        }
                    }
                case MotionEvent.ACTION_UP:
                    if (this.NumOfMoves == 0) { // check if the turn has ended
                        this.rolled = false; // set rolled as false
                        // set arrow to the correct turn
                        ImageView arrow = findViewById(R.id.turnArrow);
                        if(this.turn == 1){
                            arrow.setImageResource(R.drawable.bluearrow);
                        }
                        else
                        {
                            arrow.setImageResource(R.drawable.redarrow);
                        }
                        // set roll button to not glow
                        Button button1 = findViewById(R.id.roll1);
                        button1.setBackgroundResource(R.drawable.reddice);
                    }
                    else {
                        // if there aren't any possible moves for the player change turn
                        if (!AreThereAnyPossibleMoves(this.turn)) {
                            this.turn = Math.abs(this.turn - 1);
                            this.NumOfMoves = 0;
                        }
                    }
            }
        }
        return true;
    }
}