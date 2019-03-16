package com.projet3.polypaint.Gallery;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

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

        //RequestManager.currentInstance.postImage(createNewImage());

        ArrayList<String> names = RequestManager.currentInstance.fetchGalleryContent();/*new ArrayList<>();
        for (int i = 0; i < 70; i ++) {
            names.add("img" + i);
        }*/
        if (names != null && !names.isEmpty())
            addImagesToTable(names);

        return rootView;
    }

    private void addImagesToTable(ArrayList<String> names) {
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
    }

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
