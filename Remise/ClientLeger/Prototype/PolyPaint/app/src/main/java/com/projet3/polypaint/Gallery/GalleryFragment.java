package com.projet3.polypaint.Gallery;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TableLayout;
import android.widget.TableRow;

import com.projet3.polypaint.DrawingSession.ImageEditingFragment;
import com.projet3.polypaint.HomeActivity;
import com.projet3.polypaint.R;
import com.projet3.polypaint.Network.RequestManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class GalleryFragment extends Fragment {

    private final int MAX_ROW_LENGTH = 9;

    private View rootView;
    private Switch privacySwitch;
    private TableLayout table;

    private ArrayList<GalleryThumbnailFragment> thumbnails;

    private boolean isPrivateImages = false;

    public GalleryFragment() {}
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView=inflater.inflate(R.layout.fragment_gallery, container, false);
        table = (TableLayout) rootView.findViewById(R.id.table);

        thumbnails = new ArrayList<>();

        setPrivacySwitch();

        populateGrid();

        return rootView;
    }

    private void setPrivacySwitch() {
        privacySwitch = (Switch) rootView.findViewById(R.id.privacySwitch);
        isPrivateImages = privacySwitch.isChecked();

        privacySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isPrivateImages = isChecked;
                populateGrid();
            }
        });
    }

    public void refresh() { populateGrid(); }

    private void populateGrid() {
        ArrayList<JSONObject> images;

        if (isPrivateImages)
            images = RequestManager.currentInstance.fetchPrivateGallery();
        else
            images = RequestManager.currentInstance.fetchPublicGallery();

        if (images != null /*&& !images.isEmpty()*/)
            addImagesToTable(images);
    }

    private void addImagesToTable(ArrayList<JSONObject> images) {
        FragmentManager manager = getFragmentManager();
        for (GalleryThumbnailFragment t : thumbnails)
            t.clearThumbnail();
        thumbnails = new ArrayList<>();
        table.removeAllViews();
        FragmentTransaction transaction = manager.beginTransaction();
        TableRow row;

        // All but last row
        for (int i = 0; i < images.size() / MAX_ROW_LENGTH; i++) {
            row = new TableRow(getContext());
            row.setId(i + 1);
            table.addView(row);

            for (int j = 0; j < MAX_ROW_LENGTH; j++) {
                GalleryThumbnailFragment thumbnail = new GalleryThumbnailFragment();
                thumbnail.setImageInfo(images.get(i * MAX_ROW_LENGTH + j));
                thumbnails.add(thumbnail);
                transaction.add(row.getId(), thumbnail);
            }
        }

        // Last row
        row = new TableRow(getContext());
        row.setId((images.size() / MAX_ROW_LENGTH) + 2);
        table.addView(row);
        for (int i = (images.size() / MAX_ROW_LENGTH) * MAX_ROW_LENGTH; i < images.size(); i++) {
            GalleryThumbnailFragment thumbnail = new GalleryThumbnailFragment();
            thumbnail.setImageInfo(images.get(i));
            thumbnails.add(thumbnail);
            transaction.add(row.getId(), thumbnail);
        }
        transaction.addToBackStack(null);
        transaction.commit();
    }


}
