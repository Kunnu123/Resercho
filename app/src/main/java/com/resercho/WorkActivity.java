package com.resercho;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.resercho.Adapters.AdapterDiscover;
import com.resercho.Adapters.MultiPdfAdapter;
import com.resercho.Adapters.PhotoAdapter;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.Html;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.resercho.POJO.ModelCitation;
import com.resercho.POJO.ModelWork;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static com.resercho.NetworkHandler.root_dir;

public class WorkActivity extends AppCompatActivity {

    ModelWork work;

    ImageView mainImage,profImage;
    TextView title,uname,about,desc;
    Button openBtn;

    View citeBox,copyBtn;
    TextView citeText;

    View citeBtn, shareBtn, saveBtn, downloadBtn;

    private PlayerView playerView;
    private SimpleExoPlayer player;

    ///
    private boolean playWhenReady = true;
    private int currentWindow = 0;
    private long playbackPosition = 0;

    TextView saveText;
    ImageView saveIcon;

    MultiPdfAdapter multiAdapter;
    RecyclerView multiRecycler;

    PhotoAdapter photoAdapter;
    TextView attachTv;

    boolean isMedia = false;


    // Similar
    // Work
    RecyclerView workRecycler;
    AdapterDiscover adapterWork;

    private class SimilarType{
        int category;
        String type;
        String pid;
        boolean university;

        public String getPid() {
            return pid;
        }

        public void setPid(String pid) {
            this.pid = pid;
        }

        public SimilarType() {
        }

        public int getCategory() {
            return category;
        }

        public void setCategory(int category) {
            this.category = category;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public boolean isUniversity() {
            return university;
        }

        public void setUniversity(boolean university) {
            this.university = university;
        }
    }


    private SimilarType similarType;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work);

        init();

