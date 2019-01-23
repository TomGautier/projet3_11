package com.projet3.polypaint;

import android.app.Activity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;

public class ChatActivity extends Activity {

	private EditText chatEntry;
	private ImageButton chatEnterButton;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat);
		Initialize();
	}

	private void Initialize(){
		chatEntry = (EditText)findViewById(R.id.chatEditText);
		chatEnterButton = (ImageButton)findViewById(R.id.chatEnterButton);
		Utilities.buttonEffect(chatEnterButton);
		//etc...
	}
}
