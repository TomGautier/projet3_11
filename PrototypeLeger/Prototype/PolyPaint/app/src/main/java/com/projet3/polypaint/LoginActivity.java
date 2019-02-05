package com.projet3.polypaint;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

public class LoginActivity extends Activity {

	ImageButton connexionButton;
	EditText usernameEntry;
	EditText passwordEntry;
	EditText ipEntry;
	ProgressBar progressBar;
	UserInformation userInformation;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		Initialize();
	}
	private void Initialize() {
		connexionButton = (ImageButton)findViewById(R.id.connectButton);
		usernameEntry = (EditText)findViewById(R.id.usernameEditText);
		passwordEntry = (EditText)findViewById(R.id.passwordEditText);
		progressBar = (ProgressBar)findViewById(R.id.loginProgressBar);
		ipEntry = (EditText)findViewById(R.id.ipEditText);
		progressBar.setVisibility(View.GONE);
		connexionButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (usernameEntry.getText().length() > 0 && passwordEntry.getText().length() > 0
						&& isIPAddress(ipEntry.getText().toString())){

					progressBar.setVisibility(View.VISIBLE);
					android.content.Intent intent = new android.content.Intent(getBaseContext(), HomeActivity.class);
					userInformation = new UserInformation(usernameEntry.getText().toString(), passwordEntry.getText().toString(),
							ipEntry.getText().toString());
					intent.putExtra("USER_INFORMATION", userInformation);
					startActivity(intent);
				}
				else
					Toast.makeText(getBaseContext(), getString(R.string.loginToast), Toast.LENGTH_LONG).show();
			}
		});
		Utilities.SetButtonEffect(connexionButton);

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
				if (Integer.parseInt(octals[i]) < 0 || Integer.parseInt(octals[i]) > 255) {
					return false;
				}
			}
			return true;
		}
		return false;

	}

}
