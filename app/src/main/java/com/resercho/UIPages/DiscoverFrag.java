package com.resercho.UIPages;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.resercho.Adapters.AdapterCategory;
import com.resercho.Adapters.AdapterDiscover;
import com.resercho.Adapters.AdapterSuggested;
import com.resercho.Adapters.AdapterTypes;
import com.resercho.ConverterJson;
import com.resercho.DataHandler;
import com.resercho.HomeActivity;
import com.resercho.NetworkHandler;
import com.resercho.POJO.ModelCategory;
import com.resercho.POJO.ModelSuggested;
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


public class DiscoverFrag extends Fragment {


    // Work
    RecyclerView workRecycler;
    AdapterDiscover adapterWork;

    Context context;

    List<ModelWork> modelWorkList;

    ProgressBar pbar;

    /// Newly added
    View noGroups;

    // Category
    RecyclerView categoryRc;
    AdapterCategory category;
    List<ModelCategory> categories;

    //Types
    RecyclerView typesRv;
    AdapterTypes typeAdapter;
    List<ModelType> typesList;
    boolean loading=false;

    TextView loadMore;
    public String cat, type,selClass;

    // Spinner
    String[] classes = { "All Classes","Class 5th","Class 6th","Class 7th","Class 8th","Class 9th","Class 10th","Class 11th","Class 12th"};
    Spinner classSpinner;



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        Log.d("Fragger2","EventFrag onAttach()");
    }

    public DiscoverFrag() {
        // Required empty public constructor
    }
    public static DiscoverFrag newInstance(String param1) {
        DiscoverFrag fragment = new DiscoverFrag();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
        Log.d("Fragger","Fragger Discover Attached" );
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
    }

    public void loadThisCategory(String cat){
        hideView(false,pbar);
        loadTrendingWork(false,cat);
    }

    public void loadThisType(String cat){
        hideView(false,pbar);
        loadTrendingWork(true,cat);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_discover, container, false);
        init(view);

