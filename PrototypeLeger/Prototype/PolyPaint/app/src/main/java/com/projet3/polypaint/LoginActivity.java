package com.projet3.polypaint;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.projet3.polypaint.Chat.SocketManager;

public class LoginActivity extends Activity  {

	ImageButton userConnexionButton;
    ImageButton serverConnexionButton;
	EditText usernameEntry;
	EditText passwordEntry;
	EditText ipEntry;
	ProgressBar progressBar;
	RelativeLayout userEntriesLayout;
	UserInformation userInformation;
	LoginManager loginManager;
	SocketManager socketManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		initialize();
	}
	private void initialize() {
		userConnexionButton = (ImageButton)findViewById(R.id.connectUserButton);
		serverConnexionButton = (ImageButton)findViewById(R.id.connectServerButton);
		usernameEntry = (EditText)findViewById(R.id.usernameEditText);
		passwordEntry = (EditText)findViewById(R.id.passwordEditText);
		progressBar = (ProgressBar)findViewById(R.id.loginProgressBar);
		ipEntry = (EditText)findViewById(R.id.ipEditText);
		userEntriesLayout = (RelativeLayout)findViewById(R.id.connexionLayout);
		userEntriesLayout.setVisibility(View.GONE);
		progressBar.setVisibility(View.GONE);

		setUserLoginButton();
		setServerLoginButton();

        Utilities.SetButtonEffect(serverConnexionButton);
		Utilities.SetButtonEffect(userConnexionButton);
	}

	private void setUserLoginButton() {
		userConnexionButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (usernameEntry.getText().length() > 0 && passwordEntry.getText().length() > 0){
					userInformation = new UserInformation(usernameEntry.getText().toString(), passwordEntry.getText().toString());

					progressBar.setVisibility(View.VISIBLE);
					socketManager.verifyUser(userInformation.username);
					while(loginManager.waitingForResponse()) {}

					if (loginManager.isLogged()){
						android.content.Intent intent = new android.content.Intent(getBaseContext(), HomeActivity.class);
						intent.putExtra("USER_INFORMATION", userInformation);
						startActivity(intent);
					}
					else{
						Toast.makeText(getBaseContext(), getString(R.string.loginUserAlreadyExistsToast),Toast.LENGTH_LONG).show();
						loginManager.reset();
						progressBar.setVisibility(View.GONE);
					}
				}
				else
					Toast.makeText(getBaseContext(), getString(R.string.loginToast), Toast.LENGTH_LONG).show();
			}
		});
	}

	private void setServerLoginButton() {
		serverConnexionButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				String ipAddress = ipEntry.getText().toString();
				if (isIPAddress(ipAddress)) {
					if (socketManager == null || !socketManager.isConnected() ){
						socketManager = new SocketManager(ipAddress);
						handleSocketConnect();
					}
				}
				else
					Toast.makeText(getBaseContext(), getString(R.string.loginInvalidIp), Toast.LENGTH_LONG).show();
			}
		});
	}
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
	}

	public boolean isIPAddress(String ipAddress) {
		if (ipAddress.isEmpty())
			return false;
		String[] octals = ipAddress.split("\\.");
		if (octals != null && octals.length == 4) {
			for (int i = 0; i < octals.length; i++) {
				if (octals[i].isEmpty() || Integer.parseInt(octals[i]) < 0 || Integer.parseInt(octals[i]) > 255) {
					return false;
				}
			}
			return true;
		}
		return false;

	}

	private void handleSocketConnect(){
		final Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				if(socketManager.isConnected()){
					Toast.makeText(getBaseContext(), getString(R.string.loginSuccessSocketConnect), Toast.LENGTH_LONG).show();
					loginManager = new LoginManager();
					socketManager.setupLoginListener(loginManager);
					userEntriesLayout.setVisibility(View.VISIBLE);
				}
				else
					Toast.makeText(getBaseContext(), getString(R.string.loginFailedSocketConnect), Toast.LENGTH_LONG).show();
			}
		}, 1500);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		SocketManager.currentInstance.leave(userInformation.username);
	}


}
