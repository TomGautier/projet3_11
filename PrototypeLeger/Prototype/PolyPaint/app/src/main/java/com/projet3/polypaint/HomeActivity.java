package com.projet3.polypaint;

import android.app.Activity;
import android.os.Bundle;

import com.projet3.polypaint.Chat.Chat;
import com.projet3.polypaint.Chat.SocketManager;

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
		SocketManager.currentInstance.setupNewMessageListener(chat);
	}



	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
		savedInstanceState.putBundle("chat", chat.GetChatBundle());
		savedInstanceState.putParcelable("USER_INFORMATION",userInformation);
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
	}
	@Override
	public void onDestroy() {
		super.onDestroy();
		SocketManager.currentInstance.leave(userInformation.username);
	}

}


