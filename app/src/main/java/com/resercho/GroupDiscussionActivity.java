package com.resercho;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.resercho.Adapters.AdapterGroupMsgs;
import com.resercho.Adapters.AdapterMsgs;
import com.resercho.POJO.ModelGroupMsg;
import com.resercho.POJO.ModelMsg;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class GroupDiscussionActivity extends AppCompatActivity {

    TextView username, noMsgs;

    ImageView profilePic, backBtn;

    String uname, profile,  uid, gid;

    List<ModelGroupMsg> chatList;
    AdapterGroupMsgs adapterMsgs;
    RecyclerView chatRv;
    ProgressBar pbar;

    //
    EditText msgContent;
    ImageView sendBtn;

    private void init(){
        username = findViewById(R.id.headerUname);
        profilePic = findViewById(R.id.headerPic);
        backBtn = findViewById(R.id.backArrow);
        chatRv = findViewById(R.id.msgRv);
        noMsgs = findViewById(R.id.noTextMsg);
        pbar = findViewById(R.id.pbar);
        msgContent = findViewById(R.id.msgContent);
        sendBtn = findViewById(R.id.sendBtn);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        username.setText(uname);
        Glide.with(getApplicationContext()).load(profile).placeholder(R.drawable.downloading).into(profilePic);


        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(msgContent!=null&&msgContent.getText()!=null&&msgContent.getText().length()>0){
                    sendMessage(getMsgModelFromText(msgContent.getText().toString()));
                }else{
                    Toast.makeText(getApplicationContext(),"Message can't be empty",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }




    protected void enableSend(boolean enable){
        if(enable) {
            sendBtn.setEnabled(true);
            sendBtn.setAlpha(1.0f);
        }else{
            sendBtn.setEnabled(true);
            sendBtn.setAlpha(0.6f);
        }
    }

    Handler handler;
    Runnable refreshRun= new Runnable() {
        @Override
        public void run() {
            loadMsgs();
            Log.d("Pgchamp","Loading messages");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_discussion);

        uname = getIntent().getStringExtra("username");
        profile = getIntent().getStringExtra("profile");
        uid = getIntent().getStringExtra("uid");
        gid = getIntent().getStringExtra("gid");

        handler = new Handler();
        init();
        loadMsgs();
    }



    protected void refresh(){
        handler.postDelayed(refreshRun,15000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(refreshRun);
        Log.d("Pgchamp","Stoppping messages");
    }

    protected void loadMsgs(){
        showPbar(true);

        Thread thread =new Thread(new Runnable() {
            @Override
            public void run() {
                NetworkHandler.fetchGroupMsgs(getApplicationContext(),gid, new Callback() {
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

        refresh();

    }



    private ModelGroupMsg getMsgModelFromText(String msgContent){
        ModelGroupMsg msg = new ModelGroupMsg();
        msg.setMsg(msgContent);
        msg.setUid(DataHandler.getUserId(getApplicationContext()));
        msg.setGid(gid);
        msg.setAttime((System.currentTimeMillis()) + "");
        msg.setSending(true);

        return msg;
    }

    protected void addMessageToList(ModelGroupMsg msg){
        msgContent.setText(null);
        chatList.add(0,msg);
        if(adapterMsgs!=null)
            adapterMsgs.notifyDataSetChanged();
        else{
            chatList = new ArrayList<>();
            chatList.add(msg);
            showSavedRecycller(chatList);
        }
    }

    private void sendMessage(final ModelGroupMsg msg){
        addMessageToList(msg);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                NetworkHandler.sendGroupMessage(getApplicationContext(), msg.getMsg(), msg.getGid(), new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.d("msg","msgin Failed:"+e);
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String resp = response.body().string();
                        Log.d("msg","msgin onResponse:"+resp);
                        try{
                            JSONObject jsonObject = new JSONObject(resp);
                            if(jsonObject.getInt("success")==1){
                                setMessageConfirm();
                            }else{

                            }
                        }catch (JSONException e){

                        }
                    }
                });
            }
        });

        thread.start();
    }

    protected void setMessageConfirm(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                chatList.get(0).setSending(false);
                adapterMsgs.notifyItemChanged(0);
            }
        });

    }

    protected void parseJson(String resp){
        chatList = new ArrayList<>();
        try{

            JSONObject object = new JSONObject(resp);


            if(object.getInt("code")==1){
                JSONArray data = object.getJSONArray("data");

                for(int i =0; i<data.length();i++){
                    ModelGroupMsg saved = ConverterJson.parseGroupMsgFromJson(data.getJSONObject(i));
                    if(saved!=null)
                        chatList.add(saved);
                }
                showNoMsgs(false);
            }else{
                showNoMsgs(true);
            }

            showSavedRecycller(chatList);
        }catch (JSONException e){
            Log.d("saved","savek JSON Exception:"+e);
            toastOnMain("JSON");
        }
    }
    protected void showNoMsgs(final boolean show){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(show){
                    noMsgs.setVisibility(View.VISIBLE);
                }else{
                    noMsgs.setVisibility(View.GONE);
                }
            }
        });
    }

    protected void showSavedRecycller(final List<ModelGroupMsg> list){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(list.size()>0) {
                    showNoMsgs(false);
                    LinearLayoutManager layman = new LinearLayoutManager(GroupDiscussionActivity.this, LinearLayoutManager.VERTICAL, true);
                    layman.setStackFromEnd(true);
                    uid = DataHandler.getUserId(getApplicationContext());
                    adapterMsgs = new AdapterGroupMsgs(GroupDiscussionActivity.this, list, uname, uid);
                    chatRv.setLayoutManager(layman);
                    chatRv.setAdapter(adapterMsgs);
                    chatRv.scrollToPosition(chatRv.getChildCount());
                }else{
                    showNoMsgs(true);
                }
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

}
