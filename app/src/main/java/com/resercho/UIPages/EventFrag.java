package com.resercho.UIPages;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.resercho.Adapters.AdapterEvents;
import com.resercho.ConverterJson;
import com.resercho.CreateEventActivity;
import com.resercho.HomeActivity;
import com.resercho.NetworkHandler;
import com.resercho.POJO.ModelEvent;
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


public class EventFrag extends Fragment {


    // Work
    RecyclerView recycler;
    AdapterEvents adapter;
    Context context;
    LinearLayoutManager layoutManager;
    FloatingActionButton addFab;

    List<ModelEvent> list;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;

    }

    public EventFrag() {
        // Required empty public constructor
    }
    public static EventFrag newInstance(String param1) {
        EventFrag fragment = new EventFrag();
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
        View view =  inflater.inflate(R.layout.fragment_events, container, false);
        init(view);

        ((NestedScrollView)view.findViewById(R.id.nestedScrollView)).setNestedScrollingEnabled(false);
        loadEvents();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    protected void init(View view){
        recycler = view.findViewById(R.id.recycler);
        addFab = view.findViewById(R.id.addEventBtn);

        addFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent7 = new Intent(context, CreateEventActivity.class);
                startActivity(intent7);
            }
        });
    }

    protected void loadEvents(){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                NetworkHandler.fetchEvents(context,new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.d("Resercho","HomeFrag : onFailure::"+e);
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
                    ModelEvent w = ConverterJson.parseEventJson(arr.getJSONObject(i));
                    if(w!=null) {
                        list.add(w);
                    }
                    else
                        Log.d("Resercho","HomeFrag : Null Model : "+i);
                }

                showRecycler(list);
            }else{
                Log.d("Resercho","HomeFrag : Request Success:"+jsonObject.getInt("success"));
            }
        }catch (JSONException e){

        }
    }

    protected void showRecycler(final List<ModelEvent> list){
        ((Activity)context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                layoutManager = new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false);
                adapter = new AdapterEvents(context,list);
                recycler.setLayoutManager(layoutManager);
                recycler.setAdapter(adapter);

                recycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                        super.onScrollStateChanged(recyclerView, newState);
                        if(isLastVisible()){
                            Log.d("res","resk scrolled");
                        }
                    }
                });
            }
        });
        Log.d("Resercho","Dashboard Work showRv : "+list.size());

    }

    boolean isLastVisible() {
        try {
            int pos = layoutManager.findLastCompletelyVisibleItemPosition();
            int numItems = adapter.getItemCount();
            return (pos >= numItems);
        }catch (Exception e){
            return false;
        }
    }


}
