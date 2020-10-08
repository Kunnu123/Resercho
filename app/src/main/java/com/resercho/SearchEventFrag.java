package com.resercho;


import android.app.Activity;
import android.content.Context;
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

import com.resercho.Adapters.AdapterEvents;
import com.resercho.POJO.ModelEvent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class SearchEventFrag extends Fragment {

    RecyclerView recycler;
    AdapterEvents adapter;
    Context context;
    LinearLayoutManager layoutManager;

    ProgressBar pbar;
    TextView nomsg;
    String key;

    List<ModelEvent> list;
    public SearchEventFrag() {
        // Required empty public constructor
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

    protected void setMsgText(final String msg){
        ((Activity)context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
               nomsg.setText(msg);
            }
        });
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event, container, false);

        recycler = view.findViewById(R.id.eventRv);
        pbar = view.findViewById(R.id.pbar);
        nomsg = view.findViewById(R.id.noMsg);

        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    protected void recyclerView(){
        ((Activity)context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                recycler.removeAllViews();
                adapter = new AdapterEvents(context,new ArrayList<ModelEvent>());
                recycler.setAdapter(adapter);
                recycler.setVisibility(View.GONE);
            }
        });
    }



    public void loadEvents(final String key){
        hideView(false,pbar);
        this.key = key;
        recyclerView();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                NetworkHandler.searchEvents(context,key,new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        setMsgText("Something went wrong");
                        hideView(true,pbar);
                        hideView(false,nomsg);
                        hideView(true,recycler);
                        if(e.toString().contains("java.net.SocketTimeoutException")){
                            loadEvents(key);
                        }
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String resp = response.body().string();
                        Log.d("Resercho","HomeFrag : onResponse:"+resp);
                        parseJson(resp);
                        hideView(true,pbar);
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
                    ModelEvent w = ConverterJson.parseEventJson(arr.getJSONObject(i));
                    if(w!=null) {
                        list.add(w);
                    }
                    else
                        Log.d("Resercho","HomeFrag : Null Model : "+i);
                }

                showRecycler(list);
            }else{
                setMsgText("No such events found!");
                hideView(false,nomsg);
                Log.d("Resercho","HomeFrag : Request Success:"+jsonObject.getInt("success"));
            }
        }catch (JSONException e){
            setMsgText("No such events found!");
            hideView(false,nomsg);
        }
    }

    protected void showRecycler(final List<ModelEvent> list){
        ((Activity)context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(list!=null&&list.size()>0) {
                    recycler.setVisibility(View.VISIBLE);
                    layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
                    adapter = new AdapterEvents(context, list);
                    recycler.setLayoutManager(layoutManager);
                    recycler.setAdapter(adapter);
                }else{
                    recycler.setVisibility(View.GONE);
                    setMsgText("No such events found!");
                    hideView(false,nomsg);
                }
            }
        });
        Log.d("Resercho","Dashboard Work showRv : "+list.size());

    }
}
