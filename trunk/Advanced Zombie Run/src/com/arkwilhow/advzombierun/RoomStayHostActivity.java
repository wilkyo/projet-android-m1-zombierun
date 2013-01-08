package com.arkwilhow.advzombierun;

import java.util.ArrayList;

import com.arkwilhow.serveur.Host;

import android.net.wifi.WifiConfiguration;
import android.os.Bundle;
import android.app.Activity;
import android.graphics.Color;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class RoomStayHostActivity extends Activity {

	Host test;
	String pseudo;
	ListView listView;
	ArrayList<String> player = new ArrayList<String>();
	ArrayAdapter<String> adapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_roomstayhost);
		Bundle extra = getIntent().getExtras();
		pseudo = extra.getString("pseudo");
		listView = (ListView) findViewById(R.id.hostedGamesList);
		player.add(pseudo);
		
		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, android.R.id.text1, player) {
			public View getView(int position, View convertView, ViewGroup parent) {
				View view = super.getView(position, convertView, parent);

				/* Change le style du textView ayant pour id text1 */
				TextView textView = (TextView) view
						.findViewById(android.R.id.text1);

				textView.setTextColor(Color.parseColor("#8A0808"));
				textView.setBackgroundColor(Color.parseColor("#424242"));
				textView.setTextSize(20);

				return view;
			}
		};
		adapter.notifyDataSetChanged();
		// Assign adapter to ListView
		listView.setAdapter(adapter);
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