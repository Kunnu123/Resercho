package com.resercho;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.resercho.Adapters.AdapterWork;
import com.resercho.POJO.ModelWork;
import com.bumptech.glide.Glide;
import com.resercho.POJO.ModelGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class GroupActivity extends AppCompatActivity {

    ModelGroup group;
    ImageView mainImage,profImage;
    TextView title,uname,about,desc;
    Button joinBtn;
    View shareBtn;

    RecyclerView workRecycler;
    List<ModelWork> modelWorkList;
    AdapterWork adapterWork;
    ProgressBar pbar;
    String gid;

    boolean owner = false;

    protected void init(){

        mainImage = findViewById(R.id.postImage);
        profImage = findViewById(R.id.userProfPic);
        title = findViewById(R.id.title);
        uname = findViewById(R.id.username);
        about = findViewById(R.id.uabout);
        desc = findViewById(R.id.description);
        joinBtn = findViewById(R.id.joinBtn);
        pbar = findViewById(R.id.pbar);

        workRecycler = findViewById(R.id.groupPosts);
        shareBtn = findViewById(R.id.shareBtn);

        uname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(group!=null){
                    Intent intent = new Intent(GroupActivity.this,ProfileActivity.class);
                    intent.putExtra("uid",group.getUid());
                    startActivity(intent);
                }
            }
        });

        joinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                joinGroup(group,group.isHasJoined());
            }
        });

        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"Shared",Toast.LENGTH_LONG).show();
            }
        });

        findViewById(R.id.discussionBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initDiscussionPage();
            }
        });

    }

    private void initDiscussionPage(){
        Intent intent = new Intent(GroupActivity.this,GroupDiscussionActivity.class);
        intent.putExtra("gid",group.getGid());
        intent.putExtra("username",group.getName());
        intent.putExtra("profile",group.getProfPic());
        startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);

        init();

        try {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);

            group = new ModelGroup();
            group.setTime(getIntent().getStringExtra("time"));
            group.setUid(getIntent().getStringExtra("uid"));
            group.setName(getIntent().getStringExtra("name"));
            group.setImageUrl(getIntent().getStringExtra("imgurl"));
            group.setGid(getIntent().getStringExtra("gid"));
            group.setCategory(getIntent().getStringExtra("category"));
            group.setAbout(getIntent().getStringExtra("about"));
            group.setProfPic(getIntent().getStringExtra("profpic"));
            group.setUserBio(getIntent().getStringExtra("bio"));
            group.setUsername(getIntent().getStringExtra("username"));
            group.setHasJoined(getIntent().getBooleanExtra("joined",false));

            getSupportActionBar().setTitle(group.getName());

