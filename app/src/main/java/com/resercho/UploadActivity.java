package com.resercho;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class UploadActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        try{
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setTitle("Select Post Type");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }catch (Exception e){

        }

        findViewById(R.id.academicBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UploadActivity.this, NewPostActivity.class);
                intent.putExtra("type","research");
                startActivity(intent);
            }
        });

        findViewById(R.id.socialFeed).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UploadActivity.this, NewPostActivity.class);
                intent.putExtra("type","post");
                startActivity(intent);
            }
        });
    }
}