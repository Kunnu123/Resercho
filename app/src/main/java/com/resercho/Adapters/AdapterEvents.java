package com.resercho.Adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.format.DateFormat;
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
import com.resercho.DataHandler;
import com.resercho.EventActivity;
import com.resercho.NetworkHandler;
import com.resercho.POJO.ModelEvent;
import com.resercho.R;
import com.resercho.UtilityMethods;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AdapterEvents extends RecyclerView.Adapter<AdapterEvents.ViewHolder> {

    private Context context;
    private List<ModelEvent> list;

    public AdapterEvents(Context context, List<ModelEvent> list) {
        this.context = context;
        this.list = list;
        Log.d("Resercho","AdapterGroup const HomeFrag size:"+list.size() + " itemcount:"+getItemCount());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_model_event, viewGroup, false);
        return new AdapterEvents.ViewHolder(view);
    }


    protected void showDialog(final int pos, final String pid){
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle(list.get(pos).getName())
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

    protected void toastOnMain(final String msg){
        ((Activity)context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context,msg,Toast.LENGTH_LONG).show();
            }
        });
    }

    protected void deletePost(final int pos, final String pid){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                NetworkHandler.deleteEvent(context, pid, new Callback() {
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

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

            holder.title.setText(list.get(position).getName());


            try {
                SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
                Date date = fmt.parse(list.get(position).getDate());

                String dayOfTheWeek = (String) DateFormat.format("EEEE", date); // Thursday
                String day = (String) DateFormat.format("dd", date); // 20
                String monthString = (String) DateFormat.format("MMM", date); // Jun
                String monthNumber = (String) DateFormat.format("MM", date); // 06
                String year = (String) DateFormat.format("yyyy", date); // 2013

                String toDisp = day + " " + monthString + ", " + year + "\n" + dayOfTheWeek;
                holder.date.setText(toDisp);
            }catch (ParseException e){
                holder.date.setText(list.get(position).getDate());
            }
            holder.time.setText(UtilityMethods.getTimeFromHHMMSS(list.get(position).getTime()));
            holder.place.setText(list.get(position).getLocation());

            if(list.get(position).getHost().equalsIgnoreCase(DataHandler.getUserId(context))){
                holder.options.setVisibility(View.VISIBLE);
                holder.options.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showDialog(position,list.get(position).getId());
                    }
                });
            }

            Glide.with(context)
                    .load(list.get(position).getImage()
                    ).placeholder(R.drawable.downloading)
                    .into(holder.image);



            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, EventActivity.class);
                    intent.putExtra("image",list.get(position).getImage());
                    intent.putExtra("date",list.get(position).getDate());
                    intent.putExtra("createdat",list.get(position).getCreatedAt());
                    intent.putExtra("desc",list.get(position).getDescription());
                    intent.putExtra("id",list.get(position).getId());
                    intent.putExtra("host",list.get(position).getHost());
                    intent.putExtra("name",list.get(position).getName());
                    intent.putExtra("time",list.get(position).getTime());
                    intent.putExtra("interest",list.get(position).isInterested());
                    intent.putExtra("location",list.get(position).getLocation());
                    context.startActivity(intent);
                }
            });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView title, date,time,place;
        ImageView image;
        View options;

        public ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            options = itemView.findViewById(R.id.options);
            time = itemView.findViewById(R.id.time);
            date = itemView.findViewById(R.id.date);
            place = itemView.findViewById(R.id.place);
            image = itemView.findViewById(R.id.image);
        }
    }

}
