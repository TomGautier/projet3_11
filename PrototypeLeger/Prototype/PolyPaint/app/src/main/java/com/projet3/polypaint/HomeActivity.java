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
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.projet3.polypaint.Chat.ChatFragment;
import com.projet3.polypaint.Gallery.GalleryFragment;
import com.projet3.polypaint.DrawingCollabSession.CollabImageEditingFragment;
import com.projet3.polypaint.DrawingSession.ImageEditingFragment;
import com.projet3.polypaint.ImageAccessibility.AccessibilityManager;
import com.projet3.polypaint.Network.FetchManager;
import com.projet3.polypaint.Network.RequestManager;
import com.projet3.polypaint.Network.SocketManager;
import com.projet3.polypaint.UserLogin.LoginActivity;
import com.projet3.polypaint.UserList.UsersListFragment;

public class HomeActivity extends AppCompatActivity implements AccessibilityManager.AccessibilityDialogSubscriber, HomeActivityListener {

	//private final String USER_INFORMATION_PARCELABLE_TAG = "USER_INFORMATION";
	//private UserInformation userInformation;
    private final String CHAT_TAG = "CHAT_FRAGMENT";
    private final String IMAGE_EDITING_TAG = "IMAGE_EDITING_FRAGMENT";
	private final String USER_TABLE_TAG = "USER_TABLE_FRAGMENT";
	private final String GALLERY_TAG = "GALLERY_FRAGMENT";
	private final String COLLAB_EDITING_TAG = "COLLAB_IMAGE_EDITING_FRAGMENT";

	private  Toolbar mainToolbar;
	private FrameLayout chatFragmentLayout;
	private FrameLayout imageEditingFragmentLayout;
	private FrameLayout galleryFragmentLayout;
	private FrameLayout collabImageEditingFragmentLayout;
	private FrameLayout usersListFragmentLayout;

	private CollabImageEditingFragment collabImageEditingFragment;
	private GalleryFragment galleryFragment;

	private String sessionPassword = "";
	private String imageId = "";

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
		galleryFragmentLayout = (FrameLayout)findViewById(R.id.galleryFragment);
		collabImageEditingFragmentLayout = (FrameLayout)findViewById(R.id.collabImageEditingFragment);
		usersListFragmentLayout = (FrameLayout)findViewById(R.id.usersTableFragment);

		AccessibilityManager.getInstance().subscribe(this);

