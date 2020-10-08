package com.resercho;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.Toast;

public class VideoPlayerActivity extends AppCompatActivity {


    String path = "/sdcard/download/Sample_audio.mp3";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_upload);


        try{
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }catch (Exception e){

        }

        audioPlayer(path);

    }

    public void audioPlayer(String path){
        //set up MediaPlayer
        MediaPlayer mp = new MediaPlayer();

        try {
            mp.setDataSource(path);
            mp.prepare();
            mp.start();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(),"Audio failued:"+e,Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
