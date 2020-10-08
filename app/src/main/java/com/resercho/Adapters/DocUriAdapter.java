package com.resercho.Adapters;


import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.resercho.NewPostActivity;
import com.resercho.R;
import java.util.List;

public class DocUriAdapter extends RecyclerView.Adapter<DocUriAdapter.ViewHolder> {

    private Context context;
    private List<Uri> list;

    public DocUriAdapter(Context context, List<Uri> list) {
        this.context = context;
        this.list = list;
        Log.d("DocList","Context : "+list.size());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_doc_uri, viewGroup, false);
        view.findViewById(R.id.removeFile).setVisibility(View.VISIBLE);
        return new DocUriAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        holder.docName.setText(list.get(position).getLastPathSegment());
        holder.removeFile.setVisibility(View.VISIBLE);
        holder.removeFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("DocList","Before removing : "+list.size() + " Pos:"+holder.getAdapterPosition());
                ((NewPostActivity)context).removeFromDocList(list.get(holder.getAdapterPosition()));
//                list.remove(position);
//                Log.d("DocList","Before removing : Null ? "+(list==null));
                notifyItemRemoved(holder.getAdapterPosition());
            }
        });
    }
    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView docName;
        ImageView removeFile;

        public ViewHolder(View itemView) {
            super(itemView);
            docName = itemView.findViewById(R.id.docName);
            removeFile = itemView.findViewById(R.id.removeFile);
        }
    }

}
