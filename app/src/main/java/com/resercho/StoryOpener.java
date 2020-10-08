package com.resercho;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class StoryOpener extends AppCompatActivity {


    View nextStory, prevStory;
    ImageView storyImage;

    TextView uname, timestamp;

    Handler handler;

    int prog = 0;

    View storyBar;

    CountDownTimer cTimer = null;

    //start timer function
    void startTimer() {
        cTimer = new CountDownTimer(10000, 10) {
            public void onTick(long millisUntilFinished) {
                setBarProg();
            }
            public void onFinish() {
                nextStory();
            }
        };
        cTimer.start();
    }


    //cancel timer
    void cancelTimer() {
        if(cTimer!=null)
            cTimer.cancel();
    }

    protected void setBarProg(){
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.MATCH_PARENT,
                prog
        );

        storyBar.setLayoutParams(param);
        Log.d("Res","Pxts:"+prog);
        prog +=1;
    }

    String name, profile, imgUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_story_opener);
        storyBar = findViewById(R.id.storyBar);

        name = getIntent().getStringExtra("name");
        profile = getIntent().getStringExtra("profile");
        imgUrl = getIntent().getStringExtra("imgurl");

    }

    @Override
    protected void onResume() {
        super.onResume();

        startTimer();

    }

    protected void startsTimer(){

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(prog<=100){
                    setBarProg();
                    startTimer();
                }else{
                    handler.removeCallbacks(this);
                    nextStory();
                }
            }
        },500);

    }


        protected void nextStory(){
            cancelTimer();
            Toast.makeText(getApplicationContext(),"Going to the next Story",Toast.LENGTH_LONG).show();
        }

}
