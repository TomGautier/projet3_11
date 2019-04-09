package com.projet3.polypaint.Others;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.projet3.polypaint.R;

import java.util.ArrayList;

public class TutorialFragment extends Fragment {
    public View rootView;

    private ImageView iView;
    private Button buttonNext;
    private Button buttonPrev;

    private ArrayList<Bitmap> images;
    private int imageCounter = 0;

    public TutorialFragment() { super(); }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_tutorial, container, false);

        initializeViews();
        images = new ArrayList<>();
        //images.add(BitmapFactory.decodeResource(getResources(), R.drawable.toolbar));
        images.add(BitmapFactory.decodeResource(getResources(), R.drawable.chat));
        images.add(BitmapFactory.decodeResource(getResources(), R.drawable.user_list));
        images.add(BitmapFactory.decodeResource(getResources(), R.drawable.gallery));
        images.add(BitmapFactory.decodeResource(getResources(), R.drawable.accessibility));
        images.add(BitmapFactory.decodeResource(getResources(), R.drawable.editing1));
        images.add(BitmapFactory.decodeResource(getResources(), R.drawable.editing2));

        iView.setImageBitmap(images.get(imageCounter));
        iView.invalidate();

        return rootView;
    }

    private void initializeViews() {
        iView = (ImageView) rootView.findViewById(R.id.imageView);
        buttonNext = (Button) rootView.findViewById(R.id.buttonNext);
        buttonPrev = (Button) rootView.findViewById(R.id.buttonBack);

        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextImage();
            }
        });
        buttonPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prevImage();
            }
        });
    }

    private void nextImage() {
        if (imageCounter < images.size() - 1) {
            imageCounter++;
            iView.setImageBitmap(images.get(imageCounter));
            iView.invalidate();

            buttonPrev.setVisibility(View.VISIBLE);

            if (imageCounter == images.size() - 1) buttonNext.setVisibility(View.INVISIBLE);
        }
    }
    private void prevImage() {
        if (imageCounter > 0) {
            imageCounter--;
            iView.setImageBitmap(images.get(imageCounter));
            iView.invalidate();

            buttonNext.setVisibility(View.VISIBLE);

            if (imageCounter == 0) buttonPrev.setVisibility(View.INVISIBLE);
        }
    }

}
