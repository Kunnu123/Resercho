package com.resercho.UIPages;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.resercho.Adapters.AdapterStory;
import com.resercho.Adapters.AdapterSuggested;
import com.resercho.Adapters.AdapterWork;
import com.resercho.AddStoryActivity;
import com.resercho.ConverterJson;
import com.resercho.HomeActivity;
import com.resercho.NetworkHandler;
import com.resercho.POJO.ModelStoryV2;
import com.resercho.POJO.ModelSuggested;
import com.resercho.POJO.ModelWork;
import com.resercho.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


public class HomeFrag extends Fragment {


    // Work
//    RecyclerView workRecycler;
//    AdapterWork adapterWork;

    // Suggested
    RecyclerView suggestedRv;
    AdapterSuggested adapterSuggested;

    // Follow
    RecyclerView followRv;
    AdapterWork adapterFollow;

    // Stories
    RecyclerView storyRv;
    AdapterStory adapterStory;

    Context context;

    List<ModelWork> modelWorkList;
    List<ModelWork> followWorkList;
    List<ModelStoryV2> storiesList;
    List<ModelSuggested> modelSuggestedList;

    ProgressBar sugPbar, trendPbar, follPbar;

    View commentBox, noFollowMsg;

    TextView loadMoreBtn;


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    public HomeFrag() {
        // Required empty public constructor
    }
    public static HomeFrag newInstance(String param1) {
        HomeFrag fragment = new HomeFrag();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_home, container, false);
        init(view);

        sugPbar = view.findViewById(R.id.suggestedPbar);
//        trendPbar = view.findViewById(R.id.trendingPbar);
        follPbar = view.findViewById(R.id.followingPbar);
        commentBox = view.findViewById(R.id.homeComment);
        noFollowMsg = view.findViewById(R.id.notFollowMsg);
        loadMoreBtn = view.findViewById(R.id.loadMore);

        loadMoreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(followWorkList!=null && followWorkList.size()>0) {
                    Log.d("Loamore","Fetching more : "+followWorkList.size() );
                    loadMoreFollowingPosts(true, followWorkList.get(followWorkList.size() - 1).getId());
                }
                else
                    loadTrendingWork(true);
            }
        });
        hideView(true,sugPbar);

        return view;
    }

    protected void showCommentBox(boolean show){
        if(show){
            commentBox.setVisibility(View.VISIBLE);
        }else{
            commentBox.setVisibility(View.GONE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

//        loadTrendingWork(false);
        if(((HomeActivity)context).newpost) {
            loadFollowingWorkWithNewPost(((HomeActivity)context).newpostid);
//            ((HomeActivity)context).newpost = false;
        }else{
            loadTrendingWork(true); // Fetch that new post here!
        }
        loadSuggestedWork();
        loadStory();
    }



    protected void hideView(final boolean hide, final View view){
        ((Activity)context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(hide)
                    view.setVisibility(View.GONE);
                else
                    view.setVisibility(View.VISIBLE);
            }
        });
    }

    boolean loading=false;
    protected void init(View view){


        view.findViewById(R.id.addNewPostBtnHome).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((HomeActivity)context).createNewPost();
            }
        });

//        workRecycler = view.findViewById(R.id.rvWork);
        suggestedRv = view.findViewById(R.id.suggestedRv);
        followRv = view.findViewById(R.id.followWork);

        followRv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (!recyclerView.canScrollVertically(1)) {
                    if(!loading) {
                        if(followWorkList!=null && followWorkList.size()>0) {
                            Log.d("Loamore","Fetching more : "+followWorkList.size() );
                            loadMoreFollowingPosts(true, followWorkList.get(followWorkList.size() - 1).getId());
                        }
                        else
                            loadTrendingWork(true);
                        loading = true;
                    }
                }
                Log.d("Vertiii","Loamore outside");
            }
        });

        storyRv = view.findViewById(R.id.storyRecycler);
        noFollowMsg =view.findViewById(R.id.notFollowMsg);

        view.findViewById(R.id.addStoryBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, AddStoryActivity.class);
                startActivity(intent);
            }
        });

