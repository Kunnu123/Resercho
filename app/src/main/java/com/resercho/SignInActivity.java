package com.resercho;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.SpringAnimation;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.resercho.POJO.ModelProfile;

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
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static com.resercho.NetworkHandler.root_dir;

public class SignInActivity extends AppCompatActivity {

    GoogleSignInClient mGoogleSignInClient;
    SignInButton signInButton;
    private static final int RC_SIGN_IN = 1030;

    public static final String TAG = "Resercho";

    public static final String SIGN_IN_LINK = "https://resercho.com/linkapp/signin.php";


    EditText lgemail, lgpass;

    View pbar;

    ///////////////


    String[] genderLabels = { "Female","Male","Other"};
    Spinner genderSpinner;
    String selGender=genderLabels[0];

    String SIGN_UP_LINK = "https://resercho.com/linkapp/signup.php";
    ///
    EditText email, uname, mobile, dob, password,fullname;
    View signBtn;

    String selDate;
    DatePickerDialog.OnDateSetListener date;
    Calendar myCalendar;



    protected void init(){
        genderSpinner = findViewById(R.id.genderSpinner);
        spinnerSetup();

        //
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
                if(checkValidity2()){
                    sendSignup();
                }else{
                    Toast.makeText(getApplicationContext(),"Check all the details",Toast.LENGTH_LONG).show();
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
                    new DatePickerDialog(SignInActivity.this, date, myCalendar
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

    protected boolean checkValidity2(){
        boolean allOk = true;


        if(email==null||email.getText()==null||!email.getText().toString().contains("@")||email.getText().toString().length()<5){
            allOk = false;
        }

        if(uname==null||uname.getText()==null||!(uname.getText().toString().length()>4)||uname.getText().toString().contains(" ")){
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
    protected void spinnerSetup(){

        ArrayAdapter aa = new ArrayAdapter(SignInActivity.this,android.R.layout.simple_spinner_item,genderLabels);
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



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin_last);

        findViewById(R.id.linearBody).setTranslationX(800);
        findViewById(R.id.signupform).setTranslationX(800);
        googleAuth();
        init();

        findViewById(R.id.signupBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignInActivity.this,SignupActivity.class);
                startActivity(intent);
            }
        });

        ///

        pbar = findViewById(R.id.lgPbar);
        lgemail = findViewById(R.id.lgEmail);
        lgpass = findViewById(R.id.lgPwd);

        findViewById(R.id.loginBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkValidity()){
                    login(lgemail.getText().toString(),lgpass.getText().toString());
                }
            }
        });
        findViewById(R.id.forgoetPwd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignInActivity.this,ForgotPasswordActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.terms).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "https://resercho.com/login/tnc.php";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });

        findViewById(R.id.terms2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "https://resercho.com/login/tnc.php";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });

    }

    protected boolean checkValidity(){
        boolean allok = true;

        String msg = "Please enter ";
//        if(lgemail==null||lgemail.getText()==null||!lgemail.getText().toString().contains("@")) {
//            allok = false;
//            msg += " valid email address,";
//        }

        if(lgpass==null||lgpass.getText()==null||lgpass.getText().toString().length()<4) {
            allok = false;
            msg += " password with length greater than 4,";
        }

        if(!allok)
            Toast.makeText(getApplicationContext(),msg.substring(0,msg.length()-2),Toast.LENGTH_LONG).show();
        return allok;
    }

    protected void login(String email, String password){
        new LoginAsync().execute(email,password);
    }

    private void signIn() {
        try {
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        }catch (Exception e){
            Toast.makeText(getApplicationContext(),"Exception:"+e,Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);



        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.

                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                handleSignInResult(task);

        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        Log.d(TAG,"Google Sik Handle Signin");
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.
            updateUI(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.d(TAG,"Google Sik Exception:"+e);
            Log.w(TAG, "Google Sik signInResult:failed code=" + e.getStatusCode());
            updateUI(null);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

        updateUI(account);


        findViewById(R.id.signupBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final SpringAnimation springAnim = new SpringAnimation(findViewById(R.id.linearBody), DynamicAnimation.TRANSLATION_X, 800);
                springAnim.setStartValue(0);
                springAnim.start();
                springAnim.addEndListener(new DynamicAnimation.OnAnimationEndListener() {
                    @Override
                    public void onAnimationEnd(DynamicAnimation animation, boolean canceled, float value, float velocity) {
                        findViewById(R.id.linearBody).setVisibility(View.GONE);
                        animateSignup();
                    }
                });

            }
        });

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                final SpringAnimation springAnim1 = new SpringAnimation(findViewById(R.id.hii), DynamicAnimation.TRANSLATION_X, 0);
                springAnim1.setStartValue(40);
                springAnim1.start();
                springAnim1.addEndListener(new DynamicAnimation.OnAnimationEndListener() {
                    @Override
                    public void onAnimationEnd(DynamicAnimation animation, boolean canceled, float value, float velocity) {
                        final SpringAnimation springAnim1 = new SpringAnimation(findViewById(R.id.hii), DynamicAnimation.TRANSLATION_X, 0);
                        springAnim1.setStartValue(40);
                        springAnim1.start();
                    }
                });

            }
        },800);

        findViewById(R.id.loginSwitch).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLogin();
            }
        });
    }



    protected void showLogin(){
        final SpringAnimation springAnim = new SpringAnimation(findViewById(R.id.signupform), DynamicAnimation.TRANSLATION_X, 800);
        springAnim.setStartValue(0);
        springAnim.start();
        springAnim.addEndListener(new DynamicAnimation.OnAnimationEndListener() {
            @Override
            public void onAnimationEnd(DynamicAnimation animation, boolean canceled, float value, float velocity) {
                findViewById(R.id.signupform).setVisibility(View.GONE);
                findViewById(R.id.loginSwitch).setVisibility(View.GONE);
                findViewById(R.id.signupform).setVisibility(View.GONE);
                animateLogin();
            }
        });
    }

    protected void updateUI(GoogleSignInAccount account){
        if(account!=null&& !account.isExpired()){
            syncGSign(account);
            DataHandler.setUserMail(account.getEmail()+"",SignInActivity.this);
            DataHandler.setDisplayName(account.getDisplayName(),SignInActivity.this);
            try {
                DataHandler.setProfilePic(account.getPhotoUrl().toString(), SignInActivity.this);
            }catch (NullPointerException e){

            }
            Log.d("Res","Goik prof: "+account.getPhotoUrl());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();


    }

    protected void hideSignup(boolean hide){
        if(!hide) {
            findViewById(R.id.signupBtn).setVisibility(View.VISIBLE);
            final SpringAnimation springAnim = new SpringAnimation(findViewById(R.id.signupBtn), DynamicAnimation.TRANSLATION_X, 0);
            springAnim.setStartValue(800);
            springAnim.start();
        }else{
            final SpringAnimation springAnim = new SpringAnimation(findViewById(R.id.signupBtn), DynamicAnimation.TRANSLATION_X, 800);
            springAnim.setStartValue(0);
            springAnim.start();
            springAnim.addEndListener(new DynamicAnimation.OnAnimationEndListener() {
                @Override
                public void onAnimationEnd(DynamicAnimation animation, boolean canceled, float value, float velocity) {
                    findViewById(R.id.signupBtn).setVisibility(View.GONE);
                }
            });
        }
    }

    protected void hideGsign(boolean hide){
        if(!hide) {
            findViewById(R.id.gsigninBtn).setVisibility(View.VISIBLE);
            final SpringAnimation springAnim = new SpringAnimation(findViewById(R.id.gsigninBtn), DynamicAnimation.TRANSLATION_X, 0);
            springAnim.setStartValue(800);
            springAnim.start();
        }else{
            final SpringAnimation springAnim = new SpringAnimation(findViewById(R.id.gsigninBtn), DynamicAnimation.TRANSLATION_X, 800);
            springAnim.setStartValue(0);
            springAnim.start();
            springAnim.addEndListener(new DynamicAnimation.OnAnimationEndListener() {
                @Override
                public void onAnimationEnd(DynamicAnimation animation, boolean canceled, float value, float velocity) {
                    findViewById(R.id.gsigninBtn).setVisibility(View.GONE);
                }
            });
        }
    }

    protected void hideLogin(boolean hide){
        if(!hide) {
            findViewById(R.id.loginSwitch).setVisibility(View.VISIBLE);
            final SpringAnimation springAnim = new SpringAnimation(findViewById(R.id.loginSwitch), DynamicAnimation.TRANSLATION_X, 0);
            springAnim.setStartValue(800);
            springAnim.start();
        }else{
            final SpringAnimation springAnim = new SpringAnimation(findViewById(R.id.loginSwitch), DynamicAnimation.TRANSLATION_X, 800);
            springAnim.setStartValue(0);
            springAnim.start();
            springAnim.addEndListener(new DynamicAnimation.OnAnimationEndListener() {
                @Override
                public void onAnimationEnd(DynamicAnimation animation, boolean canceled, float value, float velocity) {
                    findViewById(R.id.loginSwitch).setVisibility(View.GONE);
                }
            });
        }
    }


    protected void animateLogin(){

        findViewById(R.id.closeLogin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final SpringAnimation springAnim = new SpringAnimation(findViewById(R.id.linearBody), DynamicAnimation.TRANSLATION_X, 800);
                springAnim.setStartValue(0);
                springAnim.start();
                springAnim.addEndListener(new DynamicAnimation.OnAnimationEndListener() {
                    @Override
                    public void onAnimationEnd(DynamicAnimation animation, boolean canceled, float value, float velocity) {
                        findViewById(R.id.linearBody).setVisibility(View.GONE);
                        hideLogin(false);
                        hideGsign(false);
                        hideSignup(false);
                    }
                });
            }
        });

        findViewById(R.id.linearBody).setVisibility(View.VISIBLE);

        final SpringAnimation springAnim = new SpringAnimation(findViewById(R.id.linearBody), DynamicAnimation.TRANSLATION_X, 0);
        springAnim.setStartValue(800);
        springAnim.start();

        hideGsign(false);
        hideSignup(false);

    }
    protected void animateSignup(){
        findViewById(R.id.closeSignup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final SpringAnimation springAnim = new SpringAnimation(findViewById(R.id.signupform), DynamicAnimation.TRANSLATION_X, 800);
                springAnim.setStartValue(0);
                springAnim.start();
                springAnim.addEndListener(new DynamicAnimation.OnAnimationEndListener() {
                    @Override
                    public void onAnimationEnd(DynamicAnimation animation, boolean canceled, float value, float velocity) {
                        findViewById(R.id.signupform).setVisibility(View.GONE);
                        hideLogin(false);
                        hideGsign(false);
                        hideSignup(false);
                    }
                });
            }
        });
        hideGsign(true);
        hideSignup(true);
        hideLogin(true);

                findViewById(R.id.signupform).setVisibility(View.VISIBLE);
                final SpringAnimation springAnim = new SpringAnimation(findViewById(R.id.signupform), DynamicAnimation.TRANSLATION_X, 0);
                springAnim.setStartValue(800);
                springAnim.start();

    }

    protected void syncGSign(final GoogleSignInAccount account){
        Log.d("Resercho","Gsignin syncG:called");

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                NetworkHandler.syncGoogleSignInWithServer(account.getDisplayName(), account.getIdToken(), account.getEmail(), new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.d("Resercho","Gsignin sik syncG:onFailure:"+e);
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String resp = response.body().string();
                        Log.d("Resercho","Gsignin sik syncG:onResponse:"+resp);
                        try {
                            JSONObject jsonObject = new JSONObject(resp);
                            if(jsonObject.getInt("success")==1){
                                if(jsonObject.getInt("profile")==0){
                                    toastOnMain("All set");
                                    if(jsonObject.getLong("uid")>0){
                                        DataHandler.setUid(jsonObject.getString("uid"),getApplicationContext());
                                        signInSuccess();
                                    }else{
                                        toastOnMain("Uid is less than 1:"+jsonObject.getInt("uid"));
                                    }
                                }else{
                                    toastOnMain("Profile is not set");
                                }
                            }else{
                                if(jsonObject.getString("reason").equalsIgnoreCase("exists")){
                                    DataHandler.setUid(jsonObject.getString("uid"),getApplicationContext());
                                    signInSuccess();
                                }else{
                                    toastOnMain("There seems to be an error with your login credentials, Please try again later");
                                }
                            }
                        }catch (JSONException e){
                            Log.d("Resercho","Gsignin sik syncG:onResponse:"+resp);
                        }
                    }
                });
            }
        });

        thread.start();
    }

    protected void toastOnMain(final String msg){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_LONG).show();
            }
        });
    }

    protected void signInSuccess(){
        Intent i = new Intent(SignInActivity.this,HomeActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }

    protected void googleAuth(){
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestProfile()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        findViewById(R.id.gsigninBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
    }

    private void signOut() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // ...
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


    protected void setLoginSession(JSONObject jsonObject){
        try {
            DataHandler.setUid(jsonObject.getString("id"),getApplicationContext());
            DataHandler.setUsernameName(jsonObject.getString("username"),getApplicationContext());
            DataHandler.setDisplayName(jsonObject.getString("fullname"),getApplicationContext());
            DataHandler.setUserMail(jsonObject.getString("email"),getApplicationContext());
            DataHandler.setProfilePic(root_dir + jsonObject.getString("prof"),getApplicationContext());

            ModelProfile profile1 = new ModelProfile();

            String about = jsonObject.getString("about");
            String website = jsonObject.getString("website");
            String education = jsonObject.getString("education");
            String since = jsonObject.getString("since");
            String fname = jsonObject.getString("fullname");
            String profurl = root_dir + jsonObject.getString("prof");

            profile1.setBio(about);
            profile1.setWebsite(website);
            profile1.setEducation(education);
            profile1.setSince(since);
            profile1.setUname(fname);
            profile1.setProfUrl(profurl);

            DataHandler.setUserProfile(profile1,getApplicationContext());

            Intent intent = new Intent(SignInActivity.this,HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }catch (JSONException e){
            Log.d("Res","LoginAsync : JSON Exception :"+e);
        }
    }


    public class LoginAsync extends AsyncTask<String,String,String> {
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            Log.d("Res","LoginAsync : "+s);
            try{
                JSONObject object = new JSONObject(s);
                if(object.getInt("success")==1){
                    // Successfully Registered!
                    toastOnMain("Login Successful");
                    JSONObject profile = object.getJSONObject("profile");
                    setLoginSession(profile);
                }else if(object.getInt("success") == 2){
                    toastOnMain("No account found linked to this email. Sign Up!");
                }else if(object.getInt("success")==7){
                    toastOnMain("Enter correct credentials");
                }
            }catch (JSONException e){
                Log.d("Res","LoginAsync onError : JSON Exception: "+e);
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

            String email = strings[0];
            String pwd = strings[1];

            try {
                String data =   URLEncoder.encode("email", "UTF-8") + "=" + URLEncoder.encode(email, "UTF-8") + "&" +
                        URLEncoder.encode("pwd", "UTF-8") + "=" + URLEncoder.encode(pwd, "UTF-8");

                return postReq(SIGN_IN_LINK,data);
            }catch (UnsupportedEncodingException e){
                Log.d("Res","LoginAsync Exception :" + e);
                toastOnMain("Something went wrong");
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

            Log.e("POSTIK LoginAsync : ", data);
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
            Log.d("Res","LoginAsync Exception : "+e);
            e.printStackTrace();
        } catch (IOException e) {
            Log.d("Res","LoginAsync IO Exception  : "+e);
            e.printStackTrace();
        }

        return null;
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
                    showLogin();
                }else{
                    if(object.has("reason")&&object.getString("reason").equalsIgnoreCase("email_exists")){
                        toastOnMain("There's already an account with this email address");
                    }else if(object.has("reason")&&object.getString("reason").equalsIgnoreCase("username_exists")) {
                        toastOnMain("Username exists, try another one");
                    }else{
                        toastOnMain("Failed to register. Try again later!");
                    }
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

                return postReq2(SIGN_UP_LINK,data);
            }catch (UnsupportedEncodingException e){
                Log.d("Res","PostAsync Exception :" + e);
            }

            return null;
        }
    }

    public String postReq2(String link, String data) {
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
