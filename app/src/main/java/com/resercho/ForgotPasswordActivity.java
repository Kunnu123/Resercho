package com.resercho;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

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

public class ForgotPasswordActivity extends AppCompatActivity {

    public static final String url = "https://resercho.com/smtpmail/ur_username.php";
    public static final String URL_FORGOT ="https://resercho.com/linkapp/sendForgetRequest.php";

    View pbar;
    Button sendmail;
    EditText username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        pbar = findViewById(R.id.sgPbar);
        username = findViewById(R.id.username);
        sendmail = findViewById(R.id.sendMail);

        sendmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(username!=null&& username.getText()!=null&&username.getText().toString().length()>0){
                    postForget();
                }
            }
        });

    }

    protected void showPbar(final boolean show){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(show)
                    pbar.setVisibility(View.VISIBLE);
                else
                    pbar.setVisibility(View.GONE);
            }
        });
    }

    public void postForget(){
        new SignAsync().execute(username.getText().toString());
    }

    public class SignAsync extends AsyncTask<String,String,String> {
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try{
                JSONObject object = new JSONObject(s);
                if(object.getInt("success")==1){
                    // Successfully Registered!
                    toastOnMain("Check your inbox, Recovery mail sent!");
                    finish();
                }else{
                    toastOnMain("Failed to send recovery mail!");
                }
            }catch (JSONException e){
                toastOnMain("Failed to send recovery mail!");
            }

            showPbar(false);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showPbar(true);
        }

        @Override
        protected String doInBackground(String... strings) {

            try {
                String data =   URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(strings[0], "UTF-8");

                return postReq(URL_FORGOT,data);
            }catch (UnsupportedEncodingException e){
                Log.d("Res","PostAsync Exception :" + e);
            }

            return null;
        }
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
            IS.close    ();

            Log.d("Res","PostAsync Forgot Response : "+str.toString());
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

    protected void toastOnMain(String msg){
        Toast t = Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_LONG);
        t.setGravity(Gravity.CENTER,0,0);
        t.show();
    }

}
