package com.projet3.polypaint.Chat;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
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
import android.widget.Spinner;
import android.widget.TextView;


import com.projet3.polypaint.R;
import com.projet3.polypaint.User.UserManager;
import com.projet3.polypaint.Utilities;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.ArrayList;


public class ChatFragment extends Fragment implements NewMessageListener {

    private final int CHAT_OPEN_COEF= 2;
    private final int CHAT_CLOSE_COEF = 4;


    private EditText chatEntry;
    public ImageButton chatEnterButton;
    private ImageButton chatExpendButton;
    public LinearLayout chatMessageZoneTable;
    private RelativeLayout chatMessageZone;
    private ScrollView chatMessageZoneScrollView;
    private Spinner conversationSpinner;

    //private ArrayList<Conversation> conversations;
    public Conversation currentConversation;
    private boolean chatIsExpended;
   // private UserInformation userInformation;
    public View rootView;

    public ChatFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        rootView=inflater.inflate(R.layout.activity_chat, container, false);
        if (UserManager.currentInstance.getUserConversationsNames().size() != 0) {
            currentConversation = UserManager.currentInstance.getUserConversationAt(0);
        }
        else {
            currentConversation = new Conversation("GENERAL");
        }
        initializeChat();
        SocketManager.currentInstance.setupNewMessageListener(this);
        return rootView;
        //Bundle bundle = getArguments();
        //if (savedInstanceState == null && bundle != null){
        //conversations = bundle.getParcelableArrayList("CONVERSATIONS");
        //if (conversations.size() == 0)
        //conversations.add(new Conversation("General", new ArrayList()));
        // currentConversation
        //  UserManager.currentInstance.

        //userInformation = bundle.getParcelable("USER_INFORMATION");
        //}
        //else{
        // conversations = savedInstanceState.getParcelableArrayList("CONVERSATIONS");
        //currentConversation = conversations.get(savedInstanceState.getInt("CURRENT_INDEX"));
        //userInformation = savedInstanceState.getParcelable("USER_INFORMATION");
        //}

    }

   /* public static ChatFragment newInstance(ArrayList<Conversation> conversations) {
        ChatFragment f = new ChatFragment();
        Bundle args = new Bundle();
        //args.putParcelable("USER_INFORMATION" ,userInformation_);
        args.putParcelableArrayList("CONVERSATIONS", conversations);
        f.setArguments(args);
        return f;
    }*/

    private void initializeChat(){

        chatEntry = (EditText)rootView.findViewById(R.id.chatEditText);
        chatEnterButton = (ImageButton)rootView.findViewById(R.id.chatEnterButton);
        chatExpendButton = (ImageButton) rootView.findViewById(R.id.chatExtendButton);
        chatMessageZone = (RelativeLayout)rootView.findViewById(R.id.chatMessageZone);
        //chatMessageZone.getLayoutParams().height = getScreenSize().y/CHAT_CLOSE_COEF;
        chatMessageZoneTable = (LinearLayout)rootView.findViewById(R.id.chatMessageZoneTable);
        chatMessageZoneScrollView = (ScrollView)rootView.findViewById(R.id.chatVerticalScrollView);
        conversationSpinner = (Spinner)rootView.findViewById(R.id.conversationSpinner);

        setupChatEntry();
        SetupChatEnterButton();
        setupChatExtendButton();


        if (currentConversation.getHistorySize() > 0)
            loadConversation();


        setupChatConversationSpinner();


        Utilities.setButtonEffect(chatEnterButton);
        Utilities.setButtonEffect(chatExpendButton);

        chatIsExpended = false;
    }

    private Point getScreenSize() {
        WindowManager wm = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return  size;
    }

    private void setupChatExtendButton() {

        chatExpendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chatMessageZone.requestLayout();
                if(!chatIsExpended){
                    chatMessageZone.getLayoutParams().height*=2;
                    chatIsExpended = true;
                }
                else {
                    chatMessageZone.getLayoutParams().height/=2;
                    chatIsExpended = false;
                    scrollDownMessages();
                }
            }
        });
    }
    public void WriteMessage(String message, boolean withHistory) {
        TextView newView = (TextView)View.inflate(getContext(), R.layout.chat_message, null);
        chatMessageZoneTable.addView(newView);
        newView.setText(message);
        if (withHistory)
            currentConversation.addToHistory(newView.getText().toString());

        scrollDownMessages();
    }

    public void sendMessage(String message) {
        //SocketManager.currentInstance.sendMessage(getDate(), RequestManager.currentInstance.getUserUsername(), message);
        SocketManager.currentInstance.sendMessage(currentConversation.getName(), message);
    }

    private void SetupChatEnterButton() {
        chatEnterButton.setColorFilter(Color.GRAY);
        chatEnterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleSendMessage(chatEntry.getText().toString());
            }
        });
    }
    private String getDate() {
        String timeStamp = new SimpleDateFormat("HH:mm:ss a").format(Calendar.getInstance().getTime());
        return timeStamp;
    }



    /*private ArrayList<String> getConversationsNames() {
        ArrayList<String> stringConversations = new ArrayList<>();
        for (int i = 0; i < UserManager.currentInstance.getUserConversationsNames().size(); i++) {
            stringConversations.add();
        }
        return stringConversations;
    }*/
    public void setupChatConversationSpinner() {
        android.widget.ArrayAdapter<String> spinnerArrayAdapter = new android.widget.ArrayAdapter<>
                (getContext(), android.R.layout.simple_spinner_item, UserManager.currentInstance.getUserConversationsNames());
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout
                .simple_spinner_dropdown_item);
        conversationSpinner.setAdapter(spinnerArrayAdapter);
        conversationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selected = adapterView.getItemAtPosition(i).toString();
                if (currentConversation.getName() != selected) {
                    for (int j = 0; j < UserManager.currentInstance.getUserConversationsNames().size(); j++) {
                        Conversation current = UserManager.currentInstance.getUserConversationAt(j);
                        if (current.getName() == selected && current.getName() != currentConversation.getName()){
                            currentConversation = current;
                            loadConversation();
                            break;
                        }
                    }
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });



    }
    private void setupChatEntry() {
        chatEntry.addTextChangedListener(new android.text.TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(s.toString().trim().length()==0){
                    chatEnterButton.setEnabled(false);
                    chatEnterButton.setColorFilter(Color.GRAY);
                } else {
                    chatEnterButton.setEnabled(true);
                    chatEnterButton.clearColorFilter();
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {
                // TODO Auto-generated method stub
            }
        });
        chatEntry.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE && chatEntry.getText().toString().trim().length()!=0) {
                    handleSendMessage(chatEntry.getText().toString());
                }
                return false;
            }
        });
    }
    private void handleSendMessage(String message) {
        chatMessageZone.requestLayout();
        sendMessage(message);
        chatEntry.setText("");
    }
    private void scrollDownMessages() {

        chatMessageZoneScrollView.postDelayed(new Runnable() {
            @Override
            public void run() {
                chatMessageZoneScrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        }, 100);
    }
    private void loadConversation() {
        chatMessageZoneTable.removeAllViews();
        for (int j = 0; j < currentConversation.getHistorySize(); j++)
            WriteMessage(currentConversation.getHistoryAt(j), false);
    }

    @Override
    public void onNewMessage(final String message) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                WriteMessage(message,true);
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
       // savedInstanceState.putParcelableArrayList("CONVERSATIONS",conversations);
        //savedInstanceState.putParcelable("USER_INFORMATION", userInformation);
        //savedInstanceState.putInt("CURRENT_INDEX", conversations.indexOf(currentConversation));
    }



}