		if (savedInstanceState == null){
			createUsersListFragment();
			createChatFragment();
			createImageEditingFragment();
			toggleImageEditingVisibility();
			toggleCollabImageEditingVisibility();
			createGalleryFragment();
		}
		/*int[] position = {1,2};
		CollabShapeProperties properties = new CollabShapeProperties("UmlClass","white","black",position,200,300,0);
		CollabShape shape = new CollabShape("id","MockSessionId","Tristan",properties);*/
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
	}

	private void createImageEditingFragment(){
		FragmentManager manager = getFragmentManager();
		FragmentTransaction transaction = manager.beginTransaction();
		transaction.add(R.id.imageEditingFragment,new ImageEditingFragment(),IMAGE_EDITING_TAG);
        transaction.addToBackStack(null);
        transaction.commit();
	}
	private void createCollabImageEditingFragment(String imageId, String visibility, String password){
		FragmentManager manager = getFragmentManager();
		FragmentTransaction transaction = manager.beginTransaction();

		collabImageEditingFragment = CollabImageEditingFragment.newInstance(imageId, visibility, password);
		transaction.replace(R.id.collabImageEditingFragment, collabImageEditingFragment, COLLAB_EDITING_TAG);
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
	public void onStop() {
		AccessibilityManager.getInstance().unsubscribe(this);
		super.onStop();
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
				SocketManager.currentInstance.leave(FetchManager.currentInstance.getUserUsername());
				startActivity(new android.content.Intent(getBaseContext(), LoginActivity.class));
				break;
			case R.id.galleryAction:
				toggleGalleryVisibility();
				break;
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
		else {
			imageEditingFragmentLayout.setVisibility(View.VISIBLE);
		}
	}
	private void toggleCollabImageEditingVisibility(){
		if (collabImageEditingFragmentLayout.getVisibility() == View.VISIBLE)
			collabImageEditingFragmentLayout.setVisibility(View.GONE);
		else {
			AccessibilityManager.getInstance().showAccessibilityDialog(getFragmentManager());
			//createCollabImageEditingFragment(null);
			//collabImageEditingFragmentLayout.setVisibility(View.VISIBLE);
		}
	}
	public void joinCollabEditingSession(String imageId) {
		sessionPassword = RequestManager.currentInstance.getImagePassword(imageId);
		this.imageId = imageId;
		if (sessionPassword == null || sessionPassword.isEmpty()) {
			createCollabImageEditingFragment(imageId, null, null);
			collabImageEditingFragmentLayout.setVisibility(View.VISIBLE);
		}
		else AccessibilityManager.getInstance().showPasswordDialog(getFragmentManager());
	}

	private void createGalleryFragment(){
		FragmentManager manager = getFragmentManager();
		FragmentTransaction transaction = manager.beginTransaction();
		galleryFragment = new GalleryFragment();
		transaction.add(R.id.galleryFragment, galleryFragment, GALLERY_TAG);
		transaction.addToBackStack(null);
		transaction.commit();
	}
	public void toggleGalleryVisibility(){
		if (galleryFragmentLayout.getVisibility() == View.VISIBLE)
			galleryFragmentLayout.setVisibility(View.GONE);
		else {
			galleryFragmentLayout.setVisibility(View.VISIBLE);
			galleryFragment.refresh();
		}
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
		SocketManager.currentInstance.leave(FetchManager.currentInstance.getUserUsername());
		startActivity(new android.content.Intent(getBaseContext(), LoginActivity.class));
	}

	@Override
	public void onAccessibilityPositiveResponse(boolean isPrivate, boolean isProtected, String password) {
		String privacy = isPrivate ? "private" : "public";
		String pass = isProtected ? password : "";
    	createCollabImageEditingFragment(null, privacy, pass);
		collabImageEditingFragmentLayout.setVisibility(View.VISIBLE);
	}

	@Override
	public void onAccessibilityNegativeResponse() {
		// Do nothing
	}

	@Override
	public void onPasswordPositiveResponse(String password) {
    	System.out.println("Passwords : " + password + " - " + sessionPassword);
		if (!imageId.isEmpty() && password.equals(sessionPassword)) {
			createCollabImageEditingFragment(imageId, null, null);
			collabImageEditingFragmentLayout.setVisibility(View.VISIBLE);
		}
		else {
			toggleGalleryVisibility();
			Toast.makeText(this, "Mot de passe incorrect", Toast.LENGTH_SHORT).show();
		}
	}
    @Override
	public void onPasswordNegativeResponse(){
    	toggleGalleryVisibility();
	}

	@Override
	public void onInviteToDrawingSession(final String from, final String imageId) {
		LayoutInflater inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
		int width = LinearLayout.LayoutParams.WRAP_CONTENT;
		int height = LinearLayout.LayoutParams.WRAP_CONTENT;
		boolean focusable = true;
		final View popupView = inflater.inflate(R.layout.response_to_invite, null);
		TextView text = (TextView)popupView.findViewById(R.id.inviteToTextView);
		text.setText(from + " vous a invité à la session collaborative " + imageId);

		final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);
		popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);
		popupView.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (v != popupView){
					SocketManager.currentInstance.sendResponseToDrawingSessionInvitation(from,imageId,false);
					popupWindow.dismiss() ;
				}
				return true;
			}
		});
		ImageButton acceptButton = (ImageButton)popupView.findViewById(R.id.acceptButton);
		acceptButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				SocketManager.currentInstance.sendResponseToDrawingSessionInvitation(from,imageId,true);
				popupWindow.dismiss() ;
			}
		});
		ImageButton declineButton = (ImageButton)popupView.findViewById(R.id.declineButton);
		declineButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				SocketManager.currentInstance.sendResponseToDrawingSessionInvitation(from,imageId,false);
				popupWindow.dismiss() ;
			}
		});
	}

	@Override
	public void onResponseToDrawingSessionInvitation(String username, String imageId, boolean response) {
		if (response){
			//ECRIRE UN MESSAGE QUE LE USER A ACCEPTE L'INVITATION
			Toast.makeText(getBaseContext(),"",Toast.LENGTH_LONG).show();
		}
	}
}