//        ((NestedScrollView)view.findViewById(R.id.nestedScrollView)).setNestedScrollingEnabled(false);
        loadTrendingWork(false,null);
        loadCategoryData();
        loadTypesData();

        return view;
    }
    protected void showCategoryRecycler(final List<ModelCategory> list){
        ((Activity)context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                RecyclerView.LayoutManager layman = new LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false);
                category = new AdapterCategory(context,list);
                category.setFilter(AdapterCategory.FILTER_DISCOVER);
                categoryRc.setLayoutManager(layman);
                categoryRc.setAdapter(category);
            }
        });
        Log.d("Resercho","Dashboard Catin showRv : "+list.size());

    }

    protected void showTypesRecycler(final List<ModelType> list){
        ((Activity)context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                RecyclerView.LayoutManager layman = new LinearLayoutManager((HomeActivity)context,LinearLayoutManager.HORIZONTAL,false);
                typeAdapter = new AdapterTypes(context,list);
                typesRv.setLayoutManager(layman);
                typesRv.setAdapter(typeAdapter);
            }
        });
        Log.d("Resercho","Dashboard Catin showRv : "+list.size());

    }
    protected void loadTypesData(){
        Log.d("Res","Catin : loadcat()");
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                NetworkHandler.fetchTypes(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.d("Resercho","HomeFrag : Catin onFailure::"+e);
                        loadTypesData();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String resp = response.body().string();
                        Log.d("Resercho","HomeFrag : Catin onResponse:"+resp);
                        parseTypeJson(resp);
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

    protected void parseTypeJson(String json){
        typesList = new ArrayList<>();
        ModelType cat = new ModelType();
        cat.setId(null);
        cat.setName("All Categories");
        typesList.add(cat);

        try{
            JSONObject jsonObject =new JSONObject(json);
            if(jsonObject.getInt("success")==1){
                JSONArray arr = jsonObject.getJSONArray("data");
                for(int i=0;i<arr.length();i++){
                    ModelType w = ConverterJson.parseTypeJson(arr.getJSONObject(i));
                    if(w!=null) {
                        typesList.add(w);
                    }
                    else
                        Log.d("Resercho","HomeFrag : Catin Null Model : "+i);
                }

                showTypesRecycler(typesList);
            }else{
                Log.d("Resercho","HomeFrag : Catin Success:"+jsonObject.getInt("success"));
            }
        }catch (JSONException e){

        }
    }


    protected void parseCategoryJson(String json){
        categories = new ArrayList<>();
        ModelCategory cat = new ModelCategory();
        cat.setId(null);
        cat.setName("All Categories");
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
    protected void init(View view){
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
                            loadTrendingWork(type != null, type != null ? type : cat);
                        }
                        loading = true;
                    }
                    Log.d("REs","Scrottt Scrolling... Fetching....");
                }
            }
        });
        categoryRc = view.findViewById(R.id.categoryRv);
        typesRv = view.findViewById(R.id.typesRv);
        noGroups = view.findViewById(R.id.noGroupMsg);
        loadMore = view.findViewById(R.id.loadMore);

        loadMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(modelWorkList!=null && modelWorkList.size()>0)
                    loadOffsetWork(modelWorkList.get(modelWorkList.size()-1).getId(),type!=null,type!=null?type:cat);
                else
                    loadTrendingWork(type!=null,type!=null?type:cat);
            }
        });

        view.findViewById(R.id.addNewPostBtnHome).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((HomeActivity)context).createNewPost();
            }
        });


        // Spinner Load
        classSpinner = view.findViewById(R.id.spinnerClass);
        if(DataHandler.isSchoolMode(context)) {
            classSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    selClass = classes[position];

                    if(position==0)
                        selClass = null;
                    else
                        selClass = 4+position+"";
                    loadTrendingWork(false,cat);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });

            ArrayAdapter aa = new ArrayAdapter(context, android.R.layout.simple_spinner_item, classes);
            aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            classSpinner.setAdapter(aa);
        }else{
            classSpinner.setVisibility(View.GONE);
        }

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
                NetworkHandler.fetchOffsetTrending((HomeActivity)context,offset,cat,selClass,type,new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        loadTrendingWork(type,cat);
                        Log.d("Resercho","HomeFrag Disin : onFailure::"+e);
                        hideView(true,pbar);
                        hideView(true,loadMore);
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String resp = response.body().string();
                        Log.d("Resercho","HomeFrag Disin : onResponse:"+resp);
                        hideView(false,loadMore);
                        parseJson(resp,true);

                    }
                });
            }
        });
        thread.start();
    }


    protected void loadTrendingWork(final boolean type,final String cat){
        hideView(false,pbar);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                NetworkHandler.fetchTrending((HomeActivity)context,false,cat,selClass,type,new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        loadTrendingWork(type,cat);
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

    protected void hideView(final boolean hide, final View view){

        try {
            ((HomeActivity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    loadMore.setText("Load More");
                    if (hide)
                        view.setVisibility(View.GONE);
                    else
                        view.setVisibility(View.VISIBLE);
                }
            });
        }catch (Exception e){

        }
    }


    protected void  parseJson(String json, boolean offset){
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
                Log.d("Resercho","HomeFrag : Disin Request Success:"+jsonObject.getInt("success"));
            }

            if(modelWorkList==null || !(modelWorkList.size()>0)){
                hideView(true,loadMore);
            }
        }catch (JSONException e){
            hideView(true,loadMore);
        }

        hideView(true,pbar);
    }


    protected void showWorkRecycler(final List<ModelWork> list){
        ((Activity)context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(list.size()<0){
                    showNoGroup(true);
                }else{
                    showNoGroup(false);
                }
//                LinearLayoutManager layman = new GridLayoutManager(context,3);
                StaggeredGridLayoutManager layman = new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL);
                adapterWork = new AdapterDiscover((HomeActivity)context,list);
                workRecycler.setLayoutManager(layman);
                workRecycler.setAdapter(adapterWork);
                adapterWork.notifyDataSetChanged();


            }
        });
        Log.d("Resercho","Dashboard Disin showRv : "+list.size());

    }


}
