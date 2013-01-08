package com.arkwilhow.advzombierun;

import java.util.ArrayList;
import java.util.List;

import com.arkwilhow.serveur.Host;

import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.Settings;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class MultiPlayerActivity extends Activity {

	/* private int[] gamesIds; */
	WifiManager mainWifi;
	WifiReceiver receiverWifi;
	List<ScanResult> wifiList;
	String sb;
	ArrayList<String> hostedGames =  new ArrayList<String>();
	Host test;
	ListView listView;
	int nid = 0;
	private static final String PREFIXE_MULTI = "AZR-";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_multi_player);

		listView = (ListView) findViewById(R.id.hostedGamesList);

		mainWifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		receiverWifi = new WifiReceiver();
		registerReceiver(receiverWifi, new IntentFilter(
				WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

		/*
		 * listView.setOnItemClickListener(new OnItemClickListener() { public
		 * void onItemClick(AdapterView<?> parent, View view, int position, long
		 * id) { ArrayList<WifiConfiguration> AP =
		 * (ArrayList<WifiConfiguration>) mainWifi .getConfiguredNetworks();
		 * TextView txt
		 * =(TextView)parent.getChildAt(position-listView.getFirstVisiblePosition
		 * ()).findViewById(R.id.hostedGamesList); String SSID =
		 * txt.getText().toString(); for (int i = 0; i < AP.size(); i++){ if
		 * (AP.get(i).equals(SSID)){ nid = AP.get(i).networkId; } }
		 * mainWifi.enableNetwork(nid, true); } });
		 */
	}

	private void refreshListView(ListView listView) {
		// Bouchon TODO
		mainWifi.startScan();
		/* gamesIds = new int[] { 85, 123435 }; */
		// String[] hostedGames = new String[] { "wilkyo", "HowiePowie" }
		if (!(hostedGames.isEmpty())) {
			for (int i = 0; i < hostedGames.size(); i++) {
				Log.d("refreshListView", hostedGames.get(i));
			}
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
					android.R.layout.simple_list_item_1, android.R.id.text1,
					hostedGames) {
				public View getView(int position, View convertView,
						ViewGroup parent) {
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
			// Assign adapter to ListView
			listView.setAdapter(adapter);
		}
	}

	/*
	 * protected void loadHostedGame(int id) { Log.d("loadHostedGame",
	 * String.valueOf(id)); Intent intent = new Intent(this, Map.class); // Pas
	 * directement la Map, // il faudra peut être // une activité // d'attente
	 * if (intent != null) { // Affectations à faire ici si nécessaire TODO
	 * this.startActivity(intent); } }
	 */

	/*
	 * public void newHostedGame(View v) { Intent intent = new Intent(this,
	 * PreferencesActivity.class); if (intent != null) { // Affectations à faire
	 * ici si necessaire // Définir qu'on est en multi TODO
	 * this.startActivity(intent); } }
	 */

	public void refresh(View v) {
		if (!(mainWifi.isWifiEnabled())) {
			Dialog control = onCreateDialog();
			control.show();
		} else {
			refreshListView((ListView) findViewById(R.id.hostedGamesList));
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_multi_player, menu);
		return true;
	}

	public void previous(View v) {
		unregisterReceiver(receiverWifi);
		finish();
	}

	/* Fenetre d'alert si le wifi n'est pas activé */
	public Dialog onCreateDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.diagtitle);
		builder.setMessage(R.string.diagtext);
		builder.setNegativeButton(R.string.diagok,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
					}
				});
		builder.setPositiveButton(R.string.diagprevious,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						finish();
					}
				});
		return builder.create();
	}

	class WifiReceiver extends BroadcastReceiver {

		public void onReceive(Context c, Intent intent) {
			wifiList = mainWifi.getScanResults();
			for (int i = 0; i < wifiList.size(); i++) {
				sb = wifiList.get(i).SSID.toString();
				if (sb.substring(0, PREFIXE_MULTI.length()).equals(
						PREFIXE_MULTI)) {
					hostedGames.add(sb);
				}
			}
		}
	}

	/* A conserver pour plus tard */
	public void testhost(View v) {
		test = new Host(this);
		WifiConfiguration conf = new WifiConfiguration();
		conf.SSID = "AZR-test";
		conf.preSharedKey = "pojnootankurdenwooc8";
		conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
		test.setWifiApEnabled(conf, true);
		PreferencesActivity.setMulti(true);
		Intent i = new Intent();
		i.setClass(this, RoomStayHostActivity.class);
		startActivity(i);
	}

	public void testclient(View v) {
		Intent i = new Intent();
		i.setClass(this, RoomStayHostActivity.class);
		startActivity(i);
	}

}