//        workRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//                super.onScrollStateChanged(recyclerView, newState);
//
//                if (!recyclerView.canScrollVertically(1)) {
//                    Log.d("REs","debint : can scroll !!");
//                }
//            }
//        });
    }

    protected void loadStory(){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                NetworkHandler.fetchStories(context,new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        if(e.toString().contains("java.net.SocketTimeoutException")){
                            loadStory();
                        }
                    }


                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String resp = response.body().string();
                        parseStoryJson(resp);
                    }
                });
            }
        });
        thread.start();
    }

    protected void loadMoreFollowingPosts(final boolean follow, final String offset){
        loadMoreBtn.setVisibility(View.GONE);
        hideView(false,follPbar);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                NetworkHandler.fetchOffsetFollowing(context,offset,new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        hideView(true,follow?follPbar:trendPbar);
                        if(e.toString().contains("java.net.SocketTimeoutException")){
                            loadTrendingWork(follow);
                        }
                    }


                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String resp = response.body().string();
                        hideView(true,follow?follPbar:trendPbar);

                        parseFollowJson(resp,true);

                    }
                });
            }
        });
        thread.start();
    }

    protected void loadFollowingWorkWithNewPost(final int pid){
        final boolean follow = true;
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                NetworkHandler.fetchTrendingWithNewPost(context,pid,new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        hideView(true,follow?follPbar:trendPbar);
                        if(e.toString().contains("java.net.SocketTimeoutException")){
                            loadFollowingWorkWithNewPost(pid);
                        }
                    }


                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String resp = response.body().string();
                        hideView(true,follow?follPbar:trendPbar);
                        if(follow)
                            parseFollowJson(resp,false);
//                        else
//                            parseJson(resp);
                    }
                });
            }
        });
        thread.start();
    }

    protected void loadTrendingWork(final boolean follow){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                NetworkHandler.fetchTrending(context,follow,null,null,false,new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        hideView(true,follow?follPbar:trendPbar);
                        if(e.toString().contains("java.net.SocketTimeoutException")){
                            loadTrendingWork(follow);
                        }
                    }


                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String resp = response.body().string();
                        hideView(true,follow?follPbar:trendPbar);
                        if(follow)
                            parseFollowJson(resp,false);
//                        else
//                            parseJson(resp);
                    }
                });
            }
        });
        thread.start();
    }



        protected void loadSuggestedWork(){
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    NetworkHandler.fetchSuggested(context,new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            hideView(true,sugPbar);

                            if(e.toString().contains("java.net.SocketTimeoutException")){
                                loadSuggestedWork();
                            }
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            String resp = response.body().string();
                            hideView(true,sugPbar);
                            parseSuggestedJson(resp);
                        }
                    });
                }
            });
            thread.start();
        }


    protected void  parseStoryJson(String json){
        storiesList = new ArrayList<>();
        try{
            JSONObject jsonObject =new JSONObject(json);
            if(jsonObject.getInt("success")==1){
                JSONArray arr = jsonObject.getJSONArray("data");
                for(int i=0;i<arr.length();i++){
                    ModelStoryV2 w = ConverterJson.parseStoryJsonV2(arr.getJSONObject(i));
                    if(w!=null) {
                        storiesList.add(w);
                        Log.d("Resercho","HomeFrag : Iteration : "+i+ " W:"+w.getAdded_by());
                    }
                    else
                        Log.d("Resercho","HomeFrag : Null Model : "+i);
                }

                showStoriesRecycler(storiesList);
            }else{
                Log.d("Resercho","HomeFrag : Request Success:"+jsonObject.getInt("success"));
            }
        }catch (JSONException e){

        }
    }
