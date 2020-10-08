package com.resercho.Adapters;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.resercho.PdfWebActivity;
import com.resercho.VideoStreamingActivity;

import com.resercho.R;

import java.util.List;

import static com.resercho.NetworkHandler.root_dir;


public class MultiPdfAdapter extends RecyclerView.Adapter<MultiPdfAdapter.ViewHolder> {

    private Context context;
    private List<String> list;
    private String type,pid,owner;
    private boolean downloadEnabled;

    public boolean isDownloadEnabled() {
        return downloadEnabled;
    }

    public void setDownloadEnabled(boolean downloadEnabled) {
        this.downloadEnabled = downloadEnabled;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public MultiPdfAdapter(Context context, List<String> list) {
        this.context = context;
        this.list = list;
        this.downloadEnabled= false;
    }

    public MultiPdfAdapter(Context context, List<String> list, String type) {
        this.context = context;
        this.list = list;
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_doc_uri, viewGroup, false);
        return new MultiPdfAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.docName.setText("PDF "+(position+1));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(type!=null) {
                    if (type.equalsIgnoreCase("pdf")) {
                        Intent intent = new Intent(context, PdfWebActivity.class);
                        intent.putExtra("url",root_dir.trim() + list.get(position).trim());
                        intent.putExtra("pid",pid);
                        intent.putExtra("owner",owner);
                        intent.putExtra("download",downloadEnabled);
                        context.startActivity(intent);
                    }else if(type.equalsIgnoreCase("mp4")||type.equalsIgnoreCase("avi")){
                        Intent intent = new Intent(context,VideoStreamingActivity.class);
                        intent.putExtra("url",root_dir.trim() +list.get(position).trim());
                        intent.putExtra("pid",pid);
                        intent.putExtra("owner",owner);
                        context.startActivity(intent);
                    }
                }else{
                    Toast.makeText(context,"There has been some error. Try again!",Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView docName;

        public ViewHolder(View itemView) {
            super(itemView);
            docName = itemView.findViewById(R.id.docName);
        }
    }

}
