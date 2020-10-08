package com.resercho;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.resercho.Adapters.AdapterFollower;
import com.resercho.POJO.ModelSuggested;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class FollowerActivity extends AppCompatActivity {


    // Suggested
    RecyclerView suggestedRv;
    AdapterFollower adapterSuggested;
    List<ModelSuggested> list;

    boolean following = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        following = getIntent().getBooleanExtra("following",false);
        suggestedRv = findViewById(R.id.peopleRecycler);

        try{
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            if(following)
                getSupportActionBar().setTitle("Following");
            else
                getSupportActionBar().setTitle("Followers");
        }catch (Exception e){

        }

        fetchPeople();
    }

    protected void showPbar(final boolean show){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(show)
                    findViewById(R.id.pbar).setVisibility(View.VISIBLE);
                else
                    findViewById(R.id.pbar).setVisibility(View.GONE);
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        fetchPeople();
    }

    protected void fetchPeople(){
        showPbar(true);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                NetworkHandler.fetchFollowing(FollowerActivity.this,following,new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        showPbar(false);
                        if(e.toString().contains("java.net.SocketTimeoutException")){
                            fetchPeople();
                        }
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String resp = response.body().string();

                            parseSuggestedJson(resp);
                        showPbar(false);
                    }
                });
            }
        });
        thread.start();
    }

    protected void parseSuggestedJson(String json){
        list = new ArrayList<>();
        try{
            JSONObject jsonObject =new JSONObject(json);
            if(jsonObject.getInt("success")==1){
                JSONArray arr = jsonObject.getJSONArray("data");
                for(int i=0;i<arr.length();i++){
                    ModelSuggested w = ConverterJson.parseSuggestedJson(arr.getJSONObject(i));
                    if(w!=null) {
                        list.add(w);
                        Log.d("Resercho","HomeFrag : Iteration : "+i+ " W:"+w.getId());
                    }
                    else
                        Log.d("Resercho","HomeFrag : Null Model : "+i);
                }

                showSuggestedRecycler(list);
            }else{
                Log.d("Resercho","HomeFrag : Request Success:"+jsonObject.getInt("success"));
            }
        }catch (JSONException e){

        }
    }
    protected void showSuggestedRecycler(final List<ModelSuggested> list){

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                RecyclerView.LayoutManager layman = new LinearLayoutManager(FollowerActivity.this,LinearLayoutManager.VERTICAL,false);
                adapterSuggested = new AdapterFollower(FollowerActivity.this,list);
                suggestedRv.setLayoutManager(layman);
                suggestedRv.setAdapter(adapterSuggested);
            }
        });

    }


    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
