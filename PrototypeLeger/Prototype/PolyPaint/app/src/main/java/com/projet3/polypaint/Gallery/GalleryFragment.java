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

import com.projet3.polypaint.R;
import com.projet3.polypaint.UserLogin.RequestManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class GalleryFragment extends Fragment {

    private final int MAX_ROW_LENGTH = 9;

    private View rootView;
    private Switch privacySwitch;

    private boolean isPrivateImages = false;

    public GalleryFragment() {}
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView=inflater.inflate(R.layout.fragment_gallery, container, false);

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
        TableLayout table = (TableLayout) rootView.findViewById(R.id.table);
        table.removeAllViews();
        TableRow row;
        FragmentTransaction transaction = manager.beginTransaction();

        // All but last row
        for (int i = 0; i < images.size() / MAX_ROW_LENGTH; i++) {
            row = new TableRow(getContext());
            row.setId(i + 1);
            table.addView(row);

            for (int j = 0; j < MAX_ROW_LENGTH; j++) {
                GalleryThumbnailFragment thumbnail = new GalleryThumbnailFragment();
                thumbnail.setImageInfo(images.get(i * MAX_ROW_LENGTH + j));
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
            transaction.add(row.getId(), thumbnail);
        }
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private JSONObject createNewImage() {
        JSONObject image = new JSONObject();

        try {
            JSONObject testShape = new JSONObject();
            testShape.put("id", "Tom_randomid");
            testShape.put("drawingSessionId", "testSessionId");
            testShape.put("author", "TestAuthor");
            testShape.put("properties", generateShapeProperties());

            JSONArray shapes = new JSONArray();
            shapes.put(testShape);

            image.put("author", "OtherAuthor2");
            image.put("visibility", "public");
            image.put("protection", "protected");
            image.put("shapes", shapes);

            System.out.println(image.toString(2));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return image;
    }

    private JSONObject generateShapeProperties() {
        JSONObject testProperties = new JSONObject();

        try {
            testProperties.put("type", "class");
            testProperties.put("fillingColor", "ffffff");
            testProperties.put("borderColor", "000000");
            JSONArray coords = new JSONArray();
            coords.put(100);
            coords.put(100);
            testProperties.put("middlePointCoord", coords);
            testProperties.put("height", 100);
            testProperties.put("width", 100);
            testProperties.put("rotation", 0);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return testProperties;
    }


}
