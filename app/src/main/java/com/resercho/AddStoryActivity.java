package com.resercho;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AddStoryActivity extends AppCompatActivity {

    ImageView story;


    public static final int REQ_CODE = 101;
    Button pickBtn;
    View scaleBtn;

    List<ImageView.ScaleType> image_scales;
    int scaleCount = 0;

    View pbar;

    String photoUri, photoFileName;
    LinearLayout storyWindow;
    boolean imagePicked = false;
    String upLoadServerUri = "https://resercho.com/linkapp/uploadFileUniv.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_story);

        image_scales = new ArrayList<>();
        image_scales.add(ImageView.ScaleType.CENTER);
        image_scales.add(ImageView.ScaleType.CENTER_CROP);
        image_scales.add(ImageView.ScaleType.FIT_CENTER);
        image_scales.add(ImageView.ScaleType.FIT_XY);

        story = findViewById(R.id.storyImage);
        pickBtn = findViewById(R.id.pickBtn);
        scaleBtn = findViewById(R.id.scaleBtn);
        pbar = findViewById(R.id.pbar);
        storyWindow = findViewById(R.id.storyWindow);

        scaleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(scaleCount==image_scales.size())
                    scaleCount = 0;

                story.setScaleType(image_scales.get(scaleCount++));
            }
        });

        pickBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!imagePicked)
                    pickImage(REQ_CODE);
                else
                    sendStory();
            }
        });
        pickImage(REQ_CODE);
    }
    protected void sendStory(){
        showSending(true);
        savePhoto(getBitmapFromView(storyWindow));
    }

    public static Bitmap getBitmapFromView(View view) {
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(),Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        Drawable bgDrawable =view.getBackground();
        if (bgDrawable!=null)
            bgDrawable.draw(canvas);
        else
            canvas.drawColor(Color.WHITE);
        view.draw(canvas);
        return returnedBitmap;
    }


    protected void showSending(final boolean show){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(show){
                    pbar.setVisibility(View.VISIBLE);
                }else{
                    pbar.setVisibility(View.GONE);
                }
            }
        });
    }

    public void pickImage(int REQUEST_CODE) {
        if(checkForStoragePermissions()) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);

            startActivityForResult(intent, REQUEST_CODE);
            imagePicked = false;
        }else{
            askForStoragePermission();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            if(requestCode == REQ_CODE){
                imagePicked = true;
                processImage(data.getData());
            }
        }
    }

    protected void processImage(Uri data){
        if(imagePicked)
            pickBtn.setText("Send Story");
        else
            pickBtn.setText("Pick Image");

        try {
            InputStream stream = getContentResolver().openInputStream(data);
            Bitmap bitmap = BitmapFactory.decodeStream(stream);
            stream.close();
            story.setImageBitmap(bitmap);
            story.setVisibility(View.VISIBLE);

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
        int quality = 50;
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, bytes);
        Log.d("GSE","Compressor after : "+bitmap.getByteCount() + " Quality set : "+quality);
        String rand = System.currentTimeMillis()+"";
        rand = rand.replace(" ","_");
        rand = rand.replace(":","_");
        rand = rand.replace("/","_");

        String prefix = "STORY_";

        File f = new File(Environment.getExternalStorageDirectory() + File.separator + prefix+DataHandler.getUserId(getApplicationContext())+"_"+rand+".jpg");

        try {
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());

            photoUri = f.getAbsolutePath();

            sendImageFile();

            Log.d("GSE","Firing saved to file:"+f.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void sendImageFile(){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Intent intent =CommonMethods.postData(upLoadServerUri,photoUri);
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

            sendStoryToDb(file);
            Log.d("NGOApp","NGOApp File Path = "+receivedIntent.getExtras().getString("filepath"));
        }else{
            showSending(false);
            toastOnMain("Story sending failed!");
            Log.d("NGOApp","NGOApp File serverResponseCode="+receivedIntent.getExtras().getInt("resp_code"));
        }
    }

    protected void sendStoryToDb(String photoUri){
        final String imgurl = "app_res/"+photoUri;
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                NetworkHandler.sendStory(getApplicationContext(), imgurl, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        toastOnMain("Failed to send story");
                        showSending(false);
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String resp = response.body().string();
                        try{
                            JSONObject jsonObject = new JSONObject(resp);
                            if(jsonObject.getInt("success") == 1){
                               toastOnMain("Story sent!");
                               finish();
                            }else{
                                toastOnMain("Comment failed");
                            }
                        }catch (JSONException e){
                            Log.d("GSE","JSON Exception :"+e);
                        }
                        showSending(false);
                    }
                });
            }
        });

        thread.start();
    }

    protected void toastOnMain(final String msg){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast t = Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_LONG);
                t.setGravity(Gravity.CENTER,0,0);
                t.show();
            }
        });
    }

    public boolean checkForStoragePermissions(){
        if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED)
            return true;
        else
            return false;
    }

    public void askForStoragePermission(){
        ActivityCompat.requestPermissions(AddStoryActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE},REQ_CODE);
    }


}