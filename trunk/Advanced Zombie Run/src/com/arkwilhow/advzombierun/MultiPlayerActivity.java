package com.arkwilhow.advzombierun;

import java.util.ArrayList;
import java.util.List;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.os.Bundle;
import android.provider.Settings;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
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
	List<ScanResult> wifiList;
	String sb;
	ArrayList<String> hostedGames = new ArrayList<String>();
	ListView listView;
	int nid = 0;
	boolean firstpass = false;
	ArrayAdapter<String> adapter;
	private static final String PREFIXE_MULTI = "AZR-";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_multi_player);

		listView = (ListView) findViewById(R.id.hostedGamesList);

		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				TextView text = (TextView) view
						.findViewById(android.R.id.text1);
				String content = text.getText().toString();

				WifiConfiguration wfc = new WifiConfiguration();
				wfc.SSID = "\"".concat(content).concat("\"");
				wfc.status = WifiConfiguration.Status.DISABLED;
				wfc.priority = 40;
				wfc.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
				wfc.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
				wfc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
				wfc.allowedPairwiseCiphers
						.set(WifiConfiguration.PairwiseCipher.CCMP);
				wfc.allowedPairwiseCiphers
						.set(WifiConfiguration.PairwiseCipher.TKIP);
				wfc.allowedGroupCiphers
						.set(WifiConfiguration.GroupCipher.WEP40);
				wfc.allowedGroupCiphers
						.set(WifiConfiguration.GroupCipher.WEP104);
				wfc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
				wfc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
				wfc.preSharedKey = "\"".concat("pojnootankurdenwooc8").concat(
						"\"");
				testclient(view);
			}
		});
	}

	private void refreshListView(ListView listView) {
		// Bouchon TODO
		if (!(hostedGames.isEmpty())) {
			for (int i = 0; i < hostedGames.size(); i++) {
				Log.d("refreshListView", hostedGames.get(i));
			}
			adapter = new ArrayAdapter<String>(this,
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
			adapter.notifyDataSetChanged();
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

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_multi_player, menu);
		return true;
	}

	public void previous(View v) {
		finish();
	}

	/* Fenetre d'alert si le wifi n'est pas activé */
	public Dialog onCreateDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.diag_wifi_title);
		builder.setMessage(R.string.diag_wifi_text);
		builder.setNegativeButton(R.string.diag_ok,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
					}
				});
		builder.setPositiveButton(R.string.diag_main_previous,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						finish();
					}
				});
		return builder.create();
	}

	/* A conserver pour plus tard */
	public void testhost(View v) {
		Intent i = new Intent();
		i.setClass(this, PreferencesActivity.class);
		i.putExtra("multi", true);
		i.putExtra("home", false);
		startActivity(i);
	}

	public void testclient(View v) {
		Intent i = new Intent();
		i.setClass(this, RoomStayHostActivity.class);
		i.putExtra("pseudo", "test");
		i.putExtra("host", false);
		startActivity(i);
	}

}