//
//    protected void parseJson(String json){
//        modelWorkList = new ArrayList<>();
//        try{
//            JSONObject jsonObject =new JSONObject(json);
//            if(jsonObject.getInt("success")==1){
//                JSONArray arr = jsonObject.getJSONArray("data");
//                for(int i=0;i<arr.length();i++){
//                    ModelWork w = ConverterJson.parseTrendingJson(arr.getJSONObject(i));
//                    if(w!=null) {
//                        modelWorkList.add(w);
//                    }
//
//                }
//                    showWorkRecycler(modelWorkList);
//            }else{
//            }
//        }catch (JSONException e){
//
//        }
//    }

    protected void parseFollowJson(String json, final boolean loadmore){

        if(!loadmore)
            followWorkList = new ArrayList<>();
        try{
            JSONObject jsonObject =new JSONObject(json);
            if(jsonObject.getInt("success")==1){
                JSONArray arr = jsonObject.getJSONArray("data");
                for(int i=0;i<(arr.length()>20?20:arr.length());i++){
                    ModelWork w = ConverterJson.parseTrendingJson(arr.getJSONObject(i));
                    if(w!=null) {
                        followWorkList.add(w);
                    }

                }

                if(!loadmore)
                    showFollowWorkRecycler(followWorkList);
                else {
                    ((HomeActivity)context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapterFollow.notifyDataSetChanged();
                            loadMoreBtn.setVisibility(View.VISIBLE);
                        }
                    });
                }
            }else{

                if(jsonObject.getString("reason").equalsIgnoreCase("no_follower")){
                    showNoFollow(true,null);
                }else if(jsonObject.getString("reason").equalsIgnoreCase("init")){
                    showNoFollow(true,"No posts to show, follow more people to get more content");
                }

            }
        }catch (JSONException e){
            Log.d("Resercho","Parsing HomeFrag : JSON Exception:"+e);
        }
    }

    protected void showNoFollow(final boolean show, final String msg){
        ((Activity)context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(show){
                    noFollowMsg.setVisibility(View.VISIBLE);
                    if(msg!=null)
                        ((TextView)noFollowMsg).setText(msg);
                }else
                    noFollowMsg.setVisibility(View.GONE);
            }
        });
    }



    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
    }


    protected void parseSuggestedJson(String json){
        modelSuggestedList = new ArrayList<>();
        try{
            JSONObject jsonObject =new JSONObject(json);
            if(jsonObject.getInt("success")==1){
                JSONArray arr = jsonObject.getJSONArray("data");
                for(int i=0;i<arr.length();i++){
                    ModelSuggested w = ConverterJson.parseSuggestedJson(arr.getJSONObject(i));
                    if(w!=null) {
                        modelSuggestedList.add(w);
                        Log.d("Resercho","HomeFrag : Iteration : "+i+ " W:"+w.getId());
                    }
                    else
                        Log.d("Resercho","HomeFrag : Null Model : "+i);
                }

                showSuggestedRecycler(modelSuggestedList);
            }else{
                Log.d("Resercho","HomeFrag : Request Success:"+jsonObject.getInt("success"));
            }
        }catch (JSONException e){

        }
    }

    protected void showStoriesRecycler(final List<ModelStoryV2> list){
        ((Activity)context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                RecyclerView.LayoutManager layman = new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false);
                adapterStory = new AdapterStory(context,list);
                storyRv.setLayoutManager(layman);
                storyRv.setAdapter(adapterStory);
            }
        });
        Log.d("Resercho","Dashboard Story showRv : "+list.size());

    }
//
//    protected void showWorkRecycler(final List<ModelWork> list){
//        ((Activity)context).runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                RecyclerView.LayoutManager layman = new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false);
//                adapterWork = new AdapterWork(context,list);
//                workRecycler.setLayoutManager(layman);
//                workRecycler.setAdapter(adapterWork);
//            }
//        });
//
//    }

    protected void showFollowWorkRecycler(final List<ModelWork> list){
        ((Activity)context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                loadMoreBtn.setVisibility(View.VISIBLE);
                RecyclerView.LayoutManager layman = new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false);
                adapterFollow = new AdapterWork(context,list);
                adapterFollow.setHorizontal(false);
                followRv.setLayoutManager(layman);
                followRv.setAdapter(adapterFollow);
            }
        });

    }

    protected void showSuggestedRecycler(final List<ModelSuggested> list){

        ((Activity)context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                RecyclerView.LayoutManager layman = new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false);
                adapterSuggested = new AdapterSuggested(context,list);
                suggestedRv.setLayoutManager(layman);
                suggestedRv.setAdapter(adapterSuggested);
            }
        });

    }



}
