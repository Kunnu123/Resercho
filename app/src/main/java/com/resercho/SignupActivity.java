package com.resercho;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class SignupActivity extends AppCompatActivity {

    String[] genderLabels = { "Female","Male","Other"};
    Spinner genderSpinner;
    String selGender=genderLabels[0];

    String SIGN_UP_LINK = "https://resercho.com/linkapp/signup.php";
    ///
    EditText email, uname, mobile, dob, password,fullname;
    View signBtn, pbar;

    String selDate;
    DatePickerDialog.OnDateSetListener date;
    Calendar myCalendar;

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
    protected void init(){
        genderSpinner = findViewById(R.id.genderSpinner);
        spinnerSetup();

        //
        pbar = findViewById(R.id.sgPbar);
        email = findViewById(R.id.sgEmail);
        uname = findViewById(R.id.sgUname);
        mobile = findViewById(R.id.sgMobile);
        dob = findViewById(R.id.sgDob);
        password = findViewById(R.id.sgPassword);
        fullname = findViewById(R.id.sgFullname);
        signBtn = findViewById(R.id.signUpBtn);

        signBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkValidity()){
                    sendSignup();
                }else{
                    Toast.makeText(getApplicationContext(),"Enter all details",Toast.LENGTH_LONG).show();
                }
            }
        });

        setupDatePicker();
    }

    protected void setupDatePicker(){
        myCalendar = Calendar.getInstance();

        date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

        };



        dob.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    new DatePickerDialog(SignupActivity.this, date, myCalendar
                            .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                            myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                }
            }
        });

    }

    private void updateLabel() {
        String myFormat = "MM/dd/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.ENGLISH);
        selDate = sdf.format(myCalendar.getTime());
        dob.setText(sdf.format(myCalendar.getTime()));
    }


    public void sendSignup(){
        String username = uname.getText().toString();
        String emails = email.getText().toString();
        String mob = mobile.getText().toString();
        String gender = selGender;
        String pass = password.getText().toString();
        String dab = dob.getText().toString();
        String fname = fullname.getText().toString();

        new SignAsync().execute("signup",emails,username,gender,fname,mob,dab,pass);
    }

    protected boolean checkValidity(){
        boolean allOk = true;


        if(email==null||email.getText()==null||!email.getText().toString().contains("@")||email.getText().toString().length()<5){
            allOk = false;
        }

        if(uname==null||uname.getText()==null||!(uname.getText().toString().length()>4)){
            allOk = false;
        }

        if(fullname==null||fullname.getText()==null||!(fullname.getText().toString().length()>4)){
            allOk = false;
        }

        if(mobile==null||mobile.getText()==null||mobile.getText().toString().length()!=10){
            allOk = false;
        }

        if(password==null||password.getText()==null||password.getText().toString().length()<6){
            allOk = false;
        }

        if(selDate==null)
            allOk = false;


        return allOk;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        init();
    }

    protected void spinnerSetup(){

        ArrayAdapter aa = new ArrayAdapter(SignupActivity.this,android.R.layout.simple_spinner_item,genderLabels);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner = findViewById(R.id.genderSpinner);
        genderSpinner.setAdapter(aa);
        genderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selGender = genderLabels[i];
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Log.d("Stock","Dynamic Options : Spinner countrySpinner Selected : NothingSelected");
            }
        });
    }

    protected void toastOnMain(String msg){
        Toast t = Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_LONG);
        t.setGravity(Gravity.CENTER,0,0);
        t.show();
    }

    public class SignAsync extends AsyncTask<String,String,String>{
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try{
                JSONObject object = new JSONObject(s);
                if(object.getInt("success")==1){
                    // Successfully Registered!
                    toastOnMain("Registered Successfully\nLogin Now!");
                    finish();
                }else{
                    toastOnMain("Failed to register. Try again later!");
                }
            }catch (JSONException e){
                toastOnMain("Failed to register. Try again later!");
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
Log.e("SignUP","-->" + data);
                return postReq(SIGN_UP_LINK,data);
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
