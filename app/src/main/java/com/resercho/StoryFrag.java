package com.resercho;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.resercho.POJO.ModelStory;


public class StoryFrag extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    ModelStory modelStory;

    private String profile;
    private String name;

    Context context;


    ///

    View nextStory, prevStory;
    ImageView storyImage, profImage;

    TextView uname, timestamp;


    int prog = 0, storyPosition=0;

    View storyBar,pbar,mainlay;
    boolean imageLoaded = false;

    protected void init(View view){
        storyBar = view.findViewById(R.id.storyBar);
        nextStory= view.findViewById(R.id.nextStory);
        prevStory = view.findViewById(R.id.prevStory);
        storyImage = view.findViewById(R.id.storyImage);
        uname = view.findViewById(R.id.storyUname);
        timestamp = view.findViewById(R.id.storyTime);
        profImage = view.findViewById(R.id.profImage);
        pbar = view.findViewById(R.id.pbar);
        mainlay = view.findViewById(R.id.storyLay);
        imageLoaded = false;
    }


    CountDownTimer cTimer = null;

    //start timer function
    void startTimer() {
        cTimer = new CountDownTimer(8000, 10) {
            public void onTick(long millisUntilFinished) {
                setBarProg();
            }
            public void onFinish() {
                nextStory();
            }
        };
        cTimer.start();
    }


    //cancel timer
    void cancelTimer() {
        if(cTimer!=null)
            cTimer.cancel();
    }

    protected void setBarProg(){
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.MATCH_PARENT,
                prog
        );

        storyBar.setLayoutParams(param);
        Log.d("Res","Pxts:"+prog);
        prog +=1;
    }


    public StoryFrag() {
        // Required empty public constructor
    }
    public static StoryFrag newInstance() {
        StoryFrag fragment = new StoryFrag();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            modelStory = new ModelStory();
            modelStory.setTime(getArguments().getString("time"));
            modelStory.setImage(getArguments().getString("image"));
            modelStory.setId(getArguments().getString("sid"));
            modelStory.setProfile(getArguments().getString("profile"));
            modelStory.setFullname(getArguments().getString("name"));
            storyPosition = getArguments().getInt("pos");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_story, container, false);
        init(view);

        if(modelStory!=null){
            Log.d("Lins","Lins Story :" + modelStory.getFullname() + " <<" +modelStory.getId());
            uname.setText(modelStory.getFullname());
            Glide.with(context).load(modelStory.getProfile()).into(profImage);
            String time = (Long.parseLong(UtilityMethods.getServerTimeStamp(modelStory.getTime()))/1000)+"";
            Log.d("Ress","Strex story time:"+ modelStory.getTime());
            Log.d("Ress","Strex story time1:"+ time);
            Log.d("Ress","Strex story time2:"+ UtilityMethods.getAgoTimeFromTimestamp(time));
            timestamp.setText(UtilityMethods.getAgoTimeFromTimestamp(time));

            Glide.with(context)
                    .load(modelStory.getImage())
                    .asBitmap()
                    .placeholder(R.drawable.downloading)
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                            storyImage.setImageBitmap(resource);
                            storyImage.clearColorFilter();
                            storyImage.setAlpha(1.0f);
                            pbar.setVisibility(View.GONE);
                            imageLoaded = true;
                            if(((StoryHolder)context).shouldStartTimer(storyPosition)){
                                startTimer();
                            }
                        }
                    });

            nextStory.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    nextStory();
                }
            });

            prevStory.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    prevStory();
                }
            });

            View.OnLongClickListener listener = new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    cancelTimer();
                    return true;
                }
            };
            mainlay.setOnLongClickListener(listener);
            nextStory.setOnLongClickListener(listener);
            prevStory.setOnLongClickListener(listener);
        }
        return view;
    }

    protected void startTimerForReal(){
        startTimer();
    }


    protected void nextStory(){
        cancelTimer();
        ((StoryHolder)context).nextStory(storyPosition+1);
    }

    protected void prevStory(){
        cancelTimer();
        ((StoryHolder)context).prevStory(storyPosition);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

}
