package com.resercho.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.resercho.POJO.ModelSuggested;
import com.resercho.ProfileActivity;
import com.resercho.R;

import java.util.List;

public class AdapterFollower extends RecyclerView.Adapter<AdapterFollower.ViewHolder> {

    private Context context;
    private List<ModelSuggested> list;

    public AdapterFollower(Context context, List<ModelSuggested> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_follower_person, viewGroup, false);
        return new AdapterFollower.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        Log.d("Resercho","Suggested onBindView :" +list.get(position).getUsername());
        holder.uname.setText(list.get(position).getFullname().substring(0,1).toUpperCase() + list.get(position).getFullname().substring(1));
        holder.username.setText(list.get(position).getUsername());

        Glide.with(context)
                .load(list.get(position).getProfUrl()
                )
                .asBitmap()
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

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView uname, username;
        ImageView image;


        public ViewHolder(View itemView) {
            super(itemView);
            uname = itemView.findViewById(R.id.fullname);
            username = itemView.findViewById(R.id.username);
            image = itemView.findViewById(R.id.profPic);
        }
    }

}
