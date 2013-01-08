package com.arkwilhow.advzombierun;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;

public class RoomStayHostActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_roomstayhost);
		
		

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_home, menu);
		return true;
	}

	public void quitter(View v) {
		super.finish();
		System.exit(0);
	}

	/*public void openMulti(View v) {
		PreferencesActivity.setMulti(true);
		Intent i = new Intent();
		i.setClass(this, MultiPlayerActivity.class);
		startActivity(i);
	}*/
}