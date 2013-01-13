package com.arkwilhow.advzombierun;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
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
	BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter(); ;
	BluetoothDevice device;
	ConnectThread connect;
	ArrayList<BluetoothDevice> hostedGames = new ArrayList<BluetoothDevice>();
	ArrayList<String> nameDevice = new ArrayList<String>();
	ListView listView;
	int nid = 0;
	boolean firstpass = false;
	ArrayAdapter<String> adapter;
	// private static final String PREFIXE_MULTI = "AZR-";
	private final static int REQUEST_ENABLE_BT = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_multi_player);

		listView = (ListView) findViewById(R.id.hostedGamesList);
		runBluetooth();

		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		registerReceiver(mReceiver, filter);
		refreshListView(listView);
	}

	private void refreshListView(ListView listView) {
		// Bouchon TODO
		if (!(nameDevice.isEmpty())) {
			for (int i = 0; i < nameDevice.size(); i++) {
				Log.d("refreshListView", nameDevice.get(i));
			}
			adapter = new ArrayAdapter<String>(this,
					android.R.layout.simple_list_item_1, android.R.id.text1,
					nameDevice) {
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

			listView.setOnItemClickListener(new OnItemClickListener() {
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					for (int i = 0; i < nameDevice.size(); i++) {
						if (hostedGames.get(i).getName()
								.equals(nameDevice.get(i))) {
							connect = new ConnectThread(device);
						}
					}
					connect.start();
					testclient(view);
				}
			});

			adapter.notifyDataSetChanged();
			// Assign adapter to ListView
			listView.setAdapter(adapter);
		}
	}

	// Create a BroadcastReceiver for ACTION_FOUND
	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			// When discovery finds a deviceadapter.notifyDataSetChanged();
			if (BluetoothDevice.ACTION_FOUND.equals(action)) {
				// Get the BluetoothDevice object from the Intent
				device = intent
						.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				if (device.getName().substring(0, 5).equals("AZR-H")) {
					hostedGames.add(device);
					nameDevice.add(device.getName());
				}
			}
		}
	};

	// Register the BroadcastReceiver

	public void refresh(View v) {
		mBluetoothAdapter.startDiscovery();
		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		registerReceiver(mReceiver, filter);
		refreshListView(listView);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_multi_player, menu);
		return true;
	}

	public void previous(View v) {
		unregisterReceiver(mReceiver);
		mBluetoothAdapter.disable();
		finish();
	}
	
	public void onDestroy(View v){
		unregisterReceiver(mReceiver);
		super.onDestroy();
	}

	public void runBluetooth() {
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (mBluetoothAdapter == null) {
			onCreateDialog();
		} else {
			if (!mBluetoothAdapter.isEnabled()) {
				Intent enableBtIntent = new Intent(
						BluetoothAdapter.ACTION_REQUEST_ENABLE);
				startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
			}
		}
	}

	/* Fenetre d'alert si le wifi n'est pas activé */
	public Dialog onCreateDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(R.string.diag_wifi_text);
		builder.setNeutralButton(R.string.diag_backMenu,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						finish();
					}
				});
		return builder.create();
	}

	/* A conserver pour plus tard */
	public void testhost(View v) {
		if (!(mBluetoothAdapter.getName().substring(0, 6).equals("AZR-H"))) {
			mBluetoothAdapter
					.setName("AZR-Host-" + mBluetoothAdapter.getName());
		}
		Intent i = new Intent();
		i.setClass(this, PreferencesActivity.class);
		i.putExtra("multi", true);
		startActivity(i);
	}

	public void testclient(View v) {
		Intent i = new Intent();
		i.setClass(this, RoomStayHostActivity.class);
		i.putExtra("host", false);
		startActivity(i);
	}

	private class ConnectThread extends Thread {
		private final UUID MY_UUID = UUID
				.fromString("00001101-0000-1000-8000-00805F9B34FB");;
		private final BluetoothSocket mmSocket;
		private final BluetoothDevice mmDevice;

		public ConnectThread(BluetoothDevice device) {
			// Use a temporary object that is later assigned to mmSocket,
			// because mmSocket is final
			BluetoothSocket tmp = null;
			mmDevice = device;

			// Get a BluetoothSocket to connect with the given BluetoothDevice
			try {
				// MY_UUID is the app's UUID string, also used by the server
				// code
				tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
			} catch (IOException e) {
			}
			mmSocket = tmp;
		}

		public void run() {
			// Cancel discovery because it will slow down the connection
			mBluetoothAdapter.cancelDiscovery();

			try {
				// Connect the device through the socket. This will block
				// until it succeeds or throws an exception
				mmSocket.connect();
			} catch (IOException connectException) {
				// Unable to connect; close the socket and get out
				try {
					mmSocket.close();
				} catch (IOException closeException) {
				}
				return;
			}
		}

		/** Will cancel an in-progress connection, and close the socket */
		public void cancel() {
			try {
				mmSocket.close();
			} catch (IOException e) {
			}
		}
	}

}
