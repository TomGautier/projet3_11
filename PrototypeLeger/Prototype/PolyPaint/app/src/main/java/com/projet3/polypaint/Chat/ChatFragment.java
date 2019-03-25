package com.projet3.polypaint.Chat;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.projet3.polypaint.R;
import com.projet3.polypaint.SocketManager;
import com.projet3.polypaint.UserLogin.RequestManager;
import com.projet3.polypaint.UserLogin.UserManager;
import com.projet3.polypaint.Others.Utilities;

import java.text.SimpleDateFormat;
import java.util.Calendar;


public class ChatFragment extends Fragment implements NewMessageListener {



    private EditText chatEntry;
    public ImageButton chatEnterButton;
    private ImageButton chatExpendButton;
    private ImageButton addConversationButton;
    private ImageButton removeConversationButton;
    public LinearLayout chatMessageZoneTable;
    private RelativeLayout chatMessageZone;
    private RelativeLayout chatToolbar;
    private ScrollView chatMessageZoneScrollView;
    private Spinner conversationSpinner;

    public Conversation currentConversation;
    private boolean chatIsExpended;
    private boolean isOpen;

    public View rootView;

    public ChatFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.activity_chat, container, false);
        RequestManager.currentInstance.fetchUserConversations();
        if (UserManager.currentInstance.getUserConversationsNames().size() != 0) {
            currentConversation = UserManager.currentInstance.getUserConversationAt(0);
        } else {
            currentConversation = new Conversation("GENERAL");
        }
        initializeChat();
        SocketManager.currentInstance.setupNewMessageListener(this);
        return rootView;
    }

    private void initializeChat(){

        chatIsExpended = false;
        isOpen = true;

        chatEntry = (EditText)rootView.findViewById(R.id.chatEditText);
        chatEnterButton = (ImageButton)rootView.findViewById(R.id.chatEnterButton);
        chatExpendButton = (ImageButton) rootView.findViewById(R.id.chatExtendButton);
        addConversationButton = (ImageButton)rootView.findViewById(R.id.addConversationImageButton);
        removeConversationButton = (ImageButton)rootView.findViewById(R.id.removeConversationImageButton);
        chatMessageZone = (RelativeLayout)rootView.findViewById(R.id.chatMessageZone);
        chatToolbar = (RelativeLayout)rootView.findViewById(R.id.chatToolbar);
        chatMessageZoneTable = (LinearLayout)rootView.findViewById(R.id.chatMessageZoneTable);
        chatMessageZoneScrollView = (ScrollView)rootView.findViewById(R.id.chatVerticalScrollView);
        conversationSpinner = (Spinner)rootView.findViewById(R.id.conversationSpinner);

        setupChatEntry();
        setupChatToolbar();
        SetupChatEnterButton();
        setupChatExtendButton();


        if (currentConversation.getHistorySize() > 0)
            loadConversation();


        setupChatConversationSpinner();

        Utilities.setButtonEffect(addConversationButton);
        Utilities.setButtonEffect(removeConversationButton);
        Utilities.setButtonEffect(chatEnterButton);
        Utilities.setButtonEffect(chatExpendButton);


    }

    /*private Point getScreenSize() {
        WindowManager wm = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return  size;
    }*/
    public void createRemoveConversationPopup(){
        LayoutInflater inflater = (LayoutInflater)
                getActivity().getSystemService(getActivity().LAYOUT_INFLATER_SERVICE);
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true;
        final View popupView = inflater.inflate(R.layout.remove_conversation_popup, null);
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);
        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);
        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (v != popupView){
                    popupWindow.dismiss();
                }
                return true;
            }
        });
        final Spinner spinner = (Spinner)popupView.findViewById(R.id.removeConversationSpinner);
        android.widget.ArrayAdapter<String> spinnerArrayAdapter = new android.widget.ArrayAdapter
                (getActivity(), android.R.layout.simple_spinner_item, UserManager.currentInstance.getUserConversationsNames());
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout
                .simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerArrayAdapter);
        Button button = (Button)popupView.findViewById(R.id.removeConversationButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String conversation = spinner.getSelectedItem().toString();
                UserManager.currentInstance.removeUserConversation(conversation);
                SocketManager.currentInstance.leaveConversation(conversation);
                setupChatConversationSpinner();
                popupWindow.dismiss();
            }
        });
    }

    public void createAddConversationPopup(){
        LayoutInflater inflater = (LayoutInflater)
                getActivity().getSystemService(getActivity().LAYOUT_INFLATER_SERVICE);
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true;
        final View popupView = inflater.inflate(R.layout.add_conversation_popup, null);
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);
        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);
        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (v != popupView){
                    popupWindow.dismiss();
                }
                return true;
            }
        });
        Button button = (Button)popupView.findViewById(R.id.addConversationButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = ((EditText)popupView.findViewById(R.id.addConversationEditText)).getText().toString();
                if (!name.isEmpty()){
                    boolean ret = RequestManager.currentInstance.addUserConversation(name);
                    if (ret){
                        UserManager.currentInstance.addUserConversation(name);
                        setupChatConversationSpinner();
                        Toast.makeText(getActivity(),"Conversation " + name + " cree",Toast.LENGTH_SHORT).show();
                        popupWindow.dismiss();
                    }
                    else
                        Toast.makeText(getActivity(),"Cette conversation existe deja",Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(getActivity(),"Veuillez entrer un nom de conversation valide",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setupChatToolbar(){
        addConversationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAddConversationPopup();
            }
        });
        removeConversationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createRemoveConversationPopup();
            }
        });
        chatToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chatMessageZone.requestLayout();
                if (isOpen){
                    chatMessageZone.setVisibility(View.GONE);
                    isOpen = false;
                }
                else{
                    chatMessageZone.setVisibility(View.VISIBLE);
                    isOpen = true;
                }
            }
        });


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
    private void setupChatConversationSpinner() {
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
