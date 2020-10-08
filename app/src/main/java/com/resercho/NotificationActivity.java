package com.resercho;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.resercho.Adapters.AdapterNotifs;
import com.resercho.POJO.ModelNotif;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class NotificationActivity extends AppCompatActivity {

    List<ModelNotif> notifList;

    RecyclerView recyclerView;
    AdapterNotifs adapterNotif;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        getSupportActionBar().setTitle("Notifications");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        init();
        fetchNotifs();
    }

    private void init(){
        recyclerView = findViewById(R.id.notifRecycler);
    }


    protected void fetchNotifs(){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                NetworkHandler.fetchNotifs(getApplicationContext(),new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.d("Resercho","Notif : onFailure::"+e);
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String resp = response.body().string();
                        Log.d("Resercho","Notif : onResponse:"+resp);
                        parseJson(resp);
                    }
                });
            }
        });
        thread.start();
    }

    protected void parseJson(String json){
        notifList = new ArrayList<>();
        try{
            JSONObject jsonObject =new JSONObject(json);
            if(jsonObject.getInt("success")==1){
                JSONArray arr = jsonObject.getJSONArray("data");
                for(int i=0;i<arr.length();i++){
                    ModelNotif w = ConverterJson.parseNotifJson(arr.getJSONObject(i));
                    if(w!=null) {
                        notifList.add(w);
                        Log.d("Resercho","Notif : Iteration : "+i+ " W:"+w.getId());
                    }
                    else
                        Log.d("Resercho","Notif : Null Model : "+i);
                }

                showRecycler(notifList);
            }else{
                Log.d("Resercho","Notif : Request Success:"+jsonObject.getInt("success"));
            }
        }catch (JSONException e){

        }
    }

    protected void showRecycler(final List<ModelNotif> list){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ((TextView)findViewById(R.id.notifCount)).setText("You have "+list.size()+" notifications!");
                RecyclerView.LayoutManager layman = new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.VERTICAL,false);
                adapterNotif = new AdapterNotifs(NotificationActivity.this,list);
                recyclerView.setLayoutManager(layman);
                recyclerView.setAdapter(adapterNotif);
            }
        });
        Log.d("Resercho","Notif showRv : "+list.size());

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
