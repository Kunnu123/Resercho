package com.resercho;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.resercho.POJO.ModelNewGroup;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class CreateGroupActivity extends AppCompatActivity {

    EditText groupName, grpDesc, friendsEd;
    Button pickBtn, uploadBtn;
    Spinner catSpinners;
    ImageView selImage;
    SwitchCompat privateGroup;

    ModelNewGroup newGroup;
    boolean imgSelected = false;
    String upLoadServerUri = "https://resercho.com/linkapp/uploadFileUniv.php";
    String uriImg;

    ProgressBar pbar;

    public static final int REQ_IMAGE_CODE = 11;

    private void init(){
        privateGroup = findViewById(R.id.privateGroup);
        groupName = findViewById(R.id.groupNameEd);
        grpDesc = findViewById(R.id.groupDescEd);
        friendsEd = findViewById(R.id.addFriendsEd);
        pickBtn = findViewById(R.id.pickBtn);
        uploadBtn = findViewById(R.id.createGroupbtn);
        selImage = findViewById(R.id.selImage);
        pbar = findViewById(R.id.pbar);

        pickBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickImage(REQ_IMAGE_CODE);
            }
        });

        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkValidity()){
                    if(uriImg!=null) {
                        newGroup = new ModelNewGroup();
                        newGroup.setPrivateGroup(privateGroup.isChecked());
                        newGroup.setAbout(grpDesc.getText().toString());
                        newGroup.setFriends(friendsEd.getText().toString());
                        newGroup.setName(groupName.getText().toString());
                        newGroup.setCatid(selCat+"");
                        uploadThisMedia(uriImg);
                    }else{
                        Toast.makeText(getApplicationContext(),"Please select a cover image for your group!",Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    int selCat=-1;

    String[] catLabels = { "Science","Technology","Maths","Social Sciences & Humanities","Business & Entrepreneurship","Architecture","Journalism & Mass Communication",
            "Law & Government","Food and Cullinary Arts","Fashion & Style","Beauty & Makeup","Fine Arts & Design","Music & Performance Arts","Environment","Fitness & Health",
            "Education","Sports","Economics","Commerce","Cinematography & Photography","Other"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        try {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setTitle("");
        }catch (Exception e){
            Toast.makeText(getApplicationContext(),"Exception while setting nav",Toast.LENGTH_LONG).show();
        }
            init();
        spinnerSetup();

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

            uriImg = f.getAbsolutePath();

            Log.d("GSE","Firing saved to file:"+f.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && requestCode == REQ_IMAGE_CODE){
            imgSelected = true;
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
        ActivityCompat.requestPermissions(CreateGroupActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE},REQ_IMAGE_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQ_IMAGE_CODE:
                Toast.makeText(getApplicationContext(),"Let's try again",Toast.LENGTH_LONG).show();
                break;

        }
    }


    private void sendGroup(final ModelNewGroup group){
        if(group!=null){
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    NetworkHandler.sendNewGroup(getApplicationContext(), group, new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            showPbar(false);
                            Log.d("Resin","Resin : NGIN onFailure while sending group : "+e);
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
                    Toast.makeText(getApplicationContext(),"New Group Created",Toast.LENGTH_LONG).show();
                    finish();
                }else{
                    Toast.makeText(getApplicationContext(),"There was some problem while creating group! Try again later.",Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    protected boolean checkValidity(){
        boolean allok= true;
        String msg = "Please enter ";

        if(TextUtils.isEmpty(groupName.getText().toString())){
            allok = false;
            msg += "group name, ";
        }

        if(TextUtils.isEmpty(grpDesc.getText().toString())){
            allok = false;
            msg += "group description, ";
        }


        if(selCat==-1){
            allok = false;
            msg += "category, ";
        }


        if(!allok)
            Toast.makeText(getApplicationContext(),"Enter missing fields!",Toast.LENGTH_LONG).show();

        return allok;
    }




    protected void spinnerSetup(){
        catSpinners = findViewById(R.id.catSpinner);
        ArrayAdapter aa = new ArrayAdapter(CreateGroupActivity.this,android.R.layout.simple_spinner_item,catLabels);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        catSpinners.setVisibility(View.VISIBLE);
        catSpinners.setAdapter(aa);
        catSpinners.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d("Stock","Dynamic Options : Spinner countrySpinner Selected : "+catLabels[i]);
                selCat = i+1;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Log.d("Stock","Dynamic Options : Spinner countrySpinner Selected : NothingSelected");
            }
        });
    }

    protected void uploadThisMedia(final String uri){
        showPbar(true);
        Toast t = Toast.makeText(getApplicationContext(),"Uploading...",Toast.LENGTH_LONG);
        t.setGravity(Gravity.CENTER,0,0);
        t.show();

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d("Tindd","Starting thread...");
                Intent intent =CommonMethods.postData(upLoadServerUri,uri);
                handleResponse(intent);
            }
        });
        thread.start();
    }

    protected void handleResponse(Intent receivedIntent){
        Log.d("Tindd","Handling response...");
        if (receivedIntent.getExtras().getInt("resp_code") == 200) {
            Log.d("Tindd","Inside handle resp : if part : " + receivedIntent.getExtras().getInt("resp_code"));
            String file = receivedIntent.getStringExtra("filepath");
            File f = new File(Environment.getExternalStorageDirectory() + File.separator+ file);
            CommonMethods.deleteFile(f,getApplicationContext());
            newGroup.setImage("app_res/"+file);
            sendGroup(newGroup);
            Log.d("NGOApp","NGOApp File Path = "+receivedIntent.getExtras().getString("filepath"));
        }else{
            Log.d("Tindd","Inside handle resp : else part");
            showPbar(false);
            Toast.makeText(getApplicationContext(),"Error sending file to server",Toast.LENGTH_LONG).show();
            Log.d("NGOApp","NGOApp File serverResponseCode="+receivedIntent.getExtras().getInt("resp_code"));
        }
    }

    protected void showPbar(final boolean show){
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

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
