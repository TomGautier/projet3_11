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
import com.projet3.polypaint.User.UserManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class GalleryFragment extends Fragment {

    // TEMP This needs to be in a network manager class
    private final String AZURE_IP = "40.122.119.160";
    private final String IP = "10.200.4.205";
    // ------------------------------------------------

    private View rootView;

    public GalleryFragment() {}
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView=inflater.inflate(R.layout.fragment_gallery, container, false);

        ArrayList<String> names = fetchContent();/*new ArrayList<>();
        for (int i = 0; i < 70; i ++) {
            names.add("img" + i);
        }*/
        if (names != null && !names.isEmpty())
            addImagesToTable(names);

        return rootView;
    }

    private ArrayList<String> fetchContent() {
        GalleryFetchContentTask fetchTask = new GalleryFetchContentTask();
        UserManager user = UserManager.currentInstance;
        String url = "http://" + AZURE_IP + ":3000/api/images/common/" + user.getSessionId() + "/" + user.getUserUsername();
        fetchTask.execute(url);

        try {
            JSONArray jsons = fetchTask.get(5, TimeUnit.SECONDS);
            ArrayList<String> authors = new ArrayList<>();
            for (int i = 0; i < jsons.length(); i ++){
                JSONObject jsonObject;
                String author = "";
                try {
                    jsonObject = jsons.getJSONObject(i);
                    author = jsonObject.getString("author");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (!author.isEmpty()){
                    authors.add(author);
                }
            }
            return authors;
        }catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }

        return null;
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
}
