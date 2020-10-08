package com.resercho;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class UpdateActivity extends AppCompatActivity {

    public static final String FULLNAME = "fullname";
    public static final String USERNAME = "username";
    public static final String BIO = "bio";
    public static final String MOBILE = "mobile";
    public static final String DOB = "dob";
    public static final String EDUCATION1 = "education1";
    public static final String EDUCATION2 = "education2";
    public static final String EDUCATION3 = "education3";
    public static final String PROFILE = "profile";
    public static final String WEBSITE = "website";
    public static final String GENDER = "gender";

    String key,val;
    TextView keytv;
    EditText valtv;
    Button update;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        try{
            getSupportActionBar().setTitle("Select Post Type");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }catch (Exception e){}

        try {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setTitle("Update");

        }catch (Exception e){}

        keytv = findViewById(R.id.keyTv);
        valtv = findViewById(R.id.valTv);
        update = findViewById(R.id.updateBtn);

        key = getIntent().getStringExtra("key");
        val = getIntent().getStringExtra("val");
        valtv.setText(val);

        keytv.setText("Update your "+getFormmatted(key));
        if(key==null)
            finish();

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkValidity()){
                    sendUpdateReq(key, valtv.getText().toString());
                }else{
                    Toast.makeText(getApplicationContext(),"Please enter something in the field",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    protected void showPbar(final boolean show){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(show) {
                    findViewById(R.id.upbar).setVisibility(View.VISIBLE);
                    update.setEnabled(false);
                }
                else {
                    findViewById(R.id.upbar).setVisibility(View.GONE);
                    update.setEnabled(true);
                }
            }
        });
    }

    protected String getFormmatted(String key){
        return key.substring(0,1).toUpperCase() + key.substring(1);
    }

    protected void sendUpdateReq(final String key,final String val){
        showPbar(true);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                NetworkHandler.updateDbField(getApplicationContext(), key, val, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        updateUI(false,"Failed updating the "+key);
                        showPbar(false);
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String resp = response.body().string();
                        try{
                            JSONObject object = new JSONObject(resp);

                            if(object.getInt("success")==1){
                                updateUI(true,"Profile updated!");
                            }else{
                                updateUI(false,"Failed updating the "+key);
                            }

                        }catch (JSONException e){
                            updateUI(false,"Failed updating the "+key);
                        }
                        showPbar(false);
                    }
                });
            }
        });

        thread.start();
    }

    protected void updateUI(final boolean good, final String msg){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }

    protected boolean checkValidity(){
        if(valtv!=null&&valtv.getText()!=null&&valtv.getText().toString().length()>0)
            return true;
        else
            return false;
    }

}
