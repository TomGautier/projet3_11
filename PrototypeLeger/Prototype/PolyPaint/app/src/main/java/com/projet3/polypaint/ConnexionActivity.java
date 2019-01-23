package com.projet3.polypaint;

import android.app.Activity;
import android.os.Bundle;

public class ConnexionActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		android.content.Intent intent = new android.content.Intent(this, HomeActivity.class);
		this.startActivity(intent);
	}
}