//            if(group.getUid().equalsIgnoreCase(DataHandler.getUserId(getApplicationContext()))){
//                owner= true;
//                findViewById(R.id.editGroupBtn).setVisibility(View.VISIBLE);
//
//                findViewById(R.id.editGroupBtn).setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Intent intent = new Intent(GroupActivity.this,EditGroupActivity.class);
//                        startActivity(intent);
//                    }
//                });
//                findViewById(R.id.joinBtn).setVisibility(View.GONE);
//            }

            if(group.isHasJoined()||owner) {
                findViewById(R.id.newGroupBtn).setVisibility(View.VISIBLE);

                findViewById(R.id.newGroupBtn).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(GroupActivity.this, NewPostActivity.class);
                        intent.putExtra("type", "research");
                        intent.putExtra("gid", group.getGid()+"");
                        startActivity(intent);
                    }
                });
            }else{
                findViewById(R.id.newGroupBtn).setVisibility(View.GONE);
            }

            title.setText(group.getName());
            Glide.with(getApplicationContext()).load(group.getImageUrl()).placeholder(R.drawable.downloading).into(mainImage);
            uname.setText(group.getUsername());
            if(group.getUserBio()==null||group.getUserBio().equalsIgnoreCase("null")||group.getUserBio().length()<1)
                about.setText("Bio details not provided by user");
            else
                about.setText(group.getUserBio());
            desc.setText(group.getAbout());

            if(group.isHasJoined())
                joinBtn.setText("Opt-out of group");

        }catch (Exception e){
            Toast.makeText(getApplicationContext(),e+"",Toast.LENGTH_LONG).show();
            Log.d("Res","Exception excs : "+e );
            e.printStackTrace();
        }
    }

    ///////////////////////////////////////////////////////////////////////




    @Override
    protected void onResume() {
        super.onResume();
        loadTrendingWork();
    }

    protected void loadTrendingWork(){
        Log.d("Resin","Resin : Load Trending");
        hideView(false,pbar);

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                NetworkHandler.fetchGroupPosts(getApplicationContext(),group.getGid(),new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.d("Resin","Resin : Load Trending onFailure : "+e);
                        hideView(true,pbar);
                        hideView(true,workRecycler);
                        if(e.toString().contains("java.net.SocketTimeoutException")){
                            loadTrendingWork();
                        }
                    }


                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String resp = response.body().string();
                        Log.d("Resin","Resin : Load Trending onResponse:"+resp);
                        hideView(true,pbar);

                        parseJson(resp);
                    }
                });
            }
        });
        thread.start();
    }

    protected void parseJson(String json){
        modelWorkList = new ArrayList<>();

        try{
            JSONObject jsonObject =new JSONObject(json);
            if(jsonObject.getInt("success")==1){
                JSONArray arr = jsonObject.getJSONArray("data");
                for(int i=0;i<arr.length();i++){
                    ModelWork w = ConverterJson.parseTrendingJson(arr.getJSONObject(i));
                    if(w!=null) {
                        modelWorkList.add(w);
                    }

                }

                showWorkRecycler(modelWorkList);
            }else{
                Log.d("Resin","Resin : Load Trending else part");
            }
        }catch (JSONException e){
            Log.d("Resin","Resin : Load Trending JSON Exception : "+e);
        }
    }

    protected void showWorkRecycler(final List<ModelWork> list){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(list!=null&&list.size()>0) {
                    workRecycler.setVisibility(View.VISIBLE);
                    RecyclerView.LayoutManager layman = new LinearLayoutManager(GroupActivity.this, LinearLayoutManager.VERTICAL, false);
                    adapterWork = new AdapterWork(GroupActivity.this, list);
                    adapterWork.setHorizontal(false);
                    workRecycler.setLayoutManager(layman);
                    workRecycler.setAdapter(adapterWork);
                }else{
                    workRecycler.setVisibility(View.GONE);
                }
            }
        });

    }


    protected void hideView(final boolean hide, final View view){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(hide)
                    view.setVisibility(View.GONE);
                else
                    view.setVisibility(View.VISIBLE);
            }
        });
    }

    ////////////////////////////////////////////////////////



    protected void joinGroup(final ModelGroup group,final boolean remove){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                NetworkHandler.joinGroup(getApplicationContext(), group, remove, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        toastOnMain("Join failed! Try again later");
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String resp = response.body().string();

                        try{
                            JSONObject object = new JSONObject(resp);
                            if(object.getInt("success")==1){
                                if(object.getString("reason").equalsIgnoreCase("deleted")) {
                                    group.setHasJoined(false);
                                    setButtonText("Join Group");
                                }
                                else {
                                    group.setHasJoined(true);
                                    setButtonText("Opt-Out");
                                }
                            }else{
                                toastOnMain("Request failed!");
                            }
                        }catch (JSONException e){
                            toastOnMain("Join failed!");
                        }
                    }
                });
            }
        });

        thread.start();
    }

    protected void setButtonText(final String text){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                joinBtn.setText(text);
            }
        });
    }


    protected void toastOnMain(final String msg){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_LONG).show();

            }
        });
    }




    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
