package com.resercho;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;
import com.resercho.SearchPeopleFrag;
import com.resercho.Adapters.AdapterComments;
import com.resercho.Adapters.CustomViewPager;
import com.resercho.Adapters.ViewPagerAdapter;
import com.resercho.POJO.ModelJoinedGroups;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class SearchActivity extends AppCompatActivity {

    ViewPagerAdapter adapter;
    public CustomViewPager viewPager;
    private TabLayout tabLayout;

    EditText searchEdit;
    ImageButton searchBtn;

    // Posts/Groups/People/Events

    // Nav
    TextView noCommentsTv;
    View commentBox;
    AdapterComments commentAdapter;
    RecyclerView commentRv;
    ProgressBar pbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

//        initTops();

        initViewPager();

        searchEdit = findViewById(R.id.searchKey);
        searchBtn = findViewById(R.id.searchBtn);

        commentBox = findViewById(R.id.homeComment);
        commentBox.setOnClickListener(null);
        noCommentsTv = commentBox.findViewById(R.id.noCommentsMsg);
        commentRv = findViewById(R.id.commentRv);
        pbar = findViewById(R.id.comPbar);

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


        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(searchEdit!=null&searchEdit.getText()!=null&&searchEdit.getText().toString().length()>0){
                    ((PostSearchFrag)adapter.getItem(0)).loadTrendingWork(searchEdit.getText().toString());
                    ((PostSearchFrag)adapter.getItem(1)).loadTrendingWork(searchEdit.getText().toString());
                    ((SearchPeopleFrag)adapter.getItem(2)).loadSuggestedWork(searchEdit.getText().toString());
                    ((SearchGroupFrag)adapter.getItem(3)).loadGroupData(searchEdit.getText().toString());
                    ((SearchEventFrag)adapter.getItem(4)).loadEvents(searchEdit.getText().toString());
                }else{

                }
            }
        });

    }
    private void initTops(){
        try{
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setTitle("Search");
        }catch (Exception e){}
    }

    private void initViewPager(){
        viewPager = findViewById(R.id.homeViewPager);
        tabLayout = findViewById(R.id.tabs);

        viewPager.setOffscreenPageLimit(4);
        viewPager.disableScroll(true);
        adapter = new ViewPagerAdapter(getSupportFragmentManager());

        // FIRST FRAG
        PostSearchFrag discFrag = new PostSearchFrag();
        adapter.AddFragment(discFrag,"Posts");

        PostSearchFrag workFrag = PostSearchFrag.newInstance(true);
        adapter.AddFragment(workFrag,"Works");

        SearchPeopleFrag peopleFrag = new SearchPeopleFrag();
        adapter.AddFragment(peopleFrag,"People");

        SearchGroupFrag groupFrag = new SearchGroupFrag();
        adapter.AddFragment(groupFrag,"Groups");


        SearchEventFrag eventFrag = new SearchEventFrag();
        adapter.AddFragment(eventFrag,"Events");

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        try {
            tabLayout.getTabAt(0).setCustomView(R.layout.item_tab_posts);
            tabLayout.getTabAt(1).setCustomView(R.layout.item_tab_works);
            tabLayout.getTabAt(2).setCustomView(R.layout.item_tab_people);
            tabLayout.getTabAt(3).setCustomView(R.layout.item_tab_groups);
            tabLayout.getTabAt(4).setCustomView(R.layout.item_tab_events);
        }catch (NullPointerException e){
            e.printStackTrace();
        }

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

    private void toastOnMain(final String msg){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showCommentRv(final List<ModelJoinedGroups.ModelComment> list){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(list!=null) {
                    commentAdapter = new AdapterComments(SearchActivity.this, list);
                    LinearLayoutManager layman = new LinearLayoutManager(SearchActivity.this, RecyclerView.VERTICAL, false);
                    commentRv.setLayoutManager(layman);
                    commentRv.setAdapter(commentAdapter);
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


}
