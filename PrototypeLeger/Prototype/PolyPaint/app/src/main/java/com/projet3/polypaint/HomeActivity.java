package com.projet3.polypaint;

import android.app.Activity;
import android.os.Bundle;

import com.projet3.polypaint.Chat.Chat;

public class HomeActivity extends Activity  {
	private Chat chat;
	private UserInformation userInformation;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		if (savedInstanceState == null) {
            userInformation = getIntent().getExtras().getParcelable("USER_INFORMATION");
            chat = new Chat(this, userInformation);
        }
		else {
            userInformation = savedInstanceState.getParcelable("USER_INFORMATION");
            chat = new Chat(this, userInformation, savedInstanceState.getBundle("chat"));
        }
	}



	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		// save chat history
		//savedInstanceState.putStringArray("chatHistory", chat.chatHistory.toArray(new String[chat.chatHistory.size()]));
		super.onSaveInstanceState(savedInstanceState);
		savedInstanceState.putBundle("chat", chat.GetChatBundle());
		savedInstanceState.putParcelable("USER_INFORMATION",userInformation);
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
	}

}


