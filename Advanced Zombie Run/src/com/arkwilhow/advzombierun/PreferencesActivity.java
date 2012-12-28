package com.arkwilhow.advzombierun;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Spinner;

public class PreferencesActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_preferences);

		loadPreferences();
		setListeners();
	}

	private void loadPreferences() {
		SharedPreferences s = getPreferences(MODE_PRIVATE);
		((Spinner) findViewById(R.id.spinner_density)).setSelection(s.getInt(
				"density", 0));
		((Spinner) findViewById(R.id.spinner_speed)).setSelection(s.getInt(
				"speed", 0));
		((Spinner) findViewById(R.id.spinner_life)).setSelection(s.getInt(
				"life", 0));
		((RadioGroup) findViewById(R.id.alertChoices)).check(s.getInt("alert", R.id.alertChoice1));
	}

	private void setListeners() {
		((Spinner) findViewById(R.id.spinner_density))
		.setOnItemSelectedListener(new OnItemSelectedListener() {

			public void onItemSelected(AdapterView<?> parent,
					View view, int pos, long id) {
				SharedPreferences.Editor editor = getPreferences(
						MODE_PRIVATE).edit();
				editor.putInt("density", pos);
				editor.commit();
			}

			public void onNothingSelected(AdapterView<?> arg0) {
			}

		});
		((Spinner) findViewById(R.id.spinner_speed))
		.setOnItemSelectedListener(new OnItemSelectedListener() {

			public void onItemSelected(AdapterView<?> parent,
					View view, int pos, long id) {
				SharedPreferences.Editor editor = getPreferences(
						MODE_PRIVATE).edit();
				editor.putInt("speed", pos);
				editor.commit();
			}

			public void onNothingSelected(AdapterView<?> arg0) {
			}

		});
		((Spinner) findViewById(R.id.spinner_life))
		.setOnItemSelectedListener(new OnItemSelectedListener() {

			public void onItemSelected(AdapterView<?> parent,
					View view, int pos, long id) {
				SharedPreferences.Editor editor = getPreferences(
						MODE_PRIVATE).edit();
				editor.putInt("life", pos);
				editor.commit();
			}

			public void onNothingSelected(AdapterView<?> arg0) {
			}

		});

		((RadioGroup) findViewById(R.id.alertChoices)).setOnCheckedChangeListener(new OnCheckedChangeListener() {

			public void onCheckedChanged(RadioGroup rg, int checkedId) {
				System.out.println(checkedId);
				SharedPreferences.Editor editor = getPreferences(
						MODE_PRIVATE).edit();
				editor.putInt("alert", checkedId);
				editor.commit();
			}
			
		});
}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_preferences, menu);
		return true;
	}
	
	public void previous(View v) {
		Intent i = new Intent();
		i.setClass(this, HomeActivity.class);
		startActivity(i);
		super.finish();
	}
}
