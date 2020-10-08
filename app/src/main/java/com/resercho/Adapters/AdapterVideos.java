package com.resercho.Adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.resercho.DataHandler;
import com.resercho.HomeActivity;
import com.resercho.NetworkHandler;
import com.resercho.POJO.ModelWork;
import com.resercho.ProfileActivity;
import com.resercho.R;
import com.resercho.SearchActivity;
import com.resercho.UtilityMethods;
import com.resercho.WorkActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static com.resercho.NetworkHandler.root_dir;
import static com.resercho.SignInActivity.TAG;

public class AdapterVideos extends RecyclerView.Adapter<AdapterVideos.ViewHolder> {

    private Context context;
    private List<ModelWork> list;

    boolean horizontal = true;
    boolean profileAct = false;

    public AdapterVideos(Context context, List<ModelWork> list) {
        this.context = context;
        this.list = list;
        Log.d("Resercho","AdapterWork const HomeFrag size:"+list.size() + " itemcount:"+getItemCount());
    }

    public boolean isProfileAct() {
        return profileAct;
    }

    public void setProfileAct(boolean profileAct) {
        this.profileAct = profileAct;
    }

    public boolean isHorizontal() {
        return horizontal;
    }

    public void setHorizontal(boolean horizontal) {
        this.horizontal = horizontal;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view;

        if(horizontal)
            view= LayoutInflater.from(context).inflate(R.layout.item_work_horiz_v2, viewGroup, false);
        else
            view= LayoutInflater.from(context).inflate(R.layout.item_model_work, viewGroup, false);

        return new AdapterVideos.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

            if(list.get(position).getHeading().equalsIgnoreCase("null"))
                holder.title.setText("No title provided");
            else
                holder.title.setText(list.get(position).getHeading().trim());

            if(list.get(position).getPost_of().equalsIgnoreCase(DataHandler.getUserId(context))){
                holder.options.setVisibility(View.VISIBLE);
            }else{
                holder.options.setVisibility(View.GONE);
            }

            String time = (Long.parseLong(UtilityMethods.getServerTimeStamp(list.get(position).getTime()))/1000)+"";
            holder.sub.setText(UtilityMethods.getAgoTimeFromTimestamp(time));
            holder.uname.setText(list.get(position).getUsername().trim());

            if(!isHorizontal())
            holder.desc.setText(list.get(position).getStatus());
            holder.letter.setText(list.get(position).getUsername().substring(0,1).toUpperCase());

            holder.likeCount.setText(UtilityMethods.getFormatCount(list.get(position).getLikes()));
            holder.commentCount.setText(UtilityMethods.getFormatCount(list.get(position).getComments()));

            if(!horizontal && list.get(position).getPost_of().equalsIgnoreCase(DataHandler.getUserId(context))) {
                holder.citeCount.setText(UtilityMethods.getFormatCount(list.get(position).getCiteCount()) + " Citations");
                holder.citeCount.setVisibility(View.VISIBLE);
                holder.saveText.setText(UtilityMethods.getFormatCount(list.get(position).getSavedCount()));
                holder.shareCount.setText(UtilityMethods.getFormatCount(list.get(position).getShareCount()));
            }

            if(list.get(position).getUserBio()==null||list.get(position).getUserBio().equalsIgnoreCase("null")||list.get(position).getUserBio().length()<1){
                holder.bioline.setVisibility(View.GONE);
                holder.title.setTextSize(14.0f);
                holder.title.setPadding(4,4,4,0);
            }else
                holder.bioline.setText(list.get(position).getUserBio());

            int colorLike = R.color.colorLike;

            Log.d("savit","savit >> "+list.get(position).getUsername() + " Saved:"+list.get(position).isSaved());
            if(list.get(position).isSaved()){
                holder.saveImg.setImageTintList(ContextCompat.getColorStateList(context,colorLike));
                holder.saveText.setTextColor(ContextCompat.getColorStateList(context,colorLike));
                if(list.get(position).getPost_of().equalsIgnoreCase(DataHandler.getUserId(context))){
                    if(horizontal){
                        holder.saveText.setText("Saved");
                    }
                }else{
                    holder.saveText.setText("Saved");
                }

            }else{
                holder.saveImg.setImageTintList(ContextCompat.getColorStateList(context,R.color.gray));
                holder.saveText.setTextColor(ContextCompat.getColorStateList(context,R.color.gray));
                if(list.get(position).getPost_of().equalsIgnoreCase(DataHandler.getUserId(context))){
                    if(horizontal){
                        holder.saveText.setText("Save");
                    }
                }else{
                    holder.saveText.setText("Save");
                }
            }

        if(list.get(position).getMedia_src()!=null && !list.get(position).getMedia_src().equalsIgnoreCase("null")) {
            String mime = UtilityMethods.getMimeFromUrl(list.get(position).getMedia_src());
            if(mime!=null) {
                if (mime.equalsIgnoreCase("pdf")) {
                    list.get(position).setPdflist(new ArrayList<String>(Arrays.asList(list.get(position).getMedia_src().split(","))));
                    Log.d("Resercho", "CJson Setted list of PDFS" + list.get(position).getPdflist().size());
                } else {
                    list.get(position).setImglist(new ArrayList<String>(Arrays.asList(list.get(position).getMedia_src().split(","))));
                    Log.d("Resercho", "CJson Setted list of IMAGES : " + list.get(position).getImglist().size());
                }
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



            holder.uname.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ProfileActivity.class);
                    intent.putExtra("uid",list.get(position).getPost_of());
                    context.startActivity(intent);
                }
            });

            // Change the UI if liked
            if(list.get(position).isLike()) {
                Log.d(TAG,"isLik inside:"+position+" : "+list.get(position).isLike());
                holder.likethumb.setImageTintList(ContextCompat.getColorStateList(context, colorLike));
                holder.likeCount.setTextColor(ContextCompat.getColorStateList(context, colorLike));
            }else{
                holder.likethumb.setImageTintList(ContextCompat.getColorStateList(context, R.color.grayBgLt));
                holder.likeCount.setTextColor(ContextCompat.getColorStateList(context, R.color.gray));
            }

            if(!horizontal&&list.get(position).getImglist()!=null && list.get(position).getImglist().size()>0){
                Log.d("jinki","Img list is : "+ list.get(position).getImglist().size());
                showPhotoRecycler(list.get(position).getImglist(),holder);
            }else{
                if(!horizontal)
                    holder.multiPhotoRv.setVisibility(View.GONE);
            }
            /////////////////////////////////////
            /// Cover Image


        /*

        if(isCover){
            setCover()
        }else{
            if(hasMediaPics()){
                setPicOne(1)
            }else{
                setReserchoLogo()
            }
        }

        if(mediaIsVideo){
            attach.setText("Vide"))
        }

         */

        // 1. Cover Pic
        Log.d("Longkit","Cover Image for : "+list.get(position).getHeading());
        if(!list.get(position).getCover_img().trim().equalsIgnoreCase(root_dir+"null")){
            Log.d("Longkit","Loading Cove Image : "+list.get(position).getCover_img());

            Glide.with(context)
                    .load(list.get(position).getCover_img())
                    .asBitmap()
                    .error(R.drawable.resercho)
                    .placeholder(R.drawable.resercho)
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {

                            holder.image.setImageBitmap(resource);
                            holder.image.setScaleType(ImageView.ScaleType.CENTER_CROP);
                            holder.image.clearColorFilter();
                            holder.image.setAlpha(1.0f);
                        }
                    });


            if(!horizontal){
                holder.multiPhotoRv.setVisibility(View.GONE);
            }
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
                            .placeholder(R.drawable.resercho)
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
                holder.image.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.resercho));
                holder.image.setScaleType(ImageView.ScaleType.CENTER_CROP);
                holder.image.setAlpha(0.8f);
            }
        }


        // 2. Attachments Cues

        if(list.get(position).getPdflist()!=null&&list.get(position).getPdflist().size()>0){
            holder.imgCounter.setText(list.get(position).getPdflist().size() + " PDF Attached");
            holder.imgCounter.setVisibility(View.VISIBLE);
        }else{
            String mime = UtilityMethods.getMimeFromUrl(list.get(position).getMedia_src());
            holder.imgCounter.setVisibility(View.VISIBLE);
            if(mime!=null){
                if(UtilityMethods.isImageMime(mime)){
                    holder.imgCounter.setText(list.get(position).getImglist().size()+" Pictures Attached");
                }else if(UtilityMethods.isAudioMime(mime)){
                    holder.imgCounter.setText("Audio Attached");
                }else if(UtilityMethods.isVideoMime(mime)){
                    holder.imgCounter.setText("Video Attached");
                }else{
                    holder.imgCounter.setText("No Attachments");
                }
            }else{
                holder.imgCounter.setText("No attachments");
            }

        }



        Glide.with(context)
                            .load(list.get(position).getProfpic())
                            .asBitmap()
                            .placeholder(R.drawable.resercho)
                            .into(new SimpleTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                    holder.prof.setImageBitmap(resource);
                                    holder.prof.setScaleType(ImageView.ScaleType.CENTER_CROP);
                                    holder.prof.clearColorFilter();
                                    holder.prof.setAlpha(1.0f);
                                }
                            });


                    int height = HomeActivity.screen_height / 3;
                    int width = HomeActivity.screen_width - 100;

