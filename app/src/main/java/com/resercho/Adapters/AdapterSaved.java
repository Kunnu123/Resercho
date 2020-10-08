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
import com.resercho.NetworkHandler;
import com.resercho.POJO.ModelSaved;
import com.resercho.POJO.ModelWork;
import com.resercho.ProfileActivity;
import com.resercho.R;
import com.resercho.SavedWorkActivity;
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


public class AdapterSaved extends RecyclerView.Adapter<AdapterSaved.ViewHolder> {

    private Context context;
    private List<ModelSaved> list;

    boolean horizontal = false;

    public AdapterSaved(Context context, List<ModelSaved> list) {
        this.context = context;
        this.list = list;
        Log.d("Resercho","AdapterWork const HomeFrag size:"+list.size() + " itemcount:"+getItemCount());
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
            view= LayoutInflater.from(context).inflate(R.layout.item_model_work_horiz, viewGroup, false);
        else
            view= LayoutInflater.from(context).inflate(R.layout.item_model_work, viewGroup, false);

        return new AdapterSaved.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        if(list.get(position).getWork().getHeading().equalsIgnoreCase("null"))
            holder.title.setText("No title provided");
        else
            holder.title.setText(list.get(position).getWork().getHeading().trim());

        if(list.get(position).getWork().getPost_of().equalsIgnoreCase(DataHandler.getUserId(context))){
            holder.options.setVisibility(View.VISIBLE);
        }else{
            holder.options.setVisibility(View.GONE);
        }

        String time = (Long.parseLong(UtilityMethods.getServerTimeStamp(list.get(position).getWork().getTime()))/1000)+"";
        holder.sub.setText(UtilityMethods.getAgoTimeFromTimestamp(time));
        holder.uname.setText(list.get(position).getWork().getUsername().trim());

        if(!isHorizontal())
            holder.desc.setText(list.get(position).getWork().getStatus());
        holder.letter.setText(list.get(position).getWork().getUsername().substring(0,1).toUpperCase());

        holder.likeCount.setText(UtilityMethods.getFormatCount(list.get(position).getWork().getLikes()));
        holder.commentCount.setText(UtilityMethods.getFormatCount(list.get(position).getWork().getComments()));

        if(!horizontal && list.get(position).getWork().getPost_of().equalsIgnoreCase(DataHandler.getUserId(context))) {
            holder.citeCount.setText(UtilityMethods.getFormatCount(list.get(position).getWork().getCiteCount()) + " Citations");
            holder.citeCount.setVisibility(View.VISIBLE);
            holder.saveText.setText(UtilityMethods.getFormatCount(list.get(position).getWork().getSavedCount()));
            holder.shareCount.setText(UtilityMethods.getFormatCount(list.get(position).getWork().getShareCount()));
        }

        if(list.get(position).getWork().getUserBio()==null||list.get(position).getWork().getUserBio().equalsIgnoreCase("null")||list.get(position).getWork().getUserBio().length()<1){
            holder.bioline.setVisibility(View.GONE);
            holder.title.setTextSize(14.0f);
            holder.title.setPadding(4,4,4,0);
        }else
            holder.bioline.setText(list.get(position).getWork().getUserBio());

        int colorLike = R.color.colorLike;

        Log.d("savit","savit >> "+list.get(position).getWork().getUsername() + " Saved:"+list.get(position).getWork().isSaved());
        if(list.get(position).getWork().isSaved()){
            holder.saveImg.setImageTintList(ContextCompat.getColorStateList(context,colorLike));
            holder.saveText.setTextColor(ContextCompat.getColorStateList(context,colorLike));
            if(list.get(position).getWork().getPost_of().equalsIgnoreCase(DataHandler.getUserId(context))){
                if(horizontal){
                    holder.saveText.setText("Saved");
                }
            }else{
                holder.saveText.setText("Saved");
            }

        }else{
            holder.saveImg.setImageTintList(ContextCompat.getColorStateList(context,R.color.gray));
            holder.saveText.setTextColor(ContextCompat.getColorStateList(context,R.color.gray));
            if(list.get(position).getWork().getPost_of().equalsIgnoreCase(DataHandler.getUserId(context))){
                if(horizontal){
                    holder.saveText.setText("Save");
                }
            }else{
                holder.saveText.setText("Save");
            }
        }

        if(list.get(position).getWork().getMedia_src()!=null && !list.get(position).getWork().getMedia_src().equalsIgnoreCase("null")) {
            String mime = UtilityMethods.getMimeFromUrl(list.get(position).getWork().getMedia_src());
            if(mime!=null) {
                if (mime.equalsIgnoreCase("pdf")) {
                    list.get(position).getWork().setPdflist(new ArrayList<String>(Arrays.asList(list.get(position).getWork().getMedia_src().split(","))));
                    Log.d("Resercho", "CJson Setted list of PDFS" + list.get(position).getWork().getPdflist().size());
                } else {
                    list.get(position).getWork().setImglist(new ArrayList<String>(Arrays.asList(list.get(position).getWork().getMedia_src().split(","))));
                    Log.d("Resercho", "CJson Setted list of IMAGES : " + list.get(position).getWork().getImglist().size());
                }
            }
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ModelWork m = list.get(position).getWork();
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
                intent.putExtra("uid",list.get(position).getWork().getPost_of());
                context.startActivity(intent);
            }
        });

        // Change the UI if liked
        if(list.get(position).getWork().isLike()) {
            Log.d(TAG,"isLik inside:"+position+" : "+list.get(position).getWork().isLike());
            holder.likethumb.setImageTintList(ContextCompat.getColorStateList(context, colorLike));
            holder.likeCount.setTextColor(ContextCompat.getColorStateList(context, colorLike));
        }else{
            holder.likethumb.setImageTintList(ContextCompat.getColorStateList(context, R.color.grayBgLt));
            holder.likeCount.setTextColor(ContextCompat.getColorStateList(context, R.color.gray));
        }

        if(!horizontal&&list.get(position).getWork().getImglist()!=null && list.get(position).getWork().getImglist().size()>0){
            Log.d("jinki","Img list is : "+ list.get(position).getWork().getImglist().size());
            showPhotoRecycler(list.get(position).getWork().getImglist(),holder);
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
        Log.d("Longkit","Cover Image for : "+list.get(position).getWork().getHeading());
        if(!list.get(position).getWork().getCover_img().trim().equalsIgnoreCase(root_dir+"null")){
            Log.d("Longkit","Loading Cove Image : "+list.get(position).getWork().getCover_img());

            Glide.with(context)
                    .load(list.get(position).getWork().getCover_img())
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
            if(list.get(position).getWork().getImglist()!=null&&list.get(position).getWork().getImglist().size()>0){
                Log.d("Longkit","Loading Image Image : "+list.get(position).getWork().getImglist().get(0));

                if (list.get(position).getWork().getImglist().get(0).contains("wav")
                        || list.get(position).getWork().getImglist().get(0).contains("mkv")
                        || list.get(position).getWork().getImglist().get(0).contains("mp3")
                        || list.get(position).getWork().getImglist().get(0).contains("mp4")
                        || list.get(position).getWork().getImglist().get(0).contains("m4a")
                ) {
                    holder.image.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.resercho));
                    holder.image.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    holder.image.setAlpha(0.8f);
                }else {
                    Glide.with(context)
                            .load(root_dir + list.get(position).getWork().getImglist().get(0))
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

        if(list.get(position).getWork().getPdflist()!=null&&list.get(position).getWork().getPdflist().size()>0){
            holder.imgCounter.setText(list.get(position).getWork().getPdflist().size() + " PDF Attached");
            holder.imgCounter.setVisibility(View.VISIBLE);
        }else{
            String mime = UtilityMethods.getMimeFromUrl(list.get(position).getWork().getMedia_src());
            holder.imgCounter.setVisibility(View.VISIBLE);
            if(mime!=null){
                if(UtilityMethods.isImageMime(mime)){
                    holder.imgCounter.setText(list.get(position).getWork().getImglist().size()+" Pictures Attached");
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
                .load(list.get(position).getWork().getProfpic())
                .asBitmap()
                .placeholder(R.drawable.downloading)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        holder.prof.setImageBitmap(resource);
                        holder.prof.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        holder.prof.clearColorFilter();
                        holder.prof.setAlpha(1.0f);
                    }
                });

        initSocial(holder, position);

        holder.options.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(position,list.get(position).getId());
            }
        });
    }

    protected void initSocial(ViewHolder holder, final int position){
        holder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendlike(!list.get(position).getWork().isLike(),list.get(position).getPid(),position);
            }
        });

        holder.share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendShared(list.get(position).getPid(),position);
                share(list.get(position).getPid());
            }
        });

        holder.save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendSaving(list.get(position).getPid(),position);
            }
        });

        holder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((SavedWorkActivity)context).showCommentBox(list.get(position).getPid());
            }
        });
    }


    protected void showPhotoRecycler(final List<String> list, AdapterSaved.ViewHolder holder){

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
                                list.get(pos).getWork().setShareCount(jsonObject.getLong("count"));
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


    protected void sendSaving(final String pid, final int pos){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                NetworkHandler.sendSaved(context, pid, list.get(pos).getWork().isSaved(), new Callback() {
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
                                    list.get(pos).getWork().setSaved(false);
                                }else{
                                    list.get(pos).getWork().setSaved(true);
                                }

                            }else{
                                if(jsonObject.getString("reason").equalsIgnoreCase("exists")){
                                    toastOnMain("Already Saved!");
                                }else{
                                    toastOnMain("Something went wrong");
                                }
                            }

                            if(jsonObject.has("count")){
                                list.get(pos).getWork().setSavedCount(jsonObject.getLong("count"));
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
            list.get(pos).getWork().setLike(true);
            list.get(pos).getWork().setLikes(list.get(pos).getWork().getLikes() + 1);
        }else{
            list.get(pos).getWork().setLike(false);
            long likes = list.get(pos).getWork().getLikes();
            list.get(pos).getWork().setLikes(likes==0?0:likes-1);
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
                                list.get(pos).getWork().setLike(true);
                                list.get(pos).getWork().setLikes(jsonObject.getLong("likes"));
                                Log.d("Likint","Liked like count : "+jsonObject.getLong("likes") + " Response:"+resp);
                            }else if (jsonObject.getString("reason").equalsIgnoreCase("unliked")){
                                list.get(pos).getWork().setLike(false);
                                Log.d("Likint","Disliked like count : "+jsonObject.getLong("likes") + " Response:"+resp);
                                list.get(pos).getWork().setLikes(jsonObject.getLong("likes"));
                            }else if (jsonObject.getString("reason").equalsIgnoreCase("exists")){
                                list.get(pos).getWork().setLike(true);
                                list.get(pos).getWork().setLikes(jsonObject.getLong("likes"));
                                Log.d("Likint","Exists like count : "+jsonObject.getLong("likes") + " Response:"+resp);
                            }
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
    protected void showDialog(final int pos, final String pid){
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle(list.get(pos).getWork().getHeading())
                .setMessage("Are your sure to delete this content?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setCancelable(true)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        deletePost(pos,pid);
                    }})
                .setNegativeButton(android.R.string.no, null).show();
        dialog.setCanceledOnTouchOutside(true);
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
                                        list.remove(pos);
                                        notifyItemRemoved(pos);
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
