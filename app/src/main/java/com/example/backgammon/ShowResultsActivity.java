package com.example.backgammon;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;

public class ShowResultsActivity extends Activity {
    final String FileName = "Results.txt";
    private LinkedList<String> results = new LinkedList<>();
    private int[] TextIDs = new int[]{R.id.result1,R.id.result2,R.id.result3,R.id.result4,R.id.result5};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_results);
        readFile();
        if(this.results.size() >= 5) {
            for (int j=0, i = this.results.size() - 1; i > this.results.size() - 6; i--) {
                String text = "";
                TextView txtview = findViewById(TextIDs[j]);
                String [] parts = this.results.get(i).split("#");
                if (parts[0] == "1")
                {
                    text += "Blue won, opponent had ";
                }
                else
                {
                    text += "Red won, opponent had ";
                }
                text += parts[1];
                text += " pieces left.";
                txtview.setText(text);
                j++;
            }
        }
        else{
            for (int j = 0, i = this.results.size() - 1; i > -1; i--) {
                String text = "";
                TextView txtview = findViewById(TextIDs[j]);
                String [] parts = this.results.get(i).split("#");
                if (((int)parts[0].charAt(0)-48) == 1)
                {
                    text += "Blue won, opponent had ";
                }
                else
                {
                    text += "Red won, opponent had ";
                }
                text += parts[1];
                text += " pieces left.";
                txtview.setText(text);
                j++;
            }
        }
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
    public void exitActivity(View view) {
        finish();
    }
}