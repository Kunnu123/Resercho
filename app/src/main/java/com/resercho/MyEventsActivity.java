package com.resercho;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.resercho.Adapters.AdapterEvents;
import com.resercho.POJO.ModelEvent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MyEventsActivity extends AppCompatActivity {

    RecyclerView recycler;
    AdapterEvents adapter;
    LinearLayoutManager layoutManager;

    List<ModelEvent> list;

    TextView noSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_events);


        try{
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setTitle("My Events");
        }catch (Exception e){

        }


        recycler = findViewById(R.id.recycler);
        noSave = findViewById(R.id.eventsMsg);

        loadEvents();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        loadEvents();
    }

    protected void loadEvents(){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                NetworkHandler.fetchInterestEvent(MyEventsActivity.this,new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.d("Resercho","HomeFrag : onFailure::"+e);
                        showNoEvents();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String resp = response.body().string();
                        Log.d("Resercho","HomeFrag : onResponse:"+resp);
                        parseJson(resp);
                    }
                });
            }
        });
        thread.start();
    }

    protected void parseJson(String json){
        list = new ArrayList<>();
        try{
            JSONObject jsonObject =new JSONObject(json);
            if(jsonObject.getInt("success")==1){
                JSONArray arr = jsonObject.getJSONArray("data");
                for(int i=0;i<arr.length();i++){
                    ModelEvent w = ConverterJson.parseEventJson(arr.getJSONObject(i));
                    if(w!=null) {
                        list.add(w);
                    }
                    else
                        Log.d("Resercho","HomeFrag : Null Model : "+i);
                }

                showRecycler(list);
            }else{

                showNoEvents();
                Log.d("Resercho","HomeFrag : Request Success:"+jsonObject.getInt("success"));
            }
        }catch (JSONException e){

        }
    }

    protected void showRecycler(final List<ModelEvent> list){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(list!=null&&list.size()>0) {
                    layoutManager = new LinearLayoutManager(MyEventsActivity.this, LinearLayoutManager.VERTICAL, false);
                    adapter = new AdapterEvents(MyEventsActivity.this, list);
                    recycler.setLayoutManager(layoutManager);
                    recycler.setAdapter(adapter);
                }else{
                    showNoEvents();
                }
            }
        });
        Log.d("Resercho","Dashboard Work showRv : "+list.size());

    }

    protected void showNoEvents(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                noSave.setText("You haven't marked any event as interested!");
            }
        });

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
