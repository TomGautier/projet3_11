package com.projet3.polypaint;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import java.lang.Object;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		android.content.Intent intent = new android.content.Intent(this, ChatActivity.class);
		this.startActivity(intent);
	}
}
