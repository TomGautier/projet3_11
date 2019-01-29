package com.projet3.polypaint;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.view.MotionEvent;
import android.widget.ScrollView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.sql.Time;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.zip.Inflater;

public class HomeActivity extends Activity  {
	private Chat chat;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		if (savedInstanceState == null)
			chat = new Chat(this);
		else
			chat = new Chat(this, savedInstanceState.getBundle("chat"));
	}



	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		// save chat history
		//savedInstanceState.putStringArray("chatHistory", chat.chatHistory.toArray(new String[chat.chatHistory.size()]));
		super.onSaveInstanceState(savedInstanceState);
		savedInstanceState.putBundle("chat", chat.GetChatBundle());
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		//restore chat
		//Bundle chatBundle = savedInstanceState.getBundle("chat");

	}

}


