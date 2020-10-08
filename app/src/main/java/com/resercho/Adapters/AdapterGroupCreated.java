package com.resercho.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.resercho.EditGroupActivity;
import com.resercho.GroupActivity;
import com.resercho.GroupViewerActivity;
import com.resercho.NetworkHandler;
import com.resercho.POJO.ModelGroup;
import com.resercho.POJO.ModelNewGroup;
import com.resercho.R;
import com.resercho.UtilityMethods;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AdapterGroupCreated extends RecyclerView.Adapter<AdapterGroupCreated.ViewHolder> {

    private Context context;
    private List<ModelNewGroup> list;
    boolean joined = false;

    public AdapterGroupCreated(Context context, List<ModelNewGroup> list) {
        this.context = context;
        this.list = list;
        Log.d("Resercho","AdapterGroup const HomeFrag size:"+list.size() + " itemcount:"+getItemCount());
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_group_created, viewGroup, false);
        return new AdapterGroupCreated.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        try {
            holder.title.setText(list.get(position).getName());

            String imgurl = "https://resercho.com/"+list.get(position).getImage();
            Log.d("NewGroup","Adapter : " +imgurl);
            Glide.with(context)
                    .load(imgurl)
                    .asBitmap()
                    .placeholder(R.drawable.downloading)
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                            holder.image.setImageBitmap(resource);
                        }
                    });




            holder.editBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ModelNewGroup g= list.get(position);

                    Intent intent;
                    intent = new Intent(context, EditGroupActivity.class);
                    intent.putExtra("about",g.getAbout());
                    intent.putExtra("category",g.getCatid());
                    intent.putExtra("gid",g.getGid());
                    intent.putExtra("imgurl",g.getImage());
                    intent.putExtra("name",g.getName());
                    intent.putExtra("private",g.isPrivateGroup());
                    intent.putExtra("univ",g.isUniversity());
                    context.startActivity(intent);
                }
            });

        }catch (Exception e){
            Log.d("Resercho","HomeFrag bindView:"+e);
        }
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

            TextView title;
            View editBtn;
            ImageView image;

            public ViewHolder(View itemView) {
                super(itemView);

                title = itemView.findViewById(R.id.title);
                editBtn = itemView.findViewById(R.id.groupEdit);
                image = itemView.findViewById(R.id.image);
            }
    }

}
