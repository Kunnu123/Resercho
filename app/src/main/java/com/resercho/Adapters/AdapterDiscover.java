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
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.resercho.CommonMethods;
import com.resercho.DataHandler;
import com.resercho.POJO.ModelWork;
import com.resercho.R;
import com.resercho.UtilityMethods;
import com.resercho.WorkActivity;
import java.util.List;

import static com.resercho.NetworkHandler.root_dir;

public class AdapterDiscover extends RecyclerView.Adapter<AdapterDiscover.ViewHolder> {

    private Context context;
    private List<ModelWork> list;

    public AdapterDiscover(Context context, List<ModelWork> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_grid_discover, viewGroup, false);
        return new AdapterDiscover.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        if (DataHandler.isUnivMode(context)) {
            holder.cardView.setCardBackgroundColor(ContextCompat.getColor(context,R.color.colorPrimaryDark));
        }else{
            holder.cardView.setCardBackgroundColor(ContextCompat.getColor(context,R.color.colorAccent2));
        }
        holder.title.setText(list.get(position).getHeading().trim());
        holder.image.setScaleType(ImageView.ScaleType.CENTER_CROP);

        if(!list.get(position).getCover_img().trim().equalsIgnoreCase(root_dir+"null")){
            Log.d("Longkit","Loading Cove Image : "+list.get(position).getCover_img());
            Glide.with(context)
                    .load(list.get(position).getCover_img())
                    .asBitmap()
                    .placeholder(R.drawable.downloading)
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {

                            holder.image.setImageBitmap(resource);
                            holder.image.setScaleType(ImageView.ScaleType.CENTER_CROP);
                            holder.image.clearColorFilter();
                            holder.image.setAlpha(1.0f);
                        }
                    });


        }else{
            if(list.get(position).getImglist()!=null&&list.get(position).getImglist().size()>0){
                Log.d("Longkit","Loading Image Image : "+list.get(position).getImglist().get(0));

                if (list.get(position).getImglist().get(0).contains("wav")
                        || list.get(position).getImglist().get(0).contains("mkv")
                        || list.get(position).getImglist().get(0).contains("mp3")
                        || list.get(position).getImglist().get(0).contains("mp4")
                        || list.get(position).getImglist().get(0).contains("m4a")
                ) {
                    holder.image.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.resercho));
                    holder.image.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    holder.image.setAlpha(0.8f);
                }else {
                    Glide.with(context)
                            .load(root_dir + list.get(position).getImglist().get(0))
                            .asBitmap()
                            .error(R.drawable.resercho)
                            .placeholder(R.drawable.downloading)
                            .into(new SimpleTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                    holder.image.setImageBitmap(resource);
                                    holder.image.setScaleType(ImageView.ScaleType.CENTER_CROP);
                                    holder.image.clearColorFilter();
                                    holder.image.setAlpha(1.0f);
                                }
                            });
                }


            }else{
                Log.d("Longkit","Loading Setting Resercho ");
//                String mime = UtilityMethods.getMimeFromUrl(list.get(position).getMedia_src());
//                if(mime!=null&&(mime.equalsIgnoreCase("png")||mime.equalsIgnoreCase("jpeg")||mime.equalsIgnoreCase("mp4")
//                        ||mime.equalsIgnoreCase("jpg")||mime.equalsIgnoreCase("mp3")||mime.equalsIgnoreCase("pdf")||mime.equalsIgnoreCase("avi"))) {
//                    holder.image.setVisibility(View.VISIBLE);
//                    holder.image.setImageDrawable(ContextCompat.getDrawable(context, CommonMethods.getIconIdFromMime(mime)));
////                    holder.image.setImageTintList(ContextCompat.getColorStateList(context, R.color.gray));
//                    holder.image.setPadding(30, 30, 30, 30);
//                    holder.image.setScaleType(ImageView.ScaleType.CENTER);
//                }else{
////               holder.image.setVisibility(View.GONE);
                    holder.image.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.resercho));
//                }
            }
        }



        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ModelWork m = list.get(position);
                Intent intent = new Intent(context, WorkActivity.class);
                intent.putExtra("media_src",m.getMedia_src());
                intent.putExtra("heading",m.getHeading());
                intent.putExtra("uname",m.getUsername());
                intent.putExtra("desc",m.getStatus());
                intent.putExtra("uid",m.getPost_of());
                intent.putExtra("pid",m.getId());
                intent.putExtra("cover",m.getCover_img());
                intent.putExtra("prof",m.getProfpic());
                intent.putExtra("bio",m.getUserBio());
                intent.putExtra("pid",m.getId());
                intent.putExtra("time",m.getTime());
                intent.putExtra("saved",m.isSaved());
                intent.putExtra("liked",m.isLike());
                intent.putExtra("doi",m.getDoi());
                intent.putExtra("author",m.getAuthor());
                intent.putExtra("dop",m.getDop());
                intent.putExtra("denabled",m.isDenabled());
                intent.putExtra("senabled",m.isSenabled());
                intent.putExtra("likes",m.getLikes());
                intent.putExtra("shares",m.getShareCount());
                intent.putExtra("cites",m.getCiteCount());
                intent.putExtra("saves",m.getSavedCount());
                intent.putExtra("type",m.getType());
                intent.putExtra("category",m.getCategory());
                context.startActivity(intent);
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


        ImageView image;
        TextView title;
        CardView cardView;

        public ViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.imageViewDis);
            title = itemView.findViewById(R.id.disTitle);
            cardView = itemView.findViewById(R.id.mainCard);
        }
    }

}
