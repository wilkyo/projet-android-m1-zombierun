package com.arkwilhow.advzombierun;

import android.os.Bundle;
import android.view.View;
import android.app.Activity;
import android.content.Intent;

public class AboutActivity extends Activity {

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
	}

	public void previous(View v) {
		finish();
	}

	public void run(View v) {
		Intent i = new Intent();
		i.setClass(this, GameMapActivity.class);
		startActivity(i);
	}

}