//                    int horiz = 8;
//                    int verti = 8;
//
//                    if (horizontal) {
//                        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(width, 320);
//                        layoutParams.setMargins(horiz, verti, horiz, verti);
//                        holder.mainlayout.setLayoutParams(layoutParams);
//                    }

            initSocial(holder, position);

            holder.options.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDialog(position,list.get(position).getId());
                }
            });

    }

    protected void showDialog(final int pos, final String pid){
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle(list.get(pos).getHeading())
                .setMessage("Are your sure to delete this content?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setCancelable(true)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        deletePost(pos,pid);
                    }})
                .setNegativeButton(android.R.string.no, null).show();
        dialog.setInverseBackgroundForced(true);
        dialog.setCanceledOnTouchOutside(true);
    }

    protected void showPhotoRecycler(final List<String> list, ViewHolder holder){

            if(list.size()>0) {
                if (list.size() > 1) {
                    holder.imgCounter.setText(list.size() + " Images");
                    holder.imgCounter.setVisibility(View.VISIBLE);
                }
                holder.multiPhotoRv.setVisibility(View.VISIBLE);
                PhotoAdapter photoAdapter = new PhotoAdapter(context, list);
                photoAdapter.setUseGlide(true);
                photoAdapter.setLongRv(true);
                LinearLayoutManager layman = new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false);
                holder.multiPhotoRv.setLayoutManager(layman);
                holder.multiPhotoRv.setAdapter(photoAdapter);
            }else{
                holder.multiPhotoRv.setVisibility(View.GONE);
            }

    }

    protected void deletePost(final int pos, final String pid){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                NetworkHandler.deletePost(context, pid, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.d("Debitt","onFailure  :"+e);
                        toastOnMain("Failed to delete");
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String resp = response.body().string();
                        Log.d("Debitt","onResponse  :"+resp);
                        try{
                            JSONObject jsonObject = new JSONObject(resp);
                            if(jsonObject.getInt("success")==1 && jsonObject.getString("reason").equalsIgnoreCase("deleted")){
                                ((Activity)context).runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if(pos<list.size()) {
                                            list.remove(pos);
                                            notifyItemRemoved(pos);
                                        }
                                    }
                                });
                            }else{
                                toastOnMain("Failed to delete");
                            }

                        }catch (JSONException e){
                            toastOnMain("Failed to delete");
                            Log.d("Debitt","JSON Exception : "+e);
                        }
                    }
                });
            }
        });

        thread.start();
    }

    protected void initSocial(ViewHolder holder, final int position){
        holder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendlike(!list.get(position).isLike(),list.get(position).getId(),position);
            }
        });

        holder.share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(list.get(position).isSenabled()) {
                    sendShared(list.get(position).getId(),position);
                    share(list.get(position).getId());
                }else{
                    Toast.makeText(context,"Sharing is disabled on this post by the owner",Toast.LENGTH_SHORT).show();
                }
            }
        });

        holder.save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendSaving(list.get(position).getId(),position);
            }
        });



        holder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (profileAct)
                        ((ProfileActivity) context).showCommentBox(list.get(position).getId());
                    else
                        ((HomeActivity) context).showCommentBox(list.get(position).getId());
                }catch (ClassCastException e){
                    ((SearchActivity) context).showCommentBox(list.get(position).getId());
                }
            }
        });
    }

    protected void sendSaving(final String pid, final int pos){

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                NetworkHandler.sendSaved(context, pid, list.get(pos).isSaved(), new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.d("Res","savit onFailure:"+e);
                        toastOnMain("Failed Saving");
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String resp = response.body().string();
                        Log.d("Res","savit onResponse:"+resp);
                        try{
                            JSONObject jsonObject = new JSONObject(resp);
                            if(jsonObject.getInt("success")==1){
                                if(jsonObject.has("deleted")){
                                    list.get(pos).setSaved(false);
                                }else{
                                    list.get(pos).setSaved(true);
                                }

                            }else{
                                if(jsonObject.getString("reason").equalsIgnoreCase("exists")){
                                    toastOnMain("Already Saved!");
                                }else{
                                    toastOnMain("Something went wrong");
                                }
                            }

                            if(jsonObject.has("count")){
                                list.get(pos).setSavedCount(jsonObject.getLong("count"));
                            }
                            ((Activity)context).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    notifyItemChanged(pos);
                                }
                            });
                        }catch (JSONException e){
                            Log.d("Res","savit JSON Exception :"+e);
                        }
                    }
                });
            }
        });

        thread.start();
    }


    protected void sendShared(final String pid, final int pos){

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                NetworkHandler.sendShared(context, pid, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.d("Res","savit onFailure:"+e);
                        toastOnMain("Failed Saving");
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String resp = response.body().string();
                        Log.d("Res","savit onResponse:"+resp);
                        try{
                            JSONObject jsonObject = new JSONObject(resp);

                            if(jsonObject.has("count")){
                                list.get(pos).setShareCount(jsonObject.getLong("count"));
                            }
                            ((Activity)context).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    notifyItemChanged(pos);
                                }
                            });
                        }catch (JSONException e){
                            Log.d("Res","savit JSON Exception :"+e);
                        }
                    }
                });
            }
        });

        thread.start();
    }


    protected void share(String id){

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, NetworkHandler.post_share_link+id);
        context.startActivity(intent);
    }

    protected void toastOnMain(final String msg){
        ((Activity)context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context,msg,Toast.LENGTH_LONG).show();
            }
        });
    }

    protected void sendlike(final boolean like, final String pid,final int pos){

        if(like) {
            list.get(pos).setLike(true);
            list.get(pos).setLikes(list.get(pos).getLikes() + 1);
        }else{
            list.get(pos).setLike(false);
            long likes = list.get(pos).getLikes();
            list.get(pos).setLikes(likes==0?0:likes-1);
        }

        notifyItemChanged(pos);

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                NetworkHandler.sendlike(context, like, pid, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.d(TAG,"sendlie onFailure:"+e);
                        toastOnMain("Like failed!");
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String resp = response.body().string();
                        Log.d(TAG,"sendlie onResponse:"+resp);
                        try{
                            JSONObject jsonObject = new JSONObject(resp);

                            if(jsonObject.getString("reason").equalsIgnoreCase("liked")){
                                list.get(pos).setLike(true);
                                list.get(pos).setLikes(jsonObject.getLong("likes"));
                                Log.d("Likint","Liked like count : "+jsonObject.getLong("likes") + " Response:"+resp);
                            }else if (jsonObject.getString("reason").equalsIgnoreCase("unliked")){
                                list.get(pos).setLike(false);
                                Log.d("Likint","Disliked like count : "+jsonObject.getLong("likes") + " Response:"+resp);
                                list.get(pos).setLikes(jsonObject.getLong("likes"));
                            }else if (jsonObject.getString("reason").equalsIgnoreCase("exists")){
                                list.get(pos).setLike(true);
                                list.get(pos).setLikes(jsonObject.getLong("likes"));
                                Log.d("Likint","Exists like count : "+jsonObject.getLong("likes") + " Response:"+resp);
                            }
//                            ((Activity)context).runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    notifyItemChanged(pos);
//                                }
//                            });



                        }catch (JSONException e){
                            toastOnMain("Like failed!");
                            Log.d(TAG,"sendlie JSON Exception:"+e);
                        }
                    }
                });
            }
        });

        thread.start();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView title, sub,uname,desc;
        ImageView image,prof;
        CardView mainlayout;

        View like, comment,share, save, mimeCont;
        ImageView likethumb,saveImg, mimeIcon;
        TextView letter,bioline,saveText, mimeText,likeCount,commentCount;
        View options;
        RecyclerView multiPhotoRv;
        TextView imgCounter, citeCount, shareCount;

        public ViewHolder(View itemView) {
            super(itemView);

                options  = itemView.findViewById(R.id.options);

            title = itemView.findViewById(R.id.workTitle);
            sub = itemView.findViewById(R.id.workTimestamp);
            image = itemView.findViewById(R.id.workImg);
            uname = itemView.findViewById(R.id.workUsername);
            mainlayout = itemView.findViewById(R.id.workMainLay);
            letter = itemView.findViewById(R.id.letterHead);
            if(!isHorizontal()) {
                desc = itemView.findViewById(R.id.workDesc);
                multiPhotoRv = itemView.findViewById(R.id.multiPhotoRv);
                citeCount = itemView.findViewById(R.id.citeCount);
                shareCount = itemView.findViewById(R.id.shareCount);
            }
            likeCount = itemView.findViewById(R.id.liketxt);
            commentCount = itemView.findViewById(R.id.commText);
            imgCounter = itemView.findViewById(R.id.imgCounter);
            // Social
            like = itemView.findViewById(R.id.likeCont);
            likethumb = itemView.findViewById(R.id.likethumb);
            comment = itemView.findViewById(R.id.commentCont);
            share= itemView.findViewById(R.id.shareCont);
            save = itemView.findViewById(R.id.saveCont);
            prof = itemView.findViewById(R.id.profpic);
            bioline = itemView.findViewById(R.id.bioline);
            saveImg = itemView.findViewById(R.id.saveImg);
            saveText = itemView.findViewById(R.id.saveText);
                mimeCont = itemView.findViewById(R.id.mimeCont);
                mimeText = itemView.findViewById(R.id.mimeText);
                mimeIcon = itemView.findViewById(R.id.cueImage);
        }
    }

}
