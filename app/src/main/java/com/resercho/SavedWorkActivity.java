package com.resercho;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.resercho.Adapters.AdapterComments;
import com.resercho.Adapters.AdapterSaved;
import com.resercho.POJO.ModelJoinedGroups;
import com.resercho.POJO.ModelSaved;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class SavedWorkActivity extends AppCompatActivity {

    List<ModelSaved> savedList;
    RecyclerView savedRv;
    AdapterSaved adapterSaved;

    View pbaar;
    View commentBox;
    TextView noCommentsTv;
    AdapterComments commentAdapter;
    ProgressBar pbar;
    RecyclerView commentRv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_work);

            try{
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setHomeButtonEnabled(true);
                getSupportActionBar().setTitle("Saved");
            }catch (Exception e){

            }

        savedRv = findViewById(R.id.savedRv);
        pbaar = findViewById(R.id.pbar);
        pbar = findViewById(R.id.comPbar);
        commentRv = findViewById(R.id.commentRv);
        noCommentsTv = findViewById(R.id.noCommentsMsg);
        commentBox = findViewById(R.id.homeComment);

        findViewById(R.id.commentClose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.homeComment).animate().translationY(400).alpha(0.0f).setDuration(140).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        findViewById(R.id.homeComment).setVisibility(View.GONE);
                    }
                }).start();
            }
        });

        fetchSaved();
    }

    @Override
    public void onBackPressed() {
        if(commentBox.getVisibility() == View.VISIBLE) {
            commentBox.setVisibility(View.GONE);
        }
        else
            finish();
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
                    commentAdapter = new AdapterComments(SavedWorkActivity.this, list);
                    LinearLayoutManager layman = new LinearLayoutManager(SavedWorkActivity.this, RecyclerView.VERTICAL, false);
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
                    pbaar.setVisibility(View.VISIBLE);
                }else{
                    pbaar.setVisibility(View.GONE);
                }
            }
        });

    }
    @Override
    protected void onRestart() {
        super.onRestart();
        fetchSaved();
    }

    protected void fetchSaved(){
        showPbar(true);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                NetworkHandler.fetchSavedWork(getApplicationContext(), new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.d("saved","savek onFailure:"+e);

                        toastOnMain("Failed loaodingn");
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

        protected void toastOnMain(final String msg){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_LONG).show();
                }
            });
        }

    protected void parseJson(String resp){
        savedList = new ArrayList<>();
        try{

            JSONObject object = new JSONObject(resp);

            if(object.getInt("success")==1){
                JSONArray data = object.getJSONArray("data");

                for(int i =0; i<data.length();i++){
                    ModelSaved saved = ConverterJson.parseSaveJson(data.getJSONObject(i));

                    if(saved!=null) {
                        savedList.add(saved);
                    }
                }
            }

            showSavedRecycller(savedList);
        }catch (JSONException e){
            Log.d("saved","savek JSON Exception:"+resp);
            toastOnMain("JSON");
        }
    }


    protected void showSavedRecycller(final List<ModelSaved> list){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(list!=null && list.size()>0) {
                    RecyclerView.LayoutManager layman = new LinearLayoutManager(SavedWorkActivity.this, LinearLayoutManager.VERTICAL, false);
                    adapterSaved = new AdapterSaved(SavedWorkActivity.this, list);
                    savedRv.setLayoutManager(layman);
                    savedRv.setAdapter(adapterSaved);
                }
            }
        });
        Log.d("Resercho","Dashboard Story showRv : "+list.size());

    }


    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
