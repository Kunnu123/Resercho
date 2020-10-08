package com.resercho.Adapters;

import android.content.Context;
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
import com.resercho.POJO.ModelJoinedGroups;
import com.resercho.R;
import com.resercho.UtilityMethods;

import java.util.List;

public class AdapterComments extends RecyclerView.Adapter<AdapterComments.ViewHolder> {

    private Context context;
    private List<ModelJoinedGroups.ModelComment> list;

    public AdapterComments(Context context, List<ModelJoinedGroups.ModelComment> list) {
        this.context = context;
        this.list = list;
        Log.d("Resercho","AdapterGroup const HomeFrag size:"+list.size() + " itemcount:"+getItemCount());
    }

    public List<ModelJoinedGroups.ModelComment> getList(){
        return list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_comment, viewGroup, false);
        return new AdapterComments.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        try {
            holder.title.setText(list.get(position).getComname());
            holder.time.setText(UtilityMethods.getAgoTimeFromTimestamp(UtilityMethods.getTimeStamp(list.get(position).getTime())));
            holder.comment.setText(list.get(position).getComment());
            //
            Glide.with(context)
                    .load(list.get(position).getComprof())
                    .asBitmap()
                    .placeholder(R.mipmap.ic_launcher)
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                            holder.image.setImageBitmap(resource);
                        }
                    });
        }catch (Exception e){
            Log.d("Resercho","HomeFrag bindView:"+e);
        }
    }

    public void addComment(ModelJoinedGroups.ModelComment modelComment){
        list.add(modelComment);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView title, time,comment;
        ImageView image;

        public ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.comUname);
            time = itemView.findViewById(R.id.comTime );
            image = itemView.findViewById(R.id.comProf);
            comment = itemView.findViewById(R.id.comText);
        }
    }

}
