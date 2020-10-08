package com.resercho.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.resercho.NetworkHandler;
import com.resercho.POJO.ModelSuggested;
import com.resercho.ProfileActivity;
import com.resercho.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static com.resercho.SignInActivity.TAG;

public class AdapterSuggested extends RecyclerView.Adapter<AdapterSuggested.ViewHolder> {

    private Context context;
    private List<ModelSuggested> list;
    boolean search = false;

    public boolean isSearch() {
        return search;
    }

    public void setSearch(boolean search) {
        this.search = search;
    }

    public AdapterSuggested(Context context, List<ModelSuggested> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view;

        if(!search)
            view = LayoutInflater.from(context).inflate(R.layout.item_suggested_box, viewGroup, false);
        else
            view = LayoutInflater.from(context).inflate(R.layout.item_suggested_v2, viewGroup, false);

        return new AdapterSuggested.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        Log.d("Resercho","Suggested onBindView :" +list.get(position).getUsername());
        try {
            holder.uname.setText(list.get(position).getFullname().substring(0,1).toUpperCase() + list.get(position).getFullname().substring(1));
        }catch (Exception e){
            holder.uname.setText(list.get(position).getFullname());
        }

        if(list.get(position).isSentRequest())
            setFollowUI("Following",holder);
        else
            setFollowUI("Follow",holder);

        holder.follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendFollow(list.get(position).getId(),!list.get(position).isSentRequest(), position,holder);
            }
        });

        //
        Log.e("@@",list.get(position).getProfUrl());
        Glide.with(context)
                .load(list.get(position).getProfUrl())
                .asBitmap()
                .thumbnail(0.1f)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.mipmap.ic_launcher)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                       holder.image.setImageBitmap(resource);
                    }
                });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ProfileActivity.class);
                intent.putExtra("uid",list.get(position).getId());
                context.startActivity(intent);
            }
        });

    }

    protected void sendFollow(final String fid, final boolean follow, final int pos, final ViewHolder holder){
        setFollowUI("Requesting",holder);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                NetworkHandler.sendFollow(context, follow, fid, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.d(TAG,"sendfollow onFailure:"+e);
                        toastOnMain("Follow Failed!");
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String resp = response.body().string();

                        Log.d(TAG,"sendfollow onResponse:"+resp);
                        try{
                            JSONObject object = new JSONObject(resp);
                            if(object.getString("reason").equalsIgnoreCase("followed")){
                                list.get(pos).setSentRequest(true);
                                toastOnMain("You are now following "+list.get(pos).getFullname());
//                                setFollowUI("Following",holder);

                                ((Activity)context).runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        notifyItemChanged(pos);
                                    }
                                });
                            }else if(object.getString("reason").equalsIgnoreCase("unfollowed")){
                                list.get(pos).setSentRequest(false);
                                toastOnMain("Unfollowed "+list.get(pos).getFullname());
//                                setFollowUI("Follow",holder);
                                ((Activity)context).runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        notifyDataSetChanged();
                                    }
                                });
                            }else if(object.getString("reason").equalsIgnoreCase("exists")){
                                list.get(pos).setSentRequest(true);
                                toastOnMain("You are now following "+list.get(pos).getFullname());
//                                setFollowUI("Following",holder);

                                ((Activity)context).runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        notifyItemChanged(pos);
                                    }
                                });
                            }

                        }catch (JSONException e){
                            toastOnMain("Follow failed!");
                        }
                    }
                });
            }
        });

        thread.start();
    }

    protected void setFollowUI(final String text, final ViewHolder holder){
        ((Activity)context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                holder.follow.setText(text);
            }
        });
    }

    protected void toastOnMain(final String msg){
        ((Activity)context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context,msg,Toast.LENGTH_LONG).show();
            }
        });
    }
    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView uname, follow;
        ImageView image;


        public ViewHolder(View itemView) {
            super(itemView);
            uname = itemView.findViewById(R.id.sugUname);
            follow = itemView.findViewById(R.id.sugFollowBtn);
            image = itemView.findViewById(R.id.sugProfImg);
        }
    }

}
