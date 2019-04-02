package com.projet3.polypaint.Gallery;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.projet3.polypaint.HomeActivity;
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
        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((HomeActivity)getActivity()).joinCollabEditingSession(v.getContentDescription().toString());
                ((HomeActivity)getActivity()).toggleGalleryVisibility();
            }
        });

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
        ImageView iv = (ImageView) rootView.findViewById(R.id.image);
        try {
            tv.setText(image.getString("author"));
            if (image.has("thumbnail")) {
                byte[] decoded = Base64.decode(image.getString("thumbnail"), Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(decoded, 0, decoded.length);

                if (bitmap != null) {
                    iv.setImageBitmap(bitmap);
                    iv.invalidate();
                }
            }

            rootView.setContentDescription(image.getString("id"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
