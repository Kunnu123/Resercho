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
import com.resercho.POJO.ModelNotif;
import com.resercho.R;
import com.resercho.UtilityMethods;

import java.util.List;

public class AdapterNotifs extends RecyclerView.Adapter<AdapterNotifs.ViewHolder> {

    private Context context;
    private List<ModelNotif> list;

    public AdapterNotifs(Context context, List<ModelNotif> list) {
        this.context = context;
        this.list = list;
        Log.d("Resercho","AdapterGroup const HomeFrag size:"+list.size() + " itemcount:"+getItemCount());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_notification, viewGroup, false);
        return new AdapterNotifs.ViewHolder(view);
    }

    protected String getFormatted(String type){
        if(type.equalsIgnoreCase("like"))
            return "liked your post";
        if(type.equalsIgnoreCase("comment"))
            return "commented on a post";
        if(type.equalsIgnoreCase("follow"))
            return "started following you";

        return "";
    }
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        try {

            String formatted = list.get(position).getProfName()+" "+getFormatted(list.get(position).getType());
            holder.title.setText(formatted);

            String time = (Long.parseLong(UtilityMethods.getServerTimeStamp(list.get(position).getTime()))/1000)+"";
            holder.time.setText(UtilityMethods.getAgoTimeFromTimestamp(time));

            //
            Glide.with(context)
                    .load(list.get(position).getProfUrl())
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

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView title, time;
        ImageView image;

        public ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.notifMsg);
            time = itemView.findViewById(R.id.notifTime );
            image = itemView.findViewById(R.id.profPic);
        }
    }

}
