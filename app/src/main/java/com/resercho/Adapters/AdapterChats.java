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
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.resercho.ChatWindowActivity;
import com.resercho.POJO.ModelChat;
import com.resercho.R;

import java.util.List;

public class AdapterChats extends RecyclerView.Adapter<AdapterChats.ViewHolder> {

    private Context context;
    private List<ModelChat> list;

    public AdapterChats(Context context, List<ModelChat> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_message_person, viewGroup, false);
        return new AdapterChats.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        Log.d("Resercho","Suggested onBindView :" +list.get(position).getUsername());

        holder.uname.setText(list.get(position).getUsername())  ;
        holder.lastmsg.setText(list.get(position).getLastMsg());

        Glide.with(context)
                .load(list.get(position).getProfile()
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
                Intent intent = new Intent(context, ChatWindowActivity.class);
                intent.putExtra("uid",list.get(position).getMid());
                intent.putExtra("profile",list.get(position).getProfile());
                intent.putExtra("username",list.get(position).getUsername());
                context.startActivity(intent);
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                delDialog();
                return true;
            }
        });

    }

    private void delDialog(){

        AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle("Delete This Chat")
                .setMessage("Are your sure to delete this chat?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setCancelable(true)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {

                    }})
                .setNegativeButton(android.R.string.no, null).show();
        dialog.setInverseBackgroundForced(true);
        dialog.setCanceledOnTouchOutside(true);

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

        TextView uname, lastmsg;
        ImageView image;


        public ViewHolder(View itemView) {
            super(itemView);
            uname = itemView.findViewById(R.id.username);
            lastmsg = itemView.findViewById(R.id.lastMsg);
            image = itemView.findViewById(R.id.profPic);
        }
    }

}
