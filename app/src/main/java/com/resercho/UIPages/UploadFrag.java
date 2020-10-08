package com.resercho.UIPages;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import com.resercho.NewPostActivity;
import com.resercho.R;


public class UploadFrag extends Fragment {


    Context context;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    public UploadFrag() {
        // Required empty public constructor
    }
    public static UploadFrag newInstance(String param1) {
        UploadFrag fragment = new UploadFrag();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_upload, container, false);
        
        view.findViewById(R.id.academicBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, NewPostActivity.class);
                intent.putExtra("type","research");
                startActivity(intent);
            }
        });

        view.findViewById(R.id.socialFeed).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, NewPostActivity.class);
                intent.putExtra("type","post");
                startActivity(intent);
            }
        });

        ((NestedScrollView)view.findViewById(R.id.nestedScrollView)).setNestedScrollingEnabled(false);

        return view;
    }


}
