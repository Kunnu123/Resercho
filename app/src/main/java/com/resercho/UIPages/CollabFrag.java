package com.resercho.UIPages;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.resercho.CreateGroupActivity;
import com.resercho.DataHandler;
import com.resercho.HomeActivity;
import com.resercho.R;
import com.resercho.Adapters.AdapterCategory;
import com.resercho.Adapters.AdapterGroup;
import com.resercho.ConverterJson;
import com.resercho.NetworkHandler;
import com.resercho.POJO.ModelCategory;
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


public class CollabFrag extends Fragment {

    // Work
    RecyclerView recycler;
    AdapterGroup adapter;
    List<ModelGroup> list;

    // Category
    RecyclerView categoryRc;
    AdapterCategory category;
    List<ModelCategory> categories;

    ProgressBar pbar;
    View noGroups;
    Context context;
    FloatingActionButton addFab;

    private boolean loading=false;
    TextView loadMore;

    public String cat;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }


    public CollabFrag() {
        // Required empty public constructor
    }

    public static CollabFrag newInstance(String param1) {
        CollabFrag fragment = new CollabFrag();
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
        View view =  inflater.inflate(R.layout.frag_collab_v2, container, false);
        init(view);
        loadGroupData(null);
        loadCategoryData();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    protected void init(View view){
        recycler = view.findViewById(R.id.recycler);
        categoryRc = view.findViewById(R.id.categoryRv);
        pbar = view.findViewById(R.id.pbar);
        noGroups = view.findViewById(R.id.noGroupMsg);
        addFab = view.findViewById(R.id.addGroupBtn);

        addFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent6 = new Intent(context, CreateGroupActivity.class);
                startActivity(intent6);
            }
        });

        recycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (!recyclerView.canScrollVertically(1)) {
                    if(!loading) {
                        if (list != null && list.size() > 0) {
                            loadOffsetGroups(list.get(list.size() - 1).getGid(),  cat);
                        } else {
                            loadGroupData( cat);
                        }
                        loading = true;
                    }
                    Log.d("REs","Scrottt Scrolling... Fetching....");
                }
            }
        });
    }

    protected void showNoGroup(final boolean show){
        ((Activity)context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(show){
                    noGroups.setVisibility(View.VISIBLE);
                    if(list.size()>0){
                        ((TextView)noGroups).setText("That's all for now!");
                    }
                }else
                    noGroups.setVisibility(View.GONE);
            }
        });
    }

    protected void showPbar(final boolean show){
        ((HomeActivity)context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(show){
                    pbar.setVisibility(View.VISIBLE);
                }else
                    pbar.setVisibility(View.GONE);
            }
        });
    }

    public void loadThisCategory(String cat){
        loadGroupData(cat);
    }



    protected void loadOffsetGroups(final String offset, final String cat){
        showPbar(true);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                NetworkHandler.fetchOffsetGroups(context,offset,cat,new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.d("Resercho","HomeFrag : onFailure::"+e);
                        showPbar(false);
                        loadGroupData(cat);
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String resp = response.body().string();
                        Log.d("Resercho","HomeFrag : onResponse:"+resp);
                        parseJson(resp,true);
                    }
                });
            }
        });
        thread.start();
    }



    protected void loadGroupData(final String cat){
        showPbar(true);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                NetworkHandler.fetchGroups(context,cat,new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.d("Resercho","HomeFrag : onFailure::"+e);
                        showPbar(false);
                        loadGroupData(cat);
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String resp = response.body().string();
                        Log.d("Resercho","HomeFrag : onResponse:"+resp);
                        parseJson(resp,false);
                    }
                });
            }
        });
        thread.start();
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

    protected void parseCategoryJson(String json){
        categories = new ArrayList<>();
        ModelCategory cat = new ModelCategory();
        cat.setId(null);
        cat.setName("All");
        categories.add(cat);
        try{
            JSONObject jsonObject =new JSONObject(json);
            if(jsonObject.getInt("success")==1){
                JSONArray arr = jsonObject.getJSONArray("data");
                for(int i=0;i<arr.length();i++){
                    ModelCategory w = ConverterJson.parseCategoryJson(arr.getJSONObject(i));
                    if(w!=null) {
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

    protected void parseJson(String json, final boolean offset){
        if(!offset)
            list = new ArrayList<>();

        loading =false;

        try{
            JSONObject jsonObject =new JSONObject(json);
            if(jsonObject.getInt("success")==1){
                JSONArray arr = jsonObject.getJSONArray("data");
                for(int i=0;i<arr.length();i++){
                    ModelGroup w = ConverterJson.parseGroupJson(arr.getJSONObject(i));
                    if(w.getUid().equalsIgnoreCase(DataHandler.getUserId(context))){
                        w.setHasJoined(true);
                    }

                    if(w!=null) {
                        list.add(w);
                    }
                    else
                        Log.d("Resercho","HomeFrag : Null Model : "+i);
                }

                if(!offset)
                    showWorkRecycler(list);
                else {
                    ((Activity)context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyDataSetChanged();
                        }
                    });
                }
            }else{
                if(!offset) {
                    list = new ArrayList<>();
                    showWorkRecycler(list);
                }
                Log.d("Resercho","HomeFrag : Request Success:"+jsonObject.getInt("success"));
            }
        }catch (JSONException e){

        }
        showPbar(false);
    }


    protected void showWorkRecycler(final List<ModelGroup> list){
        ((Activity)context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(list!=null && list.size()<1){
                    showNoGroup(true);
                }else
                    showNoGroup(false);
                RecyclerView.LayoutManager layman = new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false);
                adapter = new AdapterGroup(context,list);
                recycler.setLayoutManager(layman);
                recycler.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
        });
        Log.d("Resercho","Dashboard Work showRv : "+list.size());

    }

    protected void showCategoryRecycler(final List<ModelCategory> list){
        ((Activity)context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                RecyclerView.LayoutManager layman = new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false);
                category = new AdapterCategory(context,list);
                category.setFilter(AdapterCategory.FILTER_COLLAB);
                categoryRc.setLayoutManager(layman);
                categoryRc.setAdapter(category);
            }
        });
        Log.d("Resercho","Dashboard Catin showRv : "+list.size());

    }



}
