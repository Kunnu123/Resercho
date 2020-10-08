package com.resercho;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.resercho.Adapters.AdapterSuggested;
import com.resercho.ConverterJson;
import com.resercho.NetworkHandler;
import com.resercho.POJO.ModelSuggested;
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


public class SearchPeopleFrag extends Fragment {


    Context context;

    // Work
    // Suggested
    RecyclerView suggestedRv;
    AdapterSuggested adapterSuggested;

    List<ModelSuggested> modelSuggestedList;

    String key;
    ProgressBar pbar;
    TextView searchEv;

    public SearchPeopleFrag() {
        // Required empty public constructor
    }

    public static SearchPeopleFrag newInstance() {
        SearchPeopleFrag fragment = new SearchPeopleFrag();

        return fragment;
    }

    protected void setMsgText(final String msg){
        ((Activity)context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                searchEv.setText(msg);
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_people, container, false);

        pbar = view.findViewById(R.id.pbar);
        searchEv = view.findViewById(R.id.searchEv);
        suggestedRv = view.findViewById(R.id.rvSearch);

        return view;
    }

    protected void showSuggestedRecycler(final List<ModelSuggested> list){
        this.key = key;
        ((Activity)context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(list!=null&&list.size()>0) {
                    suggestedRv.setVisibility(View.VISIBLE);
                    hideView(true,searchEv);
                    RecyclerView.LayoutManager layman = new GridLayoutManager(getActivity(), 3);
                    adapterSuggested = new AdapterSuggested(context, list);
                    adapterSuggested.setSearch(true);
                    suggestedRv.setLayoutManager(layman);
                    suggestedRv.setAdapter(adapterSuggested);
                }else{
                    suggestedRv.setVisibility(View.GONE);
                    setMsgText("No such people found");
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

    protected void recyclerView(){
        ((Activity)context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                suggestedRv.removeAllViews();
                adapterSuggested = new AdapterSuggested(context,new ArrayList<ModelSuggested>());
                suggestedRv.setAdapter(adapterSuggested);
                suggestedRv.setVisibility(View.GONE);
            }

        });

    }

    public void loadSuggestedWork(final String key){
        hideView(false,pbar);
        recyclerView();

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                NetworkHandler.searchPeople(context,key,new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        hideView(true,pbar);
                        hideView(true,suggestedRv);
                        if(e.toString().contains("java.net.SocketTimeoutException")){
                            loadSuggestedWork(key);
                        }
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String resp = response.body().string();
                        hideView(true,pbar);
                        setMsgText("No such people found");
                        hideView(false,searchEv);
                        parseSuggestedJson(resp);
                    }
                });
            }
        });
        thread.start();
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
                setMsgText("No such people found");
                hideView(false,searchEv);
                Log.d("Resercho","HomeFrag : Request Success:"+jsonObject.getInt("success"));
            }
        }catch (JSONException e){
            setMsgText("No such people found");
            hideView(false,searchEv);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

}
