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
import com.resercho.POJO.ModelType;
import com.resercho.R;

import java.util.List;

public class AdapterTypes extends RecyclerView.Adapter<AdapterTypes.ViewHolder> {

    private Context context;
    private List<ModelType> list;

    public AdapterTypes(Context context, List<ModelType> list) {
        this.context = context;
        this.list = list;
        Log.d("Resercho","AdapterGroup const HomeFrag size:"+list.size() + " itemcount:"+getItemCount());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view;

            view = LayoutInflater.from(context).inflate(R.layout.item_category_bigger, viewGroup, false);

        return new AdapterTypes.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        try {
            holder.title.setText(list.get(position).getName().trim());

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((HomeActivity)context).loadThisType(list.get(position).getId());
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