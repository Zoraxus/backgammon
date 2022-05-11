package com.example.backgammon;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

public class ShowRulesActivity extends AppCompatActivity {
    private int[] Pages = new int[]{R.drawable.rulespage1,R.drawable.rulespage2,R.drawable.rulespage3,
            R.drawable.rulespage4,R.drawable.rulespage5};
    private int currentPage = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_rules);
    }

    public void goNext(View view) {
        if(this.currentPage < 5)
        {
            ImageView img = findViewById(R.id.viewRules);
            img.setImageResource(this.Pages[currentPage]);
            this.currentPage ++;
        }
    }
    public void goPrev(View view){
        if(this.currentPage > 1) {
            ImageView img = findViewById(R.id.viewRules);
            img.setImageResource(this.Pages[currentPage - 2]);
            this.currentPage--;
        }
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