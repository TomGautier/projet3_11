package com.projet3.polypaint;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;


public class UsersListFragment extends Fragment {

    private LinearLayout connectedUsersTable;
    private RelativeLayout usersTableRelativeLayout;
    private RelativeLayout hiddenTitleRelativeLayout;
    private View rootView;
    private TextView title;
    private ArrayList<String> users;
    private boolean isOpen;

    public static UsersListFragment newInstance(ArrayList<String> users_){
        Bundle bundle = new Bundle();
        bundle.putStringArrayList("USERS",users_);
        UsersListFragment fragobj = new UsersListFragment();
        fragobj.setArguments(bundle);
        return fragobj;
    }
    public UsersListFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        rootView=inflater.inflate(R.layout.users_list, container, false);
        connectedUsersTable = (LinearLayout)rootView.findViewById(R.id.connectedUsersTable);
        usersTableRelativeLayout = (RelativeLayout)rootView.findViewById(R.id.usersTable);
        hiddenTitleRelativeLayout = (RelativeLayout)rootView.findViewById(R.id.hiddenTitleRelativeLayout);
        hiddenTitleRelativeLayout.setVisibility(View.GONE);
        title = (TextView)rootView.findViewById(R.id.usersTableTitle);
        users = getArguments().getStringArrayList("USERS");
        isOpen = true;
        drawUsers();
        setupListeners();
        return rootView;
    }
    private void setupListeners(){
        title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rootView.requestLayout();
                if (isOpen){
                    usersTableRelativeLayout.setVisibility(View.GONE);
                    hiddenTitleRelativeLayout.setVisibility(View.VISIBLE);
                    isOpen = false;
                }
            }
        });
        hiddenTitleRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isOpen){
                    usersTableRelativeLayout.setVisibility(View.VISIBLE);
                    hiddenTitleRelativeLayout.setVisibility(View.GONE);
                    isOpen = true;
                }
            }
        });

    }
    private void drawUsers(){
        for (String user : users) {
            LinearLayout userView = (LinearLayout)View.inflate(getContext(),R.layout.user_entry,null);
            ((TextView)userView.findViewById(R.id.usernameTextView)).setText(user);
            connectedUsersTable.addView(userView);
        }

    }





}
