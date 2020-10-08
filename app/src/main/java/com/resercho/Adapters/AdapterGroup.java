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
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import com.resercho.GroupActivity;
import com.resercho.GroupViewerActivity;
import com.resercho.NetworkHandler;
import com.resercho.POJO.ModelGroup;

import com.resercho.R;
import com.resercho.UtilityMethods;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AdapterGroup extends RecyclerView.Adapter<AdapterGroup.ViewHolder> {

    private Context context;
    private List<ModelGroup> list;
    boolean joined = false;

    public AdapterGroup(Context context, List<ModelGroup> list) {
        this.context = context;
        this.list = list;
        Log.d("Resercho","AdapterGroup const HomeFrag size:"+list.size() + " itemcount:"+getItemCount());
    }

    public boolean isJoined() {
        return joined;
    }

    public void setJoined(boolean joined) {
        this.joined = joined;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_group_v3, viewGroup, false);
        return new AdapterGroup.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        try {
            holder.title.setText(list.get(position).getName());

            String time = (Long.parseLong(UtilityMethods.getServerTimeStamp(list.get(position).getTime()))/1000)+"";
            holder.time.setText(UtilityMethods.getAgoTimeFromTimestamp(time));

            if(list.get(position).isHasJoined()) {
                holder.join.setText("You're member");
                holder.join.setOnClickListener(null);
            }
            else {
                holder.join.setText("Join Group");
                holder.join.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        holder.join.setText("You're member");
                        joinGroup(list.get(position),list.get(position).isHasJoined(),position);
                    }
                });
            }

            //
            Glide.with(context)
                    .load(list.get(position).getImageUrl())
                    .asBitmap()
                    .placeholder(R.drawable.downloading)
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                            holder.image.setImageBitmap(resource);
                        }
                    });




            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ModelGroup g= list.get(position);
                    Intent intent;
                    if(isJoined())
                         intent = new Intent(context, GroupActivity.class);
                    else
                         intent = new Intent(context, GroupViewerActivity.class);

                    intent.putExtra("about",g.getAbout());
                    intent.putExtra("category",g.getCategory());
                    intent.putExtra("gid",g.getGid());
                    intent.putExtra("imgurl",g.getImageUrl());
                    intent.putExtra("name",g.getName());
                    intent.putExtra("profpic",g.getProfPic());
                    intent.putExtra("username",g.getUsername());
                    intent.putExtra("bio",g.getUserBio());
                    intent.putExtra("time",g.getTime());
                    intent.putExtra("uid",g.getUid());
                    intent.putExtra("joined",g.isHasJoined());
                    context.startActivity(intent);
                }
            });

        }catch (Exception e){
            Log.d("Resercho","HomeFrag bindView:"+e);
        }
    }
    protected void joinGroup(final ModelGroup group,final boolean remove, final int pos){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                NetworkHandler.joinGroup(context, group, remove, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        toastOnMain("Join failed! Try again later");
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String resp = response.body().string();

                        try{
                            JSONObject object = new JSONObject(resp);
                            if(object.getInt("success")==1){
                                if(object.getString("reason").equalsIgnoreCase("deleted")) {

                                    list.get(pos).setHasJoined(false);
                                }
                                else {
                                    list.get(pos).setHasJoined(true);
                                    toastOnMain("Joined group");
                                }
                                setJoined(pos);
                            }else{
                                toastOnMain("Request failed!");
                            }
                        }catch (JSONException e){
                            toastOnMain("Join failed!");
                        }
                    }
                });
            }
        });

        thread.start();
    }

    protected void toastOnMain(final String msg){
        ((Activity)context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context,msg,Toast.LENGTH_LONG).show();

            }
        });
    }

    protected void setJoined(final int pos){


            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(isJoined()){
                        list.remove(pos);
                        notifyItemRemoved(pos);
                    }else
                        notifyItemChanged(pos);
                }
            });

    }




    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

            TextView title, desc,time,join;
            ImageView image;

            public ViewHolder(View itemView) {
                super(itemView);
                join = itemView.findViewById(R.id.joinGroupBtn);
                title = itemView.findViewById(R.id.title);

                time = itemView.findViewById(R.id.timestamp);
                image = itemView.findViewById(R.id.image);
            }
    }

}
