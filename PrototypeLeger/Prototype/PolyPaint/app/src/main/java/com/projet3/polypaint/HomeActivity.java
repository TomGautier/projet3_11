package com.projet3.polypaint;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.MenuItem;

import com.projet3.polypaint.Chat.Chat;
import com.projet3.polypaint.Chat.SocketManager;

public class HomeActivity extends Activity  {

	private final String CHAT_BUNDLE_TAG = "chat";
	private final String USER_INFORMATION_PARCELABLE_TAG = "USER_INFORMATION";
	private Chat chat;
	private UserInformation userInformation;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		if (savedInstanceState == null) {
            userInformation = getIntent().getExtras().getParcelable(USER_INFORMATION_PARCELABLE_TAG);
            chat = new Chat(this, userInformation);
        }
		else {
            userInformation = savedInstanceState.getParcelable(USER_INFORMATION_PARCELABLE_TAG);
            chat = new Chat(this, userInformation, savedInstanceState.getBundle(CHAT_BUNDLE_TAG));
        }
        SocketManager.currentInstance.setupNewMessageListener(chat);
	}



	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
		savedInstanceState.putBundle(CHAT_BUNDLE_TAG, chat.getChatBundle());
		savedInstanceState.putParcelable(USER_INFORMATION_PARCELABLE_TAG,userInformation);
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				SocketManager.currentInstance.leave(userInformation.getUsername());
				startActivity(new android.content.Intent(getBaseContext(), LoginActivity.class));
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	@Override
	public void onBackPressed() {
		SocketManager.currentInstance.leave(userInformation.getUsername());
		startActivity(new android.content.Intent(getBaseContext(), LoginActivity.class));
	}

}


