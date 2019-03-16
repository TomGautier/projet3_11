package com.projet3.polypaint.Gallery;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.projet3.polypaint.Chat.ChatFragment;
import com.projet3.polypaint.R;
import com.projet3.polypaint.USER.RequestManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class GalleryFragment extends Fragment {

    private View rootView;

    public GalleryFragment() {}
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView=inflater.inflate(R.layout.fragment_gallery, container, false);

        ArrayList<JSONObject> images = RequestManager.currentInstance.fetchGalleryContent();

        if (images != null && !images.isEmpty())
            addImagesToTable(images);

        return rootView;
    }

    private void addImagesToTable(ArrayList<JSONObject> images) {
        final int MAX_ROW_LENGTH = 3;

        LayoutInflater inflater = LayoutInflater.from(getContext());
        FragmentManager manager = getFragmentManager();
        TableLayout table = (TableLayout) rootView.findViewById(R.id.table);
        //View gRow;
        TableRow row;

        // All but last row
        for (int i = 0; i < images.size() / MAX_ROW_LENGTH; i++) {
            //gRow = inflater.inflate(R.layout.gallery_row, null);
            row = new TableRow(getContext());
            row.setId(i);
            table.addView(row);
            //row = (TableRow)gRow.findViewById(R.id.row);
            //row = new TableRow(getContext());
            System.out.println("Row id #" + i + " : " + row.getId());
            FragmentTransaction transaction = manager.beginTransaction();

            for (int j = 0; j < MAX_ROW_LENGTH; j++) {
                GalleryThumbnailFragment thumbnail = new GalleryThumbnailFragment();
                thumbnail.setImageInfo(images.get(i * MAX_ROW_LENGTH + j));
                transaction.add(row.getId(), thumbnail);
            }

            transaction.addToBackStack(null);
            transaction.commit();
        }

        // Last row
        /*gRow = inflater.inflate(R.layout.gallery_row, null);
        table.addView(gRow);
        row = (TableRow)gRow.findViewById(R.id.row);
        FragmentTransaction transaction = manager.beginTransaction();
        for (int i = images.size() - (images.size() % MAX_ROW_LENGTH); i < images.size(); i++) {
            GalleryThumbnailFragment thumbnail = new GalleryThumbnailFragment();
            thumbnail.setImageInfo(images.get(i));
            transaction.add(row.getId(), thumbnail);
            /*View v = inflater.inflate(R.layout.gallery_thumbnail, null);
            TextView tv = (TextView) v.findViewById(R.id.title);
            try {
                tv.setText(images.get(i).getString("author"));
            } catch (JSONException e) { e.printStackTrace(); }
            row.addView(v);
        }
        transaction.addToBackStack(null);
        transaction.commit();*/
    }

    /*private void addImagesToTable(ArrayList<String> names) {
        final int MAX_ROW_LENGTH = 9;

        LayoutInflater inflater = LayoutInflater.from(getContext());
        TableLayout table = (TableLayout) rootView.findViewById(R.id.table);
        View gRow;
        TableRow row;

        // All but last row
        for (int i = 0; i < names.size() / MAX_ROW_LENGTH; i++) {
            gRow = inflater.inflate(R.layout.gallery_row, null);
            table.addView(gRow);
            row = (TableRow)gRow.findViewById(R.id.row);

            for (int j = 0; j < MAX_ROW_LENGTH; j++) {
                final View v = inflater.inflate(R.layout.gallery_thumbnail, null);
                final TextView tv = (TextView) v.findViewById(R.id.title);
                tv.setText(names.get(i * MAX_ROW_LENGTH + j));
                row.addView(v);
            }
        }

        // Last row
        gRow = inflater.inflate(R.layout.gallery_row, null);
        table.addView(gRow);
        row = (TableRow)gRow.findViewById(R.id.row);
        for (int i = names.size() - (names.size() % MAX_ROW_LENGTH); i < names.size(); i++) {
            View v = inflater.inflate(R.layout.gallery_thumbnail, null);
            TextView tv = (TextView) v.findViewById(R.id.title);
            tv.setText(names.get(i));
            row.addView(v);
        }
    }*/

    private JSONObject createNewImage() {
        JSONObject image = new JSONObject();

        try {
            JSONObject testShape = new JSONObject();
            testShape.put("id", "Tom_randomid");
            testShape.put("drawingSessionId", "testSessionId");
            testShape.put("author", "TestAuthor");
            testShape.put("properties", generateImageProperties());

            JSONArray shapes = new JSONArray();
            shapes.put(testShape);

            image.put("author", "TestAuthor");
            image.put("visibility", "public");
            image.put("protection", "protected");
            //image.put("shapes", shapes);

            System.out.println(image.toString(2));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return image;
    }

    private JSONObject generateImageProperties() {
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
