package com.resercho.Adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.resercho.DataHandler;
import com.resercho.NetworkHandler;
import com.resercho.POJO.ModelGroupMsg;
import com.resercho.POJO.ModelMsg;
import com.resercho.R;
import com.resercho.UtilityMethods;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AdapterGroupMsgs extends RecyclerView.Adapter<AdapterGroupMsgs.ViewHolder> {

    private Context context;
    private List<ModelGroupMsg> list;
    String username;
    String fid;

    public AdapterGroupMsgs(Context context, List<ModelGroupMsg> list, String uname, String fid) {
        this.context = context;
        this.list = list;
        this.username = uname;
        this.fid = fid;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_msg, viewGroup, false);
        return new AdapterGroupMsgs.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        if(!(list.get(position).getUid().equalsIgnoreCase(fid))){
            holder.itemView.setBackground(ContextCompat.getDrawable(context,R.drawable.chat_incoming));
            holder.itemView.setPadding(66,0,16,0);
            holder.uname.setText(username);
        }else{
            holder.itemView.setBackground(ContextCompat.getDrawable(context,R.drawable.chat_outgoing));
            holder.itemView.setPadding(26,0,30,0);
            holder.uname.setText("You");
        }

        holder.lastmsg.setText(list.get(position).getMsg());


        try{
            holder.time.setText(UtilityMethods.getFormattedTime(list.get(position).getAttime()));
        }catch (ParseException e){
            holder.time.setText(UtilityMethods.getCurrentTimeFromTimestamp(list.get(position).getAttime()));
        }

        if(list.get(position).isSending()){
            holder.sending.setVisibility(View.VISIBLE);
        }else {
            holder.sending.setVisibility(View.GONE);
        }
        /// Long tap
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
//                if(list.get(position).getFrom().equalsIgnoreCase(DataHandler.getUserId(context))){
//                    delDialog(list.get(position).getId(),list.get(position).getTo(),position);
//                    return true;
//                }else{
//                    return false;
//                }

                return true;
            }
        });
    }

    private void delDialog(final String msgid, final String toid, final int pos){

            AlertDialog dialog = new AlertDialog.Builder(context)
                    .setTitle("Delete message")
                    .setMessage("Are your sure to delete this content?")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setCancelable(true)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int whichButton) {
                            deleteMessage(msgid,toid,pos);
                        }})
                    .setNegativeButton(android.R.string.no, null).show();
            dialog.setInverseBackgroundForced(true);
            dialog.setCanceledOnTouchOutside(true);

    }

    protected void deleteMessage(final String msgid, final String toid,final int pos){
        Toast.makeText(context,"Going to delete : "+msgid + " TO: "+toid,Toast.LENGTH_LONG).show();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                NetworkHandler.deleteMessage(context, msgid, toid, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        toastOnMain("Failed to delete msg1");
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String resp = response.body().string();
                        Log.d("Dekinn","onResponse : "+resp);
                        try {
                            JSONObject jsonObject = new JSONObject(resp);
                            if(jsonObject.getInt("success")==1){
                                toastOnMain("Message deleted");
                                ((Activity)context).runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        list.remove(pos);
                                        notifyItemRemoved(pos);
                                    }
                                });

                            }else{
                                toastOnMain("Failed to delete msg2");
                            }
                        }catch (JSONException e){
                            toastOnMain("Failed deleting message3");
                        }
                    }
                });
            }
        });

        thread.start();
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

        TextView lastmsg,uname,time;
        ImageView sending;


        public ViewHolder(View itemView) {
            super(itemView);
            lastmsg = itemView.findViewById(R.id.msgContent);
            uname = itemView.findViewById(R.id.msgUname);
            time = itemView.findViewById(R.id.timestamp);
            sending = itemView.findViewById(R.id.sending);
        }
    }

}
