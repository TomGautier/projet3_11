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

import java.util.ArrayList;

public class GalleryFragment extends Fragment {

    private View rootView;

    public GalleryFragment() {}
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView=inflater.inflate(R.layout.fragment_gallery, container, false);

        initializeButtons();


        ArrayList<String> names = new ArrayList<>();
        for (int i = 0; i < 70; i ++) {
            names.add("img" + i);
        }
        addImagesToTable(names);

        //setTouchListener();
        return rootView;
    }

    private void initializeButtons() {

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
