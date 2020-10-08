package com.resercho;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.resercho.Adapters.AdapterComments;
import com.resercho.Adapters.CustomViewPager;
import com.resercho.Adapters.ViewPagerAdapter;
import com.resercho.POJO.ModelJoinedGroups;
import com.resercho.UIPages.CollabFrag;
import com.resercho.UIPages.DiscoverFrag;
import com.resercho.UIPages.EventFrag;
import com.resercho.UIPages.HomeFrag;
import com.resercho.UIPages.UploadFrag;
import com.resercho.UIPages.VideoFrag;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class HomeActivity extends AppCompatActivity {

    ViewPagerAdapter adapter;
    public CustomViewPager viewPager;

    DrawerLayout drawer;
    NavigationView navigationView;
    ActionBarDrawerToggle toggle;
    GoogleSignInClient mGoogleSignInClient;
    View navHeader;
    ProgressBar pbar;
    AdapterComments commentAdapter;

    public static int screen_height,screen_width,screen_orientation;

    // Nav
    TextView navUname, navFname,navMode,noCommentsTv;
    ImageView navProfile;
    View commentBox;
    RecyclerView commentRv;

    int mode;
    public boolean newpost = false;
    public int newpostid = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mode = DataHandler.getMode(getApplicationContext());

        setNavigationAndDrawer();
        googleAuth();
        screenDims();

        init();
        setBottomBar();

        checkNewPost();
        Log.d("HomeActivity","UID :"+ DataHandler.getUserId(getApplicationContext()));
    }

    protected void checkNewPost(){
        if(getIntent().getBooleanExtra("newpost",false)){
            int pid = getIntent().getIntExtra("pid",-1);
            newpostid = pid;
            newpost = true;
            Toast.makeText(getApplicationContext(),"New Post Up : PID "+pid,Toast.LENGTH_LONG).show();
        }
    }

    protected void screenDims(){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screen_height = displayMetrics.heightPixels;
        screen_width = displayMetrics.widthPixels;

        Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        screen_orientation = display.getRotation();
    }


    protected void setNavigationAndDrawer(){
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);

        toggle.syncState();
        NavigationView navigationView = findViewById(R.id.nav_view);


        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id = menuItem.getItemId();
                switch (id){
                    case R.id.nav_logout:
                        signOut();break;
                    case R.id.nav_settings:
                        Intent intent = new Intent(HomeActivity.this,SettingsActivity.class);
                        startActivity(intent);break;

                    case R.id.nav_groups:
                        Intent intent1 = new Intent(HomeActivity.this, JoinedGroupActivity.class);
                        startActivity(intent1);break;

                    case R.id.nav_events:
                        Intent intent2 = new Intent(HomeActivity.this, MyEventsActivity.class);
                        startActivity(intent2);break;

                    case R.id.nav_profile:
                        Intent intent4 = new Intent(HomeActivity.this,ProfileActivity.class);
                        intent4.putExtra("uid",DataHandler.getUserId(getApplicationContext()));
                        startActivity(intent4);
                        break;

                    case R.id.nav_saved:
                        Intent intent5 = new Intent(HomeActivity.this,SavedWorkActivity.class);
                        startActivity(intent5);
                        break;
                        default:
                            Toast.makeText(getApplicationContext(),"No items",Toast.LENGTH_SHORT).show();
                }

                drawer.closeDrawers();
                return true;
            }


        });
    }

    protected void setBottomBar(){

        findViewById(R.id.tab1Cont).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setThisTabActive(R.id.tab1Cont);
                viewPager.setCurrentItem(2,false);
            }
        });
        findViewById(R.id.tab2Cont).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setThisTabActive(R.id.tab2Cont);
                viewPager.setCurrentItem(0,false);
            }
        });
        findViewById(R.id.tab3Cont).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setThisTabActive(R.id.tab3Cont);
                viewPager.setCurrentItem(1,false);
            }
        });
        findViewById(R.id.tab4Cont).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setThisTabActive(R.id.tab4Cont);
                viewPager.setCurrentItem(3,false);
            }
        });
        findViewById(R.id.tab5Cont).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setThisTabActive(R.id.tab5Cont);
                viewPager.setCurrentItem(4,false);
            }
        });
    }

    boolean allset = false;

    @Override
    protected void onPause() {
        super.onPause();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(allset) {
                    setOnline(false);
                    Log.d("ToggleOnline","All Set If Part : "+allset);
                }
                else {
                    Log.d("ToggleOnline", "All Set Else Part : " + allset);
                }
            }
        },500);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("ToggleOnline","onStop");
        allset = true;
    }

    @Override
    protected void onDestroy() {
        Log.d("ToggleOnline","onDestroyed");
        super.onDestroy();
    }



    protected void setThisTabActive(int id){
        refreshTabs();

        View view = findViewById(id);
        view.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.WhiteSmoke));
        view.setElevation(2.0f);
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

    protected void refreshTabs(){
        int disablecolor = R.color.colorTransparent;

        findViewById(R.id.tab1Cont).setElevation(0.0f);
        findViewById(R.id.tab2Cont).setElevation(0.0f);
        findViewById(R.id.tab3Cont).setElevation(0.0f);
        findViewById(R.id.tab4Cont).setElevation(0.0f);
        findViewById(R.id.tab5Cont).setElevation(0.0f);

        findViewById(R.id.tab1Cont).setBackgroundColor(ContextCompat.getColor(getApplicationContext(),disablecolor));
        findViewById(R.id.tab2Cont).setBackgroundColor(ContextCompat.getColor(getApplicationContext(),disablecolor));
        findViewById(R.id.tab3Cont).setBackgroundColor(ContextCompat.getColor(getApplicationContext(),disablecolor));
        findViewById(R.id.tab4Cont).setBackgroundColor(ContextCompat.getColor(getApplicationContext(),disablecolor));
        findViewById(R.id.tab5Cont).setBackgroundColor(ContextCompat.getColor(getApplicationContext(),disablecolor));
    }

    protected void initNav(){
        navFname.setText(DataHandler.getDisplayName(getApplicationContext()));
        if(DataHandler.getUsername(getApplicationContext())!=null)
            navUname.setText(DataHandler.getUsername(getApplicationContext()));
        else
            navUname.setText(DataHandler.getDisplayName(getApplicationContext()));
        navMode.setText(DataHandler.getModeName(getApplicationContext()));

        Log.d("debit","Profile : "+ DataHandler.getProfilePic(getApplicationContext()));

        Glide.with(HomeActivity.this)
                .load(DataHandler.getProfilePic(getApplicationContext())
                ).placeholder(R.drawable.downloading)
                .into(navProfile);


    }

    @Override
    protected void onResume() {
        super.onResume();
        initNav();
        if(mode!=DataHandler.getMode(getApplicationContext())){
            Handler handler = new Handler();
            handler.postDelayed(new Runnable()
            {
                @Override
                public void run()
                {
                    recreate();
                }
            }, 1);
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();

    }

    //    protected void fetchProfile(){
//        Thread thread = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                NetworkHandler.fetchSingleProfile(getApplicationContext(), new Callback() {
//                    @Override
//                    public void onFailure(Call call, IOException e) {
//                        Log.d("profilic","profilic onFailre:"+e);
//                    }
//
//                    @Override
//                    public void onResponse(Call call, Response response) throws IOException {
//                        String resp = response.body().string();
//                        Log.d("profilic","profilic onResp:"+resp);
//
//                        try{
//                            JSONObject jsonObject = new JSONObject(resp);
//                            if(jsonObject.getInt("success")==1){
//
//                            }else{
//                                Log.d("profilic","profilic onResp: Else Part");
//                            }
//                        }catch (JSONException e){
//                            Log.d("profilic","profilic onResp: JSON Exception:"+e);
//                        }
//                    }
//                });
//            }
//        });
//
//        thread.start();
//    }

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
                    commentAdapter = new AdapterComments(HomeActivity.this, list);
                    LinearLayoutManager layman = new LinearLayoutManager(HomeActivity.this, RecyclerView.VERTICAL, false);
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

    protected void init(){

         navigationView = (NavigationView) findViewById(R.id.nav_view);

         navHeader = navigationView.getHeaderView(0);

         commentBox = findViewById(R.id.homeComment);
         commentBox.setOnClickListener(null);
         noCommentsTv = commentBox.findViewById(R.id.noCommentsMsg);
         commentRv = findViewById(R.id.commentRv);
         pbar = findViewById(R.id.comPbar);

        navFname = navHeader.findViewById(R.id.navUserFname);
        navUname = navHeader.findViewById(R.id.navUsername);
        navProfile = navHeader.findViewById(R.id.navProfImage);
        navMode = navHeader.findViewById(R.id.modeName);

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

        navMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this,SettingsActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.floatingNewBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(HomeActivity.this,UploadActivity.class);
//                startActivity(intent);

                Intent intent = new Intent(HomeActivity.this, NewPostActivity.class);
                intent.putExtra("type","research");
                startActivity(intent);
            }
        });


        ///////
        viewPager = findViewById(R.id.homeViewPager);

