package com.resercho.Adapters;


import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.resercho.R;

import java.util.List;

public class ImageUriAdapter extends RecyclerView.Adapter<ImageUriAdapter.ViewHolder> {

    private Context context;
    private List<Uri> list;

    public ImageUriAdapter(Context context, List<Uri> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_doc_uri, viewGroup, false);
        return new ImageUriAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.docName.setText(list.get(position).getLastPathSegment());
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
