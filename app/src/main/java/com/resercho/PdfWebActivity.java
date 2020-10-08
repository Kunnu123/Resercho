package com.resercho;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class PdfWebActivity extends AppCompatActivity {

    WebView webView;

    String path, pid,ownerid;
    FloatingActionButton downFab;
    boolean download = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_web);

        try{
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setTitle("");
        }catch (Exception e){

        }

        pid = getIntent().getStringExtra("pid");
        ownerid = getIntent().getStringExtra("owner");
        download = getIntent().getBooleanExtra("download",false);
        path = getIntent().getStringExtra("url");


        webView = findViewById(R.id.webView);

        try {
            String url = getIntent().getStringExtra("url");
            Log.d("PdfWw","PdfWw url :"+url);
            url = "http://docs.google.com/gview?embedded=true&url="+url;
            Log.d("PdfWw","PdfWw overall url :"+url);
            Log.d("TAG","WorkAct : "+  url);
            loadWebview(url);
        }catch (Exception e){
            Toast.makeText(getApplicationContext(),"Something went wrong",Toast.LENGTH_LONG).show();
        }

    }

    protected void loadWebview(String url){
        WebSettings webSetting = webView.getSettings();
        webSetting.setDomStorageEnabled(true);
        webSetting.setJavaScriptEnabled(true);
        webSetting.setBuiltInZoomControls(true);
        webSetting.setSupportZoom(true);
        webSetting.setLoadWithOverviewMode(true);
        webSetting.setUseWideViewPort(true);
        webSetting.setAppCacheEnabled(true);

        webView.setVisibility(View.INVISIBLE);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                showPbar(false);
                super.onPageFinished(view, url);
                webView.loadUrl("javascript:(function() { " +
                        "document.querySelector('[role=\"toolbar\"]').remove();})()");
                webView.setVisibility(View.VISIBLE);
            }
        });
        webView.setWebChromeClient(new WebChromeClient());
        webView.loadUrl(url);
    }

    protected void showPbar(boolean show){
        if(show){
            findViewById(R.id.pbar).setVisibility(View.VISIBLE);
        }else{
            findViewById(R.id.pbar).setVisibility(View.GONE);
        }

    }

    protected void toastOnMain(final String msg){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(download) {
            getMenuInflater().inflate(R.menu.menu_pdf_viewer, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.menu_download){
            if(checkForStoragePermissions()) {
                if (download) {
                    sendDownload(pid, ownerid);
                }
            }else{
                askForStoragePermission();
            }
        }else if(item.getItemId() == android.R.id.home){
            finish();
        }
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
        ActivityCompat.requestPermissions(PdfWebActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},101);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 101 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
            Toast.makeText(getApplicationContext(),"Now you can download the file",Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(getApplicationContext(),"We need permission to save this document on your device",Toast.LENGTH_LONG).show();
        }
    }


    protected void sendDownload(final String pid, final String ownerid){
        Toast.makeText(getApplicationContext(),"Starting...",Toast.LENGTH_SHORT).show();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                NetworkHandler.sendDownload(PdfWebActivity.this, ownerid, pid, new Callback() {
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
//                                downfileInit();
                                createFile();
                            }else{
                                toastOnMain("There was a problem while downloading the file");
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

    private static final int CREATE_FILE = 1626;

    private void createFile() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Intent intent1 = new Intent(Intent.ACTION_CREATE_DOCUMENT);
                intent1.addCategory(Intent.CATEGORY_OPENABLE);
                intent1.setType("application/pdf");
                intent1.putExtra(Intent.EXTRA_TITLE,"DOWNLOAD_"+System.currentTimeMillis()+".pdf");

                startActivityForResult(intent1, CREATE_FILE);
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 final Intent resultData) {
        super.onActivityResult(requestCode,resultCode,resultData);
        if (requestCode == CREATE_FILE   && resultCode == RESULT_OK) {
            // The result data contains a URI for the document or directory that
            // the user selected.
            Uri uri = null;
            if (resultData != null) {
                uri = resultData.getData();
                // Perform operations on the document using its URI.
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        downLoadOnThread(path,resultData.getData());
                        toastOnMain("File downloaded");
                    }
                });

                thread.start();
            }else{
                toastOnMain("There was some problem");
            }
        }else{
            toastOnMain("There was some problem");
        }
    }

    protected void downfileInit(){
        String mime = UtilityMethods.getMimeFromUrl(path);
        if (mime != null && (mime.equalsIgnoreCase("png") || mime.equalsIgnoreCase("jpeg") || mime.equalsIgnoreCase("mp4")
                || mime.equalsIgnoreCase("jpg") || mime.equalsIgnoreCase("mp3") || mime.equalsIgnoreCase("pdf") || mime.equalsIgnoreCase("avi"))) {

            File f = new File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) + File.separator + "DOWNLOAD_"+DataHandler.getUserId(getApplicationContext())+System.currentTimeMillis()+"."+mime);
            File folder = new File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) +
                    File.separator + "Resercho");
            boolean success = true;
            if (!folder.exists()) {
                success = folder.mkdirs();
            }
            success = true;
            if (success) {
                f = new File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)+File.separator  + folder.getName() + File.separator + "DOWNLOAD_"+DataHandler.getUserId(getApplicationContext())+System.currentTimeMillis()+"."+mime);
            } else {

                // Do something else on failure
                toastOnMain("Folder not created!");
            }
            downloadFile(path,f);
        }else{
            toastOnMain("File is not downloadable");
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

    protected void downLoadOnThread( final String url, final Uri uri){

        try {
            URL u = new URL(url);
            URLConnection conn = u.openConnection();
            int contentLength = conn.getContentLength();

            DataInputStream stream = new DataInputStream(u.openStream());

            byte[] buffer = new byte[contentLength];
            stream.readFully(buffer);
            stream.close();

            OutputStream outputStream;
            try {
                outputStream = getContentResolver().openOutputStream(uri);
                outputStream.write(buffer);
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch(FileNotFoundException e) {
            toastOnMain("File could not be downloaded : "+e);
            return; // swallow a 404
        } catch (IOException e) {
            toastOnMain("File could not be downloaded: "+e);
            return; // swallow a 404
        }
    }

    protected void downLoadOnThread( final String url, final File outputFile){

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
            toastOnMain("File downloaded : " + outputFile.getAbsolutePath());
        } catch(FileNotFoundException e) {
            toastOnMain("File could not be downloaded : "+e);
            return; // swallow a 404
        } catch (IOException e) {
            toastOnMain("File could not be downloaded: "+e);
            return; // swallow a 404
        }
    }


    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}