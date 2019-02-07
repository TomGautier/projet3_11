package com.projet3.polypaint.Chat;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
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
import com.projet3.polypaint.UserInformation;
import com.projet3.polypaint.Utilities;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;


public class Chat implements NewMessageListener {

    private final int CHAT_OPEN_COEF= 2;
    private final int CHAT_CLOSE_COEF = 4;


    private EditText chatEntry;
    public ImageButton chatEnterButton;
    private ImageButton chatExpendButton;
    public LinearLayout chatMessageZoneTable;
    private RelativeLayout chatMessageZone;
    private ScrollView chatMessageZoneScrollView;
    private Spinner conversationSpinner;
    public static Chat currentInstance;

    private ArrayList<Conversation> conversations;
    public Conversation currentConversation;
    private boolean chatIsExpended;
    public Activity currentActivity;
    private UserInformation userInformation;


    public  Chat(Activity currentActivity_, UserInformation userInformation_){
            currentActivity = currentActivity_;
            userInformation = userInformation_;
            conversations = new ArrayList<>();
            conversations.add(new Conversation("conversation1"));
            conversations.add(new Conversation("conversation2"));
            currentConversation = conversations.get(0);
            initializeChat();
    }

    public  Chat(Activity currentActivity_, UserInformation userInformation_, Bundle bundle){
        currentActivity = currentActivity_;
        userInformation = userInformation_;
        conversations = bundle.getParcelableArrayList("conversations");
        currentConversation = conversations.get(bundle.getInt("currentConversationIndex"));
        initializeChat();
    }



    private void initializeChat(){
        chatEntry = (EditText)currentActivity.findViewById(R.id.chatEditText);
        chatEnterButton = (ImageButton)currentActivity.findViewById(R.id.chatEnterButton);
        chatExpendButton = (ImageButton) currentActivity.findViewById(R.id.chatExtendButton);
        chatMessageZone = (RelativeLayout)currentActivity.findViewById(R.id.chatMessageZone);
        chatMessageZone.getLayoutParams().height = getScreenSize().y/CHAT_CLOSE_COEF;
        chatMessageZoneTable = (LinearLayout)currentActivity.findViewById(R.id.chatMessageZoneTable);
        chatMessageZoneScrollView = (ScrollView)currentActivity.findViewById(R.id.chatVerticalScrollView);
        conversationSpinner = (Spinner)currentActivity.findViewById(R.id.conversationSpinner);


        setupChatEntry();
        SetupChatEnterButton();
        setupChatExtendButton();


        if (currentConversation.GetHistorySize() > 0)
            loadConversation();


        setupChatConversationSpinner();


        Utilities.SetButtonEffect(chatEnterButton);
        Utilities.SetButtonEffect(chatExpendButton);

        currentInstance = this;
        chatIsExpended = false;
    }

    private Point getScreenSize() {
        WindowManager wm = (WindowManager) currentActivity.getSystemService(Context.WINDOW_SERVICE);
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
                    chatMessageZone.getLayoutParams().height = getScreenSize().y/CHAT_OPEN_COEF;
                    chatIsExpended = true;
                }
                else {
                    chatMessageZone.getLayoutParams().height = getScreenSize().y/CHAT_CLOSE_COEF;
                    chatIsExpended = false;
                    scrollDownMessages();
                }
            }
        });
    }
    public void WriteMessage(String message, boolean withHistory) {
        TextView newView = (TextView)View.inflate(currentActivity, R.layout.chat_message, null);
        chatMessageZoneTable.addView(newView);
        newView.setText(message);
        if (withHistory)
            currentConversation.AddToHistory(newView.getText().toString());

        scrollDownMessages();
    }

    public void sendMessage(String message) {
        SocketManager.currentInstance.sendMessage(getDate(),userInformation.getUsername(), message);
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
        return DateFormat.getTimeInstance().format(new Date());
    }


    private ArrayList<String> getConversationsNames() {
        ArrayList<String> stringConversations = new ArrayList<>();
        for (Conversation elem : conversations) {
            stringConversations.add(elem.GetName());
        }
        return stringConversations;
    }
    private void setupChatConversationSpinner() {
        android.widget.ArrayAdapter<String> spinnerArrayAdapter = new android.widget.ArrayAdapter<>
                (currentActivity, android.R.layout.simple_spinner_item, getConversationsNames());
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout
                .simple_spinner_dropdown_item);
        conversationSpinner.setAdapter(spinnerArrayAdapter);
        conversationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selected = adapterView.getItemAtPosition(i).toString();
                if (currentConversation.GetName() != selected) {
                    for (int j = 0; j < conversations.size(); j++) {
                        Conversation current = conversations.get(j);
                        if (current.GetName() == selected && current.GetName() != currentConversation.GetName()){
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
        for (int j = 0; j < currentConversation.GetHistorySize(); j++)
            WriteMessage(currentConversation.GetHistoryAt(j), false);
    }

    public Bundle getChatBundle() {
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("conversations",conversations);
        bundle.putInt("currentConversationIndex", conversations.indexOf(currentConversation));
        return bundle;
    }


    @Override
    public void onNewMessage(final String message) {
        currentActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                WriteMessage(message,true);
            }
        });
    }
}
