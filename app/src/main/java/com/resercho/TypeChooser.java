package com.resercho;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class TypeChooser extends AppCompatActivity {

    String gid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_type_chooser);

        gid = getIntent().getStringExtra("gid");

        findViewById(R.id.academicBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TypeChooser.this, NewPostActivity.class);
                intent.putExtra("type", "research");
                intent.putExtra("gid", gid+"");
                startActivity(intent);
            }
        });

        findViewById(R.id.socialFeed).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TypeChooser.this, NewPostActivity.class);
                intent.putExtra("type", "post");
                intent.putExtra("gid", gid);
                startActivity(intent);
            }
        });
    }
}
