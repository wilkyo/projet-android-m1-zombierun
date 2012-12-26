package com.arkwilhow.advzombierun;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

public class HomeActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
	
		TextView textViewTron = (TextView) findViewById(R.id.titlepart1);
		Typeface fontTron = Typeface.createFromAsset(getAssets(), "coldnightforalligators.ttf");
		textViewTron.setTypeface(fontTron);
		TextView textViewTron2 = (TextView) findViewById(R.id.titlepart2);
		Typeface fontTron2 = Typeface.createFromAsset(getAssets(), "coldnightforalligators.ttf");
		textViewTron2.setTypeface(fontTron2);
		/*TextView textViewTron3 = (TextView) findViewById(R.id.titleaccueil);
		Typeface fontTron3 = Typeface.createFromAsset(getAssets(), "coldnightforalligators.ttf");
		textViewTron3.setTypeface(fontTron3);*/

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_home, menu);
		return true;
	}
	
	public void quitter(View v){
		super.finish();
		System.exit(0);
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
