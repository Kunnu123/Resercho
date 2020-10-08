package com.resercho;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.resercho.POJO.ModelEvent;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class EventActivity extends AppCompatActivity {

    ModelEvent work;

    TextView title, date,time,location,desc;
    ImageView image;

    protected void init(){
        title = findViewById(R.id.title);
        time = findViewById(R.id.time);
        date = findViewById(R.id.date);
        image = findViewById(R.id.image);
        location = findViewById(R.id.location);
        desc = findViewById(R.id.about);

        findViewById(R.id.interestBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Button)findViewById(R.id.interestBtn)).setText("Sending...");
                sendEvent();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        init();

        try {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);


            work = new ModelEvent();
            work.setImage(getIntent().getStringExtra("image"));
            work.setHost(getIntent().getStringExtra("host"));
            work.setCreatedAt(getIntent().getStringExtra("createat"));
            work.setDescription(getIntent().getStringExtra("desc"));
            work.setName(getIntent().getStringExtra("name"));
            work.setLocation(getIntent().getStringExtra("location"));
            work.setTime(getIntent().getStringExtra("time"));
            work.setDate(getIntent().getStringExtra("date"));
            work.setId(getIntent().getStringExtra("id"));
            work.setInterested(getIntent().getBooleanExtra("interest",false));


            getSupportActionBar().setTitle(work.getName());

            title.setText(work.getName());
            date.setText(work.getDate());
            time.setText(work.getTime());
            location.setText(work.getLocation());
            desc.setText(work.getDescription());


            Glide.with(getApplicationContext())
                    .load(work.getImage()
                    ).placeholder(R.drawable.downloading)
                    .into(image);

        }catch (Exception e){
            Log.d("res","eventik Exception :"+e);
            Toast.makeText(getApplicationContext(),"Exception!",Toast.LENGTH_SHORT).show();
        }

        fetchSingleEvent(work.getId());
    }

    protected void fetchSingleEvent(final String eid){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                NetworkHandler.fetchSinglEvent(EventActivity.this, eid, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.d("Resercho","Eventik : onFailure:"+e);
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String resp = response.body().string();
                        Log.d("Resercho","Eventik : onResponse:"+resp);
                        parseJson(resp);
                    }
                });
            }
        });
        thread.start();
    }
    protected void parseJson(String json){
        try{
            JSONObject jsonObject =new JSONObject(json);
            if(jsonObject.getInt("intrust")==1){
                setUpdateValues(true);
            }else{
                setUpdateValues(false);
            }
        }catch (JSONException e){
            setUpdateValues(false);
        }
    }

    protected void setUpdateValues(final boolean interested){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(interested) {
                    work.setInterested(true);
                    setButtonText("Retract Interest");
                }
                else {
                    work.setInterested(false);
                    setButtonText("Show Interest");
                }
            }
        });
    }


    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

        protected void setButtonText(final String text){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ((Button)findViewById(R.id.interestBtn)).setText(text);
                }
            });
        }

    protected void sendEvent(){
        if(work!=null){
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    NetworkHandler.sendEvent(getApplicationContext(), work.getId(),work.isInterested(), new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            Log.d("REs","Eventika onFailre : "+e);
                            toastOnMain("Failed loading");
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            String resp = response.body().string();
                            Log.d("REs","Eventika onResponse : "+resp);
                            try{
                                JSONObject object =new JSONObject(resp);

                                if(object.getInt("success")==1){
                                    if(work.isInterested()) {
                                        work.setInterested(false);
                                        toastOnMain("Removed interest");
                                        setButtonText("Show Interest");
                                    }
                                    else {
                                        work.setInterested(true);
                                        toastOnMain("Sent interest");
                                        setButtonText("Retract Interest");
                                    }
                                }else{
                                    toastOnMain("Try again later");
                                }

                            }catch (JSONException e){
                                Log.d("REs","Eventika JSON Exception : "+e);
                            }
                        }
                    })    ;
                }
            });

            thread.start();
        }
    }

    protected void toastOnMain(final String msg){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_SHORT).show();
            }
        });
    }
}
