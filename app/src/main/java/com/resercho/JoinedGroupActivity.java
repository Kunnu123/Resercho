package com.resercho;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.resercho.Adapters.AdapterGroup;
import com.resercho.Adapters.AdapterGroupCreated;
import com.resercho.POJO.ModelGroup;
import com.resercho.POJO.ModelNewGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class JoinedGroupActivity extends AppCompatActivity {


    // Joined Groupps
    RecyclerView recycler;
    AdapterGroup adapter;
    List<ModelGroup> list;

    // Created Groups
    RecyclerView recyclercre;
    AdapterGroupCreated adaptercre;
    List<ModelNewGroup> createdGroupList;

    ProgressBar pbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_joined_group);

        pbar = findViewById(R.id.pbar);
        recycler = findViewById(R.id.joinedGRv);

        recyclercre = findViewById(R.id.createdGRb);
        try{
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("My Groups");

        }catch (Exception e){

        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        loadGroupData();
        loadCreatedGroupsData();
    }

    protected void hidePbar(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                pbar.setVisibility(View.GONE);
            }
        });
    }

    protected void showNoJoined(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                findViewById(R.id.noGroupMsg).setVisibility(View.VISIBLE);
            }
        });
    }

    protected void showPbar(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                pbar.setVisibility(View.VISIBLE);
            }
        });
    }

    protected void loadGroupData(){
        Log.d("Resercho","Jgin loadGroupData()");
        showPbar();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                NetworkHandler.fetchJoinedGroups(JoinedGroupActivity.this,new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.d("Resercho","HomeFrag : Jgin onFailure::"+e);
                        hidePbar();
                        loadGroupData();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String resp = response.body().string();
                        Log.d("Resercho","HomeFrag : Jgin onResponse:"+resp);
                        parseJson(resp);
                    }
                });
            }
        });
        thread.start();
    }

    protected void loadCreatedGroupsData(){
        Log.d("Resercho","CreaGu loadGroupData()");
        showPbar();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                NetworkHandler.fetchCreatedGroups(JoinedGroupActivity.this,new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.d("Resercho","HomeFrag : CreaGu onFailure::"+e);
                        hidePbar();
                        loadCreatedGroupsData();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String resp = response.body().string();
                        Log.d("Resercho","HomeFrag : CreaGu onResponse:"+resp);
                        parseJsonNew(resp);
                    }
                });
            }
        });
        thread.start();
    }

    protected void parseJsonNew(String json){

        createdGroupList = new ArrayList<>();
        try{
            JSONObject jsonObject =new JSONObject(json);
            if(jsonObject.getInt("success")==1){
                JSONArray arr = jsonObject.getJSONArray("data");
                for(int i=0;i<arr.length();i++){

                    ModelNewGroup w = ConverterJson.parseNewGroupModel(arr.getJSONObject(i));
                    if(w!=null) {
                        createdGroupList.add(w);
                        Log.d("Resercho","NewGroup Added To List: "+w.getName());
                    }
                    else
                        Log.d("Resercho","NewGroup Jgin : Null Model : "+i);
                }

                showNewRecycler(createdGroupList);
            }else{

                Log.d("Resercho","HomeFrag : NewGroup Request Success:"+jsonObject.getInt("success"));
            }
        }catch (JSONException e){

        }

        hidePbar();
    }

    protected void parseJson(String json){

        list = new ArrayList<>();
        try{
            JSONObject jsonObject =new JSONObject(json);
            if(jsonObject.getInt("success")==1){
                JSONArray arr = jsonObject.getJSONArray("data");
                for(int i=0;i<arr.length();i++){
//                    ModelJoinedGroups w = ConverterJson.parseJoinedGroupJson(arr.getJSONObject(i));
                    ModelGroup w = ConverterJson.parseGroupJson(arr.getJSONObject(i));
                    if(w!=null) {
                        list.add(w);
                    }
                    else
                        Log.d("Resercho","HomeFrag Jgin : Null Model : "+i);
                }

                showWorkRecycler(list);
            }else{
                showNoJoined();
                Log.d("Resercho","HomeFrag : Jgin Request Success:"+jsonObject.getInt("success"));
            }
        }catch (JSONException e){

        }

        hidePbar();
    }


    protected void showWorkRecycler(final List<ModelGroup> list){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                RecyclerView.LayoutManager layman = new LinearLayoutManager(JoinedGroupActivity.this,LinearLayoutManager.VERTICAL,true);
                adapter = new AdapterGroup(JoinedGroupActivity.this,list);
                adapter.setJoined(true);
                recycler.setLayoutManager(layman);
                recycler.setAdapter(adapter);
            }
        });
        Log.d("Resercho","Dashboard Work Jgin showRv : "+list.size());

    }

    protected void showNewRecycler(final List<ModelNewGroup> list){

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                RecyclerView.LayoutManager layman = new LinearLayoutManager(JoinedGroupActivity.this,LinearLayoutManager.VERTICAL,true);
                adaptercre = new AdapterGroupCreated(JoinedGroupActivity.this,list);
                recyclercre.setLayoutManager(layman);
                recyclercre.setAdapter(adaptercre);
            }
        });
        Log.d("Resercho","Dashboard Work NewGroup showRv : "+list.size());

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
