package com.projet3.polypaint;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.view.MotionEvent;
import android.widget.TextView;

import java.sql.Time;
import java.text.DateFormat;
import java.util.Date;
import java.util.zip.Inflater;

public class HomeActivity extends Activity  {

	//Chat
	private EditText chatEntry;
	private ImageButton chatEnterButton;
	private ImageButton chatExpendButton;
	private boolean chatIsExpended;
	private RelativeLayout chatMessageZone;
	private LinearLayout chatMessageZoneTable;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat);
		InitializeChat();
	}

	private void InitializeChat(){
		chatEntry = (EditText)findViewById(R.id.chatEditText);
		chatEnterButton = (ImageButton)findViewById(R.id.chatEnterButton);
		SetupChatEnterButton();
		chatExpendButton = (ImageButton) findViewById(R.id.chatExtendButton);
		chatIsExpended = false;
		SetupChatExtendButton();
		chatMessageZone = (RelativeLayout)findViewById(R.id.chatMessageZone);
		chatMessageZoneTable = (LinearLayout)findViewById(R.id.chatMessageZoneTable);

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
				}
			}
		});
	}

    private void SetupChatEnterButton() {
        chatEnterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chatMessageZone.requestLayout();
                TextView newView = (TextView)View.inflate(getBaseContext(), R.layout.chat_message, null);
				String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
                newView.setText(currentDateTimeString + ": " + chatEntry.getText());
                chatMessageZoneTable.addView(newView);
                chatEntry.setText("");
            }
        });
    }
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {

		for(int i = 0; i < chatMessageZoneTable.getChildCount(); i++){
			if (chatMessageZoneTable.getChildAt(i).getClass() == EditText.class){
				EditText text = (EditText)chatMessageZoneTable.getChildAt(i);
				savedInstanceState.putString(Integer.toString(i), text.getText().toString());
			}

		}
		savedInstanceState.putInt("ChatTableChildCount",chatMessageZoneTable.getChildCount());
		super.onSaveInstanceState(savedInstanceState);
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {

		super.onRestoreInstanceState(savedInstanceState);
		for(int i = 0 ; i < savedInstanceState.getInt("ChatTableChildCount"); i++ ){

			TextView newView = (TextView)View.inflate(getBaseContext(), R.layout.chat_message, null);
			newView.setText(savedInstanceState.getString(Integer.toString(i)));
			chatMessageZoneTable.addView((newView));
		}
	}

}


