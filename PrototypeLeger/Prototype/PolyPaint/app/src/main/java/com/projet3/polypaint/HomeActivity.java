package com.projet3.polypaint;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
//import android.widget.Toolbar;
import android.support.v7.widget.Toolbar;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.PopupMenu;
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
			case R.id.GalleryAction:
				break;
			case R.id.ChatAction:
				//toggleChatVisibility();
				break;
			case R.id.ImageEditingAction:
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
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);
        final MenuItem item = menu.findItem(R.id.ChatAction);
		final Button button = (Button)item.getActionView();
		/*button.setBackground(R.style.Widget_AppCompat_Button_Borderless);
		style="@style/Widget.AppCompat.Button.Borderless"*/
		//button.setTextAppearance(R.style.Widget_AppCompat_Button_Borderless);
		button.setText(item.getTitle());
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				PopupMenu dropDownMenu = new PopupMenu(getApplicationContext(), button);
				dropDownMenu.getMenuInflater().inflate(R.menu.chat_menu, dropDownMenu.getMenu());
				//button.setText(item.getTitle().toString());
				dropDownMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

					@Override
					public boolean onMenuItemClick(MenuItem menuItem) {
						Toast.makeText(getApplicationContext(), "You have clicked " + menuItem.getTitle(), Toast.LENGTH_LONG).show();
						return true;
					}
				});
				dropDownMenu.show();
			}
		});


		/*MenuItem item = menu.findItem(R.id.ChatAction);
		Spinner spinner = (Spinner) item.getActionView();

		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.testArray, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		spinner.setAdapter(adapter);
		spinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

			}
		})*/
        return true;
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


