package com.projet3.polypaint.UserLogin;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.Toast;

import com.projet3.polypaint.Chat.ChatFragment;
import com.projet3.polypaint.Chat.Conversation;
import com.projet3.polypaint.Network.FetchManager;
import com.projet3.polypaint.HomeActivity;
import com.projet3.polypaint.R;
import com.projet3.polypaint.Network.RequestManager;
import com.projet3.polypaint.Network.SocketManager;
import com.projet3.polypaint.Others.Utilities;

import java.util.ArrayList;

public class LoginActivity extends Activity  {

	//private final int CONNECT_DELAY = 5000;
	private final String AZURE_IP = "52.173.73.94";

	private final String IP = /*"192.168.43.201";*/
            "52.173.73.94";


	ImageButton userConnexionButton;
    ImageButton serverConnexionButton;
	EditText usernameEntry;
	EditText passwordEntry;
	//EditText ipEntry;
	RelativeLayout userModuleLayout;
	UserInformation userInformation;
	ProgressBar progressBar;
	Switch signUpSwitch;



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		initialize();
	}
	private void initialize() {
		userConnexionButton = (ImageButton)findViewById(R.id.connectUserButton);
		//serverConnexionButton = (ImageButton)findViewById(R.id.connectServerButton);
		usernameEntry = (EditText)findViewById(R.id.usernameEditText);
		passwordEntry = (EditText)findViewById(R.id.passwordEditText);
		//ipEntry = (EditText)findViewById(R.id.ipEditText);
		userModuleLayout = (RelativeLayout)findViewById(R.id.connexionLayout);
		progressBar = (ProgressBar)findViewById(R.id.loginProgressBar);
		progressBar.setVisibility(View.GONE);
		signUpSwitch = (Switch)findViewById(R.id.signUpSwitch);
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
				if (usernameEntry.getText().length() > 0 && passwordEntry.getText().length() > 0) {
					if (!signUpSwitch.isChecked()) {
						Utilities.changeButtonState(userConnexionButton, false);
						userInformation = new UserInformation(usernameEntry.getText().toString(), passwordEntry.getText().toString());
						if (RequestManager.currentInstance.requestLogin(userInformation)) {
							//FETCH
							progressBar.setVisibility(View.VISIBLE);
							RequestManager.currentInstance.fetchUserConversations();
							progressBar.setProgress(50);
							RequestManager.currentInstance.fetchUsers();
							progressBar.setProgress(75);
							SocketManager.currentInstance.sendUsername();
							progressBar.setProgress(100);
							android.content.Intent intent = new android.content.Intent(getBaseContext(), HomeActivity.class);
							//intent.putParcelableArrayListExtra("CONVERSATIONS", fetchedConversations);
							//intent.putExtra("USER_INFORMATION", userInformation);
							startActivity(intent);
						} else {
							Toast.makeText(getBaseContext(), getString(R.string.loginImpossibleConnection), Toast.LENGTH_LONG).show();
							Utilities.changeButtonState(userConnexionButton, true);
						}
					}
					else{
							Utilities.changeButtonState(userConnexionButton, false);
							if (RequestManager.currentInstance.postSignUp(usernameEntry.getText().toString(),
									passwordEntry.getText().toString())){
								Toast.makeText(getBaseContext(),getString(R.string.signupSucess),Toast.LENGTH_LONG).show();
							}
							else{
								Toast.makeText(getBaseContext(),getString(R.string.signupFail),Toast.LENGTH_LONG).show();
							}
							Utilities.changeButtonState(userConnexionButton, true);
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
}
