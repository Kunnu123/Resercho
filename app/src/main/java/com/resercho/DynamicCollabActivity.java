package com.resercho;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.resercho.POJO.ModelGroup;

public class DynamicCollabActivity extends AppCompatActivity {

    ModelGroup collab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dynamic_collab);

        try{
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }catch (Exception e){

        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
