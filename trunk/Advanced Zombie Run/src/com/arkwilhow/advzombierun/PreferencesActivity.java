package com.arkwilhow.advzombierun;

import android.os.Bundle;
import android.app.Activity;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Menu;

public class PreferencesActivity extends Activity {

	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);

		loadPreferences();
    }

	private void loadPreferences() {
		SharedPreferences s = getPreferences(MODE_PRIVATE);
		String mem1 = s.getString("density", "");
		String mem2 = s.getString("speed", "");
		//e1.setHint(mem1);
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_preferences, menu);
        return true;
    }
}
