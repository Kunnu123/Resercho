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
import com.resercho.GroupActivity;
import com.resercho.POJO.ModelJoinedGroups;
import com.resercho.R;
import com.resercho.UtilityMethods;

import java.util.List;

public class AdapterGroupJoined extends RecyclerView.Adapter<AdapterGroupJoined.ViewHolder> {

    private Context context;
    private List<ModelJoinedGroups> list;

    public AdapterGroupJoined(Context context, List<ModelJoinedGroups> list) {
        this.context = context;
        this.list = list;
        Log.d("Resercho","AdapterGroup const HomeFrag size:"+list.size() + " itemcount:"+getItemCount());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_group_v3, viewGroup, false);
        return new AdapterGroupJoined.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        try {
            holder.title.setText(list.get(position).getName());

            String time = (Long.parseLong(UtilityMethods.getServerTimeStamp(list.get(position).getTimestamp()))/1000)+"";
            holder.time.setText(UtilityMethods.getAgoTimeFromTimestamp(time));

            holder.join.setText("You're member");
                       //
            Glide.with(context)
                    .load(list.get(position).getImage())
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
                    ModelJoinedGroups g= list.get(position);
                    Intent intent = new Intent(context, GroupActivity.class);
                    intent.putExtra("about",g.getAbout());
                    intent.putExtra("gid",g.getGid());
                    intent.putExtra("imgurl",g.getImage());
                    intent.putExtra("name",g.getName());
                    intent.putExtra("uid",g.getUid());
                    context.startActivity(intent);
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
