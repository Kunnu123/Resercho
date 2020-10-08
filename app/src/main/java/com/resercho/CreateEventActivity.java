package com.resercho;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.resercho.POJO.ModelEvent;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class CreateEventActivity extends AppCompatActivity {

    /// Date format yyyy-mm-dd
    // Time format HH:mm:ss

    int mHour=-1,mMinute=-1, hourday;
    String pm;
    TimePickerDialog timePickerDialog;

    EditText evName, evDesc,evLocation;

    TextView evDate, evTime;
    String upLoadServerUri = "https://resercho.com/linkapp/uploadFileUniv.php";
    ImageView selImage;
    Button pickBtn, createBtn;

    String evCoverUrl;

    DatePickerDialog.OnDateSetListener dopDateListener;
    Calendar myCalendarDop, timeCalender;
    ModelEvent newEvent;


    public static final int REQ_IMAGE =  101;

    ProgressBar pbar;


    private void init(){
        pbar= findViewById(R.id.pbar);
        evName = findViewById(R.id.evName);
        evDesc =findViewById(R.id.evDesc);
        evLocation = findViewById(R.id.evLocation);
        selImage = findViewById(R.id.selImage);
        evDate = findViewById(R.id.evDate);
        pickBtn = findViewById(R.id.pickBtn);
        createBtn = findViewById(R.id.createBtn);

        evTime = findViewById(R.id.evTime);

        pickBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickImage(REQ_IMAGE);
            }
        });

        evTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setupTimePicker();
            }
        });


        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkValidity()){
                    if(evCoverUrl!=null){
                        showPbar(true);
                        newEvent = new ModelEvent();
                        newEvent.setDate(evDate.getText().toString());
                        newEvent.setTime(evTime.getText().toString());
                        newEvent.setLocation(evLocation.getText().toString());
                        newEvent.setName(evName.getText().toString());
                        newEvent.setDescription(evDesc.getText().toString());
                        uploadThisMedia(evCoverUrl);
                    }else{
                        Toast.makeText(getApplicationContext(),"Select a cover picture",Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(getApplicationContext(),"Enter missing fields!",Toast.LENGTH_LONG).show();
                }
            }
        });

        setUpDopCalender();
    }


    protected void uploadThisMedia(final String uri){
        showPbar(true);
//        Toast t = Toast.makeText(getApplicationContext(),"Uploading...",Toast.LENGTH_LONG);
//        t.setGravity(Gravity.CENTER,0,0);
//        t.show();

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Intent intent =CommonMethods.postData(upLoadServerUri,uri);
                handleResponse(intent);
            }
        });
        thread.start();
    }

    protected void handleResponse(Intent receivedIntent){
        if (receivedIntent.getExtras().getInt("resp_code") == 200) {

            String file = receivedIntent.getStringExtra("filepath");
            File f = new File(Environment.getExternalStorageDirectory() + File.separator+ file);
            CommonMethods.deleteFile(f,getApplicationContext());
            newEvent.setImage("app_res/"+file);
            sendNewEvent(newEvent);
            Log.d("NGOApp","NGOApp File Path = "+receivedIntent.getExtras().getString("filepath"));
        }else{
            showPbar(false);
            Toast.makeText(getApplicationContext(),"Error sending file to server",Toast.LENGTH_LONG).show();
            Log.d("NGOApp","NGOApp File serverResponseCode="+receivedIntent.getExtras().getInt("resp_code"));
        }
    }


    protected boolean checkValidity(){
        boolean allok= true;
        String msg = "Please enter ";

        if(TextUtils.isEmpty(evTime.getText().toString())){
            allok = false;
            msg += "event time, ";
        }

        if(TextUtils.isEmpty(evDate.getText().toString())){
            allok = false;
            msg += "date, ";
        }


        if(TextUtils.isEmpty(evName.getText().toString())){
            allok = false;
            msg += "name, ";
        }



        if(TextUtils.isEmpty(evDesc.getText().toString())){
            allok = false;
            msg += "description, ";
        }



        if(TextUtils.isEmpty(evLocation.getText().toString())){
            allok = false;
            msg += "location, ";
        }


        if(!allok)
            Toast.makeText(getApplicationContext(),"Enter missing fields!",Toast.LENGTH_LONG).show();

        return allok;
    }


    protected void setUpDopCalender(){
        myCalendarDop = Calendar.getInstance();

        dopDateListener = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendarDop.set(Calendar.YEAR, year);
                myCalendarDop.set(Calendar.MONTH, monthOfYear);
                myCalendarDop.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateDop();
            }

        };

        evDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(CreateEventActivity.this, dopDateListener,myCalendarDop
                        .get(Calendar.YEAR), myCalendarDop.get(Calendar.MONTH),
                        myCalendarDop.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

    protected void updateDop(){
        String myFormat = "yyyy-MM-dd"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.ENGLISH);
        evDate.setText(sdf.format(myCalendarDop.getTime()));
    }



    protected void sendNewEvent(final ModelEvent ev){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                NetworkHandler.sendNewEvent(CreateEventActivity.this, ev, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        showPbar(false);
                        Log.d("Resin","Resin : NGIN onFailure while sending event : "+e);
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String resp = response.body().string();
                        Log.d("Resin","Resin : NGIN onResponse: "+resp);
                        handleResponse(resp);
                    }
                });
            }
        });

        thread.start();
    }


    protected void handleResponse(String resp){
        try{
            JSONObject object = new JSONObject(resp);
            if(object.getInt("success")==1){
                setResult(true);
            }else{
                setResult(false);
            }
        }catch (JSONException e){
            setResult(false);
            Log.d("Resin","Resin : NGIN JSON Exception: "+resp);
        }
        showPbar(false);
    }

    protected void setResult(final boolean success){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(success){
                    Toast.makeText(getApplicationContext(),"New Event Created",Toast.LENGTH_LONG).show();
                    finish();
                }else{
                    Toast.makeText(getApplicationContext(),"There was some problem while creating event! Try again later.",Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);


        try {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setTitle("");
        }catch (Exception e){
            Toast.makeText(getApplicationContext(),"Exception while setting nav",Toast.LENGTH_LONG).show();
        }
        init();



    }




    public void pickImage(int REQUEST_CODE) {
        if(checkForStoragePermissions()) {
            Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(pickPhoto , REQUEST_CODE);//one can be replaced with any action code
        }else{
            askForStoragePermission();
        }
    }

    protected void processImage(Uri data){

        try {
            InputStream stream = getContentResolver().openInputStream(data);
            Bitmap bitmap = BitmapFactory.decodeStream(stream);
            stream.close();

            selImage.setImageBitmap(bitmap);
            selImage.setVisibility(View.VISIBLE);

            savePhoto(bitmap);
        }catch (FileNotFoundException ex){
            Log.d("res","Wupk File Not Found:"+ex);
        }catch (IOException e){
            Log.d("res","Wupk IOException:"+e);
        }
    }

    protected void savePhoto(final Bitmap bitmap){
        Log.d("GSE","Firing save to file() called");

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        Log.d("GSE","Compressor before : "+bitmap.getByteCount());
        int quality = 15;
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, bytes);
        Log.d("GSE","Compressor after : "+bitmap.getByteCount() + " Quality set : "+quality);
        String rand = System.currentTimeMillis()+"";
        rand = rand.replace(" ","_");
        rand = rand.replace(":","_");
        rand = rand.replace("/","_");

        String prefix = "IMAGE_";

        File f = new File(getExternalCacheDir() + File.separator + prefix+DataHandler.getUserId(getApplicationContext())+"_"+rand+".jpg");
        try {
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());

            evCoverUrl = f.getAbsolutePath();

            Log.d("GSE","Firing saved to file:"+f.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }





    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && requestCode == REQ_IMAGE){
            processImage(data.getData());
        }
    }

    public boolean checkForStoragePermissions(){
        if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED)
            return true;
        else
            return false;
    }

    public void askForStoragePermission(){
        ActivityCompat.requestPermissions(CreateEventActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE},REQ_IMAGE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQ_IMAGE:
                Toast.makeText(getApplicationContext(),"Let's try again",Toast.LENGTH_LONG).show();
                break;

        }
    }



    protected void showPbar(final boolean show){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(show){
                    createBtn.setEnabled(false);
                    pbar.setVisibility(View.VISIBLE);
                }else{
                    createBtn.setEnabled(true);
                    pbar.setVisibility(View.GONE);
                }
            }
        });
    }

    protected void setupTimePicker(){

        // Get Current Time
        timeCalender = Calendar.getInstance();
        mHour = timeCalender.get(Calendar.HOUR);
        hourday = timeCalender.get(Calendar.HOUR_OF_DAY);
        mMinute = timeCalender.get(Calendar.MINUTE);

        pm = hourday>12?"PM":"AM";

        timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        evTime.setText((hourOfDay==0?"00":hourOfDay) + ":" + (minute==0?"00":minute));
                    }
                }, mHour, mMinute, false);
        timePickerDialog.show();
    }
}
