package com.projet3.polypaint.Gallery;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.projet3.polypaint.R;

import java.util.ArrayList;
import java.util.Stack;

public class GalleryFragment extends Fragment {

    private View rootView;

    public GalleryFragment() {}
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView=inflater.inflate(R.layout.activity_image_editing, container, false);

        initializeButtons();

        //setTouchListener();
        return rootView;
    }

    private void initializeButtons() {

    }
}
