package com.resercho.UIPages;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.resercho.Adapters.AdapterCategory;
import com.resercho.Adapters.AdapterDiscover;
import com.resercho.Adapters.AdapterTypes;
import com.resercho.Adapters.AdapterWork;
import com.resercho.ConverterJson;
import com.resercho.DataHandler;
import com.resercho.HomeActivity;
import com.resercho.NetworkHandler;
import com.resercho.POJO.ModelCategory;
import com.resercho.POJO.ModelType;
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


public class VideoFrag extends Fragment {


    // Work
    RecyclerView workRecycler;
    AdapterWork adapterWork;

    // Category
    RecyclerView categoryRc;
    AdapterCategory category;
    List<ModelCategory> categories;

    Context context;

    List<ModelWork> modelWorkList;

    ProgressBar pbar;

    /// Newly added
    View noGroups;

    boolean loading=false;

    TextView loadMore;
    public String cat, type;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    public VideoFrag() {
        // Required empty public constructor
    }
    public static VideoFrag newInstance(String param1) {
        VideoFrag fragment = new VideoFrag();
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
        View view =  inflater.inflate(R.layout.fragment_videos, container, false);
        init(view);

//        ((NestedScrollView)view.findViewById(R.id.nestedScrollView)).setNestedScrollingEnabled(false);
        loadTrendingWork();
        loadCategoryData();

        return view;
    }

