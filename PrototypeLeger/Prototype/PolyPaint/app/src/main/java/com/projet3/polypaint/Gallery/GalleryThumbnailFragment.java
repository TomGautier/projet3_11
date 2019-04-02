package com.projet3.polypaint.Gallery;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.projet3.polypaint.R;

import org.json.JSONException;
import org.json.JSONObject;

public class GalleryThumbnailFragment extends Fragment {
    private View rootView = null;
    private JSONObject image = null;

    public GalleryThumbnailFragment() {}
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView=inflater.inflate(R.layout.gallery_thumbnail, container, false);

        if (image != null) updateView();

        return rootView;
    }

    @Override
    public void setArguments(Bundle b){

    }

    public void setImageInfo(JSONObject image) {
        this.image = image;

        if (rootView != null) updateView();
    }

    private void updateView() {
        TextView tv = (TextView) rootView.findViewById(R.id.title);
        try {
            tv.setText(image.getString("author"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
