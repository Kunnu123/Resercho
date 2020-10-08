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

import com.resercho.Adapters.AdapterGroup;
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

public class SearchGroupFrag extends Fragment {

    Context context;
    ProgressBar pbar;
    AdapterGroup adapter;
    List<ModelGroup> list;
    RecyclerView recycler;
    View noGroups;
    String key;

    public SearchGroupFrag() {
        // Required empty public constructor
    }

    public static SearchGroupFrag newInstance(String param1, String param2) {
        SearchGroupFrag fragment = new SearchGroupFrag();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_group_search, container, false);

        recycler = view.findViewById(R.id.collabRv);
        pbar = view.findViewById(R.id.pbar);
        noGroups = view.findViewById(R.id.noGroups);
        return view;
    }

    protected void showPbar(final boolean show){
        ((Activity)context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(show){
                    pbar.setVisibility(View.VISIBLE);
                }else
                    pbar.setVisibility(View.GONE);
            }
        });
    }


    protected void recyclerView(){
        ((Activity)context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                recycler.removeAllViews();
                adapter = new AdapterGroup(context,new ArrayList<ModelGroup>());
                recycler.setAdapter(adapter);
                recycler.setVisibility(View.GONE);
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


    public void loadGroupData(final String key){
        this.key = key;
       recyclerView();
        showPbar(true);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                NetworkHandler.searchGroups(context,key,new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        showNoGroup(true);
                        showPbar(false);
                        hideView(true,recycler);
                        if(e.toString().contains("java.net.SocketTimeoutException")){
                            loadGroupData(key);
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
        list = new ArrayList<>();
        try{
            JSONObject jsonObject =new JSONObject(json);
            if(jsonObject.getInt("success")==1){
                JSONArray arr = jsonObject.getJSONArray("data");
                for(int i=0;i<arr.length();i++){
                    ModelGroup w = ConverterJson.parseGroupJson(arr.getJSONObject(i));
                    if(w!=null) {
                        list.add(w);
                    }
                    else
                        Log.d("Resercho","HomeFrag : Null Model : "+i);
                }

                showWorkRecycler(list);
            }else{
                list = new ArrayList<>();
                showWorkRecycler(list);
                Log.d("Resercho","HomeFrag : Request Success:"+jsonObject.getInt("success"));
            }
        }catch (JSONException e){
            showNoGroup(true);
        }
        showPbar(false);
    }

    protected void showWorkRecycler(final List<ModelGroup> list){
        ((Activity)context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(list!=null && list.size()>0){
                    showNoGroup(false);
                    recycler.setVisibility(View.VISIBLE);
                    RecyclerView.LayoutManager layman = new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false);
                    adapter = new AdapterGroup(context,list);
                    recycler.setLayoutManager(layman);
                    recycler.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }else {
                    recycler.setVisibility(View.GONE);
                    showNoGroup(true);
                }

            }
        });
        Log.d("Resercho","Dashboard Work showRv : "+list.size());

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


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

}
