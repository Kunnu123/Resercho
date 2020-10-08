package com.resercho.Adapters;

import android.content.Context;
import android.media.Image;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.resercho.BuildConfig;
import com.resercho.R;


import java.io.File;
import java.util.List;

import static com.resercho.NetworkHandler.root_dir;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.ViewHolder> {

    private Context context;
    private List<String> list;
    boolean useGlide;
    boolean longRv;


    public boolean isLongRv() {
        return longRv;
    }

    public void setLongRv(boolean longRv) {
        this.longRv = longRv;
    }

    public List<String> getList() {
        return list;
    }

    public void setList(List<String> list) {
        this.list = list;
    }

    public PhotoAdapter(Context context, List<String> list) {
        useGlide = false;
        this.longRv = false;
        this.context = context;
        this.list = list;
        Log.d("Resercho","AdapterGroup const HomeFrag size:"+list.size() + " itemcount:"+getItemCount());
    }

    public boolean isUseGlide() {
        return useGlide;
    }

    public void setUseGlide(boolean useGlide) {
        this.useGlide = useGlide;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view;

        if(longRv)
            view = LayoutInflater.from(context).inflate(R.layout.item_long_rv_img, viewGroup, false);
        else {
            view = LayoutInflater.from(context).inflate(R.layout.item_photo, viewGroup, false);
        }

        return new PhotoAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        if(!useGlide) {
            File mediaFile = new File(list.get(position));
            Uri mediaFileUri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", mediaFile);
            Glide.with(context).load(mediaFileUri).into(holder.imageView);

        }else{

            Log.d("Chedkk","URL            :: "+list.get(position));
            Glide.with(context).load(root_dir + list.get(position)).placeholder(R.drawable.resercho).into(holder.imageView);
            if(longRv){
                Glide.with(context).load(root_dir + list.get(position)).into(holder.coverUp);
            }
        }

        if(longRv)
            holder.remove.setVisibility(View.GONE);

        holder.remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                list.remove(holder.getAdapterPosition());
                notifyItemRemoved(holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView,coverUp,remove;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.img);
            remove = itemView.findViewById(R.id.remove);

            if(longRv){
                coverUp = itemView.findViewById(R.id.coverup);
            }
        }
    }

}