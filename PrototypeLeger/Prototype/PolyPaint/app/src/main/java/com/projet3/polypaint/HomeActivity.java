package com.projet3.polypaint;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
//import android.widget.Toolbar;
import android.support.v7.widget.Toolbar;

import com.projet3.polypaint.Chat.ChatFragment;
import com.projet3.polypaint.Chat.Conversation;
import com.projet3.polypaint.Chat.SocketManager;
import com.projet3.polypaint.Image.ImageEditingFragment;
import com.projet3.polypaint.User.UserInformation;
import com.projet3.polypaint.User.UserManager;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {

	private final String USER_INFORMATION_PARCELABLE_TAG = "USER_INFORMATION";
	private UserInformation userInformation;
	private  Toolbar mainToolbar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mainToolbar = (Toolbar)findViewById(R.id.mainToolbar);
		mainToolbar.setTitle("menu");
		setSupportActionBar(mainToolbar);
		if (savedInstanceState == null){
			//userInformation = getIntent().getExtras().getParcelable(USER_INFORMATION_PARCELABLE_TAG);
			//ArrayList convos = new ArrayList();
			//convos.add(new Conversation("convo1"));
			//convos.add(new Conversation("convo2"));

			FragmentManager manager = getFragmentManager();
			FragmentTransaction transaction = manager.beginTransaction();
			transaction.add(R.id.chatFragment, ChatFragment.newInstance(
					getIntent().getExtras().<Conversation>getParcelableArrayList("CONVERSATIONS")),"CHAT_FRAGMENT");
			transaction.addToBackStack(null);
			transaction.commit();
		}
	}



	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);
        return true;
    }
	@Override
	public void onBackPressed() {
		SocketManager.currentInstance.leave(UserManager.currentInstance.getUserUsername());
		startActivity(new android.content.Intent(getBaseContext(), LoginActivity.class));
	}
	// INTEGRATION
	public void gotoImageEditing(View button) {
		FragmentManager manager = getFragmentManager();
		FragmentTransaction transaction = manager.beginTransaction();
		transaction.add(R.id.imageEditingFragment,new ImageEditingFragment(),"EDITING_FRAGMENT");
		transaction.addToBackStack(null);
		transaction.commit();
	}
}


