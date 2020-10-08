package com.resercho;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class CommonMethods {

    public static Intent postData(String upLoadServerUri, String mCurrentPhotoPath) {
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1024 * 1024;

        Intent intent = new Intent();

        try{
            File sourceFile = new File(mCurrentPhotoPath);

            Log.v("GES", "textFile: " + mCurrentPhotoPath);
            String lineEnd = "\r\n";
            String twoHyphens = "--";
            String boundary = "*****";
            // the URL where the file will be posted
            String postReceiverUrl = upLoadServerUri;
            Log.v("Stock", "Stock postURL: " + postReceiverUrl);
            URL postServerUrl = new URL(postReceiverUrl);
            // new HttpClient
            HttpURLConnection conn = null;

            // post header1
            File file = new File(mCurrentPhotoPath);
            ////
            conn = (HttpURLConnection) postServerUrl.openConnection();
            DataOutputStream dos = null;
            conn.setDoInput(true); // Allow Inputs
            conn.setDoOutput(true); // Allow Outputs
            conn.setUseCaches(false); // Don't use a Cached Copy
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("ENCTYPE", "multipart/form-data");
            conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
            conn.setRequestProperty("uploaded_file", file.getName());
            conn.setRequestProperty("capture","");

            dos = new DataOutputStream(conn.getOutputStream());
            FileInputStream fileInputStream = new FileInputStream(sourceFile);
            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=uploaded_file;filename=\""+file.getName()+"\"" + lineEnd);
            dos.writeBytes(lineEnd);
            bytesAvailable = fileInputStream.available();

            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            buffer = new byte[bufferSize];

            // read file and write it into form...
            bytesRead = fileInputStream.read(buffer, 0, bufferSize);

            while (bytesRead > 0) {

                dos.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

            }

            // send multipart form data necesssary after file data...
            dos.writeBytes(lineEnd);
            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

            // Responses from the server (code and message)
            intent.putExtra("resp_code",conn.getResponseCode());
            intent.putExtra("resp_msg",conn.getResponseMessage());


            Log.d("Stock", "Stock File HTTP Response is : " + conn.getResponseCode() + ": " + conn.getResponseMessage() + " File : "+file.getName());

            //close the streams //
            mCurrentPhotoPath = file.getName();
            intent.putExtra("filepath",mCurrentPhotoPath);
            fileInputStream.close();
            dos.flush();
            dos.close();

        } catch (Exception e) {
            e.printStackTrace();
            Log.d("Stock","Stock Firing exception : "+e);
        }
        return intent;
    }

    public static void deleteFile(File file, Context context){
        try {
            file.delete();
            if (file.exists()) {
                file.getCanonicalFile().delete();
                if (file.exists()) {
                    context.deleteFile(file.getName());
                    Toast.makeText(context,"File deleted :"+file.getName(),Toast.LENGTH_LONG).show();
                }
            }
        }catch (IOException e){
            Toast.makeText(context,"File couldn't be deleted",Toast.LENGTH_LONG).show();
            Log.d("Rese","Delete file Exception :"+e);
        }
    }


    public static void pushToDatabaseTable(final String file){

        if(file!=null) {
            Thread thread = new Thread(new Runnable() {

                @Override
                public void run() {
                    try {
                    }catch (Exception e){

                    }
                }
            });
            thread.start();
        }
        else{
            ;
        }

    }

    public static int getIconIdFromMime(String mime){
        if(mime==null)
            return R.drawable.ic_file;

        if(mime.equalsIgnoreCase("pdf"))
            return R.drawable.ic_picture_as_pdf_black_24dp;
        else if(mime.equalsIgnoreCase("mp4"))
            return R.drawable.ic_ondemand_video_black_24dp;
        else if(mime.equalsIgnoreCase("mp3"))
            return R.drawable.ic_music_icon;
        else
            return R.drawable.ic_file;
    }

    public static String getDisplayTextFromMime(String mime){
        if(mime==null)
            return null;

        if(mime.equalsIgnoreCase("pdf"))
            return "PDF Document";
        else if(mime.equalsIgnoreCase("mp4"))
            return "Video";
        else if(mime.equalsIgnoreCase("mp3"))
            return "Audio";
        else
            return "File";
    }

}
