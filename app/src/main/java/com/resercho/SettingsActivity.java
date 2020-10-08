package com.resercho;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

public class SettingsActivity extends AppCompatActivity {


    View schoolTick, univTick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        schoolTick = findViewById(R.id.schoolTick);
        univTick = findViewById(R.id.univTick);



        findViewById(R.id.setUnivMode).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DataHandler.setMode(DataHandler.UNIV_MODE,getApplicationContext());
                updateUI();
            }
        });

        findViewById(R.id.setSchoolMode).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DataHandler.setMode(DataHandler.SCHOOL_MODE,getApplicationContext());
                updateUI();
            }
        });

        findViewById(R.id.backArrow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUI();
    }

    protected void updateUI(){
        if(DataHandler.isUnivMode(getApplicationContext())){
            univTick.setAlpha(1.0f);
            schoolTick.setAlpha(0.2f);
        }

        if(DataHandler.isSchoolMode(getApplicationContext())){
            univTick.setAlpha(0.2f);
            schoolTick.setAlpha(1.0f);
        }
    }
}
