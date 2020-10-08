package com.resercho;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.signature.StringSignature;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputEditText;
import com.resercho.Adapters.AdapterComments;
import com.resercho.Adapters.AdapterWork;
import com.resercho.POJO.ModelJoinedGroups;
import com.resercho.POJO.ModelProfile;
import com.resercho.POJO.ModelWork;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static com.resercho.NetworkHandler.root_dir;
import static com.resercho.SignInActivity.TAG;

public class ProfileActivity extends AppCompatActivity {

    // Work
    RecyclerView workRecycler;
    AdapterWork adapterWork;
    List<ModelWork> modelWorkList;
    String upLoadServerUri = "https://resercho.com/linkapp/uploadFileUniv.php";

    TextView bioTv, eduTv, fullname,membersince,nopost, fcount,pcount,citecount,downTv,visitTv, webTv;
    ImageView profImg;
    String postCount, followCount, citeCount,downloadCount,visitCount;
    String uid;

    View commentBox;
    TextView noCommentsTv;
    AdapterComments commentAdapter;
    ProgressBar pbar,mainPbar;
    RecyclerView commentRv;

    final static int REQ_PER_READ_WRITE = 1107;
    final static int REQ_NEW_PROFILE = 1007;

    Button followBtn;
    boolean follow = false;

    View editFullname;
    boolean editEnabled = false;
    String profileUri;
    Handler uploadHandler;