    protected void init(View view){

        view.findViewById(R.id.addNewPostBtnHome).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((HomeActivity)context).createNewPost();
            }
        });

        pbar = view.findViewById(R.id.workPbar);
        workRecycler = view.findViewById(R.id.rvWork);
        workRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (!recyclerView.canScrollVertically(1)) {
                    if(!loading) {
                        if (modelWorkList != null && modelWorkList.size() > 0) {
                            loadOffsetWork(modelWorkList.get(modelWorkList.size() - 1).getId(), type != null, type != null ? type : cat);
                        } else {
                            loadTrendingWork();
                        }
                        loading = true;
                    }
                    Log.d("REs","Scrottt Scrolling... Fetching....");
                }
            }
        });

        noGroups = view.findViewById(R.id.noGroupMsg);
        loadMore = view.findViewById(R.id.loadMore);
        categoryRc = view.findViewById(R.id.categoryRv);

        loadMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(modelWorkList!=null && modelWorkList.size()>0)
                    loadOffsetWork(modelWorkList.get(modelWorkList.size()-1).getId(),type!=null,type!=null?type:cat);
                else
                    loadTrendingWork();
            }
        });
    }

    protected void showCategoryRecycler(final List<ModelCategory> list){
        ((Activity)context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                RecyclerView.LayoutManager layman = new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false);
                category = new AdapterCategory(context,list);
                category.setFilter(AdapterCategory.FILTER_VIDEOS);
                categoryRc.setLayoutManager(layman);
                categoryRc.setAdapter(category);
            }
        });
        Log.d("Resercho","Dashboard Catin showRv : "+list.size());

    }

    protected void parseCategoryJson(String json){
        categories = new ArrayList<>();
        ModelCategory cat = new ModelCategory();
        cat.setId(null);
        cat.setName("All");
        categories.add(cat);


        // Hide from School :
        /*

        6. Architecture
        7. Journalism & Mass Communication
        8. Law & Government
        11 Beauty & Makeup
        5 Business & Entrepreneurship
        16 Education
        18 Economics
        9 Food & Culinary Arts
        10 Fashion & Style
        15 Fitness & Health
        20 Cinematography & Photography
         */

        try{
            JSONObject jsonObject =new JSONObject(json);
            if(jsonObject.getInt("success")==1){
                JSONArray arr = jsonObject.getJSONArray("data");
                for(int i=0;i<arr.length();i++){
                    ModelCategory w = ConverterJson.parseCategoryJson(arr.getJSONObject(i));
                    if(w!=null) {
                        if(DataHandler.isSchoolMode(context) && !hideFromSchool(Integer.parseInt(w.getId()))) {
                            categories.add(w);
                        }else{
                            Log.d("Resercho","Hidint : Hiding "+w.getName() + " from school mode");
                        }

                        if(DataHandler.isUnivMode(context))
                            categories.add(w);
                    }
                    else
                        Log.d("Resercho","HomeFrag : Catin Null Model : "+i);
                }

                showCategoryRecycler(categories);
            }else{
                Log.d("Resercho","HomeFrag : Catin Success:"+jsonObject.getInt("success"));
            }
        }catch (JSONException e){

        }
    }


    protected boolean hideFromSchool(int id){
        boolean hide=false;
        for(int i=0;i<DataHandler.hideFromSchool.length;i++){
            if(id==DataHandler.hideFromSchool[i]){
                hide = true;
            }
        }

        return hide;
    }

    protected void loadCategoryData(){
        Log.d("Res","Catin : loadcat()");
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                NetworkHandler.fetchCategories(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.d("Resercho","HomeFrag : Catin onFailure::"+e);
                        loadCategoryData();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String resp = response.body().string();
                        Log.d("Resercho","HomeFrag : Catin onResponse:"+resp);
                        parseCategoryJson(resp);
                    }
                });
            }
        });
        thread.start();
    }


    protected void showNoGroup(final boolean show){
        ((Activity)context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(show){
                    noGroups.setVisibility(View.VISIBLE);
                }else
                    noGroups.setVisibility(View.GONE);
            }
        });
    }



    protected void loadOffsetWork(final String offset, final boolean type, final String cat){
        loadMore.setText("Fetching...");
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                NetworkHandler.fetchOffsetVideos(getActivity(),offset,cat,type,new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        loadOffsetWork(offset,type,cat);
                        Log.d("Resercho","VideoFrag Disin : onFailure::"+e);
                        hideView(true,pbar);
                        hideView(true,loadMore);
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String resp = response.body().string();
                        Log.d("Resercho","VideoFrag Disin : onResponse:"+resp);
                        hideView(false,loadMore);
                        parseJson(resp,true);
                    }
                });
            }
        });
        thread.start();
    }


    protected void loadTrendingWork(final String cat){
        hideView(false,pbar);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                NetworkHandler.fetchVideoPosts(getActivity(),cat,new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        loadTrendingWork(cat);
                        Log.d("Resercho","HomeFrag Disin : onFailure::"+e);
                        hideView(true,pbar);
                        hideView(true,loadMore);
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String resp = response.body().string();
                        Log.d("Resercho","HomeFrag Disin : onResponse:"+resp);
                        hideView(false,loadMore);
                        parseJson(resp,false);

                    }
                });
            }
        });
        thread.start();
    }

    public void loadThisCategory(String cat){
        hideView(false,pbar);
        if(cat!=null)
            loadTrendingWork(cat);
        else
            loadTrendingWork();
    }

    protected void loadTrendingWork(){
        Log.d("Resercho","VideoFrag Loading trending work...");

        hideView(false,pbar);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                NetworkHandler.fetchVideoPosts(getActivity(),new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        loadTrendingWork();

                        hideView(true,pbar);
                        hideView(true,loadMore);
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String resp = response.body().string();
                        Log.d("Resercho","VideoFrag Disin : onResponse:"+resp);
                        hideView(false,loadMore);
                        parseJson(resp,false);

                    }
                });
            }
        });
        thread.start();
    }

    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
        Log.d("Fragger","Fragger VideoFrag Attached" );
    }

    protected void hideView(final boolean hide, final View view){

        ((HomeActivity)context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                loadMore.setText("Load More");
                if(hide)
                    view.setVisibility(View.GONE);
                else
                    view.setVisibility(View.VISIBLE);
            }
        });
    }


    protected void  parseJson(String json, boolean offset){
        Log.d("Resercho","VideoFrag Parsing...");
        if(!offset){
            modelWorkList = new ArrayList<>();
        }

        loading = false;
        try{
            JSONObject jsonObject =new JSONObject(json);
            if(jsonObject.getInt("success")==1){
                JSONArray arr = jsonObject.getJSONArray("data");
                for(int i=0;i<arr.length();i++){
                    ModelWork w = ConverterJson.parseTrendingJson(arr.getJSONObject(i));
                    if(w!=null) {
                        modelWorkList.add(w);
                        Log.d("Resercho","HomeFrag Disin : Iteration : "+i+ " W:"+w.getId());
                    }
                    else
                        Log.d("Resercho","HomeFrag Disin : Null Model : "+i);
                }

                if(!offset)
                    showWorkRecycler(modelWorkList);
                else {
                    ((Activity)context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapterWork.notifyDataSetChanged();
                        }
                    });
                }
            }else{
                if(!offset) {
                    modelWorkList = new ArrayList<>();
                    showWorkRecycler(modelWorkList);
                }
                Log.d("Resercho","VideoFrag : Disin Request Success:"+jsonObject.getInt("success"));
            }

            if(modelWorkList==null || !(modelWorkList.size()>0)){
                hideView(true,loadMore);
            }
        }catch (JSONException e){
            hideView(true,loadMore);
            Log.d("Resercho","VideoFrag JSON Expception:"+e);
        }

        hideView(true,pbar);
    }


    protected void showWorkRecycler(final List<ModelWork> list){
        Log.d("Resercho","VideoFrag Showing Recycler");
        ((Activity)context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(list.size()<0){
                    showNoGroup(true);
                }else{
                    showNoGroup(false);
                }
//                LinearLayoutManager layman = new GridLayoutManager(context,3);
//                StaggeredGridLayoutManager layman = new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL);
                LinearLayoutManager layman = new LinearLayoutManager(context,RecyclerView.VERTICAL,false);
                adapterWork = new AdapterWork(context,list);
                adapterWork.setHorizontal(false);
                workRecycler.setLayoutManager(layman);
                workRecycler.setAdapter(adapterWork);
                adapterWork.notifyDataSetChanged();


            }
        });
        Log.d("Resercho","VideoFrag Disin showRv : "+list.size());

    }
}