        try {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setTitle("");

            work = new ModelWork();
            work.setMedia_src(getIntent().getStringExtra("media_src"));
            work.setHeading(getIntent().getStringExtra("heading"));
            work.setUsername(getIntent().getStringExtra("uname"));
            work.setStatus(getIntent().getStringExtra("desc"));
            work.setCover_img(getIntent().getStringExtra("cover"));
            work.setProfpic(getIntent().getStringExtra("prof"));
            work.setUserBio(getIntent().getStringExtra("bio"));
            work.setId(getIntent().getStringExtra("pid"));
            work.setTime(getIntent().getStringExtra("time"));
            work.setPost_of(getIntent().getStringExtra("uid"));
            work.setSaved(getIntent().getBooleanExtra("saved", false));
            work.setLike(getIntent().getBooleanExtra("liked", false));
            work.setDoi(getIntent().getStringExtra("doi"));
            work.setAuthor(getIntent().getStringExtra("author"));
            work.setDop(getIntent().getStringExtra("dop"));
            work.setType(getIntent().getStringExtra("type"));
            work.setDenabled(getIntent().getBooleanExtra("denabled", false));
            work.setSenabled(getIntent().getBooleanExtra("senabled", false));
            work.setLikes(getIntent().getLongExtra("likes",0));
            work.setShareCount(getIntent().getLongExtra("shares",0));
            work.setSavedCount(getIntent().getLongExtra("saves",0));
            work.setCategory(getIntent().getStringExtra("category"));


            similarType = new SimilarType();

            similarType.setCategory(Integer.parseInt(work.getCategory()));
            similarType.setType("research");
            similarType.setUniversity(DataHandler.isUnivMode(WorkActivity.this));
            similarType.setPid(work.getId());

            if(work.getType().equalsIgnoreCase("research"))
                citeBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showCiteBox(true);
                    }
                });
            else
                citeBtn.setVisibility(View.GONE);


        }catch (Exception e) {

        }
            if(work.getMedia_src()!=null && !work.getMedia_src().equalsIgnoreCase("null")) {
                String mime = UtilityMethods.getMimeFromUrl(work.getMedia_src());
                if(mime!=null) {
                    if (mime.equalsIgnoreCase("pdf")) {
                        work.setPdflist(new ArrayList<String>(Arrays.asList(work.getMedia_src().split(","))));
                        Log.d("Resercho", "CJson Setted list of PDFS" + work.getPdflist().size());
                    } else {
                        work.setImglist(new ArrayList<String>(Arrays.asList(work.getMedia_src().split(","))));
                        Log.d("Resercho", "CJson Setted list of IMAGES : " + work.getImglist().size());
                    }
                }
            }

            if(work.isSenabled()){
                shareBtn.setEnabled(true);
                shareBtn.setAlpha(1.0f);
            }else{
                shareBtn.setEnabled(false);
                shareBtn.setAlpha(0.4f);
            }


            if(work.isDenabled()){
                downloadBtn.setEnabled(true);
                downloadBtn.setAlpha(1.0f);
            }else{
                downloadBtn.setEnabled(false);
                downloadBtn.setAlpha(0.4f);
            }

            setSaved(work.isSaved());
            title.setText(work.getHeading());

            Log.d("stund","stund Work : (Media)"+work.getMedia_src() );
            Log.d("stund","stund Work : (Cover)"+work.getCover_img() );

            Glide.with(getApplicationContext()).load(work.getProfpic()).placeholder(R.drawable.downloading).into(profImage);
            uname.setText(work.getUsername());



        if(work.getPdflist()!=null) {
            mainImage.setVisibility(View.VISIBLE);
            Glide.with(getApplicationContext()).load(work.getCover_img()).placeholder(R.drawable.downloading).into(mainImage);
            showMultiRecycler(work.getPdflist());
        }
        else {
            if(work.getImglist()!=null){
                showPhotoRecycler(work.getImglist());
            }else{
//                if(url!=null) {
//                    mainImage.setVisibility(View.VISIBLE);
//                    Glide.with(getApplicationContext()).load(url).placeholder(R.drawable.downloading).into(mainImage);
//                }

            }
        }

        if(!work.getCover_img().equalsIgnoreCase(root_dir+"null")) {
            mainImage.setVisibility(View.VISIBLE);
            Glide.with(getApplicationContext()).load(work.getCover_img()).placeholder(R.drawable.downloading).into(mainImage);
        }else {
            mainImage.setVisibility(View.GONE);
        }


        uname.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                    intent.putExtra("uid",work.getPost_of());
                    startActivity(intent);
                }
            });
            if(work.getUserBio()==null||work.getUserBio().equalsIgnoreCase("null")||work.getUserBio().length()<1)
                about.setText("Bio details not provided by user");
            else
                about.setText(work.getUserBio());
            desc.setText(work.getStatus());

            String type = getMimeType(WorkActivity.this, Uri.parse(root_dir + work.getMedia_src()));

            if(type.equalsIgnoreCase("pdf")){
                showDownloadBtn();
                openBtn.setText("OPEN PDF");
//                openBtn.setVisibility(View.VISIBLE);
                attachTv.setVisibility(View.VISIBLE);
                multiRecycler.setVisibility(View.VISIBLE);
            }else if(type.equalsIgnoreCase("mp4")||type.equalsIgnoreCase("avi")){
                showDownloadBtn();
                openBtn.setText("Play Video");
                mainImage.setVisibility(View.GONE);
                multiRecycler.setVisibility(View.GONE);
                playerView.setVisibility(View.VISIBLE);
                downloadBtn.setVisibility(View.VISIBLE);
                isMedia = true;
                if(work.isDenabled()){
                    downloadBtn.setEnabled(true);
                }else{
                    downloadBtn.setEnabled(false);
                }
                initializePlayer(root_dir+ work.getMedia_src());
            }else if(type.equalsIgnoreCase("mp3")){
                showDownloadBtn();
                openBtn.setText("Play Audio");
                mainImage.setVisibility(View.GONE);
                playerView.setVisibility(View.VISIBLE);
                multiRecycler.setVisibility(View.GONE);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,220);
                params.setMargins(14,10,10,14);
                playerView.setLayoutParams(params);
                initializePlayer(root_dir+work.getMedia_src());
                downloadBtn.setVisibility(View.VISIBLE);
                isMedia = true;
                if(work.isDenabled()){
                    downloadBtn.setEnabled(true);
                }else{
                    downloadBtn.setEnabled(false);
                }
            }

        listeners();


    }


    protected void fetchSimilar(final SimilarType similarType){
        final int univ = DataHandler.isUnivMode(getApplicationContext())?1:0;
        final String uid = DataHandler.getUserId(getApplicationContext());
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {

                NetworkHandler.fetchSimilarPosts(similarType.getType(), univ, similarType.getCategory(), uid,similarType.getPid(), new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.d("SimiPost","Fetching OnFailure : "+e);
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String resp = response.body().string();

                        Log.d("SimiPost","Fetching OnResponse : "+resp);
                        parseSimilarJson(resp);
                    }
                });

            }
        });
        thread.start();
    }

    List<ModelWork> modelWorkList;
    private void parseSimilarJson(String json) {
        modelWorkList = new ArrayList<>();

        try {
            JSONObject jsonObject = new JSONObject(json);
            if (jsonObject.getInt("success") == 1) {
                JSONArray arr = jsonObject.getJSONArray("data");
                for (int i = 0; i < arr.length(); i++) {
                    ModelWork w = ConverterJson.parseTrendingJson(arr.getJSONObject(i));
                    if (w != null) {
                        modelWorkList.add(w);
                        Log.d("Resercho", "HomeFrag Disin : Iteration : " + i + " W:" + w.getId());
                    } else
                        Log.d("Resercho", "HomeFrag Disin : Null Model : " + i);
                }

                showWorkRecycler(modelWorkList);
                Log.d("SimiPost","Colleceted about : "+modelWorkList.size() + " elems");
            }
        }catch (Exception e){
            Log.d("SimiPost","JSON EXCEPTION : "+e);
        }
    }


    protected void showWorkRecycler(final List<ModelWork> list){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                LinearLayoutManager layman = new GridLayoutManager(WorkActivity.this,2);
//                StaggeredGridLayoutManager layman = new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL);
                adapterWork = new AdapterDiscover(WorkActivity.this,list);
                workRecycler.setLayoutManager(layman);
                workRecycler.setAdapter(adapterWork);
                adapterWork.notifyDataSetChanged();


            }
        });
        Log.d("Resercho","Dashboard Disin showRv : "+list.size());

    }

    protected void showDownloadBtn(){
//        downloadBtn.setVisibility(View.VISIBLE);
    }

    protected void listeners(){
        openBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(work!=null&&work.getMedia_src()!=null) {
                    String type = getMimeType(WorkActivity.this, Uri.parse(work.getMedia_src()));
                    if (type.equalsIgnoreCase("pdf")) {
                        Intent intent = new Intent(WorkActivity.this,PdfWebActivity.class);
                        intent.putExtra("url",root_dir +work.getMedia_src());
                        startActivity(intent);
                    }else if(type.equalsIgnoreCase("mp4")||type.equalsIgnoreCase("avi")||type.equalsIgnoreCase("avi")){
                        Intent intent = new Intent(WorkActivity.this,VideoStreamingActivity.class);
                        intent.putExtra("url",root_dir +work.getMedia_src());
                        startActivity(intent);
                    }else{
                        Toast.makeText(getApplicationContext(),"Mime : "+type,Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(getApplicationContext(),"There has been some error. Reload",Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        toastOnMain("Here called!");
    }

    protected void init(){

        // Similar

        workRecycler = findViewById(R.id.similarRv);

        multiRecycler = findViewById(R.id.multiRecycler);
        attachTv = findViewById(R.id.attachmentTxt);
        mainImage = findViewById(R.id.postImage);
        title = findViewById(R.id.title);
        profImage = findViewById(R.id.userProfPic);
        uname = findViewById(R.id.username);
        about = findViewById(R.id.uabout);
        openBtn =findViewById(R.id.openDoc);
        desc = findViewById(R.id.description);

        saveText = findViewById(R.id.saveText);
        saveIcon= findViewById(R.id.saveIcon);
        citeBtn = findViewById(R.id.citeBtn);
        shareBtn = findViewById(R.id.shareBtn);
        saveBtn = findViewById(R.id.saveBtn);
        downloadBtn = findViewById(R.id.downloadBtn);

        citeBox = findViewById(R.id.citebox);
        copyBtn = citeBox.findViewById(R.id.citeCopy);
        citeText = citeBox.findViewById(R.id.citationText);
        playerView = findViewById(R.id.video_view);

        copyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ModelCitation citation = new ModelCitation();
                citation.setDoneby(DataHandler.getUserId(getApplicationContext()));
                citation.setOwner(work.getPost_of());
                citation.setPid(work.getId());
                sendCitation(citation);
            }
        });

        downloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"Downloading...",Toast.LENGTH_SHORT).show();
                sendDownload(work.getId(),work.getPost_of());
            }
        });

        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                share(work.getId());
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendSaving(work.getId());
            }
        });

    }

    protected void downfileInit(){
        String url  = root_dir + work.getMedia_src();
        String mime = UtilityMethods.getMimeFromUrl(url);
        if (mime != null && (mime.equalsIgnoreCase("png") || mime.equalsIgnoreCase("jpeg") || mime.equalsIgnoreCase("mp4")
                || mime.equalsIgnoreCase("jpg") || mime.equalsIgnoreCase("mp3") || mime.equalsIgnoreCase("pdf") || mime.equalsIgnoreCase("avi"))) {

            File f = new File(Environment.getExternalStorageDirectory() + File.separator + "DOWNLOAD_"+DataHandler.getUserId(getApplicationContext())+System.currentTimeMillis()+"."+mime);
            File folder = new File(Environment.getExternalStorageDirectory() +
                    File.separator + "Resercho");
            boolean success = true;
            if (!folder.exists()) {
                success = folder.mkdirs();
            }
            if (success) {
                f = new File(Environment.getExternalStorageDirectory()+File.separator + folder.getName() + File.separator + "DOWNLOAD_"+DataHandler.getUserId(getApplicationContext())+System.currentTimeMillis()+"."+mime);
            } else {
                // Do something else on failure
            }


            downloadFile(url,f);
        }else{
            Toast.makeText(getApplicationContext(),"File not downloadable",Toast.LENGTH_LONG).show();
        }

    }

    protected void downLoadOnThread(String url, File outputFile){
        try {
            URL u = new URL(url);
            URLConnection conn = u.openConnection();
            int contentLength = conn.getContentLength();

            DataInputStream stream = new DataInputStream(u.openStream());

            byte[] buffer = new byte[contentLength];
            stream.readFully(buffer);
            stream.close();

            DataOutputStream fos = new DataOutputStream(new FileOutputStream(outputFile));
            fos.write(buffer);
            fos.flush();
            fos.close();
            toastOnMain("File downloaded");
        } catch(FileNotFoundException e) {
            toastOnMain("File could not be downloaded");
            return; // swallow a 404
        } catch (IOException e) {
            toastOnMain("File could not be downloaded");
            return; // swallow a 404
        }
    }



        private void downloadFile(final String url, final File outputFile) {


            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    downLoadOnThread(url,outputFile);
                }
            });

            thread.start();

        }



    protected void sendSaving(final String pid){
        saveText.setText("Requesting...");
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                NetworkHandler.sendSaved(getApplicationContext(), pid, work.isSaved(), new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.d("Res","savit onFailure:"+e);
                        toastOnMain("Failed Saving");
                        setSaved(work.isSaved());
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String resp = response.body().string();
                        Log.d("Res","savit onResponse:"+resp);
                        try{
                            JSONObject jsonObject = new JSONObject(resp);
                            if(jsonObject.getInt("success")==1){
                                if(jsonObject.has("deleted")){
                                    work.setSaved(false);
                                }else{
                                    work.setSaved(true);
                                }
                                setSaved(work.isSaved());
                            }else{
                                if(jsonObject.getString("reason").equalsIgnoreCase("exists")){
                                    toastOnMain("Already Saved!");
                                }else{
                                    toastOnMain("Something went wrong");
                                }
                            }
                        }catch (JSONException e){
                            setSaved(work.isSaved());
                            Log.d("Res","savit JSON Exception :"+e);
                        }
                    }
                });
            }
        });

        thread.start();
    }


    protected void sendDownload(final String pid, final String ownerid){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                NetworkHandler.sendDownload(WorkActivity.this, ownerid, pid, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.d("Downin","Downin onFailure:"+e);
                        toastOnMain("There was problem downloading file");
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String resp = response.body().string();
                        Log.d("Downin","Downin onFailure:"+resp);
                        try{
                            JSONObject object = new JSONObject(resp);
                            if(object.getInt("success")==1){
//                                toastOnMain("Download acknowledged");
                                downfileInit();
                            }else{
                                toastOnMain("There was problem downloading file");
                            }
                        }catch (JSONException e){
                            toastOnMain("There was problem downloading file");
                        }
                    }
                });
            }
        });

        thread.start();
    }

    protected void setSaved(final boolean saved){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(saved){
                    saveText.setText("Saved");
                    saveIcon.setImageTintList(ContextCompat.getColorStateList(getApplicationContext(),R.color.colorLike));
                }else{
                    saveText.setText("Save");
                    saveIcon.setImageTintList(ContextCompat.getColorStateList(getApplicationContext(),R.color.colorPrimary));
                }
            }
        });
    }
    protected void share(String id){
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, NetworkHandler.post_share_link+id);
        startActivity(intent);
    }
    private void initializePlayer(String url) {
        Uri uri = Uri.parse(url);

        player = ExoPlayerFactory.newSimpleInstance(WorkActivity.this);
        playerView.setPlayer(player);

        player.setPlayWhenReady(playWhenReady);
        player.seekTo(currentWindow, playbackPosition);
        player.prepare(buildMediaSource(uri), false, false);
    }

    private MediaSource buildMediaSource(Uri uri) {
        DataSource.Factory dataSourceFactory =
                new DefaultDataSourceFactory(this, "exoplayer-codelab");
        return new ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(uri);
    }

    protected void sendCitation(final ModelCitation citation){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                NetworkHandler.sendCitation(getApplicationContext(), citation, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        toastOnMain("Citation Failed");
                        Log.d("Cite","Citk Onfailure :"+e);
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String resp = response.body().string();
                        try{
                            JSONObject object = new JSONObject(resp);
                            if(object.getInt("success")==1){
                                toastOnMain("Cited");
                            }else{
                                if(object.has("reason")&&object.getString("reason").equalsIgnoreCase("exists")){
                                    toastOnMain("A citation already exists");
                                    Log.d("Cite","Citk JSON Exception reason");
                                }else{
                                    toastOnMain("Citation failed");
                                    Log.d("Cite","Citk JSON Exception success ");
                                }
                            }
                        }catch (JSONException e){
                            toastOnMain("Citation failed");
                            Log.d("Cite","Citk JSON Exception :"+e);
                        }
                    }
                });
            }
        });

        thread.start();;
    }


    protected void toastOnMain(final String msg){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_LONG).show();
            }
        });
    }

    protected void showCiteBox(boolean show){
        if (show) {
            citeBox.setVisibility(View.VISIBLE);

            String time = (Long.parseLong(UtilityMethods.getServerTimeStamp(work.getTime()))/1000)+"";
            String month = UtilityMethods.getMonthFromTimestamp(time);
            String year = UtilityMethods.getYearFromTimestamp(time);

//            String citeFormat = "" +
//                    "Title : "+work.getHeading()+
//                    "\nAuthor : "+work.getUsername()+
//                    "\nMonth:"+month+
//                    "\nYear:"+year+
//                    "\nPublisher: Resercho";

            String doi = "";
            if(work.getDoi()!=null && !work.getDoi().equalsIgnoreCase("null"))
                doi = ", "+ work.getDoi();
            String citeFormat = "" +
                    getReversedUsername(work.getUsername())+", <i>"+work.getHeading()+"</i>, Resercho, "+UtilityMethods.getYearDividedFromTimestamp(UtilityMethods.getServerTimeStamp(work.getTime()))+doi;
            citeFormat +=".";
            citeText.setText(Html.fromHtml(citeFormat));

            citeBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

            findViewById(R.id.mailay).setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    showCiteBox(false);
                    return false;
                }
            });


        }else{
            citeBox.setVisibility(View.GONE);
            findViewById(R.id.mailay).setOnClickListener(null);
        }
    }

    protected String getReversedUsername(String username){
        String[] splitedd = username.split(" ");
        String out = splitedd[splitedd.length > 1 ? splitedd.length - 1 : 0];
        if(splitedd[0].equalsIgnoreCase("Dr")||splitedd[0].equalsIgnoreCase("Dr.")){
            out = splitedd[0] +" "+ out;
        }
            if (splitedd.length > 1)
                out += " " + username.substring(0, username.lastIndexOf(" ")).replace("Dr.","").replace("Dr","");


        return out;
    }


    protected void showMultiRecycler(final List<String> list){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                multiAdapter = new MultiPdfAdapter(WorkActivity.this,list);
                multiAdapter.setType(UtilityMethods.getMimeFromUrl(work.getMedia_src()));
                multiAdapter.setOwner(work.getPost_of());
                multiAdapter.setPid(work.getId());
                multiAdapter.setDownloadEnabled(work.isDenabled());
                LinearLayoutManager layman = new LinearLayoutManager(WorkActivity.this,RecyclerView.VERTICAL,false);
                multiRecycler.setLayoutManager(layman);
                multiRecycler.setAdapter(multiAdapter);
            }
        });
    }

    protected void showPhotoRecycler(final List<String> list){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                multiRecycler.setVisibility(View.VISIBLE);
                photoAdapter = new PhotoAdapter(WorkActivity.this,list);
                photoAdapter.setUseGlide(true);
                photoAdapter.setLongRv(true);
                LinearLayoutManager layman = new LinearLayoutManager(WorkActivity.this,RecyclerView.VERTICAL,false);
                multiRecycler.setLayoutManager(layman);
                multiRecycler.setAdapter(photoAdapter);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(citeBox.getVisibility() == View.VISIBLE)
            citeBox.setVisibility(View.GONE);
        else
            finish();
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

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }


    @Override
    public void onPause() {
        super.onPause();
        if (Util.SDK_INT < 24) {
            releasePlayer();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(similarType!=null)
            fetchSimilar(similarType);
        else
            Log.d("SimiPost","It was null!");
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Util.SDK_INT >= 24) {
            releasePlayer();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if(isMedia){
            initializePlayer(root_dir + work.getMedia_src());
        }
    }

    private void releasePlayer() {
        if (player != null) {
            playWhenReady = player.getPlayWhenReady();
            playbackPosition = player.getCurrentPosition();
            currentWindow = player.getCurrentWindowIndex();
            player.release();
            player = null;
        }
    }
}
