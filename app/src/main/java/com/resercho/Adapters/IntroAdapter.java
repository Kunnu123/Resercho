package com.resercho.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.viewpager.widget.PagerAdapter;

import com.resercho.R;

import java.util.ArrayList;
import java.util.List;

public class IntroAdapter extends PagerAdapter {

    Context context;
    LayoutInflater layoutInflater;
    List<String> headings;
    List<String> subtitles;

    public IntroAdapter(Context context) {
        this.context = context;
        headings = new ArrayList<>();
        subtitles = new ArrayList<>();

        // Headings
        headings.add(context.getResources().getString(R.string.showcaswork));
        headings.add(context.getResources().getString(R.string.studymaterials));
        headings.add(context.getResources().getString(R.string.collaborate));
        headings.add(context.getResources().getString(R.string.events));
        headings.add(context.getResources().getString(R.string.groups));
        headings.add(context.getResources().getString(R.string.upwork));

        subtitles.add(context.getResources().getString(R.string.showcaseworksub));
        subtitles.add(context.getResources().getString(R.string.studymaterialsub));
        subtitles.add(context.getResources().getString(R.string.collabsub));
        subtitles.add(context.getResources().getString(R.string.eventsub));
        subtitles.add(context.getResources().getString(R.string.groupsub));
        subtitles.add(context.getResources().getString(R.string.upworksub));
    }

    public int[] images = {
            R.drawable.showcasework,
            R.drawable.studymaterial,
            R.drawable.collaborate,
            R.drawable.events,
            R.drawable.groups,
            R.drawable.uploadwork,
    };


    @Override
    public int getCount() {
        return headings.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object o) {
        return view == (RelativeLayout) o;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.item_intro, container, false);

        ImageView image = view.findViewById(R.id.imageIntro);
        TextView slideHeading = view.findViewById(R.id.slide_heading);
        TextView slideDescription = view.findViewById(R.id.slide_desc);

        slideHeading.setText(headings.get(position));
        image.setImageDrawable(context.getResources().getDrawable(images[position]));
        slideDescription.setText(subtitles.get(position));

        container.addView(view);

        return view;
    }


    @Override
    public void destroyItem (ViewGroup container, int position, Object object) {
        container.removeView((RelativeLayout)object);
    }
}