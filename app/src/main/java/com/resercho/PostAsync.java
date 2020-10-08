package com.resercho;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class PostAsync extends AsyncTask<String,String,String> {

    public static final String
            SIGN_IN_LINK = "https://resercho.com/linkapp/signin.php",
            SIGN_UP_LINK = "https://resercho.com/linkapp/signup.php";

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        Log.d("Res","PostAsync POSTIK onResponse:"+s);
    }

    @Override
    protected String doInBackground(String... strings) {

        if(strings[0].equalsIgnoreCase("signin")){
            String email = strings[1];
            String pwd = strings[2];

            try {
                String data = URLEncoder.encode("email", "UTF-8") + "=" + URLEncoder.encode(email, "UTF-8") + "&" +
                              URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(pwd, "UTF-8") ;


               return postReq(SIGN_IN_LINK, data);
            }catch (UnsupportedEncodingException e){

            }

        }else if(strings[0].equalsIgnoreCase("signup")){
            String email = strings[1];
            String uname = strings[2];
            String gender = strings[3];
            String fullname = strings[4];
            String mobile = strings[5];
            String date = strings[6];
            String pwd = strings[7];

            try {
                String data =   URLEncoder.encode("email", "UTF-8") + "=" + URLEncoder.encode(email, "UTF-8") + "&" +
                                URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(uname, "UTF-8") + "&"+
                                URLEncoder.encode("gender", "UTF-8") + "=" + URLEncoder.encode(gender, "UTF-8") + "&"+
                                URLEncoder.encode("full_name", "UTF-8") + "=" + URLEncoder.encode(fullname, "UTF-8") + "&"+
                                URLEncoder.encode("mobile", "UTF-8") + "=" + URLEncoder.encode(mobile, "UTF-8") + "&"+
                                URLEncoder.encode("dob", "UTF-8") + "=" + URLEncoder.encode(date, "UTF-8") + "&"+
                                URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(pwd, "UTF-8");

                return postReq(SIGN_UP_LINK,data);
            }catch (UnsupportedEncodingException e){
                Log.d("Res","PostAsync Exception :" + e);
            }

        }

        return null;
    }

    public String postReq(String link, String data) {
        try {
            URL url = new URL(link);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);

            OutputStream OS = httpURLConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(OS, "UTF-8"));

            Log.e("POSTIK PostAsync URL : ", data);
            bufferedWriter.write(data);
            bufferedWriter.flush();
            bufferedWriter.close();
            OS.close();

            InputStream IS = httpURLConnection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(IS, "UTF-8"));
            StringBuilder str = new StringBuilder();
            String line = "";

            while ((line = reader.readLine()) != null) {
                str.append(line).append("\n");
            }
            IS.close();

            return str.toString();

        } catch (MalformedURLException e) {
            Log.d("Res","PostAsync Exception : "+e);
            e.printStackTrace();
        } catch (IOException e) {
            Log.d("Res","PostAsync IO Exception  : "+e);
            e.printStackTrace();
        }

        return null;
    }
}
