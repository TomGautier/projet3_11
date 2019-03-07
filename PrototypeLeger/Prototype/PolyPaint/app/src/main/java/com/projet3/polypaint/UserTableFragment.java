package com.projet3.polypaint;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Space;
import android.widget.Spinner;
import android.widget.TextView;

import com.projet3.polypaint.Chat.ChatFragment;
import com.projet3.polypaint.Chat.Conversation;
import com.projet3.polypaint.Chat.NewMessageListener;
import com.projet3.polypaint.Chat.SocketManager;
import com.projet3.polypaint.User.UserInformation;
import com.projet3.polypaint.User.UserManager;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;


public class UserTableFragment extends Fragment {

    private LinearLayout usersTable;
    private View rootView;
    private TextView title;
    private ArrayList<String> users;
    private boolean isOpen;
    private int height;

    public static UserTableFragment newInstance(ArrayList<String> users_){
        Bundle bundle = new Bundle();
        bundle.putStringArrayList("USERS",users_);
        UserTableFragment fragobj = new UserTableFragment();
        fragobj.setArguments(bundle);
        return fragobj;
    }
    public UserTableFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        rootView=inflater.inflate(R.layout.users_table, container, false);
        usersTable = (LinearLayout)rootView.findViewById(R.id.connectedUsersTable);
        title = (TextView)rootView.findViewById(R.id.usersTableTitle);
        users = getArguments().getStringArrayList("USERS");
        isOpen = true;
        initialize();
        return rootView;
    }
    private void initialize(){
        for (String user : users) {
            LinearLayout userView = (LinearLayout)View.inflate(getContext(),R.layout.user_entry,null);
            ((TextView)userView.findViewById(R.id.usernameTextView)).setText(user);
            usersTable.addView(userView);
        }
        title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rootView.requestLayout();
                if (isOpen){
                    height = rootView.getHeight();
                    rootView.getLayoutParams().height = 100;
                    isOpen = false;
                }
                else{
                    rootView.getLayoutParams().height = height;
                    isOpen = true;
                }
            }
        });

    }





}
