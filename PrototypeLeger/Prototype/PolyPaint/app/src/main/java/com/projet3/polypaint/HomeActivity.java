package com.projet3.polypaint;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.pm.ActivityInfo;
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
import com.projet3.polypaint.User.RequestManager;
import com.projet3.polypaint.User.UserManager;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {

	//private final String USER_INFORMATION_PARCELABLE_TAG = "USER_INFORMATION";
	//private UserInformation userInformation;
    private final String CHAT_TAG = "CHAT_FRAGMENT";
    private final String IMAGE_EDITING_TAG = "IMAGE_EDITING_FRAGMENT";
	private final String USER_TABLE_TAG = "USER_TABLE_FRAGMENT";

	private  Toolbar mainToolbar;
	private FrameLayout chatFragmentLayout;
	private FrameLayout imageEditingFragmentLayout;
	private FrameLayout usersListFragmentLayout;

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
		usersListFragmentLayout = (FrameLayout)findViewById(R.id.usersTableFragment);



		if (savedInstanceState == null){
			createUsersTableFragment();
			createChatFragment();
			createImageEditingFragment();
			toggleImageEditingVisibility();
		}
	}

	private void createChatFragment() {
		FragmentManager manager = getFragmentManager();
		FragmentTransaction transaction = manager.beginTransaction();
		transaction.add(R.id.chatFragment, new ChatFragment(),CHAT_TAG);
		transaction.addToBackStack(null);
		transaction.commit();
	}
	private void createImageEditingFragment(){
		FragmentManager manager = getFragmentManager();
		FragmentTransaction transaction = manager.beginTransaction();
		transaction.add(R.id.imageEditingFragment,new ImageEditingFragment(),IMAGE_EDITING_TAG);
        transaction.addToBackStack(null);
        transaction.commit();
	}
	private void createUsersTableFragment(){
		FragmentManager manager = getFragmentManager();
		FragmentTransaction transaction = manager.beginTransaction();
		ArrayList<String> users = new ArrayList<>();
		users.add("Marcel");
		users.add("Marcel2");
		users.add("Marcel3");
		users.add("Marcel4");
		users.add("Marcel5");
		users.add("Marcel6");
		users.add("Marcel7");
		users.add("Marcel8");
		users.add("Marcel9");
		transaction.add(R.id.usersTableFragment,UserTableFragment.newInstance(users),USER_TABLE_TAG);
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
							case R.id.addConversationChatAction:
								createAddConversationPopup();
								break;
							case R.id.removeConversationChatAction:
								createRemoveConversationPopup();
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
	private void createRemoveConversationPopup(){
		LayoutInflater inflater = (LayoutInflater)
				getSystemService(LAYOUT_INFLATER_SERVICE);
		int width = LinearLayout.LayoutParams.WRAP_CONTENT;
		int height = LinearLayout.LayoutParams.WRAP_CONTENT;
		boolean focusable = true;
		final View popupView = inflater.inflate(R.layout.remove_conversation_popup, null);
		final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);
		popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);
		popupView.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (v != popupView){
					popupWindow.dismiss();
				}
				return true;
			}
		});
		final Spinner spinner = (Spinner)popupView.findViewById(R.id.removeConversationSpinner);
		android.widget.ArrayAdapter<String> spinnerArrayAdapter = new android.widget.ArrayAdapter
				(HomeActivity.this, android.R.layout.simple_spinner_item, UserManager.currentInstance.getUserConversationsNames());
		spinnerArrayAdapter.setDropDownViewResource(android.R.layout
				.simple_spinner_dropdown_item);
		spinner.setAdapter(spinnerArrayAdapter);
		Button button = (Button)popupView.findViewById(R.id.removeConversationButton);
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String conversation = spinner.getSelectedItem().toString();
				UserManager.currentInstance.removeUserConversation(conversation);
				SocketManager.currentInstance.leaveConversation(conversation);
				((ChatFragment)getFragmentManager().findFragmentByTag(CHAT_TAG)).setupChatConversationSpinner();
				popupWindow.dismiss();
			}
		});
	}

    private void createAddConversationPopup(){
		LayoutInflater inflater = (LayoutInflater)
				getSystemService(LAYOUT_INFLATER_SERVICE);
		int width = LinearLayout.LayoutParams.WRAP_CONTENT;
		int height = LinearLayout.LayoutParams.WRAP_CONTENT;
		boolean focusable = true;
		final View popupView = inflater.inflate(R.layout.add_conversation_popup, null);
		final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);
		popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);
		popupView.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (v != popupView){
					popupWindow.dismiss();
				}
				return true;
			}
		});
		Button button = (Button)popupView.findViewById(R.id.addConversationButton);
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
                String name = ((EditText)popupView.findViewById(R.id.addConversationEditText)).getText().toString();
			    if (!name.isEmpty()){
					boolean ret = RequestManager.currentInstance.addUserConversation(name);
			    	if (ret){
						UserManager.currentInstance.addUserConversation(name);
						((ChatFragment)getFragmentManager().findFragmentByTag(CHAT_TAG)).setupChatConversationSpinner();
						Toast.makeText(getBaseContext(),"Conversation " + name + " cree",Toast.LENGTH_SHORT).show();
						popupWindow.dismiss();
					}
					else
						Toast.makeText(getBaseContext(),"Cette conversation existe deja",Toast.LENGTH_SHORT).show();
				}
                else{
                    Toast.makeText(getBaseContext(),"Veuillez entrer un nom de conversation valide",Toast.LENGTH_SHORT).show();
                }
			}
		});
	}

	@Override
	public void onBackPressed() {
		SocketManager.currentInstance.leave(UserManager.currentInstance.getUserUsername());
		startActivity(new android.content.Intent(getBaseContext(), LoginActivity.class));
	}
}


