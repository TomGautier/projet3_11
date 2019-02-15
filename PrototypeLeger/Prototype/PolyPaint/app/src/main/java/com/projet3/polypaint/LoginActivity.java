package com.projet3.polypaint;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.projet3.polypaint.Chat.SocketManager;
import com.projet3.polypaint.Image.ImageEditingActivity;

import java.net.InetAddress;

public class LoginActivity extends Activity  {

	private final int CONNECT_DELAY = 5000;

	ImageButton userConnexionButton;
    ImageButton serverConnexionButton;
	EditText usernameEntry;
	EditText passwordEntry;
	EditText ipEntry;
	RelativeLayout userModuleLayout;
	RelativeLayout ipModuleLayout;
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
		ipEntry = (EditText)findViewById(R.id.ipEditText);
		userModuleLayout = (RelativeLayout)findViewById(R.id.connexionLayout);

		if (SocketManager.currentInstance != null && SocketManager.currentInstance.isConnected()){
			changeToUserUI();
			assignLoginManager();
		}



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
					socketManager.currentInstance.verifyUser(userInformation.getUsername());
					while(loginManager.waitingForResponse()) {}

					if (loginManager.isLogged()){
						android.content.Intent intent = new android.content.Intent(getBaseContext(), HomeActivity.class);
						intent.putExtra("USER_INFORMATION", userInformation);
						startActivity(intent);
					}
					else{
						Toast.makeText(getBaseContext(), getString(R.string.loginUserAlreadyExistsToast),Toast.LENGTH_LONG).show();
						loginManager.reset();
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
					socketManager = new SocketManager(ipAddress);
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
                   	assignLoginManager();
                    changeToUserUI();
                }
                else{
					changeIpModuleState(true);
					Toast.makeText(getBaseContext(), getString(R.string.loginFailedSocketConnect), Toast.LENGTH_LONG).show();
				}
            }
        }, CONNECT_DELAY);
    }

    private void assignLoginManager() {
		loginManager = new LoginManager();
		SocketManager.currentInstance.setupLoginListener(loginManager);
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
    }
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
		startActivity(new android.content.Intent(getBaseContext(), ImageEditingActivity.class));
	}
}
