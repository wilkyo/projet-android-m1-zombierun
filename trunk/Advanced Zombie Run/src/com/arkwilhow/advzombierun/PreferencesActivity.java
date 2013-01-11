package com.arkwilhow.advzombierun;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.TextView;

public class PreferencesActivity extends Activity {

	private static boolean multi;
	private static int nbJoueurs = 1;
	private Context mContext = this;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_preferences);
		Bundle extra = this.getIntent().getExtras();
		multi = extra.getBoolean("multi");
		loadPreferences();
		setListeners();
		if (!multi) {
			TextView textView = (TextView) findViewById(R.id.create);
			textView.setText(R.string.run_game);
		}
	}

	private void loadPreferences() {
		SharedPreferences s = PreferenceManager
				.getDefaultSharedPreferences(this);
		((EditText) findViewById(R.id.pseudonyme)).setText(s.getString(
				"pseudo", ""));
		((Spinner) findViewById(R.id.spinner_density)).setSelection(s.getInt(
				"density", 0));
		((Spinner) findViewById(R.id.spinner_speed)).setSelection(s.getInt(
				"speed", 0));
		((Spinner) findViewById(R.id.spinner_life)).setSelection(s.getInt(
				"life", 0));
		((RadioGroup) findViewById(R.id.alertChoices)).check(s.getInt("alert",
				R.id.alertChoice1));
	}

	private void setListeners() {
		((Spinner) findViewById(R.id.spinner_density))
				.setOnItemSelectedListener(new OnItemSelectedListener() {

					public void onItemSelected(AdapterView<?> parent,
							View view, int pos, long id) {
						SharedPreferences.Editor editor = PreferenceManager
								.getDefaultSharedPreferences(mContext).edit();
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
						SharedPreferences.Editor editor = PreferenceManager
								.getDefaultSharedPreferences(mContext).edit();
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
						SharedPreferences.Editor editor = PreferenceManager
								.getDefaultSharedPreferences(mContext).edit();
						editor.putInt("life", pos);
						editor.commit();
					}

					public void onNothingSelected(AdapterView<?> arg0) {
					}

				});

		((RadioGroup) findViewById(R.id.alertChoices))
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					public void onCheckedChanged(RadioGroup rg, int checkedId) {
						System.out.println(checkedId);
						SharedPreferences.Editor editor = PreferenceManager
								.getDefaultSharedPreferences(mContext).edit();
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

	public void onPause() {
		SharedPreferences.Editor editor = PreferenceManager
				.getDefaultSharedPreferences(this).edit();
		final String name = ((EditText) findViewById(R.id.pseudonyme))
				.getText().toString();
		editor.putString("pseudo",
				name.equals("") ? getString(R.string.pseudoHint) : name);
		editor.commit();
		super.onPause();
	}

	public void goRoom(View v) {
		if (multi) {
			final String name = ((EditText) findViewById(R.id.pseudonyme))
					.getText().toString();
			Intent i = new Intent();
			i.setClass(this, RoomStayHostActivity.class);
			i.putExtra("pseudo", name);
			i.putExtra("host", true);
			startActivity(i);
		} else {
			Intent i = new Intent();
			i.setClass(this, Map.class);
			startActivity(i);
		}
	}

	public void previous(View v) {
		finish();
	}

	public static void setMulti(boolean multi) {
		PreferencesActivity.multi = multi;
	}

	public static int getNbJoueurs() {
		return PreferencesActivity.nbJoueurs;
	}

	public static void setNbJoueurs(int nbJoueurs) {
		PreferencesActivity.nbJoueurs = nbJoueurs;
	}
}
