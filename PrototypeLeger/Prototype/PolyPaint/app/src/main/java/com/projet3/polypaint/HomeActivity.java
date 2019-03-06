package com.projet3.polypaint;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
//import android.widget.Toolbar;
import android.support.v7.widget.Toolbar;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.Toast;

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
	private FrameLayout chatFragmentLayout;
	private FrameLayout imageEditingFragmentLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		mainToolbar = (Toolbar)findViewById(R.id.mainToolbar);
		mainToolbar.setTitle("PolyPaint");
		setSupportActionBar(mainToolbar);
		chatFragmentLayout = (FrameLayout)findViewById(R.id.chatFragment);
		imageEditingFragmentLayout = (FrameLayout)findViewById(R.id.imageEditingFragment);
		if (savedInstanceState == null){
			createChatFragment();
			createImageEditingFragment();
		}
	}

	private void createChatFragment() {
		FragmentManager manager = getFragmentManager();
		FragmentTransaction transaction = manager.beginTransaction();
		transaction.add(R.id.chatFragment, ChatFragment.newInstance(
				getIntent().getExtras().<Conversation>getParcelableArrayList("CONVERSATIONS")),"CHAT_FRAGMENT");
		transaction.addToBackStack(null);
		transaction.commit();
	}
	private void createImageEditingFragment(){
		FragmentManager manager = getFragmentManager();
		FragmentTransaction transaction = manager.beginTransaction();
		transaction.add(R.id.imageEditingFragment,new ImageEditingFragment(),"EDITING_FRAGMENT");
		transaction.addToBackStack(null);
		transaction.commit();
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
		switch(item.getItemId()) {
			case R.id.menuAbout:
				Toast.makeText(this, "You clicked about", Toast.LENGTH_SHORT).show();
				break;

			case R.id.menuSettings:
				Toast.makeText(this, "You clicked settings", Toast.LENGTH_SHORT).show();
				break;

			case R.id.menuLogout:
				Toast.makeText(this, "You clicked logout", Toast.LENGTH_SHORT).show();
				break;
			case R.id.galleryAction:
				break;
			case R.id.chatAction:
				PopupMenu dropDownMenu = new PopupMenu(getApplicationContext(), findViewById(R.id.chatAction));
				dropDownMenu.getMenuInflater().inflate(R.menu.chat_menu, dropDownMenu.getMenu());
				dropDownMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
					@Override
					public boolean onMenuItemClick(MenuItem menuItem) {
						switch(menuItem.getItemId()){
							case R.id.newConversationChatAction:
								createNewConversationPopup();
								break;
							case R.id.hideShowChatAction:
								toggleChatVisibility();
								break;
						}
						return true;
					}
				});
				dropDownMenu.show();
				break;
			case R.id.imageEditingAction:
				toggleImageEditingVisibility();
				break;
		}
		return true;
	}
	private void toggleImageEditingVisibility(){
		if (imageEditingFragmentLayout.getVisibility() == View.VISIBLE)
			imageEditingFragmentLayout.setVisibility(View.GONE);
		else
			imageEditingFragmentLayout.setVisibility(View.VISIBLE);
	}

	private void toggleChatVisibility(){
		if (chatFragmentLayout.getVisibility() == View.VISIBLE)
			chatFragmentLayout.setVisibility(View.GONE);
		else
			chatFragmentLayout.setVisibility(View.VISIBLE);
	}
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_menu, menu);
		return true;

    }

    private void createNewConversationPopup(){
		LayoutInflater inflater = (LayoutInflater)
				getSystemService(LAYOUT_INFLATER_SERVICE);
		final View popupView = inflater.inflate(R.layout.new_conversation_popup, null);
		Button button = (Button)popupView.findViewById(R.id.newConversationButton);
		button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getBaseContext(),"nouvelle conversation cree",Toast.LENGTH_LONG).show();
            }
        });

		// create the popup window
		int width = LinearLayout.LayoutParams.WRAP_CONTENT;
		int height = LinearLayout.LayoutParams.WRAP_CONTENT;
		boolean focusable = true; // lets taps outside the popup also dismiss it
		final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

		// show the popup window
		// which view you pass in doesn't matter, it is only used for the window tolken
		popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);

		// dismiss the popup window when touched
		popupView.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (v != popupView){
					popupWindow.dismiss();
				}
				return true;
			}
		});
	}
	@Override
	public void onBackPressed() {
		SocketManager.currentInstance.leave(UserManager.currentInstance.getUserUsername());
		startActivity(new android.content.Intent(getBaseContext(), LoginActivity.class));
	}
	// INTEGRATION
	/*public void gotoImageEditing(View button) {
		FragmentManager manager = getFragmentManager();
		FragmentTransaction transaction = manager.beginTransaction();
		transaction.add(R.id.imageEditingFragment,new ImageEditingFragment(),"EDITING_FRAGMENT");
		transaction.addToBackStack(null);
		transaction.commit();
	}*/
}


