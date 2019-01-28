package com.projet3.polypaint;

import android.app.Activity;
import android.graphics.Color;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.function.Consumer;

public class Chat {
    //Chat
    private EditText chatEntry;
    private ImageButton chatEnterButton;
    private ImageButton chatExpendButton;
    private boolean chatIsExpended;
    public ArrayList<String> chatHistory;
    public LinearLayout chatMessageZoneTable;
    private RelativeLayout chatMessageZone;
    private ScrollView chatMessageZoneScrollView;
    public Activity currentActivity;
    public static Chat currentInstance;
    private Spinner conversationSpinner;
    private ArrayList<Conversation> conversations;
    private Conversation currentConversation;

    public  Chat(Activity currentActivity_){
            currentActivity = currentActivity_;
            InitializeChat();
            currentInstance = this;
    }

    private void InitializeChat(){
        chatEntry = (EditText)currentActivity.findViewById(R.id.chatEditText);
        SetupChatEntry();
        chatEnterButton = (ImageButton)currentActivity.findViewById(R.id.chatEnterButton);
        SetupChatEnterButton();
        chatExpendButton = (ImageButton) currentActivity.findViewById(R.id.chatExtendButton);
        chatIsExpended = false;
        SetupChatExtendButton();
        chatMessageZone = (RelativeLayout)currentActivity.findViewById(R.id.chatMessageZone);
        chatMessageZoneTable = (LinearLayout)currentActivity.findViewById(R.id.chatMessageZoneTable);
        chatHistory = new ArrayList<>();
        chatMessageZoneScrollView = (ScrollView)currentActivity.findViewById(R.id.chatVerticalScrollView);

        conversations = new ArrayList<>();
        conversations.add(new Conversation("allo"));
        conversations.add(new Conversation("allo2"));
        currentConversation = conversations.get(0);

        conversationSpinner = (Spinner)currentActivity.findViewById(R.id.conversationSpinner);
        SetupChatConversationSpinner();


        Utilities.SetButtonEffect(chatEnterButton);
        Utilities.SetButtonEffect(chatExpendButton);

        //etc...
    }

    private void SetupChatExtendButton() {
        chatExpendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chatMessageZone.requestLayout();
                if(!chatIsExpended){
                    chatMessageZone.getLayoutParams().height *=2;
                    chatIsExpended = true;
                }
                else {
                    chatMessageZone.getLayoutParams().height /= 2;
                    chatIsExpended = false;
                    ScrollDownMessages();
                }
            }
        });
    }
    public void WriteMessage(String message, boolean withHistory) {
        TextView newView = (TextView)View.inflate(currentActivity, R.layout.chat_message, null);
        newView.setText(message);
        chatMessageZoneTable.addView(newView);
        if (withHistory)
            currentConversation.AddToHistory(newView.getText().toString());
        ScrollDownMessages();
    }
    private void SetupChatEnterButton() {
        chatEnterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chatMessageZone.requestLayout();
                String message = DateFormat.getDateTimeInstance().format(new Date()) + " " + chatEntry.getText().toString();
                WriteMessage(message, true);
                chatEntry.setText("");
            }
        });
    }
    private ArrayList<String> GetConversationsNames() {
        ArrayList<String> stringConversations = new ArrayList<>();
        for (Conversation elem : conversations) {
            stringConversations.add(elem.GetName());
        }
        return stringConversations;
    }
    private void SetupChatConversationSpinner() {
        android.widget.ArrayAdapter<String> spinnerArrayAdapter = new android.widget.ArrayAdapter<>
                (currentActivity, android.R.layout.simple_spinner_item, GetConversationsNames()); //selected item will look like a spinner set from XML
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
                        if (current.GetName() == selected){
                            ChangeConversation(current);
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
    private void SetupChatEntry() {
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
    }
    private void ScrollDownMessages() {

        chatMessageZoneScrollView.postDelayed(new Runnable() {
            @Override
            public void run() {
                //replace this line to scroll up or down
                chatMessageZoneScrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        }, 100);
    }
    private void ChangeConversation(Conversation convo) {


        currentConversation = convo;
        chatMessageZoneTable.removeAllViews();

        for (int j = 0; j < currentConversation.GetHistorySize(); j++) {
            WriteMessage(currentConversation.GetHistoryAt(j), false);
        }


    }


}
