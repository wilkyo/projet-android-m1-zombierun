package com.arkwilhow.advzombierun;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;

public class HomeActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_home, menu);
		return true;
	}

	public void openMulti(View v) {
		Intent i = new Intent();
		i.setClass(this, MultiPlayerActivity.class);
		startActivity(i);
	}
	
	public void openPreferences(View v) {
		Intent i = new Intent();
		i.setClass(this, PreferencesActivity.class);
		startActivity(i);
	}

	public void run(View v) {
		Intent i = new Intent();
		i.setClass(this, Map.class);
		startActivity(i);
	}
}
