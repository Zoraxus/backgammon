package com.example.backgammon;

import static java.lang.Math.round;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Bundle;
import android.text.method.Touch;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.backgammon.Classes.Column;
import com.example.backgammon.Classes.Game;
import com.example.backgammon.Classes.Piece;

import java.util.LinkedList;

public class OfflineGameActivity extends Activity {
    public int[][][] columnsPos = new int[][][] {
            {{1869,1040},{1740,600}},{{1739,1040},{1610,600}},{{1609,1040},{1480,600}},{{1479,1040},{1350,600}},{{1349,1040},{1220,600}},{{1219,1040},{1090,600}},
            {{999,1040},{870,600}},{{869,1040},{740,600}},{{739,1040},{610,600}},{{609,1040},{480,600}},{{479,1040},{350,600}},{{349,1040},{220,600}},
            {{221,38},{350,480}},{{351,38},{480,480}},{{481,38},{610,480}},{{611,38},{740,480}},{{741,38},{870,480}},{{871,38},{1000,480}},
            {{1091,38},{1220,480}},{{1221,38},{1350,480}},{{1351,38},{1480,480}},{{1481,38},{1610,480}},{{1611,38},{1740,480}},{{1741,38},{1870,480}}};
    private Game game;
@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offline_game);
        LinkedList<Integer> ll = getIds();
        this.game = new Game(this.columnsPos,ll);

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
    public int findColumn(int x,int y)
    {
        int columnNum = -1;
        for (int i=0;i<columnsPos.length;i++){
            if (i < columnsPos.length/2){
                if (x <= columnsPos[i][0][0] && x >= columnsPos[i][1][0]){
                    if (y <= columnsPos[i][0][1] && y >= columnsPos[i][1][1]){
                        columnNum = i+1;
                        break;
                    }
                }
            }
            else{
                if (x >= columnsPos[i][0][0] && x <= columnsPos[i][1][0]){
                    if (y >= columnsPos[i][0][1] && y <= columnsPos[i][1][1]){
                        columnNum = i+1;
                        break;
                    }
                }
            }
        }
        return columnNum;
    }
    public void highlightColumn(int colNum){
        Column column = this.game.getAcolumn(colNum-1);
        LinkedList<Piece> ll = column.getList();
        if (ll.size() >= 1){
            int type = ll.get(0).getType();
            for (int i=0;i<ll.size();i++) {
                ImageView imgview = findViewById(ll.get(i).getId());
                if (type == 1){
                    imgview.setImageResource(R.drawable.bluepieceglow);
                }
                else{
                    imgview.setImageResource(R.drawable.redpieceglow);
                }
            }
        }
    }
    public boolean CheckIfColumnIsEmpty(int colNum){
        return false;
    }
    @Override
    public boolean onTouchEvent(MotionEvent e){

        int x = round(e.getX());
        int y = round(e.getY());
        int col = findColumn(x,y);
        if (!CheckIfColumnIsEmpty(col)){
            highlightColumn(col);
        }
        return true;
    }
}