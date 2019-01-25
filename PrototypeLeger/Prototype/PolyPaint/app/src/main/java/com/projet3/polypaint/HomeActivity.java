package com.projet3.polypaint;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.view.MotionEvent;
import android.widget.ScrollView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.sql.Time;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.zip.Inflater;

public class HomeActivity extends Activity  {

	//Chat
	private EditText chatEntry;
	private ImageButton chatEnterButton;
	private ImageButton chatExpendButton;
	private boolean chatIsExpended;
	private ArrayList<String> chatHistory;
	private RelativeLayout chatMessageZone;
	private LinearLayout chatMessageZoneTable;
	private ScrollView chatMessageZoneScrollView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat);
		InitializeChat();
	}

	private void InitializeChat(){
		chatEntry = (EditText)findViewById(R.id.chatEditText);
		SetupChatEntry();
		chatEnterButton = (ImageButton)findViewById(R.id.chatEnterButton);
		SetupChatEnterButton();
		chatExpendButton = (ImageButton) findViewById(R.id.chatExtendButton);
		chatIsExpended = false;
		SetupChatExtendButton();
		chatMessageZone = (RelativeLayout)findViewById(R.id.chatMessageZone);
		chatMessageZoneTable = (LinearLayout)findViewById(R.id.chatMessageZoneTable);
		chatHistory = new ArrayList<>();
		chatMessageZoneScrollView = (ScrollView)findViewById(R.id.chatVerticalScrollView);


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
	private void WriteMessage(String message) {
		TextView newView = (TextView)View.inflate(getBaseContext(), R.layout.chat_message, null);
		String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
		newView.setText(currentDateTimeString + ": " + message);
		chatMessageZoneTable.addView(newView);
		chatHistory.add(newView.getText().toString());
		ScrollDownMessages();
	}
    private void SetupChatEnterButton() {
        chatEnterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chatMessageZone.requestLayout();
                WriteMessage(chatEntry.getText().toString());
                chatEntry.setText("");
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

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		// save chat history
		savedInstanceState.putInt("ChatTableChildCount",chatMessageZoneTable.getChildCount());
		savedInstanceState.putStringArray("chatHistory", chatHistory.toArray(new String[chatHistory.size()]));
		super.onSaveInstanceState(savedInstanceState);
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		// restore chat history
		String[] restoredChatHistory = savedInstanceState.getStringArray("chatHistory");
		for(String elem : restoredChatHistory){
			TextView newView = (TextView)View.inflate(getBaseContext(), R.layout.chat_message, null);
			newView.setText(elem);
			chatMessageZoneTable.addView((newView));
			chatHistory.add(elem);	}
	}

}


