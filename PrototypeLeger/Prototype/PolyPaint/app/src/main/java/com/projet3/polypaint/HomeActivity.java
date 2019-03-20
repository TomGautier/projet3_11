package com.projet3.polypaint;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
//import android.widget.Toolbar;
import android.support.v7.widget.Toolbar;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.projet3.polypaint.Chat.ChatFragment;
import com.projet3.polypaint.DrawingCollabSession.CollabImageEditingFragment;
import com.projet3.polypaint.DrawingSession.ImageEditingFragment;
import com.projet3.polypaint.Network.SocketManager;
import com.projet3.polypaint.UserLogin.LoginActivity;
import com.projet3.polypaint.UserLogin.UserManager;
import com.projet3.polypaint.UserList.UsersListFragment;

public class HomeActivity extends AppCompatActivity {

	//private final String USER_INFORMATION_PARCELABLE_TAG = "USER_INFORMATION";
	//private UserInformation userInformation;
    private final String CHAT_TAG = "CHAT_FRAGMENT";
    private final String IMAGE_EDITING_TAG = "IMAGE_EDITING_FRAGMENT";
	private final String USER_TABLE_TAG = "USER_TABLE_FRAGMENT";
	private final String COLLAB_EDITING_TAG = "COLLAB_IMAGE_EDITING_FRAGMENT";

	private  Toolbar mainToolbar;
	private FrameLayout chatFragmentLayout;
	private FrameLayout imageEditingFragmentLayout;
	private FrameLayout collabImageEditingFragmentLayout;
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
		collabImageEditingFragmentLayout = (FrameLayout)findViewById(R.id.collabImageEditingFragment);
		usersListFragmentLayout = (FrameLayout)findViewById(R.id.usersTableFragment);

		if (savedInstanceState == null){
			createUsersListFragment();
			createChatFragment();
			createImageEditingFragment();
			toggleImageEditingVisibility();
			createCollabImageEditingFragment();
			toggleCollabImageEditingVisibility();
		}
		//CollabShapeProperties properties = new CollabShapeProperties("UmlClass","white","black",new int[] {1,2},200,300,0);
		//CollabShape shape = new CollabShape("id","MockSessionId","Tristan",properties);
		//SocketManager.currentInstance.modifyElements(new CollabShape[] {shape,shape,shape});

	}

	private void createChatFragment() {
		FragmentManager manager = getFragmentManager();
		FragmentTransaction transaction = manager.beginTransaction();
		transaction.add(R.id.chatFragment, new ChatFragment(),CHAT_TAG);
		transaction.addToBackStack(null);
		transaction.commit();
	}
	private void createUsersListFragment(){
		FragmentManager manager = getFragmentManager();
		FragmentTransaction transaction = manager.beginTransaction();
		transaction.add(R.id.usersTableFragment, new UsersListFragment(),USER_TABLE_TAG);
		transaction.addToBackStack(null);
		transaction.commit();
		//ArrayList<String> users = new ArrayList<>();
		/*users.add("Marcel");
		users.add("Marcel2");
		users.add("Marcel3");
		users.add("Marcel4");
		users.add("Marcel5");
		users.add("Marcel6");
		users.add("Marcel7");
		users.add("Marcel8");
		users.add("Marcel9");*/

	}

	private void createImageEditingFragment(){
		FragmentManager manager = getFragmentManager();
		FragmentTransaction transaction = manager.beginTransaction();
		transaction.add(R.id.imageEditingFragment,new ImageEditingFragment(),IMAGE_EDITING_TAG);
        transaction.addToBackStack(null);
        transaction.commit();
	}
	private void createCollabImageEditingFragment(){
		FragmentManager manager = getFragmentManager();
		FragmentTransaction transaction = manager.beginTransaction();
		transaction.add(R.id.collabImageEditingFragment, new CollabImageEditingFragment(),COLLAB_EDITING_TAG);
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
				SocketManager.currentInstance.leave(UserManager.currentInstance.getUserUsername());
				startActivity(new android.content.Intent(getBaseContext(), LoginActivity.class));
				break;
			case R.id.galleryAction:
				break;
			/*case R.id.chatAction:
				PopupMenu dropDownMenu = new PopupMenu(getApplicationContext(), findViewById(R.id.chatAction));
				dropDownMenu.getMenuInflater().inflate(R.menu.users_list_connected_entry_menu, dropDownMenu.getMenu());
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
				toggleChatVisibility();
				break;*/
			case R.id.imageEditingAction:
				toggleImageEditingVisibility();
				break;
			case R.id.collabImageEditingAction:
				toggleCollabImageEditingVisibility();
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
	private void toggleCollabImageEditingVisibility(){
		if (collabImageEditingFragmentLayout.getVisibility() == View.VISIBLE)
			collabImageEditingFragmentLayout.setVisibility(View.GONE);
		else
			collabImageEditingFragmentLayout.setVisibility(View.VISIBLE);
	}

	/*private void toggleChatVisibility(){
		if (chatFragmentLayout.getVisibility() == View.VISIBLE)
			chatFragmentLayout.setVisibility(View.GONE);
		else
			chatFragmentLayout.setVisibility(View.VISIBLE);
	}*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_menu, menu);
		return true;

    }

	@Override
	public void onBackPressed() {
		SocketManager.currentInstance.leave(UserManager.currentInstance.getUserUsername());
		startActivity(new android.content.Intent(getBaseContext(), LoginActivity.class));
	}
}


