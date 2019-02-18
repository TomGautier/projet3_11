package com.projet3.polypaint;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.projet3.polypaint.Chat.Chat;
import com.projet3.polypaint.Chat.Conversation;
import com.projet3.polypaint.Chat.SocketManager;
import com.projet3.polypaint.Image.ImageEditingActivity;

import java.util.ArrayList;

public class HomeActivity extends Activity  {
	public static Activity currentActivityInstance;

	private final String CHAT_BUNDLE_TAG = "chat";
	private final String USER_INFORMATION_PARCELABLE_TAG = "USER_INFORMATION";
	//private Chat chat;
	private UserInformation userInformation;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		currentActivityInstance = this;
		/*if (savedInstanceState == null) {
           // userInformation = getIntent().getExtras().getParcelable(USER_INFORMATION_PARCELABLE_TAG);
            //Chat.currentInstance = new Chat(userInformation);
        }
		else {
            //userInformation = savedInstanceState.getParcelable(USER_INFORMATION_PARCELABLE_TAG);
            //Chat.currentInstance = new Chat(userInformation, savedInstanceState.getBundle(CHAT_BUNDLE_TAG));
        }*/
		if (savedInstanceState == null){
			userInformation = getIntent().getExtras().getParcelable(USER_INFORMATION_PARCELABLE_TAG);
			ArrayList convos = new ArrayList();
			convos.add(new Conversation("convo1"));
			convos.add(new Conversation("convo2"));
			FragmentManager manager = getFragmentManager();
			FragmentTransaction transaction = manager.beginTransaction();
			transaction.add(R.id.chatFragment,Chat.newInstance(userInformation,convos),"CHAT_FRAGMENT");
			transaction.addToBackStack(null);
			transaction.commit();
			//SocketManager.currentInstance.setupNewMessageListener(Chat.currentInstance);
		}
	}



	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
		//savedInstanceState.putBundle(CHAT_BUNDLE_TAG, Chat.currentInstance.getChatBundle());
		//savedInstanceState.putParcelable(USER_INFORMATION_PARCELABLE_TAG,userInformation);
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
	// INTEGRATION
	public void gotoImageEditing(View button) {
        android.content.Intent intent = new android.content.Intent(getBaseContext(), ImageEditingActivity.class);
        startActivity(intent);
        //Bundle chatBundle = Chat.currentInstance.getChatBundle();
        //intent.putExtra(CHAT_BUNDLE_TAG, chatBundle);
        //intent.putExtra(USER_INFORMATION_PARCELABLE_TAG,userInformation);
	}
}


