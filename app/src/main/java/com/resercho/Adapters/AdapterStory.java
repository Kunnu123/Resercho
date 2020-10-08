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
import com.resercho.POJO.ModelStoryV2;
import com.resercho.R;
import com.resercho.StoryHolder;

import java.util.List;

public class AdapterStory extends RecyclerView.Adapter<AdapterStory.ViewHolder> {

    private Context context;
    private List<ModelStoryV2> list;

    public AdapterStory(Context context, List<ModelStoryV2> list) {
        this.context = context;
        this.list = list;
        Log.d("Resercho","AdapterGroup const HomeFrag size:"+list.size() + " itemcount:"+getItemCount());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_story, viewGroup, false);
        return new AdapterStory.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

                holder.uname.setText(list.get(position).getUsername());

                String img = list.get(position).getProfUrl();
                Log.d("Debin","Debint :" +img);
                Glide.with(context)
                        .load(img)
                        .asBitmap()
                        .placeholder(R.drawable.downloading)
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                holder.imageView.setImageBitmap(resource);
                                holder.imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                                holder.imageView.clearColorFilter();
                                holder.imageView.setAlpha(1.0f);
                            }
                        });


            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, StoryHolder.class);
                    intent.putExtra("uid",list.get(position).getAdded_by());
                    intent.putExtra("scount",list.get(position).getCount());
                    intent.putExtra("profile",list.get(position).getProfUrl());
                    intent.putExtra("name",list.get(position).getUsername());
                    context.startActivity(intent);
                }
            });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView uname;
        ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            uname = itemView.findViewById(R.id.profName);
            imageView = itemView.findViewById(R.id.profPic);
        }
    }

}
