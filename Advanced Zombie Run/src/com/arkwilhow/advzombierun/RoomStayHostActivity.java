package com.arkwilhow.advzombierun;

import com.arkwilhow.serveur.Host;

import android.net.wifi.WifiConfiguration;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;

public class RoomStayHostActivity extends Activity {

	Host test;

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

	public void previous(View v) {
		test = new Host(this);
		WifiConfiguration conf = new WifiConfiguration();
		conf.SSID = "AndroidAP";
		conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
		test.setWifiApEnabled(conf, true);
		test.setWifiApEnabled(null, false);
		finish();
	}

	public void onDestroy() {
		test = new Host(this);
		WifiConfiguration conf = new WifiConfiguration();
		conf.SSID = "AndroidAP";
		conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
		test.setWifiApEnabled(conf, true);
		test.setWifiApEnabled(null, false);
		super.onDestroy();
	}

	/*
	 * public void openMulti(View v) { PreferencesActivity.setMulti(true);
	 * Intent i = new Intent(); i.setClass(this, MultiPlayerActivity.class);
	 * startActivity(i); }
	 */
}