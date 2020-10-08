package com.resercho;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.resercho.Adapters.CustomViewPager;
import com.resercho.Adapters.ViewPagerAdapter;
import com.resercho.POJO.ModelStory;
import com.resercho.POJO.ModelStoryV2;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class StoryHolder extends AppCompatActivity {


    ViewPagerAdapter adapter;
    public CustomViewPager viewPager;
    ModelStoryV2 story;

    List<ModelStory> userStories;
    View pbar;

    public static int currentPage = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story_holder);

        currentPage = 0;
        story = new ModelStoryV2();
        story.setUsername(getIntent().getStringExtra("name"));
        story.setProfUrl(getIntent().getStringExtra("profile"));
        story.setCount(getIntent().getStringExtra("scount"));
        story.setAdded_by(getIntent().getStringExtra("uid"));

        init();
        loadUserStories();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        loadUserStories();
    }



    public void nextStory(int pos){
        if(pos<adapter.getCount()&&pos>=0)
            viewPager.setCurrentItem(pos + 1);
        else{
            finish();
        }
    }

    public void prevStory(int pos){
        viewPager.setCurrentItem(pos-1);
    }

    protected void init(){
        pbar = findViewById(R.id.pbarMain);
        viewPager = findViewById(R.id.homeViewPager);
        viewPager.disableScroll(true);
        adapter = new ViewPagerAdapter(getSupportFragmentManager());

    }

    private void showPbar(final boolean show){

                if(show)
                    pbar.setVisibility(View.VISIBLE);
                else
                    pbar.setVisibility(View.GONE);

    }

    protected void addFrags(List<ModelStory> list){
        Log.d("Ress","Stin List Size : "+list.size());
        int pos = 0;
        for(ModelStory s:list){
            addFrag(s,pos);
            pos++;
        }

        setStories();
    }

    protected void setStories(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showPbar(false);
                viewPager.setAdapter(adapter);
                viewPager.setCurrentItem(0);
                Log.d("Stin","Stin |||||||||||||||  total views: "+viewPager.getChildCount() + " >>Adapter : "+adapter.getCount());

                viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                    }

                    @Override
                    public void onPageSelected(int position) {
                        currentPage = position;
                        startTimerFromPageChange(position);
                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {

                    }
                });
            }
        });
    }

    public boolean shouldStartTimer(int pos){
        if(currentPage==pos)
            return true;

        return false;
    }

    public void startTimerFromPageChange(int pos){
        if(((StoryFrag)adapter.getItem(pos)).imageLoaded){
            ((StoryFrag)adapter.getItem(pos)).startTimer();
        }
    }


    protected void addFrag(ModelStory s, int pos){
        // FIRST FRAG
        StoryFrag discFrag = new StoryFrag();
        discFrag.setArguments(getbundle(s,pos));
        adapter.AddFragment(discFrag,s.getId());

        Log.d("Stin","Stin added Frag:"+s.getId());
    }



    private Bundle getbundle(ModelStory story, int pos) {
        Bundle bundle = new Bundle();
        bundle.putString("sid",story.getId());
        bundle.putString("image",story.getImage());
        bundle.putString("time",story.getTime());
        bundle.putString("name",story.getFullname());
        bundle.putString("profile",story.getProfile());
        bundle.putInt("pos",pos);

        return bundle;
    }


    protected void loadUserStories(){
        Log.d("Ress","Stin loadUserStories");
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                NetworkHandler.fetchUserStories(getApplicationContext(),story.getAdded_by(), new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        toastOnMain("Failed to load");
                        Log.d("Ress","Stin onFailr : "+e);
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String resp = response.body().string();
                        Log.d("Ress","Stin response:"+resp);
                        parseStoryJson(resp);
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

    protected void  parseStoryJson(String json){
        userStories = new ArrayList<>();
        try{
            JSONObject jsonObject =new JSONObject(json);
            if(jsonObject.getInt("success")==1){
                JSONArray arr = jsonObject.getJSONArray("data");
                for(int i=0;i<arr.length();i++){
                    ModelStory w = ConverterJson.parseStoryJson(arr.getJSONObject(i));
                    if(w!=null) {
                        userStories.add(w);
                    }
                    else
                        Log.d("Ress","Stin Model is null JSON Exception");
                }

                addFrags(userStories);
            }else{
                Log.d("Ress","Stin request not success");
            }
        }catch (JSONException e){
            Log.d("Ress","Stin  JSON Exception : "+e);
        }
    }
}
