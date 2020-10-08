package com.resercho.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.resercho.HomeActivity;
import com.resercho.POJO.ModelCategory;
import com.resercho.R;


import java.util.List;

public class AdapterCategory extends RecyclerView.Adapter<AdapterCategory.ViewHolder> {

    private Context context;
    private List<ModelCategory> list;

    public static final int FILTER_DISCOVER = 1;
    public static final int FILTER_COLLAB = 2;
    public static final int FILTER_VIDEOS = 3;

    int filter = -1;

    boolean isGrid = false;

    public boolean isGrid() {
        return isGrid;
    }

    public void setGrid(boolean grid) {
        isGrid = grid;
    }

    public int getFilter() {
        return filter;
    }

    public void setFilter(int filter) {
        this.filter = filter;
    }

    public AdapterCategory(Context context, List<ModelCategory> list) {
        this.context = context;
        this.list = list;
        Log.d("Resercho","AdapterGroup const HomeFrag size:"+list.size() + " itemcount:"+getItemCount());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view;
        if(isGrid)
            view = LayoutInflater.from(context).inflate(R.layout.item_category_bigger, viewGroup, false);
        else
            view = LayoutInflater.from(context).inflate(R.layout.item_category, viewGroup, false);

        return new AdapterCategory.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        try {
            holder.title.setText(list.get(position).getName().trim());

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   if(filter == FILTER_DISCOVER)
                        ((HomeActivity)context).loadThisCategory(list.get(position).getId());

                   if(filter == FILTER_COLLAB)
                        ((HomeActivity)context).collabCategory(list.get(position).getId());

                   if(filter == FILTER_VIDEOS)
                        ((HomeActivity)context).loadThisCategoryForVideos(list.get(position).getId());

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

        TextView title;

        public ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.catName);
        }
    }

}