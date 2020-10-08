package com.resercho;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.resercho.Adapters.AdapterMsgs;
import com.resercho.POJO.ModelMsg;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ChatWindowActivity extends AppCompatActivity {

    TextView username, noMsgs;

    ImageView profilePic, backBtn;

    String uname, profile,  uid;

    List<ModelMsg> chatList;
    HashMap<Integer,ModelMsg> searchChatList;

    AdapterMsgs adapterMsgs;
    RecyclerView chatRv;
    ProgressBar pbar;

    //
    EditText msgContent;
    ImageView sendBtn;

    // Search Bar
    View searchBar, searchActionBtn, searchBtn, searchBackArrow;
    EditText searchField;

    private void init(){
        username = findViewById(R.id.headerUname);
        profilePic = findViewById(R.id.headerPic);
        backBtn = findViewById(R.id.backArrow);
        chatRv = findViewById(R.id.msgRv);
        noMsgs = findViewById(R.id.noTextMsg);
        pbar = findViewById(R.id.pbar);
        msgContent = findViewById(R.id.msgContent);
        sendBtn = findViewById(R.id.sendBtn);

        // Search
        searchBar = findViewById(R.id.searchBar);
        searchActionBtn = findViewById(R.id.searchActionBtn);
        searchBtn = findViewById(R.id.searchChatBtn);
        searchField = findViewById(R.id.chatSearchField);
        searchBackArrow = findViewById(R.id.searchBackArrow);

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchBar.setVisibility(View.VISIBLE);
                findViewById(R.id.headerBar).setVisibility(View.GONE);
            }
        });

        searchBackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.headerBar).setVisibility(View.VISIBLE);
                searchBar.setVisibility(View.GONE);
            }
        });

        searchActionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String key = searchField.getText().toString();
                searchThisKey(key);
            }
        });


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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_window);

        uname = getIntent().getStringExtra("username");
        profile = getIntent().getStringExtra("profile");
        uid = getIntent().getStringExtra("uid");

        init();
        loadMsgs();
    }

    protected void loadMsgs(){
        showPbar(true);

        Thread thread =new Thread(new Runnable() {
            @Override
            public void run() {
                NetworkHandler.fetchChats(getApplicationContext(),uid, new Callback() {
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
    private ModelMsg getMsgModelFromText(String msgContent){
        ModelMsg msg = new ModelMsg();
        msg.setMsg(msgContent);
        msg.setFrom(DataHandler.getUserId(getApplicationContext()));
        msg.setTo(uid);
        msg.setTime((System.currentTimeMillis()) + "");
        msg.setSending(true);

        return msg;
    }

    protected void addMessageToList(ModelMsg msg){
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

    protected void searchThisKey(String key){
        searchChatList = new HashMap<>();

        if(chatList!=null){
            for(ModelMsg msg : chatList){
                if(msg.getMsg().contains(key)){
                    searchChatList.put(adapterMsgs.getPositionOfItem(msg),msg);
                    Log.d("SChat","Found key match : "+adapterMsgs.getPositionOfItem(msg) + " in msg : "+msg.getMsg());
                }
            }
        }

        if(searchChatList.size()>0){
            Toast.makeText(getApplicationContext(),"Found results: "+searchChatList.size(),Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(getApplicationContext(),"No results for key",Toast.LENGTH_LONG).show();
        }

        List<ModelMsg> values = new ArrayList<ModelMsg>(searchChatList.values());
        showSearhResults(values);
    }

    private void sendMessage(final ModelMsg msg){
        addMessageToList(msg);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                NetworkHandler.sendMessage(getApplicationContext(), msg.getMsg(), msg.getTo(), new Callback() {
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

            if(object.getInt("success")==1){
                JSONArray data = object.getJSONArray("data");

                for(int i =0; i<data.length();i++){
                    ModelMsg saved = ConverterJson.parseMsgFromJson(data.getJSONObject(i));
                    if(saved!=null)
                        chatList.add(saved);
                }
                showNoMsgs(false);
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
                    noMsgs.setVisibility(View.VISIBLE);
                }else{
                    noMsgs.setVisibility(View.GONE);
                }
            }
        });
    }

    protected void showSavedRecycller(final List<ModelMsg> list){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(list.size()>0) {
                    showNoMsgs(false);
                    LinearLayoutManager layman = new LinearLayoutManager(ChatWindowActivity.this, LinearLayoutManager.VERTICAL, true);
                    layman.setStackFromEnd(true);
                    adapterMsgs = new AdapterMsgs(ChatWindowActivity.this, list, uname, uid);
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

    protected void showSearhResults(final List<ModelMsg> list){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(list.size()>0) {
                    showNoMsgs(false);
                    LinearLayoutManager layman = new LinearLayoutManager(ChatWindowActivity.this, LinearLayoutManager.VERTICAL, true);
                    layman.setStackFromEnd(true);
                    adapterMsgs = new AdapterMsgs(ChatWindowActivity.this, list, uname, uid);
                    adapterMsgs.setSearchToggle(true);
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
