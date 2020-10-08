package com.resercho;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.material.textfield.TextInputEditText;
import com.resercho.POJO.ModelWork;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WorkUploadActivity extends AppCompatActivity {

    final static int REQUEST_POST_IMAGE = 1101;
    final static int REQUEST_POST_VIDEO = 1102;
    static final int REQUEST_COVER_IMAGE = 103;
    static final int READ_WRITE_REQ = 101;

    //
    VideoView videoView;
    ImageView imageView, coverImage;
    ModelWork modelWork;
    String coverlUrl, photoUrl, videoUrl;
    String upLoadServerUri = "https://resercho.com/linkapp/uploadFileUniv.php";

    Button publishBtn;
    TextInputEditText titleEdit, descEdit;
    ProgressBar pbar;

    boolean vidUploaded = false, photoUploaded=false, coverUploaded=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work_upload);

        try{
            getSupportActionBar().setTitle("Upload your work");
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }catch (Exception e){}

        modelWork = new ModelWork();
        initUI();

    }

    private void initUI(){

        videoView = findViewById(R.id.videoView);
        coverImage = findViewById(R.id.coverImage);
        imageView = findViewById(R.id.photoView);
        titleEdit = findViewById(R.id.title);
        descEdit = findViewById(R.id.description);
        publishBtn = findViewById(R.id.publishBtn);

        findViewById(R.id.addCoverImage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickImage(REQUEST_COVER_IMAGE);
            }
        });

        findViewById(R.id.addPhoto).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickImage(REQUEST_POST_IMAGE);
            }
        });

        findViewById(R.id.addVideo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickImage(REQUEST_POST_VIDEO);
            }
        });

        publishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkValidity()){
                    publishWork();
                }else{
                    Toast t = Toast.makeText(getApplicationContext(),"Please fill all the details and select images",Toast.LENGTH_LONG);
                    t.setGravity(Gravity.CENTER,0,0);
                    t.show();
                }
            }
        });

    }

    private void publishWork(){
        showPbar(true);
        new MyAsyncTask().execute(coverlUrl,REQUEST_COVER_IMAGE+"");
    }

    protected boolean checkValidity(){
        if(descEdit!=null&&descEdit.getText().toString().length()>0&&titleEdit!=null&&titleEdit.getText().toString().length()>0&&(photoUrl!=null||videoUrl!=null)
        &&coverlUrl!=null)
            return true;

        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        if(resultCode == RESULT_OK){
            if(requestCode == REQUEST_COVER_IMAGE){
                Log.d("Res","Wupk REQ-Cover");
                processImage(data,REQUEST_COVER_IMAGE);
            }else if(requestCode == REQUEST_POST_IMAGE){
                processImage(data, REQUEST_POST_IMAGE);
                Log.d("Res","Wupk REQ-Image");
            }else if(requestCode == REQUEST_POST_VIDEO){
                processVideo(data);
            }
        }
    }
    public String getRealPathFromURI(Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Video.Media.DATA };
            cursor = getContentResolver().query(contentUri,  proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    protected void resetImage(){
        videoView.setVisibility(View.GONE);
    }

    protected void resetVideo(){
        imageView.setVisibility(View.GONE);
    }

    protected void processImage(Intent data, int req){
        if(req == REQUEST_POST_IMAGE)
            resetVideo();

        try {
            InputStream stream = getContentResolver().openInputStream(data.getData());
            Bitmap bitmap = BitmapFactory.decodeStream(stream);
            stream.close();

            if(req == REQUEST_COVER_IMAGE) {
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                coverImage.setLayoutParams(layoutParams);
                coverImage.setImageBitmap(bitmap);
                coverImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
                coverImage.setColorFilter(null);
            }else{
                imageView.setImageBitmap(bitmap);
                imageView.setVisibility(View.VISIBLE);
            }

            saveCamShotToFile(bitmap,req);
        }catch (FileNotFoundException ex){
            Log.d("res","Wupk File Not Found:"+ex);
        }catch (IOException e){
            Log.d("res","Wupk IOException:"+e);
        }
    }

    private void processVideo(Intent data){
        resetImage();
        Log.d("Res","Wupk processVideo: " +(data!=null));
        Uri videoUri = data.getData();
        saveVideoToFile(data.getData());
        playvideo(videoUri);
    }

    protected void playvideo(Uri uri){
        videoView.setVisibility(View.VISIBLE);
        MediaController mediaController= new MediaController(this);
        mediaController.setAnchorView(videoView);

        videoView.setMediaController(mediaController);
        videoView.setVideoURI(uri);
        videoView.requestFocus();
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            public void onPrepared(MediaPlayer mp) {
                videoView.start();
            }
        });
    }

    protected void setMedia(Bitmap bitmap){
        Log.d("Res","Wupk setMedia");
        imageView.setVisibility(View.VISIBLE);
        imageView.setImageBitmap(bitmap);
    }

    protected Bitmap getThumbnail(Uri uri){
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(uri, filePathColumn, null, null, null);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String picturePath = cursor.getString(columnIndex);
        cursor.close();

        return ThumbnailUtils.createVideoThumbnail(picturePath, MediaStore.Video.Thumbnails.MICRO_KIND);
    }

    public void pickImage(int REQUEST_CODE) {
        if(checkForStoragePermissions()) {
            Intent intent = new Intent();
            if(REQUEST_CODE == REQUEST_POST_VIDEO) {
                intent.setType("video/mp4");
                Log.d("Res","Wupk intenting video");
            }
            else {
                intent.setType("image/*");
                Log.d("Res","Wupk intenting image");
            }
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            startActivityForResult(intent, REQUEST_CODE);
        }else{
            askForStoragePermission();
        }
    }

    protected void saveVideoToFile(Uri uri){
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            byte[] buffer = new byte[inputStream.available()];

            inputStream.read(buffer);

            File f = new File(Environment.getExternalStorageDirectory() + File.separator + "VIDEO_"+DataHandler.getUserId(getApplicationContext())+".mp4");

            OutputStream outStream = new FileOutputStream(f);
            outStream.write(buffer);

            Log.d("Res","Wupk REQ-VID : " + f.getName() + " aaa :"+f.getAbsolutePath());
            videoUrl = f.getAbsolutePath();
        }catch (Exception e){
            Log.d("Res","Wupk REQ-VID : Exception : "+e);
        }
    }

    protected void saveCamShotToFile(final Bitmap bitmap, int reqType){
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
        if(reqType == REQUEST_POST_IMAGE)
            prefix = "POST_";
        if(reqType == REQUEST_COVER_IMAGE)
            prefix = "COVER_";

        File f = new File(Environment.getExternalStorageDirectory() + File.separator + prefix+DataHandler.getUserId(getApplicationContext())+"_"+rand+".jpg");

        try {
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());

            if(reqType == REQUEST_COVER_IMAGE)
                coverlUrl = f.getAbsolutePath();
            else if(reqType == REQUEST_POST_IMAGE)
                photoUrl = f.getAbsolutePath();


//            new MyAsyncTask().execute(f.getAbsolutePath(),reqType+"");

            Log.d("GSE","Firing saved to file:"+f.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class MyAsyncTask extends AsyncTask<String, Integer, Intent> {

        @Override
        protected Intent doInBackground(String... params) {
            Intent intent =CommonMethods.postData(upLoadServerUri,params[0]);
            intent.putExtra("req",params[1]);
            return intent;
        }

        protected void onPostExecute(Intent receivedIntent) {
            if (receivedIntent.getExtras().getInt("resp_code") == 200) {

                int req = Integer.parseInt(receivedIntent.getExtras().getString("req"));
                if(req == REQUEST_COVER_IMAGE){
                    String fpath = "app_res/" + receivedIntent.getExtras().getString("filepath");
                    modelWork.setCover_img(fpath);

                    if(photoUrl!=null) {
                        new MyAsyncTask().execute(photoUrl, REQUEST_POST_IMAGE + "");
                    }
                    if(videoUrl!=null) {
                        new MyAsyncTask().execute(videoUrl, REQUEST_POST_VIDEO + "");
                    }
                }


                if(req == REQUEST_POST_IMAGE) {
                    String fpath = "app_res/" + receivedIntent.getExtras().getString("filepath");
                    Log.d("REs","Covep Setting media_src : "+fpath);
                    modelWork.setMedia_src(fpath);
                    uploadToDb(modelWork);
                }

                if(req == REQUEST_POST_VIDEO){
                    String fpath = "app_res/" + receivedIntent.getExtras().getString("filepath");
                    modelWork.setMedia_src(fpath);
                    uploadToDb(modelWork);

                }
                Log.d("NGOApp","NGOApp File Path = "+receivedIntent.getExtras().getString("filepath"));
            }else{
                Log.d("NGOApp","NGOApp File serverResponseCode="+receivedIntent.getExtras().getInt("resp_code"));
            }
        }



        protected void onProgressUpdate(Integer... progress) {
            //    pBar.setProgress(progress[0]);
        }
    }

    protected void uploadToDb(ModelWork modelWork){
        modelWork.setHeading(titleEdit.getText().toString());
        modelWork.setStatus(descEdit.getText().toString());
        modelWork.setTypework("1");
        modelWork.setCategory("1");
        callDbNet(modelWork);
    }

    private void callDbNet(final ModelWork modelWork){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                NetworkHandler.uploadPost(WorkUploadActivity.this, modelWork, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.d("Res","dbNet :"+e);
                        if(e.toString().contains("java.net.SocketTimeoutException")){
                            callDbNet(modelWork);
                        }
                        showPbar(false);
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        showPbar(false);
                        String resp = response.body().string();
                        Log.d("Res","dbNet onResponse:"+resp);
                        try{
                            JSONObject object = new JSONObject(resp);
                            if(object.getInt("success")==1){
                                statusResponse(true,"Post is Live!");
                            }else{
                                statusResponse(false,"Post Failed in Uploading");
                            }
                        }catch (JSONException e){
                            statusResponse(false,"Post Failed in Uploading");
                        }
                    }
                });
            }
        });

        thread.start();
    }

    private void statusResponse(final boolean succ, final String msg){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_LONG).show();
            }
        });
    }


    protected void showPbar(final boolean show){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(show){
                    findViewById(R.id.pBar).setVisibility(View.VISIBLE);
                }else{
                    findViewById(R.id.pBar).setVisibility(View.GONE);
                }
            }
        });
    }


    public static String getMimeType(Context context, Uri uri) {
        String extension;

        //Check uri format to avoid null
        if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            //If scheme is a content
            final MimeTypeMap mime = MimeTypeMap.getSingleton();
            extension = mime.getExtensionFromMimeType(context.getContentResolver().getType(uri));
        } else {
            //If scheme is a File
            //This will replace white spaces with %20 and also other special characters. This will avoid returning null values on file name with spaces and special characters.
            extension = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(new File(uri.getPath())).toString());

        }

        return extension;
    }


    public boolean checkForStoragePermissions(){
        if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED)
            return true;
        else
            return false;
    }

    public void askForStoragePermission(){
        ActivityCompat.requestPermissions(WorkUploadActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE},READ_WRITE_REQ);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case READ_WRITE_REQ:
                Toast.makeText(getApplicationContext(),"Let's try again",Toast.LENGTH_LONG).show();
                break;

        }
    }


    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
