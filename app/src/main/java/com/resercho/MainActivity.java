package com.resercho;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.resercho.Adapters.IntroAdapter;

public class MainActivity extends AppCompatActivity {

    ViewPager introVp;
    IntroAdapter adapter;
    TextView startBtn;

    View logoImg,logoText,logoSub;

    RelativeLayout splashScreen;
    private LinearLayout mDotLayout;
    private TextView[] mDots;
    private int nCurrentPage;

    boolean login=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("Res","LoginAsync check :"+DataHandler.getUserId(getApplicationContext()) + ">> "+DataHandler.getUserMail(getApplicationContext()));
        if(DataHandler.getUserId(getApplicationContext())!=null && DataHandler.getUserMail(getApplicationContext())!=null){
            startHomeActivity();
        }else {
//            GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
//            if (account != null) {
//                login = true;
//                Toast.makeText(getApplicationContext(), "Google catched", Toast.LENGTH_LONG).show();
//            }else{
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        hideSplash();
                    }
                },5000);
//            }
        }
    }


    protected void startHomeActivity(){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(MainActivity.this,HomeActivity.class);
                startActivity(intent);
            }
        },3000);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if(DataHandler.getUserId(getApplicationContext())!=null && DataHandler.getUserMail(getApplicationContext())!=null){
            Intent intent = new Intent(MainActivity.this,HomeActivity.class);
            startActivity(intent);
        }else {
            GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
            if (account != null) {
                Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        splashScreen.setVisibility(View.VISIBLE);

    }

    protected void hideSplash(){
        if(login){
            Intent intent = new Intent(MainActivity.this,HomeActivity.class);
            startActivity(intent);
        }else {
            if(DataHandler.isIntroShown(getApplicationContext())){
                Intent intent = new Intent(MainActivity.this,SignInActivity.class);
                startActivity(intent);
            }else {
                splashScreen.animate().setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        splashScreen.setVisibility(View.GONE);
                        introVp.setVisibility(View.VISIBLE);
                        startBtn.setVisibility(View.VISIBLE);
                        mDotLayout.setVisibility(View.VISIBLE);
                    }
                }).alpha(0).setDuration(300).start();
                DataHandler.setIntroShown(getApplicationContext());
            }
        }
    }


    private void init(){
        introVp = findViewById(R.id.introViewPager);
        splashScreen = findViewById(R.id.splash_screen);
        mDotLayout = findViewById(R.id.dotsLayout);
        startBtn = findViewById(R.id.startBtn);

        logoImg = findViewById(R.id.logoImg);
        logoText = findViewById(R.id.logoText);
        logoSub = findViewById(R.id.logoSub);

        adapter = new IntroAdapter(MainActivity.this);
        introVp.setAdapter(adapter);
        addDotsIndicator(0);

        // Interactions

        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),SignInActivity.class);
                startActivity(intent);
            }
        });
        introVp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                addDotsIndicator(position);
                nCurrentPage = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    public void addDotsIndicator(int position){
        mDots = new TextView[adapter.getCount()];
        mDotLayout.removeAllViews();

        for(int i=0; i<mDots.length; i++){
            mDots[i] = new TextView(this);
            mDots[i].setText(Html.fromHtml("&#8226;"));
            mDots[i].setTextSize(40);
            mDots[i].setPadding(5,0,5,0);
            mDots[i].setTextColor(getResources().getColor(R.color.grayBgLt));
            mDotLayout.addView(mDots[i]);
        }

        if(mDots.length > 0){
            mDots[position].setTextColor(getResources().getColor(R.color.colorAccent));
        }
    }

}
