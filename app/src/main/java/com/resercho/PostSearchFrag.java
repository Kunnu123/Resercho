package com.resercho;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.resercho.Adapters.AdapterWork;
import com.resercho.POJO.ModelWork;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


public class PostSearchFrag extends Fragment {

    private static final String ARG_PARAM1 = "modelWork";

    Context context;
    // Work
    RecyclerView workRecycler;
    List<ModelWork> modelWorkList;
    AdapterWork adapterWork;
    String key;
    ProgressBar pbar;
    TextView searchEv;
    boolean work =false;

    public PostSearchFrag() {
        // Required empty public constructor
    }


    public static PostSearchFrag newInstance(boolean isWork) {
        PostSearchFrag fragment = new PostSearchFrag();
        Bundle args = new Bundle();
        args.putBoolean("work",isWork);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        try {
            this.work = args.getBoolean("work");
        }catch (NullPointerException e){
            this.work = false;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post_search, container, false);
        workRecycler = view.findViewById(R.id.rvSearch);
        pbar = view.findViewById(R.id.pbar);
        searchEv = view.findViewById(R.id.noMsg);

        return view;
    }
    protected void setMsgText(final String msg){
        ((Activity)context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                searchEv.setText(msg);
            }
        });
    }

    protected void recyclerView(){
        ((Activity)context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                workRecycler.removeAllViews();
                adapterWork = new AdapterWork(context,new ArrayList<ModelWork>());
                workRecycler.setAdapter(adapterWork);
                workRecycler.setVisibility(View.GONE);
            }
        });
    }

    protected void loadTrendingWork(final String key){
        this.key = key;
        recyclerView();
        Log.d("Resin","Resin : Load Trending");
        hideView(false,pbar);
        hideView(true,searchEv);

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                NetworkHandler.searchPosts(context,key,work,new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.d("Resin","Resin : Load Trending onFailure : "+e);
                        hideView(true,pbar);
                        hideView(false,searchEv);
                        hideView(true,workRecycler);
                        setMsgText("Something went wrong");
                        if(e.toString().contains("java.net.SocketTimeoutException")){
                            loadTrendingWork(key);
                        }
                    }


                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String resp = response.body().string();
                        Log.d("Resin","Resin : Load Trending onResponse:"+resp);
                        hideView(true,pbar);

                        parseJson(resp);
                    }
                });
            }
        });
        thread.start();
    }

    protected void parseJson(String json){
        modelWorkList = new ArrayList<>();

        try{
            JSONObject jsonObject =new JSONObject(json);
            if(jsonObject.getInt("success")==1){
                JSONArray arr = jsonObject.getJSONArray("data");
                for(int i=0;i<arr.length();i++){
                    ModelWork w = ConverterJson.parseTrendingJson(arr.getJSONObject(i));
                    if(w!=null) {
                        modelWorkList.add(w);
                    }

                }

                showWorkRecycler(modelWorkList);
            }else{
                Log.d("Resin","Resin : Load Trending else part");
                setMsgText("No such posts found");
                hideView(false,searchEv);
            }
        }catch (JSONException e){
            setMsgText("No such posts found");
            hideView(false,searchEv);
            Log.d("Resin","Resin : Load Trending JSON Exception : "+e);
        }
    }

    protected void showWorkRecycler(final List<ModelWork> list){
        ((Activity)context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(list!=null&&list.size()>0) {
                    workRecycler.setVisibility(View.VISIBLE);
                    RecyclerView.LayoutManager layman = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
                    adapterWork = new AdapterWork(context, list);
                    adapterWork.setHorizontal(false);
                    workRecycler.setLayoutManager(layman);
                    workRecycler.setAdapter(adapterWork);
                }else{
                    workRecycler.setVisibility(View.GONE);
                    setMsgText("No such posts found");
                    hideView(false,searchEv);
                }
            }
        });

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


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

}