    ModelProfile mainProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_v4);


        uid = getIntent().getStringExtra("uid");
        if(uid==null){
            getSupportActionBar().setTitle("Your Profile");
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }else {
            try {

                getSupportActionBar().setTitle("");
                getSupportActionBar().setHomeButtonEnabled(true);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);

                if (uid.equalsIgnoreCase(DataHandler.getUserId(getApplicationContext()))) {
                editEnabled = true;
                getSupportActionBar().setTitle("Your Profile");
                }

            } catch (Exception e) {

            }
        }

        uploadHandler = new Handler();
        init();
        loadProfileWork();
    }




    protected void saveProfilePhoto(Uri uri){
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            byte[] buffer = new byte[inputStream.available()];

            inputStream.read(buffer);

            File f = new File(Environment.getExternalStorageDirectory() + File.separator + "Profile_"+DataHandler.getUserId(getApplicationContext())+".jpg");

            OutputStream outStream = new FileOutputStream(f);
            outStream.write(buffer);

            Log.d("Res","Proftik REQ-VID : " + f.getName() + " aaa :"+f.getAbsolutePath());
            profileUri = f.getAbsolutePath();
            uploadThisMedia(profileUri,REQ_NEW_PROFILE);
        }catch (Exception e){
            Log.d("Res","Proftik REQ-VID : Exception : "+e);
        }
    }

    protected void uploadThisMedia(final String uri,final int req){

        Toast t = Toast.makeText(getApplicationContext(),"Updating...",Toast.LENGTH_LONG);
        t.setGravity(Gravity.CENTER,0,0);
        t.show();

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Intent intent =CommonMethods.postData(upLoadServerUri,uri);
                intent.putExtra("req",req);
                handleResponse(intent);
            }
        });
        thread.start();
    }

    protected void handleResponse(Intent receivedIntent){
        if (receivedIntent.getExtras().getInt("resp_code") == 200) {

            String file = receivedIntent.getStringExtra("filepath");
            File f = new File(getExternalFilesDir(null) + File.separator+ file);
            CommonMethods.deleteFile(f,getApplicationContext());
            sendUpdateReq(UpdateActivity.PROFILE,file);
            Log.d("Proftik","Profile Path= "+receivedIntent.getExtras().getString("filepath"));
        }else{
            Log.d("Proftik","Profile response serverResponseCode="+receivedIntent.getExtras().getInt("resp_code"));
        }
    }

    protected void sendUpdateReq(final String key,final String val){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                NetworkHandler.updateDbField(getApplicationContext(), key, "app_res/"+val, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.d("Proftik","Profile onFailure : "+e);
                        if(e.toString().contains("java.net.SocketTimeoutException")){
                            sendUpdateReq(key,val);
                        }
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String resp = response.body().string();
                        Log.d("Proftik","Profile onResponse : "+resp);
                        try{
                            JSONObject object = new JSONObject(resp);

                            if(object.getInt("success")==1){
                                toastOnMain("Profile picture updated");
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        loadNewProf();
                                    }
                                });

                            }else{
                                toastOnMain("Profile failed to update");
                            }

                        }catch (JSONException e){
                            toastOnMain("Profile failed to update");
                        }
                    }
                });
            }
        });

        thread.start();
    }

    protected void loadNewProf(){
        Glide.with(getApplicationContext())
                .load(mainProfile.getProfUrl())
                .placeholder(ContextCompat.getDrawable(getApplicationContext(),R.drawable.downloading))
                .signature(new StringSignature(System.currentTimeMillis()+""))
                .into(profImg);
    }

    private void init(){
        workRecycler = findViewById(R.id.workRecycler);
        bioTv = findViewById(R.id.bioTv);
        eduTv = findViewById(R.id.educationTv);
        webTv = findViewById(R.id.website);
        fullname = findViewById(R.id.fullName);
        membersince = findViewById(R.id.membersince);
        profImg = findViewById(R.id.profileImg);
        nopost = findViewById(R.id.nopostmsg);
        followBtn = findViewById(R.id.followBtn);
        mainPbar = findViewById(R.id.postPbar);

        fcount = findViewById(R.id.followCntNew);
        pcount = findViewById(R.id.postCntNew);
        citecount = findViewById(R.id.citeCount);
        downTv = findViewById(R.id.downCount);
        visitTv = findViewById(R.id.visitCount);

        pbar = findViewById(R.id.comPbar);
        commentRv = findViewById(R.id.commentRv);
        noCommentsTv = findViewById(R.id.noCommentsMsg);
        commentBox = findViewById(R.id.homeComment);

        fcount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(ProfileActivity.this,FollowerActivity.class);
                startActivity(in);
            }
        });

        if(editEnabled)
            editSequence();
        else{
            followBtn.setVisibility(View.VISIBLE);

            followBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    sendFollow(uid);
                }
            });

            findViewById(R.id.chatbtn).setVisibility(View.VISIBLE);
            findViewById(R.id.chatbtn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mainProfile!=null) {
                        Intent intent = new Intent(ProfileActivity.this, ChatWindowActivity.class);
                        intent.putExtra("uid", uid);
                        intent.putExtra("username",mainProfile.getUname() );
                        intent.putExtra("profile", mainProfile.getProfUrl());
                        startActivity(intent);
                    }
                }
            });



            ////////////////////////////////////////////////////
            findViewById(R.id.editBio).setVisibility(View.GONE);
            findViewById(R.id.editEdu).setVisibility(View.GONE);
            findViewById(R.id.editWeb).setVisibility(View.GONE);
            findViewById(R.id.editFullname).setVisibility(View.GONE);
            findViewById(R.id.editProfPic).setVisibility(View.GONE);
        }





    }

    protected void editSequence(){

        followBtn.setVisibility(View.GONE);



        followBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendFollow(uid);
            }
        });

        editFullname = findViewById(R.id.editFullname);
        followBtn.setVisibility(View.GONE);
        editFullname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this,UpdateActivity.class);
                intent.putExtra("key",UpdateActivity.FULLNAME);
                intent.putExtra("val",fullname.getText().toString());
                startActivity(intent);
            }
        });


        findViewById(R.id.editBio).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this,UpdateActivity.class);
                intent.putExtra("key",UpdateActivity.BIO);
                intent.putExtra("val",bioTv.getText().toString());
                startActivity(intent);
            }
        });

        findViewById(R.id.editProfPic).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickImage(REQ_NEW_PROFILE);
            }
        });

        findViewById(R.id.editEdu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this,UpdateActivity.class);
                intent.putExtra("key",UpdateActivity.EDUCATION1);
                intent.putExtra("val",eduTv.getText().toString());
                startActivity(intent);
            }
        });

        findViewById(R.id.editWeb).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this,UpdateActivity.class);
                intent.putExtra("key",UpdateActivity.WEBSITE);
                intent.putExtra("val",webTv.getText().toString());
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            if(requestCode == REQ_NEW_PROFILE){
                saveProfilePhoto(data.getData());
            }
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
//        loadProfileWork();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!uid.equalsIgnoreCase(DataHandler.getUserId(getApplicationContext())))
            sendProfileView();
    }

    protected void sendProfileView(){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                NetworkHandler.sendProfileView(ProfileActivity.this, uid, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.d("Sttat","Sttat : onFailrel: "+e);
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String resp = response.body().string();
                        Log.d("Sttat","Sttat : onSuccess: "+resp);
                    }
                });
            }
        });

        thread.start();
    }

    protected void noPostActivate(final boolean act){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(act){
                    nopost.setVisibility(View.VISIBLE);
                    workRecycler.setVisibility(View.GONE);
                }else{
                    nopost.setVisibility(View.GONE);
                    workRecycler.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    protected void loadProfileWork(){
        Log.d("proft","UID:"+uid);
        showPbar(true);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                NetworkHandler.fetchUserPost(ProfileActivity.this,uid,new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.d("Resercho","HomeFrag : onFailure::"+e);
                        showPbar(false);
                        if(e.toString().contains("java.net.SocketTimeoutException")){
                            loadProfileWork();
                        }
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
        Log.d("proft","onresponse: "+ json);
        modelWorkList = new ArrayList<>();
        try{
            JSONObject jsonObject =new JSONObject(json);
            if(jsonObject.getInt("success")==1){

                String about = jsonObject.getString("about");
                String website = jsonObject.getString("website");
                String education = jsonObject.getString("education");
                String fname = jsonObject.getString("fullname");
                String since = jsonObject.getString("since");
                String profurl = root_dir + jsonObject.getString("prof");

                follow = jsonObject.getInt("follow")==1;
                followCount = jsonObject.getString("followers");
                postCount = jsonObject.getString("posts");
                visitCount = jsonObject.getString("visits");
                citeCount= jsonObject.getString("cites");
                downloadCount= jsonObject.getString("downloads");

                setCounts(followCount,postCount,citeCount,downloadCount,follow,visitCount);

                if(since!=null&&since.length()>0 &&(!since.equalsIgnoreCase("null")))
                    since = since.substring(0,4);

                setUname(fname,about,website,education,since,profurl);


                JSONArray arr = jsonObject.getJSONArray("data");

                for(int i=0;i<arr.length();i++){
                    ModelWork w = ConverterJson.parseTrendingJson(arr.getJSONObject(i));
                    if(w!=null) {
                        modelWorkList.add(w);
                        Log.d("Resercho","HomeFrag : Iteration : "+i+ " W:"+w.getId());
                    }
                    else
                        Log.d("Resercho","HomeFrag : Null Model : "+i);
                }

                if(!(arr.length()>0))
                    noPostActivate(true);

                showWorkRecycler(modelWorkList);
            }else{
                Log.d("proft","onresponse: Not succesful "+ json);

                Log.d("Resercho","HomeFrag : Request Success:"+jsonObject.getInt("success"));
            }
        }catch (JSONException e){
            Log.d("proft","onresponse:  JSON Exception "+ json);

            Log.d("Resercho","HomeFrag : JSON Exceptioin:"+e);
        }
        showPbar(false);
    }

    protected void setUname(final String fname, final String about, final String website, final String educations, final String since, final String profurl){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                Log.d("Dikkun","Bio about : "+about + " Edu : "+educations + " Web : "+website);
                ModelProfile modelProfile = new ModelProfile();
                modelProfile.setSince(since);
                modelProfile.setBio(about);
                modelProfile.setProfUrl(profurl);
                modelProfile.setUname(fname);
                modelProfile.setEducation(educations);

                mainProfile = modelProfile;

                Log.d("proft","setUname: "+ fname);



                Glide.with(getApplicationContext())
                        .load(profurl)
                        .placeholder(ContextCompat.getDrawable(getApplicationContext(),R.drawable.downloading))
                        .signature(new StringSignature(System.currentTimeMillis()+""))
                        .into(profImg);


                fullname.setText(fname);
                membersince.setText("Member since "+since);

                if(about!=null&&about.length()>0&&(!about.equalsIgnoreCase("null"))) {
                    bioTv.setText(about);
                }
                else {
                    bioTv.setText("No bio details");
                }

                if(educations!=null) {
                    String education = educations.trim();

                    if (education != null && education.length() > 1 && (!education.equalsIgnoreCase("null"))) {
                        eduTv.setText(education);
                    }
                    else {
                        eduTv.setText("No education details");
                    }
                }

                if(website!=null&&website.length()>0&&(!website.equalsIgnoreCase("null"))){
                    webTv.setText(website);
                    modelProfile.setWebsite(website);
                }else{
                    webTv.setText("No website provided");
                    modelProfile.setWebsite("No website provided");
                }
            }
        });
    }

    protected void setCounts(final String follow, final String post, final String cite, final String downloads,final boolean isFollowed,final String visits){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                fcount.setText(follow+" Followers");
                pcount.setText(post + " Posts");

                if(uid.equalsIgnoreCase(DataHandler.getUserId(getApplicationContext()))) {
                    citecount.setText(cite + " Citations");
                    downTv.setText(downloads + " Downloads");
                    visitTv.setText(visits + "  Views");
                }else{
                    findViewById(R.id.stats).setVisibility(View.GONE);
                }

                setFollowBtn(isFollowed);

            }
        });
    }


    protected void sendFollow(final String fid){

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                NetworkHandler.sendFollow(getApplicationContext(), !follow, fid, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.d(TAG,"sendfollow onFailure:"+e);
                        toastOnMain("Follow Failed!");
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String resp = response.body().string();

                        Log.d(TAG,"sendfollow onResponse:"+resp);
                        try{
                            JSONObject object = new JSONObject(resp);
                            if(object.getString("reason").equalsIgnoreCase("followed")){
                                follow = true;

                               setFollowBtn(follow);
                            }else if(object.getString("reason").equalsIgnoreCase("unfollowed")){
                                follow = false;
                                        setFollowBtn(follow);
                            }else if(object.getString("reason").equalsIgnoreCase("exists")){
                                toastOnMain("Already following...");
                            }

                        }catch (JSONException e){
                            toastOnMain("Follow failed!");
                        }
                    }
                });
            }
        });

        thread.start();
    }

    protected void setFollowBtn(final boolean isFollow){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(!uid.equalsIgnoreCase(DataHandler.getUserId(getApplicationContext())))
                    followBtn.setVisibility(View.VISIBLE);
                if(isFollow){
                    followBtn.setText("Unfollow");
                }else{
                    followBtn.setText("Follow");
                }
            }
        });

    }


    protected void showWorkRecycler(final List<ModelWork> list){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                RecyclerView.LayoutManager layman = new LinearLayoutManager(ProfileActivity.this,LinearLayoutManager.VERTICAL,false);
                adapterWork = new AdapterWork(ProfileActivity.this,list);
                adapterWork.setHorizontal(false);
                adapterWork.setProfileAct(true);
                workRecycler.setLayoutManager(layman);
                workRecycler.setAdapter(adapterWork);
                adapterWork.notifyDataSetChanged();
                workRecycler.setVisibility(View.VISIBLE);
            }
        });
        Log.d("Resercho","Dashboard Work showRv : "+list.size());

    }




    private void showCpbar(final boolean show){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(show){
                    findViewById(R.id.commentSendPbar).setVisibility(View.VISIBLE);
                    findViewById(R.id.commentSendBtn).setVisibility(View.GONE);
                }else{
                    findViewById(R.id.commentSendPbar).setVisibility(View.GONE);
                    findViewById(R.id.commentSendBtn).setVisibility(View.VISIBLE);
                }
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
    protected void sendComment(final ModelJoinedGroups.ModelComment comment){
        showCpbar(true);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                NetworkHandler.sendComment(getApplicationContext(), comment, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        showCpbar(false);
                        toastOnMain("Comment failed");
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String resp = response.body().string();
                        try{
                            JSONObject jsonObject = new JSONObject(resp);
                            if(jsonObject.getInt("success") == 1){
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if(commentBox.getVisibility() == View.VISIBLE) {
                                            showNoComments(false);
                                            fetchComments(comment.getPid());
//                                            commentAdapter.addComment(comment);
                                        }

                                    }
                                });

                            }else{
                                toastOnMain("Comment failed");
                            }
                        }catch (JSONException e){
                            Log.d("GSE","JSON Exception :"+e);
                        }
                        showCpbar(false);
                    }
                });
            }
        });

        thread.start();
    }


    public void showCommentBox(String pid){
        showNoComments(false);
        Log.d("Reser","Comik  show comment:"+pid);
        showCommentRv(new ArrayList<ModelJoinedGroups.ModelComment>());
        fetchComments(pid);
        commentBox.setTranslationY(500);
        commentBox.setAlpha(0.0f);
        commentBox.setVisibility(View.VISIBLE);
        commentBox.animate().translationY(0).alpha(1.0f).setDuration(120).setListener(null).start();

        commenting(pid);
    }

    protected void fetchComments(final String pid){
        showComPbar(true);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                NetworkHandler.fetchComments(getApplicationContext(), pid, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.d("Res","Comet onFailure:"+e);
                        showComPbar(false);
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String resp = response.body().string();

                        Log.d("Res","Comet onResponse:"+resp);
                        parseCommentJson(resp);
                    }
                });
            }
        });

        thread.start();
    }

    protected void parseCommentJson(String resp){
        List<ModelJoinedGroups.ModelComment> commentList = new ArrayList<>();
        try{
            JSONObject jsonObject = new JSONObject(resp);
            if(jsonObject.getInt("success")==1){
                JSONArray array = jsonObject.getJSONArray("data");
                for(int i=0;i<array.length();i++){
                    ModelJoinedGroups.ModelComment comment = ConverterJson.parseCommentson(array.getJSONObject(i));
                    commentList.add(comment);
                }

                if(commentList.size()>0){
                    showNoComments(false);
                    showCommentRv(commentList);
                }else{
                    showNoComments(true);
                }
            }else{
                toastOnMain("There has been a problem fetching the comments");
            }
        }catch (JSONException e){
            Log.d("Res","Comet JSON Exception : "+e);
            toastOnMain("There has been a problem fetching the comments");
        }
        showComPbar(false);
    }

    protected void showNoComments(final boolean show){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(show){
                    noCommentsTv.setVisibility(View.VISIBLE);
                }else{
                    noCommentsTv.setVisibility(View.GONE);
                }
            }
        });
    }
    protected void commenting(final String pid){

        final TextInputEditText commentEdit = findViewById(R.id.commentEdit);

        findViewById(R.id.commentSendBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(commentEdit!=null&&commentEdit.getText()!=null&&commentEdit.getText().toString().length()>0){
                    ModelJoinedGroups.ModelComment comment = new ModelJoinedGroups.ModelComment();
                    comment.setPid(pid);
                    comment.setComment(commentEdit.getText().toString());
                    comment.setComname(DataHandler.getDisplayName(getApplicationContext())+"");
                    commentEdit.setText("");
                    sendComment(comment);
                }else{
                    Toast.makeText(getApplicationContext(),"Type a comment to send",Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    protected void showComPbar(final boolean show){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(show)
                    pbar.setVisibility(View.VISIBLE);
                else
                    pbar.setVisibility(View.GONE);
            }
        });
    }
    private void showCommentRv(final List<ModelJoinedGroups.ModelComment> list){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(list!=null) {
                    commentAdapter = new AdapterComments(ProfileActivity.this, list);
                    LinearLayoutManager layman = new LinearLayoutManager(ProfileActivity.this, RecyclerView.VERTICAL, false);
                    commentRv.setLayoutManager(layman);
                    commentRv.setAdapter(commentAdapter);
                }
            }
        });
    }
    protected void showPbar(final boolean show){

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(show){
                    mainPbar.setVisibility(View.VISIBLE);
                }else{
                    mainPbar.setVisibility(View.GONE);
                }
            }
        });

    }

    public void pickImage(int REQUEST_CODE) {
        if(checkForStoragePermissions()) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            Intent chooser = Intent.createChooser(intent, "Choose your app");
            startActivityForResult(chooser,REQUEST_CODE);
//
        }else{
            askForStoragePermission();
        }
    }
    public boolean checkForStoragePermissions(){
        if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED)
            return true;
        else
            return false;
    }

    public void askForStoragePermission(){
        ActivityCompat.requestPermissions(ProfileActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE},REQ_PER_READ_WRITE);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
