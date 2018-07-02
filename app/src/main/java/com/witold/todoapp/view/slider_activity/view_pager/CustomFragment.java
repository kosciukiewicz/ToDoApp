package com.witold.todoapp.view.slider_activity.view_pager;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.witold.todoapp.R;

public class CustomFragment extends Fragment {

    public static Fragment newInstance(Activity context, SliderElement sliderElement, float scale) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("sliderElement", sliderElement);
        bundle.putFloat("scale", scale);
        return Fragment.instantiate(context, CustomFragment.class.getName(), bundle);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (container == null) {
            return null;
        }

        SliderElement sliderElement = (SliderElement) getArguments().getSerializable("sliderElement");
        LinearLayout linearLayout = (LinearLayout)
                inflater.inflate(R.layout.slider_item, container, false);

        ImageView imageView =  linearLayout.findViewById(R.id.image_view_slider_item);
        Picasso.get().load(sliderElement.getResourceId()).into(imageView);
        imageView.setImageDrawable(ContextCompat.getDrawable(getContext(), sliderElement.getResourceId()));

        TextView textView =  linearLayout.findViewById(R.id.text_view_slider_item);
        textView.setText(sliderElement.getDescription());

        CustomLinearLayout root =  linearLayout.findViewById(R.id.item_root);
        float scale = this.getArguments().getFloat("scale");
        root.setScaleBoth(scale);

        return linearLayout;
    }
}
