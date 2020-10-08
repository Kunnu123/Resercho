package com.resercho;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputEditText;
import com.resercho.Adapters.DocUriAdapter;
import com.resercho.Adapters.MultiPdfAdapter;
import com.resercho.Adapters.PhotoAdapter;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class EditPostActivity extends AppCompatActivity {

    final static int REQ_PHOTO = 1101;
    final static int REQ_VIDEO = 1103;
    final static int REQ_DOC = 1105;
    final static int REQ_AUDIO = 1109;
    final static int REQ_PER_READ_WRITE = 1107;
    static final int REQUEST_COVER_IMAGE = 103;

    View photoBtn, videoBtn, audioBtn, docBtn;
    boolean newCover = false, newMedia= false;
    View audioView;
    ImageView photoView, coverImgView;
    VideoView videoView;

    RecyclerView docRecycler;
    DocUriAdapter docAdapter;

    RecyclerView imgRecycler;
    PhotoAdapter imgAdapter;

    TextInputEditText titleEdit, descEdit;

    List<Uri> docList; // To store URI from phone
    List<String> upDocList; // To store absolute path after saving it as file
    List<String> serverDocList; // To store name of file for saving in DB

    List<Uri> imgdocList; // To store URI from phone
    List<String> imgupDocList; // To store absolute path after saving it as file
    List<String> imgserverDocList; // To store name of file for saving in DB

    String photoUri, videoUri, audioUri;
    MediaPlayer mp = new MediaPlayer();

    ModelWork modelWork;
    private Handler mHandler;
    private Runnable mRunnable;

    Button publishBtn;
    String upLoadServerUri = "https://resercho.com/linkapp/uploadFileUniv.php";

    int REQ_USER = -1;

    SeekBar audioBar;

    String coverlUrl;

    Handler uploadHandler;

    String type = null;

    String[] catLabels = { "Science","Technology","Maths","Social Sciences & Humanities","Business & Entrepreneurship","Architecture","Journalism & Mass Communication",
    "Law & Government","Food and Cullinary Arts","Fashion & Style","Beauty & Makeup","Fine Arts & Design","Music & Performance Arts","Environment","Fitness & Health",
            "Education","Sports","Economics","Commerce","Cinematography & Photography","Other"};
    String[] typeLabels = { "Articles","Books","Research Work","Assignments and Projects","Study Materials and Notes","Other"};

    Spinner catSpinners, typeSpinners;
    int selCat=1, selType=1;

    TextInputEditText doi, author;
    TextView dop;

    boolean dopSet=true;

    boolean isGroup = false;
    String gid;

    Switch downSwitch, shareSwitch;

    // DOP
    DatePickerDialog.OnDateSetListener dopDateListener;
    Calendar myCalendarDop;

    ModelWork work;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_post);
        beforeAnything();
        mHandler = new Handler();
        type = "research";

        work = new ModelWork();
        work.setMedia_src(getIntent().getStringExtra("media_src"));
        work.setHeading(getIntent().getStringExtra("heading"));
        work.setUsername(getIntent().getStringExtra("uname"));
        work.setStatus(getIntent().getStringExtra("desc"));
        work.setPost_of(getIntent().getStringExtra("uid"));
        work.setId(getIntent().getStringExtra("pid"));
        work.setCover_img(getIntent().getStringExtra("cover"));
        work.setProfpic(getIntent().getStringExtra("prof"));
        work.setUserBio(getIntent().getStringExtra("bio"));
        work.setTime(getIntent().getStringExtra("time"));
        work.setSaved(getIntent().getBooleanExtra("saved",false));
        work.setLike(getIntent().getBooleanExtra("liked",false));
        work.setDoi(getIntent().getStringExtra("doi"));
        work.setAuthor(getIntent().getStringExtra("author"));
        Log.d("Zink","Fnd : Author:"+work.getId());
        work.setDop(getIntent().getStringExtra("dop"));
        work.setDenabled(getIntent().getBooleanExtra("denabled",false));
        work.setSenabled(getIntent().getBooleanExtra("senabled",false));
        work.setLikes(getIntent().getLongExtra("likes",0));
        work.setShareCount(getIntent().getLongExtra("shares",0));
        work.setSavedCount(getIntent().getLongExtra("saves",0));
        work.setCategory(getIntent().getStringExtra("category"));
        work.setTypework(getIntent().getStringExtra("typeWork"));

        if(work.getMedia_src()!=null && !work.getMedia_src().equalsIgnoreCase("null")) {
            String mime = UtilityMethods.getMimeFromUrl(work.getMedia_src());
            Log.d("Zink","Media SRC:"+work.getMedia_src());
            if(mime!=null) {
                if (mime.equalsIgnoreCase("pdf")) {
                    work.setPdflist(new ArrayList<String>(Arrays.asList(work.getMedia_src().split(","))));

                } else {
                    work.setImglist(new ArrayList<String>(Arrays.asList(work.getMedia_src().split(","))));

                }
            }
        }else{
            Log.d("Din","No wrk");
        }

        init();
        spinnerSetup();
        typeSpinnerSetup();




    }

    MultiPdfAdapter multiAdapter;
    RecyclerView multiRecycler;

    PhotoAdapter photoAdapter;

    protected void showMultiRecycler(final List<String> list){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                multiAdapter = new MultiPdfAdapter(EditPostActivity.this,list);
                multiAdapter.setType(UtilityMethods.getMimeFromUrl(work.getMedia_src()));
                multiAdapter.setOwner(work.getPost_of());
                multiAdapter.setPid(work.getId());
                multiAdapter.setDownloadEnabled(work.isDenabled());
                LinearLayoutManager layman = new LinearLayoutManager(EditPostActivity.this,RecyclerView.VERTICAL,false);
                multiRecycler.setLayoutManager(layman);
                multiRecycler.setAdapter(multiAdapter);
                multiRecycler.setVisibility(View.VISIBLE);
            }
        });
    }

    protected void showPhotoRecycler(final List<String> list){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                multiRecycler.setVisibility(View.VISIBLE);
                photoAdapter = new PhotoAdapter(EditPostActivity.this,list);
                photoAdapter.setUseGlide(true);
                photoAdapter.setLongRv(true);
                LinearLayoutManager layman = new LinearLayoutManager(EditPostActivity.this,RecyclerView.VERTICAL,false);
                multiRecycler.setLayoutManager(layman);
                multiRecycler.setAdapter(photoAdapter);
            }
        });
    }



    protected void spinnerSetup(){

        ArrayAdapter aa = new ArrayAdapter(EditPostActivity.this,android.R.layout.simple_spinner_item,catLabels);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        catSpinners = findViewById(R.id.categorySpinner);
        catSpinners.setVisibility(View.VISIBLE);
        catSpinners.setAdapter(aa);

        catSpinners.setSelection(Integer.parseInt(work.getCategory()));
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

    protected void typeSpinnerSetup(){

        ArrayAdapter aa = new ArrayAdapter(EditPostActivity.this,android.R.layout.simple_spinner_item,typeLabels);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        typeSpinners = findViewById(R.id.typeWorkSpinner);
        typeSpinners.setVisibility(View.VISIBLE);
        typeSpinners.setAdapter(aa);

        typeSpinners.setSelection(Integer.parseInt(work.getTypework()));
        typeSpinners.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d("Stock","Dynamic Options : Spinner countrySpinner Selected : "+typeLabels[i]);
                selType = i+1;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Log.d("Stock","Dynamic Options : Spinner countrySpinner Selected : NothingSelected");
            }
        });
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

        dop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(EditPostActivity.this, dopDateListener,myCalendarDop
                        .get(Calendar.YEAR), myCalendarDop.get(Calendar.MONTH),
                        myCalendarDop.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

    protected void updateDop(){
        String myFormat = "MM/dd/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.ENGLISH);
        dopSet = true;
        dop.setText(sdf.format(myCalendarDop.getTime()));
    }

    protected void init(){

        multiRecycler = findViewById(R.id.multiRecycler);

        Log.d("Tink","PDF is null"+(work.getPdflist()==null));
        if(work.getPdflist()!=null) {
            Log.d("Tink","PDF is null"+work.getPdflist().size());
            showMultiRecycler(work.getPdflist());
        }
        else {
            if(work.getImglist()!=null)
                showPhotoRecycler(work.getImglist());
        }

        photoBtn = findViewById(R.id.addPhoto);
        docBtn = findViewById(R.id.addDocument);
        videoBtn = findViewById(R.id.addVideo);
        publishBtn = findViewById(R.id.publishBtn);
        audioBtn = findViewById(R.id.addAudio);
        audioView = findViewById(R.id.audioPlayer);
        photoView = findViewById(R.id.photoView);
        coverImgView = findViewById(R.id.coverImage);
        Glide.with(getApplicationContext()).load(work.getCover_img()).placeholder(R.drawable.downloading).into(coverImgView);

        Log.d("Zink","Cover:"+NetworkHandler.root_dir+work.getCover_img());
        downSwitch = findViewById(R.id.enableDownload);
        shareSwitch = findViewById(R.id.enableShare);

        downSwitch.setChecked(work.isDenabled());
        shareSwitch.setChecked(work.isSenabled());

        findViewById(R.id.pickCoverBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickImage(REQUEST_COVER_IMAGE);
            }
        });


        doi = findViewById(R.id.doi);
        doi.setText(work.getDoi());

        dop = findViewById(R.id.dop);
        dop.setText(work.getDop());

        author = findViewById(R.id.author);
        author.setText(work.getAuthor());

        audioBar = findViewById(R.id.seekBar);

        titleEdit = findViewById(R.id.title);
        titleEdit.setText(work.getHeading());

        descEdit = findViewById(R.id.description);
        descEdit.setText(work.getStatus());

        docRecycler = findViewById(R.id.documentRv);
        imgRecycler = findViewById(R.id.imagesRv);
        videoView = findViewById(R.id.videoView);

        if(type.equalsIgnoreCase("research")) {
            setUpDopCalender();
            doi.setVisibility(View.VISIBLE);
            dop.setVisibility(View.VISIBLE);
            author.setVisibility(View.VISIBLE);

            docBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                if(upDocList!=null) {
                    if(upDocList.size()<10)
                        pickImage(REQ_DOC);
                    else
                        Toast.makeText(getApplicationContext(),"Maximum files reached",Toast.LENGTH_LONG).show();
                }else{
                    pickImage(REQ_DOC);
                }

                    }
            });
        }else{
            docBtn.setVisibility(View.GONE);
        }
        photoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(imgdocList!=null) {
                    if(imgdocList.size()<10)
                        pickImageEx(REQ_PHOTO);
                    else
                        Toast.makeText(getApplicationContext(),"Maximum files reached",Toast.LENGTH_LONG).show();
                }else{
                    pickImageEx(REQ_PHOTO);
                }
            }
        });

        videoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    pickImage(REQ_VIDEO);

            }
        });

        audioBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    pickImage(REQ_AUDIO);

            }
        });

        publishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                modelWork = new ModelWork();

                if(REQ_USER!=-1 && checkValidity(REQ_USER)) {
                    uploadCoverPhoto2();
                }
                else{
                    if(checkPlainValidity()) {
                        plainTextPublish();
                    }else{
                        Toast.makeText(getApplicationContext(),"Validity Failed",Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        uploadHandler = new Handler();
    }


    protected void uploadCoverPhoto2(){
        showPbar(true);

        if(coverlUrl!=null) {
            new CoverAsync().execute(coverlUrl, REQUEST_COVER_IMAGE + "","1");
        }else{
            publishPost();
        }
    }

    protected void uploadCoverPhoto(){
        showPbar(true);

        if(coverlUrl!=null) {
            new CoverAsync().execute(coverlUrl, REQUEST_COVER_IMAGE + "",null);
        }else{
            uploadToDb(modelWork);
        }
    }

    protected void plainTextPublish(){
        modelWork = new ModelWork();
        modelWork.setHeading(titleEdit.getText().toString());
        modelWork.setStatus(descEdit.getText().toString());

        uploadCoverPhoto();
//        uploadToDb(modelWork);
    }
    public void pickImageEx(int REQUEST_CODE) {
        if(checkForStoragePermissions()) {
            Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            Intent chooser = Intent.createChooser(pickPhoto, "Choose your action");
            chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] { cameraIntent });
            startActivityForResult(chooser, REQUEST_CODE);
        }else{
            askForStoragePermission();
        }
    }

    protected void publishPost(){

        switch (REQ_USER){
            case REQ_AUDIO:
                Toast.makeText(getApplicationContext(),"Uploading audio",Toast.LENGTH_LONG).show();
                uploadThisMedia(audioUri,REQ_AUDIO);
                break;

            case REQ_PHOTO:
                Toast.makeText(getApplicationContext(),"Uploading photo",Toast.LENGTH_LONG).show();
//                uploadThisMedia(photoUri,REQ_PHOTO);
                uploadImages();
                break;

            case REQ_VIDEO:
                Toast.makeText(getApplicationContext(),"Uploading video",Toast.LENGTH_LONG).show();
                uploadThisMedia(videoUri,REQ_VIDEO);
                break;

            case REQ_DOC:
                uploadDocs();
                break;
        }
    }

    protected boolean checkPlainValidity(){
        boolean allOk = true;
        String msg = "Please enter/select ";

        if(type.equalsIgnoreCase("research")) {
            if (author == null || author.getText() == null || author.getText().toString().length() < 1) {
                msg += "author,";
                allOk = false;
            }

            if (!dopSet) {
                msg += "date of publish,";
                allOk = false;
            }
        }

                if(titleEdit==null||titleEdit.getText()==null||titleEdit.getText().toString().length()<1) {
            msg += "title,";
            allOk = false;
        }

        if(descEdit==null||descEdit.getText()==null||descEdit.getText().toString().length()<1) {
            msg += "description,";
            allOk = false;
        }

        if(!allOk){
            Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_LONG).show();
        }

        return allOk;
    }

    protected boolean checkValidity(int req){
        boolean allOk = true;
        String msg = "Please enter/select ";

        if(type.equalsIgnoreCase("research")){
            if(author==null||author.getText()==null||author.getText().toString().length()<1) {
                msg += "author,";
                allOk = false;
            }

            if(!dopSet){
                msg += "date of publish,";
                allOk = false;
            }
        }

        if(titleEdit==null||titleEdit.getText()==null||titleEdit.getText().toString().length()<1) {
            msg += "title,";
            allOk = false;
        }

        if(descEdit==null||descEdit.getText()==null||descEdit.getText().toString().length()<1) {
            msg += "description,";
            allOk = false;
        }

        if(req == REQ_DOC){
            if(upDocList!=null&&docList!=null&&docList.size()>0&&upDocList.size()==docList.size()){

            }else{
                msg += "document files,";
                allOk = false;
            }
        }

        if(req == REQ_PHOTO){
            if(photoUri==null){
                allOk = false;
                msg+=" photo, ";
            }
        }

        if(req == REQ_VIDEO){
            if(videoUri==null){
                allOk = false;
                msg +=" video, ";
            }
        }

        if(req == REQ_AUDIO){
            if(audioUri==null){
                allOk = false;
                msg +=" audio, ";
            }
        }

        if(allOk)
            return true;
        else {
            Toast.makeText(getApplicationContext(),msg.substring(0,msg.length()-1),Toast.LENGTH_LONG).show();
            return false;
        }
    }

    protected void uploadDocs(){
        showPbar(true);
        Log.d("Res","Wupk REQ-DOC: Uploading doc : " + upDocList.size() + " Doc: "+docList.size());
        uploadThisDoc(0);
    }

    protected void uploadImages(){
        showPbar(true);
        Log.d("Res","Wupk REQ-DOC: Uploading doc : " + imgupDocList.size() + " Doc: "+imgdocList.size());
        uploadThisImg(0);
    }


    protected void uploadThisDoc(int pos){
        new MyAsyncTask().execute(upDocList.get(pos),pos+"");
    }

    protected void uploadThisImg(int pos){
        new ImgAsyncTask().execute(imgupDocList.get(pos),pos+"");
    }


    protected void uploadThisMedia(final String uri,final int req){

        Toast t = Toast.makeText(getApplicationContext(),"Post will be published. Hold tight we'll let you know when it's complete",Toast.LENGTH_LONG);
        t.setGravity(Gravity.CENTER,0,0);
        t.show();

//        new SingleAsyncTask().execute(uri,req+"");
        uploadHandler = new Handler();


        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Intent intent =CommonMethods.postData(upLoadServerUri,uri);
                intent.putExtra("req",req);
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
            modelWork.setMedia_src("app_res/"+file);
            uploadToDb(modelWork);
            Log.d("NGOApp","NGOApp File Path = "+receivedIntent.getExtras().getString("filepath"));
        }else{
            Log.d("NGOApp","NGOApp File serverResponseCode="+receivedIntent.getExtras().getInt("resp_code"));
        }
    }

    private class SingleAsyncTask extends AsyncTask<String, Integer, Intent> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showPbar(true);
        }

        @Override
        protected Intent doInBackground(String... params) {
            Intent intent =CommonMethods.postData(upLoadServerUri,params[0]);
            intent.putExtra("req",params[1]);
            return intent;
        }

        protected void onPostExecute(Intent receivedIntent) {
            if (receivedIntent.getExtras().getInt("resp_code") == 200) {

                String file = receivedIntent.getStringExtra("filepath");
                File f = new File(Environment.getExternalStorageDirectory() + File.separator+ file);
                CommonMethods.deleteFile(f,getApplicationContext());
                modelWork.setMedia_src("app_res/"+file);
                uploadToDb(modelWork);
                Log.d("NGOApp","NGOApp File Path = "+receivedIntent.getExtras().getString("filepath"));
            }else{
                Log.d("NGOApp","NGOApp File serverResponseCode="+receivedIntent.getExtras().getInt("resp_code"));
            }
        }



        protected void onProgressUpdate(Integer... progress) {
            //    pBar.setProgress(progress[0]);
        }
    }


    private class ImgAsyncTask extends AsyncTask<String, Integer, Intent> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Intent doInBackground(String... params) {
            Intent intent =CommonMethods.postData(upLoadServerUri,params[0]);
            intent.putExtra("pos",params[1]);
            return intent;
        }

        protected void onPostExecute(Intent receivedIntent) {
            try {
                if (receivedIntent.getExtras().getInt("resp_code") == 200) {

                    String file = receivedIntent.getStringExtra("filepath");
                    File f = new File(Environment.getExternalStorageDirectory() + File.separator + file);
                    CommonMethods.deleteFile(f, getApplicationContext());

                    int pos = Integer.parseInt(receivedIntent.getStringExtra("pos"));
                    if (pos != -1) {
                        pos++;
                        if (pos < imgdocList.size()) {
                            new ImgAsyncTask().execute(imgupDocList.get(pos), pos + "");
                        } else {
                            String str = "";
                            for (int i = 0; i < imgserverDocList.size(); i++)
                                str += "app_res/" + imgserverDocList.get(i) + ",";

                            str = str.substring(0, str.length() - 1);

                            modelWork.setMedia_src(str+work.getMedia_src());
                            Log.d("Tink","Adding image : '"+str+"' into '"+work.getMedia_src()+"'");
                            uploadToDb(modelWork);
                            Toast.makeText(getApplicationContext(), "All files uploaded successfully. Making your work live", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_LONG).show();
                    }
                    Log.d("NGOApp", "NGOApp File Path = " + receivedIntent.getExtras().getString("filepath"));
                } else {
                    Log.d("NGOApp", "NGOApp File serverResponseCode=" + receivedIntent.getExtras().getInt("resp_code"));
                }
            }catch (NullPointerException e){

            }
        }


        protected void onProgressUpdate(Integer... progress) {
            //    pBar.setProgress(progress[0]);
        }
    }


    private class CoverAsync extends AsyncTask<String, Integer, Intent> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Intent doInBackground(String... params) {
            Intent intent =CommonMethods.postData(upLoadServerUri,params[0]);
            intent.putExtra("req",params[1]);
            intent.putExtra("type",params[2]);
            return intent;
        }

        protected void onPostExecute(Intent receivedIntent) {

            if (receivedIntent.getExtras().getInt("resp_code") == 200) {

                int req = Integer.parseInt(receivedIntent.getExtras().getString("req"));

                if(req == REQUEST_COVER_IMAGE){
                    if(receivedIntent.getExtras().getString("type")!=null){
                        String fpath = "app_res/" + receivedIntent.getExtras().getString("filepath");
                        modelWork.setCover_img(fpath);
                        publishPost();
                    }else{
                        String fpath = "app_res/" + receivedIntent.getExtras().getString("filepath");

                        modelWork.setCover_img(fpath);
                        uploadToDb(modelWork);
                    }

                }else{
                    Toast.makeText(getApplicationContext(),"Here",Toast.LENGTH_LONG).show();
                    if(receivedIntent.getExtras().getString("type")!=null){
                        publishPost();
                        Toast.makeText(getApplicationContext(),"Publishing ",Toast.LENGTH_LONG).show();
                        Log.d("NGOApp","NGOApp2 Publishing Post");
                    }else{
                        Toast.makeText(getApplicationContext(),"Publishing ",Toast.LENGTH_LONG).show();
                        Log.d("NGOApp","NGOApp2 Not publishing post");
                    }
                }

            }else{
                Log.d("NGOApp","NGOApp File serverResponseCode="+receivedIntent.getExtras().getInt("resp_code"));
            }
        }



        protected void onProgressUpdate(Integer... progress) {
            //    pBar.setProgress(progress[0]);
        }
    }



    private class MyAsyncTask extends AsyncTask<String, Integer, Intent> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Intent doInBackground(String... params) {
            Intent intent =CommonMethods.postData(upLoadServerUri,params[0]);
            intent.putExtra("pos",params[1]);
            return intent;
        }

        protected void onPostExecute(Intent receivedIntent) {
            if (receivedIntent.getExtras().getInt("resp_code") == 200) {

                String file = receivedIntent.getStringExtra("filepath");
                File f = new File(Environment.getExternalStorageDirectory() + File.separator+ file);
                CommonMethods.deleteFile(f,getApplicationContext());

                int pos = Integer.parseInt(receivedIntent.getStringExtra("pos"));
                if(pos!=-1) {
                    pos++;
                    if (pos < upDocList.size()) {
                        new MyAsyncTask().execute(upDocList.get(pos), pos + "");
//                        Toast.makeText(getApplicationContext(), docList.get(pos).getLastPathSegment() + " uploaded, Remaining "+ (docList.size() - 1 - pos), Toast.LENGTH_LONG).show();
                    } else {
                        String str = "";
                        for(int i=0;i<serverDocList.size();i++)
                            str+="app_res/"+serverDocList.get(i)+",";

                        str = str.substring(0,str.length()-1);

                        modelWork.setMedia_src(str+work.getMedia_src());
                        Log.d("Tink","Now STR: '"+str+"' into '"+work.getMedia_src()+"'");
                        uploadToDb(modelWork);
                        Toast.makeText(getApplicationContext(), "All files uploaded successfully. Making your work live", Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(getApplicationContext(),"Couldn't get intent data",Toast.LENGTH_LONG).show();
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
        showPbar(true);
        modelWork.setHeading(titleEdit.getText().toString());
        modelWork.setStatus(descEdit.getText().toString());
        modelWork.setType(type);
        modelWork.setTypework(selType+"");
        modelWork.setCategory(selCat+"");
        modelWork.setDenabled(downSwitch.isChecked());
        modelWork.setSenabled(shareSwitch.isChecked());

        if(type.equalsIgnoreCase("research")){
            modelWork.setAuthor(author.getText().toString());
            modelWork.setDop(dop.getText().toString());
            modelWork.setDoi(doi.getText().toString());
        }else{
            modelWork.setAuthor("null");
            modelWork.setDop("null");
            modelWork.setDoi("null");
        }

        if(isGroup)
            modelWork.setPlace(gid);
        else
            modelWork.setPlace("post");
        callDbNet(modelWork);
    }

    private void callDbNet(final ModelWork modelWork){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                modelWork.setId(work.getId());
                NetworkHandler.updatePost(EditPostActivity.this, modelWork,newCover, new Callback() {
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

                                statusResponse(true,"Post Updated!");

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


    public void pickImage(int REQUEST_CODE) {
        if(checkForStoragePermissions()) {

            Intent intent = new Intent();
            if(REQUEST_CODE == REQ_VIDEO) {
                intent.setType("video/mp4");
                Log.d("Res","Wupk intenting video");
            }
            else if(REQUEST_CODE == REQ_AUDIO){
                intent.setType("audio/mp3");
                Log.d("Res","Wupk intenting image");
            }else if(REQUEST_CODE == REQ_DOC){
                intent.setType("file/*");
                Log.d("Res","Wupk intenting image");
            }else if(REQUEST_CODE == REQ_PHOTO){
                intent.setType("image/*");
                Log.d("Res","Wupk intenting image");
            }
            intent.setType("*/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            Intent chooser = Intent.createChooser(intent, "Choose your app");

//            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(chooser,REQUEST_CODE);
//            }else
//                startActivityForResult(intent, REQUEST_CODE);
        }else{
            askForStoragePermission();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        if(resultCode == RESULT_OK){
            if(requestCode == REQ_DOC){
                if(isMimeDoc(data.getData())) {
                    REQ_USER = REQ_DOC;
                    newMedia = true;
                    docSelected(data.getData());
                }else{
                    Toast.makeText(getApplicationContext(),"Please select a PDF file",Toast.LENGTH_LONG).show();
                }
            }else if(requestCode == REQ_PHOTO){
                if(isMimePhoto(data.getData())) {
                    REQ_USER = REQ_PHOTO;
                    newMedia = true;
                    photoSelected(data.getData());
                }else{
                    Toast.makeText(getApplicationContext(),"Please select an image file",Toast.LENGTH_LONG).show();
                }
            }else if(requestCode == REQ_VIDEO){
                if(isMimeVideo(data.getData())) {
                    REQ_USER = REQ_VIDEO;
                    newMedia = true;
                    videoSelected(data.getData());
                }else{
                    Toast.makeText(getApplicationContext(),"Please select a video file",Toast.LENGTH_LONG).show();
                }
            }else if(requestCode == REQ_AUDIO){
                if(isMimeAudio(data.getData())) {
                    REQ_USER = REQ_AUDIO;
                    newMedia = true;
                    audioSelected(data.getData());
                }else{
                    Toast.makeText(getApplicationContext(),"Please select an audio file",Toast.LENGTH_LONG).show();
                }
            }else if(requestCode == REQUEST_COVER_IMAGE){
                if(isMimePhoto(data.getData())) {
                    REQ_USER = -1;
                    newCover = true;
                    newMedia = true;
                    processImage(data, REQUEST_COVER_IMAGE);
                }else{
                    Toast.makeText(getApplicationContext(),"Please select an image file",Toast.LENGTH_LONG).show();
                }
            }else{
                REQ_USER = -1;
                // Something else here
            }

        }
    }



    protected String getMime(Uri uri){
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return  mime.getExtensionFromMimeType(cR.getType(uri));
    }

    protected boolean isMimeAudio(Uri uri){
        String mime = getMime(uri);
        if(mime.equalsIgnoreCase("mp3")){
            return true;
        }else
            return false;
    }

    protected boolean isMimeVideo(Uri uri){
        String mime = getMime(uri);
        if(mime.equalsIgnoreCase("mp4") || mime.equalsIgnoreCase("avi")||mime.equalsIgnoreCase("mkv")){
            return true;
        }else
            return false;
    }


    protected boolean isMimePhoto(Uri uri){
        String mime = getMime(uri);
        if(mime.equalsIgnoreCase("jpg")||
                mime.equalsIgnoreCase("jpeg")||
                mime.equalsIgnoreCase("png")){
            return true;
        }else
            return false;
    }


    protected boolean isMimeDoc(Uri uri){
        String mime = getMime(uri);
        if(mime.equalsIgnoreCase("pdf") || mime.equalsIgnoreCase("doc") || mime.equalsIgnoreCase("docx") || mime.equalsIgnoreCase("ppt") || mime.equalsIgnoreCase("pptx")){
            return true;
        }else
            return false;
    }




    protected void processImage(Intent data, int req){

        try {
            InputStream stream = getContentResolver().openInputStream(data.getData());
            Bitmap bitmap = BitmapFactory.decodeStream(stream);
            stream.close();

            if(req == REQUEST_COVER_IMAGE) {
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                coverImgView.setLayoutParams(layoutParams);
                coverImgView.setImageBitmap(bitmap);
                coverImgView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                coverImgView.setColorFilter(null);
            }

            saveCamShotToFile(bitmap,req);
        }catch (FileNotFoundException ex){
            Log.d("res","Wupk File Not Found:"+ex);
        }catch (IOException e){
            Log.d("res","Wupk IOException:"+e);
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

        String prefix = "COVERIMG_";
        File f = new File(Environment.getExternalStorageDirectory() + File.separator + prefix+DataHandler.getUserId(getApplicationContext())+"_"+rand+".jpg");

        try {
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());

            if(reqType == REQUEST_COVER_IMAGE)
                coverlUrl = f.getAbsolutePath();

            Log.d("GSE","Firing saved to file:"+f.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    protected void clearSelections(){
        photoUri = null;
        videoUri = null;
        audioUri = null;
        docList = null;
        upDocList = null;
        serverDocList = null;

        stopPlaying();

        docRecycler.setVisibility(View.GONE);
        videoView.setVisibility(View.GONE);
        photoView.setVisibility(View.GONE);
        audioView.setVisibility(View.GONE);
    }

    protected void audioSelected(Uri uri){
        clearSelections();

        mp = new MediaPlayer();
        ((TextView)findViewById(R.id.audioFileName)).setText(uri.getLastPathSegment());

        audioBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(mp!=null&&fromUser){
                    mp.seekTo(progress*1000);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        saveAudioToFile(uri);
        startAudioPlayer(uri.getPath());
        audioView.setVisibility(View.VISIBLE);
    }

    public void startAudioPlayer(String path){
        //set up MediaPlayer
        try {
            mp.setDataSource(path);
            mp.prepare();
            mp.start();

            initializeSeekBar();
            audioView.findViewById(R.id.audioPauseBtn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mp.pause();
                    audioView.findViewById(R.id.audioPauseBtn).setVisibility(View.GONE);
                    audioView.findViewById(R.id.audioPlayBtn).setVisibility(View.VISIBLE);
                }
            });

            audioView.findViewById(R.id.audioPlayBtn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mp.start();
                    audioView.findViewById(R.id.audioPauseBtn).setVisibility(View.VISIBLE);
                    audioView.findViewById(R.id.audioPlayBtn).setVisibility(View.GONE);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(),"Audio failed:"+e,Toast.LENGTH_LONG).show();
        }
    }

    protected void stopPlaying(){
        // If media player is not null then try to stop it
        if(mp!=null){
            mp.stop();
            mp.release();
            mp = null;
            if(mHandler!=null){
                mHandler.removeCallbacks(mRunnable);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopPlaying();
    }

    protected void getAudioStats(){
        int duration  = mp.getDuration()/1000; // In milliseconds
        int due = (mp.getDuration() - mp.getCurrentPosition())/1000;
        int pass = duration - due;

        if(duration>59){
            int min = duration/60;
            int ss = duration%60;
            String s = ss+"";
            String mm = min+"";
            if(ss<10)
                s = "0"+ss;
            if(min<10)
                mm = "0"+min;

            ((TextView)audioView.findViewById(R.id.audioDur)).setText("Duration : "+ mm+":"+s);
        }else{
            String mm = "00";
            String ss = duration<10?"0"+duration:duration+"";
            ((TextView)audioView.findViewById(R.id.audioDur)).setText("Duration : "+mm+":"+ss);
        }
//        mPass.setText("" + pass + " seconds");
//        mDuration.setText("" + duration + " seconds");
//        mDue.setText("" + due + " seconds");
    }

    protected void initializeSeekBar(){
        audioBar.setMax(mp.getDuration()/1000);

        mRunnable = new Runnable() {
            @Override
            public void run() {
                if(mp!=null){
                    int mCurrentPosition = mp.getCurrentPosition()/1000; // In milliseconds
                    audioBar.setProgress(mCurrentPosition);
                    getAudioStats();
                }
                mHandler.postDelayed(mRunnable,1000);
            }
        };
        mHandler.postDelayed(mRunnable,1000);
    }

    protected void videoSelected(Uri uri){
        clearSelections();

        processVideo(uri);
    }

    private void processVideo(Uri data){
        Log.d("Res","Wupk processVideo: " +(data!=null));
        saveVideoToFile(data);
        playvideo(data);
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

    protected void docSelected(Uri uri){

        if(REQ_USER!=REQ_DOC)
            clearSelections();
        else
            REQ_USER = REQ_DOC;

        if(docList==null)
            docList = new ArrayList<>();

        saveDocToFile(uri,getMimeType(getApplicationContext(),uri));
        docList.add(uri);
        setDocRecycler(docList);
        docRecycler.setVisibility(View.VISIBLE);
    }

    public void removeFromDocList(Object object){
        docList.remove(object);
    }

    protected void photoSelected(Uri uri){

        if(REQ_USER!=REQ_PHOTO)
            clearSelections();
        else
            REQ_USER = REQ_PHOTO;

        if(imgdocList==null)
            imgdocList = new ArrayList<>();

        imgdocList.add(uri);

        processImage(uri);


//        photoView.setVisibility(View.VISIBLE);
        imgRecycler.setVisibility(View.VISIBLE);
    }

    protected void processImage(Uri data){

        try {
            InputStream stream = getContentResolver().openInputStream(data);
            Bitmap bitmap = BitmapFactory.decodeStream(stream);
            stream.close();

//                photoView.setImageBitmap(bitmap);
//                photoView.setVisibility(View.VISIBLE);

            savePhoto(bitmap);
        }catch (FileNotFoundException ex){
            Log.d("res","Wupk File Not Found:"+ex);
        }catch (IOException e){
            Log.d("res","Wupk IOException:"+e);
        }
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


    protected void saveDocToFile(Uri uri, String mime){
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            byte[] buffer = new byte[inputStream.available()];

            inputStream.read(buffer);

//            Toast.makeText(getApplicationContext(),"Saving the "+mime+" file!",Toast.LENGTH_LONG).show();
            File f = new File(Environment.getExternalStorageDirectory() + File.separator + "DOC_"+DataHandler.getUserId(getApplicationContext())+System.currentTimeMillis()+"."+mime);

            OutputStream outStream = new FileOutputStream(f);
            outStream.write(buffer);

            Log.d("Res","Wupk REQ-VID : " + f.getName() + " aaa :"+f.getAbsolutePath());

            if(upDocList==null)
                upDocList = new ArrayList<>();

            upDocList.add(f.getAbsolutePath());

            if(serverDocList==null)
                serverDocList = new ArrayList<>();

            serverDocList.add(f.getName());
            Log.d("Res","Wupk REQ-DOC: Adding in doc :" + upDocList.size());


        }catch (Exception e){
            Log.d("Res","Wupk REQ-VID : Exception : "+e);
        }
    }

    protected void saveAudioToFile(Uri uri){
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            byte[] buffer = new byte[inputStream.available()];

            inputStream.read(buffer);

            File f = new File(Environment.getExternalStorageDirectory() + File.separator + "AUDIO_"+DataHandler.getUserId(getApplicationContext())+".mp3");

            OutputStream outStream = new FileOutputStream(f);
            outStream.write(buffer);

            Log.d("Res","Wupk REQ-VID : " + f.getName() + " aaa :"+f.getAbsolutePath());
            audioUri = f.getAbsolutePath();
        }catch (Exception e){
            Log.d("Res","Wupk REQ-VID : Exception : "+e);
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
            videoUri = f.getAbsolutePath();
        }catch (Exception e){
            Log.d("Res","Wupk REQ-VID : Exception : "+e);
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

            photoUri = f.getAbsolutePath();

            if(imgupDocList==null)
                imgupDocList = new ArrayList<>();

            if(imgserverDocList==null)
                imgserverDocList = new ArrayList<>();

            imgupDocList.add(f.getAbsolutePath());
            imgserverDocList.add(f.getName());

            setImageRecycler(imgupDocList);

            Log.d("GSE","Firing saved to file:"+f.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void setImageRecycler(List<String> list){
        if(list!=null&&list.size()>0){
            imgRecycler.setVisibility(View.VISIBLE);
            imgAdapter = new PhotoAdapter(getApplicationContext(),list);
            Log.d("Imin","Imin Img list : "+list.size());
            LinearLayoutManager layman = new LinearLayoutManager(getApplicationContext(),RecyclerView.HORIZONTAL,false);
            imgRecycler.setLayoutManager(layman);
            imgRecycler.setAdapter(imgAdapter);
        }else{
            Toast.makeText(getApplicationContext(),"Image RV null",Toast.LENGTH_LONG).show();
        }
    }


    protected void setDocRecycler(List<Uri> list){
        if(list!=null&&list.size()>0){
            Log.d("DocList","Size is "+list.size());
            docAdapter = new DocUriAdapter(EditPostActivity.this,list);
            LinearLayoutManager layman = new LinearLayoutManager(getApplicationContext(),RecyclerView.VERTICAL,false);
            docRecycler.setLayoutManager(layman);
            docRecycler.setAdapter(docAdapter);
        }else{
            Log.d("DocList","Size is "+0);
        }
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



    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    public boolean checkForStoragePermissions(){
        if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED)
            return true;
        else
            return false;
    }

    public void askForStoragePermission(){
        ActivityCompat.requestPermissions(EditPostActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE},REQ_PER_READ_WRITE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQ_PER_READ_WRITE:
                Toast.makeText(getApplicationContext(),"Let's try again",Toast.LENGTH_LONG).show();
                break;

        }
    }

    protected void beforeAnything(){

        type = getIntent().getStringExtra("type");

        try{
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Edit Post");

        }catch (Exception e){

        }

        if(!checkForStoragePermissions()){
            askForStoragePermission();
        }
    }
}