//        viewPager.setOffscreenPageLimit(4);
        viewPager.disableScroll(true);


        adapter = new ViewPagerAdapter(getSupportFragmentManager());

        // FIRST FRAG
        DiscoverFrag discFrag = new DiscoverFrag();
        discFrag.setArguments(getbundle(false));
        adapter.AddFragment(discFrag,"Discover");

        CollabFrag collabFrag = new CollabFrag();
        collabFrag.setArguments(getbundle(false));
        adapter.AddFragment(collabFrag,"Collaborate");

        HomeFrag homeFrag = new HomeFrag();
        homeFrag.setArguments(getbundle(false));
        adapter.AddFragment(homeFrag,"Home");

        VideoFrag uploadFrag = new VideoFrag();
        uploadFrag.setArguments(getbundle(false));
        adapter.AddFragment(uploadFrag,"Video");

        EventFrag eventFrag = new EventFrag();
        eventFrag.setArguments(getbundle(false));
        adapter.AddFragment(eventFrag,"Events");

        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(2);
        setThisTabActive(R.id.tab1Cont);

    }

    @Override
    protected void onStart() {
        super.onStart();
        setOnline(true);
    }

    public void createNewPost(){
        Intent intent = new Intent(HomeActivity.this, NewPostActivity.class);
        intent.putExtra("type","research");
        startActivity(intent);
    }

    protected void setOnline(final boolean online){
        Log.d("ToggleOnline","Calling.....");
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                NetworkHandler.toggleOnline(HomeActivity.this, online, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.d("ToggleOnline","onFailure:"+e);
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String resp = response.body().string();
                        Log.d("ToggleOnline","You are now "+ (online?"online":"offline")+"! : onResponse:"+resp);
                    }
                });
            }
        });

        thread.start();

    }

    public void setFrag(){
        int cur = viewPager.getCurrentItem();
        Log.d("Fragger3","Current Item:"+cur);
        if(cur ==3 || cur==0 || cur==2){
            findViewById(R.id.floatingNewBtn).setVisibility(View.VISIBLE);
        }else{
            findViewById(R.id.floatingNewBtn).setVisibility(View.GONE);
        }
    }

    protected void hidePencil(){

    }

    private Bundle getbundle(boolean following) {
        Bundle bundle = new Bundle();
//        bundle.putLong("uid",Long.parseLong(ServiceHandler.getUserId(getApplicationContext())));
//        bundle.putString("name",ServiceHandler.getUserName(getApplicationContext()));
        bundle.putBoolean("following",following);
        return bundle;
    }

    public void loadThisCategory(String cat){
        ((DiscoverFrag)adapter.getItem(0)).cat = cat;
        ((DiscoverFrag)adapter.getItem(0)).loadThisCategory(cat);
    }

    public void loadThisCategoryForVideos(String cat){
        ((VideoFrag)adapter.getItem(3)).cat = cat;
        ((VideoFrag)adapter.getItem(3)).loadThisCategory(cat);
    }



    public void collabCategory(String cat){
        ((CollabFrag)adapter.getItem(1)).cat = cat;
        ((CollabFrag)adapter.getItem(1)).loadThisCategory(cat);
    }

    public void loadThisType(String cat){
        ((DiscoverFrag)adapter.getItem(0)).type = cat;
        ((DiscoverFrag)adapter.getItem(0)).loadThisType(cat);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_dashboard, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_notifs:
                startActivity(NotificationActivity.class);break;
            case R.id.menu_msg:
                startActivity(MessagesActivity.class);break;
            case R.id.menu_search:
                startActivity(SearchActivity.class);break;
        }
        return true;
    }

    protected void startActivity(Class name){
        Intent intent = new Intent(HomeActivity.this,name);
        startActivity(intent);
    }

    protected void googleAuth(){
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    @Override
    public void onBackPressed() {
        if(commentBox.getVisibility() == View.VISIBLE) {
            commentBox.setVisibility(View.GONE);
            setNavigationAndDrawer();
        }
        else
            finish();
    }

    private void signOut() {
        if(mGoogleSignInClient!=null) {
            mGoogleSignInClient.signOut()
                    .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            DataHandler.resetLogout(getApplicationContext());
                            Toast.makeText(getApplicationContext(), "You've been logged out", Toast.LENGTH_LONG).show();
                            Intent i = new Intent(HomeActivity.this, SignInActivity.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(i);
                        }
                    });
        }
        else{
            DataHandler.resetLogout(getApplicationContext());
        }
    }
}
