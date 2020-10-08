package com.resercho;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.resercho.Adapters.AdapterChats;
import com.resercho.POJO.ModelChat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MessagesActivity extends AppCompatActivity {


    AdapterChats chatAdapter;
    List<ModelChat> chatList;
    RecyclerView chatRv;
    ProgressBar pbar;

    View noMsgTv;
    private void init(){
        pbar = findViewById(R.id.pbar);
        chatRv = findViewById(R.id.chatRv);
        noMsgTv = findViewById(R.id.noMsgsTv);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

        try{
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setTitle("Messages");
        }catch (Exception e){     }

        init();
        loadMsgs();
    }

    protected void loadMsgs(){
        showPbar(true);

        Thread thread =new Thread(new Runnable() {
            @Override
            public void run() {
                NetworkHandler.fetchChats(getApplicationContext(), new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.d("saved","savek onFailure:"+e);
                        if(e.toString().contains("SocketTimeoutException"))
                            loadMsgs();
                        showPbar(false);
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String resp = response.body().string();

                        Log.d("saved","savek resp:"+resp);
                        parseJson(resp);
                        showPbar(false);
                    }
                });
            }
        });

        thread.start();

    }

    protected void parseJson(String resp){
        chatList = new ArrayList<>();
        try{

            JSONObject object = new JSONObject(resp);

            if(object.getInt("success")==1){
                JSONArray data = object.getJSONArray("data");

                for(int i =0; i<data.length();i++){
                    ModelChat saved = ConverterJson.parseChatFromJson(data.getJSONObject(i));
                    if(saved!=null)
                        chatList.add(saved);
                }
            }else{
                showNoMsgs(true);
            }

            showSavedRecycller(chatList);
        }catch (JSONException e){
            Log.d("saved","savek JSON Exception:"+resp);
            toastOnMain("JSON");
        }
    }
    protected void showNoMsgs(final boolean show){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(show){
                    noMsgTv.setVisibility(View.VISIBLE);
                }else{
                    noMsgTv.setVisibility(View.GONE);
                }
            }
        });
    }

    protected void showSavedRecycller(final List<ModelChat> list){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                RecyclerView.LayoutManager layman = new LinearLayoutManager(MessagesActivity.this,LinearLayoutManager.VERTICAL,true);
                chatAdapter = new AdapterChats(MessagesActivity.this,list);
                chatRv.setLayoutManager(layman);
                chatRv.setAdapter(chatAdapter);
            }
        });
        Log.d("Resercho","Dashboard Story showRv : "+list.size());

    }


    protected void toastOnMain(final String msg){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_LONG).show();
            }
        });
    }


    protected void showPbar(final boolean show){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(show){
                    pbar.setVisibility(View.VISIBLE);
                }else{
                    pbar.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
