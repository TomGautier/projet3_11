package com.projet3.polypaint.UserLogin;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.projet3.polypaint.Chat.ChatFragment;
import com.projet3.polypaint.Chat.Conversation;
import com.projet3.polypaint.HomeActivity;
import com.projet3.polypaint.R;
import com.projet3.polypaint.SocketManager;
import com.projet3.polypaint.Others.Utilities;

import java.util.ArrayList;

public class LoginActivity extends Activity  {

	//private final int CONNECT_DELAY = 5000;
	private final String AZURE_IP = "40.122.119.160";
	private final String IP = "10.200.26.216";



	ImageButton userConnexionButton;
    ImageButton serverConnexionButton;
	EditText usernameEntry;
	EditText passwordEntry;
	//EditText ipEntry;
	RelativeLayout userModuleLayout;
	UserInformation userInformation;



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		initialize();
	}
	private void initialize() {
		userConnexionButton = (ImageButton)findViewById(R.id.connectUserButton);
		//serverConnexionButton = (ImageButton)findViewById(R.id.connectServerButton);
		usernameEntry = (EditText)findViewById(R.id.usernameEditText);
		passwordEntry = (EditText)findViewById(R.id.passwordEditText);
		//ipEntry = (EditText)findViewById(R.id.ipEditText);
		userModuleLayout = (RelativeLayout)findViewById(R.id.connexionLayout);

		RequestManager.currentInstance = new RequestManager(IP);

		//if (SocketManager.currentInstance != null && SocketManager.currentInstance.isConnected())
		//	changeToUserUI();





		setUserLoginButton();
		//setServerLoginButton();

        //Utilities.SetButtonEffect(serverConnexionButton);
		Utilities.setButtonEffect(userConnexionButton);
	}

	private void setUserLoginButton() {
		userConnexionButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (usernameEntry.getText().length() > 0 && passwordEntry.getText().length() > 0){
					Utilities.changeButtonState(userConnexionButton,false);
					userInformation = new UserInformation(usernameEntry.getText().toString(), passwordEntry.getText().toString());

					if (RequestManager.currentInstance.requestLogin(userInformation)) {
						ArrayList<Conversation> fetchedConversations = RequestManager.currentInstance.fetchUserConversations(userInformation);
						android.content.Intent intent = new android.content.Intent(getBaseContext(), HomeActivity.class);
						intent.putParcelableArrayListExtra("CONVERSATIONS", fetchedConversations);
						//intent.putExtra("USER_INFORMATION", userInformation);
						startActivity(intent);
					}
					else{
						Toast.makeText(getBaseContext(), getString(R.string.loginUserAlreadyExistsToast),Toast.LENGTH_LONG).show();
						Utilities.changeButtonState(userConnexionButton,true);
					}
				}
				else
					Toast.makeText(getBaseContext(), getString(R.string.loginToast), Toast.LENGTH_LONG).show();
			}
		});
	}


	/*private void setServerLoginButton() {
		serverConnexionButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

				String ipAddress = ipEntry.getText().toString();
				if (isIPAddress(ipAddress)) {
					SocketManager.currentInstance = new SocketManager(ipAddress);
					handleSocketConnect();
                    changeIpModuleState(false);
				}
				else
					Toast.makeText(getBaseContext(), getString(R.string.loginInvalidIp), Toast.LENGTH_LONG).show();

			}
		});
	}

    public boolean isIPAddress(String ipAddress) {
        String PATTERN = "^((0|1\\d?\\d?|2[0-4]?\\d?|25[0-5]?|[3-9]\\d?)\\.){3}(0|1\\d?\\d?|2[0-4]?\\d?|25[0-5]?|[3-9]\\d?)$";
        return ipAddress.matches(PATTERN);
    }

    private void handleSocketConnect(){
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(SocketManager.currentInstance.isConnected()){
                    Toast.makeText(getBaseContext(), getString(R.string.loginSuccessSocketConnect), Toast.LENGTH_LONG).show();
                    RequestManager.currentInstance = new RequestManager(ipEntry.getText().toString());
                    changeToUserUI();
                }
                else{
					changeIpModuleState(true);
					Toast.makeText(getBaseContext(), getString(R.string.loginFailedSocketConnect), Toast.LENGTH_LONG).show();
				}
            }
        }, CONNECT_DELAY);
    }

	private void changeIpModuleState(boolean state) {
		serverConnexionButton.setEnabled(state);
		ipEntry.setEnabled(state);
		if (state)
			serverConnexionButton.clearColorFilter();
		else
			serverConnexionButton.setColorFilter(Color.GRAY);
	}
    private void changeToUserUI() {
        changeIpModuleState(false);
        ipEntry.setBackgroundColor(Color.GREEN);
        userModuleLayout.setVisibility(View.VISIBLE);
    }*/
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
	}

	/*Fonction temporaire pour passer directement à l'édition d'images*/
	public void gotoImageEditing(View button) {
		SocketManager.currentInstance = new SocketManager("122123","");
		UserManager.currentInstance = new UserManager(new UserInformation("allo", "allo"));
		UserManager.currentInstance.setUserConversations(new ArrayList<Conversation>());
		FragmentManager manager = getFragmentManager();
		FragmentTransaction transaction = manager.beginTransaction();
		transaction.add(R.id.chatFragment,new ChatFragment(),"CHAT_FRAGMENT");
		transaction.addToBackStack(null);
		transaction.commit();

	}
}
