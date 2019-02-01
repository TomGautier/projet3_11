package com.projet3.polypaint;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.nkzawa.socketio.client.IO;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import com.github.nkzawa.socketio.client.IO;



public class Chat {
    //server
    public final static String SERVER_ADDRESS = "https://polypaint-11.azurewebsites.net/";
    public final static String SERVER_ADDRESS_TEST = "http://localhost:3000/";
    public final static int SERVER_PORT = 80;

    //Chat
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
    private com.github.nkzawa.socketio.client.Socket mSocket;

    public  Chat(Activity currentActivity_){
            currentActivity = currentActivity_;
            conversations = new ArrayList<>();
            conversations.add(new Conversation("allo"));
            conversations.add(new Conversation("allo2"));
            currentConversation = conversations.get(0);
            InitializeChat();
    }

    public  Chat(Activity currentActivity_, Bundle bundle){
        currentActivity = currentActivity_;
        conversations = bundle.getParcelableArrayList("conversations");
        currentConversation = conversations.get(bundle.getInt("currentConversationIndex"));
        InitializeChat();
    }



    private void InitializeChat(){
        chatEntry = (EditText)currentActivity.findViewById(R.id.chatEditText);
        chatEnterButton = (ImageButton)currentActivity.findViewById(R.id.chatEnterButton);
        chatExpendButton = (ImageButton) currentActivity.findViewById(R.id.chatExtendButton);
        chatMessageZone = (RelativeLayout)currentActivity.findViewById(R.id.chatMessageZone);
        chatMessageZoneTable = (LinearLayout)currentActivity.findViewById(R.id.chatMessageZoneTable);
        chatMessageZoneScrollView = (ScrollView)currentActivity.findViewById(R.id.chatVerticalScrollView);
        conversationSpinner = (Spinner)currentActivity.findViewById(R.id.conversationSpinner);

        SetupChatEntry();
        SetupChatEnterButton();
        SetupChatExtendButton();
        SetupSocket();


        if (currentConversation.GetHistorySize() > 0)
            LoadConversation();


        SetupChatConversationSpinner();


        Utilities.SetButtonEffect(chatEnterButton);
        Utilities.SetButtonEffect(chatExpendButton);

        currentInstance = this;
        chatIsExpended = false;
    }
    private void SetupSocket()
    {
        try {
            IO.Options options = new IO.Options();
            options.port = 3000;
            mSocket = IO.socket(SERVER_ADDRESS_TEST, options);
            mSocket.connect();
        } catch (URISyntaxException e) {}
    }

    private void SetupChatExtendButton() {

        chatExpendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WindowManager wm = (WindowManager) currentActivity.getSystemService(Context.WINDOW_SERVICE);
                Display display = wm.getDefaultDisplay();
                Point size = new Point();
                display.getSize(size);
                chatMessageZone.requestLayout();
                if(!chatIsExpended){
                    chatMessageZone.getLayoutParams().height = size.y/3;
                    chatIsExpended = true;
                }
                else {
                    chatMessageZone.getLayoutParams().height = size.y/4;
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
        if (withHistory) {
            currentConversation.AddToHistory(newView.getText().toString());
            mSocket.emit("chatMessage", newView.getText().toString());
           /* if (currentConversation.conversationTask.getStatus() == AsyncTask.Status.RUNNING) {
                try {
                    currentConversation.conversationTask.dataOutputStream.writeUTF(newView.getText().toString());
                }
                catch(Exception e) {
                    Log.d("EXCEPTION", "an error in sending message");
                }
            }*/
        }
        ScrollDownMessages();
    }

    private void SetupChatEnterButton() {
        chatEnterButton.setColorFilter(Color.GRAY);
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
                        if (current.GetName() == selected && current.GetName() != currentConversation.GetName()){
                            currentConversation = current;
                            LoadConversation();
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
    private void LoadConversation() {
        chatMessageZoneTable.removeAllViews();
        for (int j = 0; j < currentConversation.GetHistorySize(); j++)
            WriteMessage(currentConversation.GetHistoryAt(j), false);
    }

    public Bundle GetChatBundle() {
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("conversations",conversations);
        bundle.putInt("currentConversationIndex", conversations.indexOf(currentConversation));
        return bundle;
    }


}
/*class ConversationTask extends AsyncTask<String, Void, String> {

    private Socket socket;
    private DataInputStream dataInputStream;
    public DataOutputStream dataOutputStream;
    private Conversation conversation;

    public ConversationTask(Conversation conversation_) {
        conversation = conversation_;
        this.execute();
    }
    @Override
    protected String doInBackground(String... args) {
        while(socket.isConnected()){
            try {
                String read = dataInputStream.readUTF();
                if (read != null) {
                    if (conversation == Chat.currentInstance.currentConversation)
                        Chat.currentInstance.WriteMessage(read, true);
                    else
                        conversation.AddToHistory(read);
                        //envoyer alerte sonore ...
                }
            }
            catch(Exception e) {
                Log.d("EXCEPTION", "an error in reading a message");
            }
        }
        return "finished";
    }

    @Override
    protected void onPostExecute(String result) {
        Log.d("result", result);
    }

    @Override
    protected void onPreExecute() {
        try {
            socket = new Socket(Chat.SERVER_ADDRESS, Chat.SERVER_PORT);
            dataInputStream = new DataInputStream(socket.getInputStream());
            dataOutputStream = new DataOutputStream(socket.getOutputStream());
        }
        catch(Exception e) {
            Log.d("exception", e.getMessage());
        }
    }
}*/

