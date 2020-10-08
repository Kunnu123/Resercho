package com.resercho;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.resercho.POJO.ModelGroup;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class GroupViewerActivity extends AppCompatActivity {


    ModelGroup group;


    ImageView mainImage,profImage;
    TextView title,uname,about,desc;
    Button joinBtn;
    View shareBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_viewer2);


        mainImage = findViewById(R.id.postImage);
        profImage = findViewById(R.id.userProfPic);
        title = findViewById(R.id.title);
        uname = findViewById(R.id.username);
        about = findViewById(R.id.uabout);
        desc = findViewById(R.id.description);
        joinBtn = findViewById(R.id.joinBtn);


        shareBtn = findViewById(R.id.shareBtn);


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

            redirect();
            getSupportActionBar().setTitle(group.getName());



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


        uname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(group!=null){
                    Intent intent = new Intent(GroupViewerActivity.this,ProfileActivity.class);
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

    }





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
                                    // Redirect to GroupActivity
                                    group.setHasJoined(true);
//                                    setButtonText("Opt-Out");
                                    redirect();
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
    protected void redirect(){
        if(group.isHasJoined()){
            Intent intent = new Intent(getIntent());
            intent.putExtra("joined",true);
            Intent start = new Intent(GroupViewerActivity.this,GroupActivity.class);
            start.putExtras(intent);
            startActivity(start);
            finish();
        }
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
