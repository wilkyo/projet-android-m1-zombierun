package com.arkwilhow.advzombierun;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;

import com.arkwilhow.metiers.Joueur;
import com.arkwilhow.metiers.MarqueursJoueurs;
import com.arkwilhow.metiers.MarqueursZombies;
import com.arkwilhow.serveur.Host;
import com.google.android.maps.GeoPoint;

import android.location.Location;
import android.net.DhcpInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.MulticastLock;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("HandlerLeak")
public class RoomStayHostActivity extends Activity {

	Thread myCommsThread = null;
	Host test;
	String pseudo;
	String pseudotest = "test";
	boolean host;
	ListView listView;
	ArrayList<String> player = new ArrayList<String>();
	ArrayAdapter<String> adapter;
	Handler handler = new Handler();
	WifiManager wifiManager;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_roomstayhost);
		Bundle extra = getIntent().getExtras();
		pseudo = extra.getString("pseudo");
		host = extra.getBoolean("host");
		listView = (ListView) findViewById(R.id.hostedGamesList);
		player.add(pseudo);
		wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);

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
		
		if(host){
			handler.postDelayed(broadcast, 5000);
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_home, menu);
		return true;
	}

	public void previous(View v) {
		handler.removeCallbacks(broadcast);
		test = new Host(this);
		WifiConfiguration conf = new WifiConfiguration();
		conf.SSID = "AndroidAP";
		conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
		test.setWifiApEnabled(conf, true);
		test.setWifiApEnabled(null, false);
		finish();
	}

	@Override
	public void onDestroy() {
		handler.removeCallbacks(broadcast);
		test = new Host(this);
		WifiConfiguration conf = new WifiConfiguration();
		conf.SSID = "AndroidAP";
		conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
		test.setWifiApEnabled(conf, true);
		test.setWifiApEnabled(null, false);
		super.onDestroy();
	}

	protected InetAddress getAdresseBroadcast() throws UnknownHostException {
		DhcpInfo dhcp = wifiManager.getDhcpInfo();
		int broadcast = (dhcp.ipAddress & dhcp.netmask) | ~dhcp.netmask;
		byte[] quads = new byte[4];
		for (int k = 0; k < 4; k++)
			quads[k] = (byte) ((broadcast >> k * 8) & 0xFF);
		return InetAddress.getByAddress(quads);
	}

	private Thread broadcast = new Thread() {

		public void run() {
			int port = 2563;
			final String reponse = "client";
			MulticastLock lock = wifiManager.createMulticastLock("dk.aboaya.pingpong");
			lock.acquire();
			try {
				
				Log.i("Run Socket", "Lancement des sockets");
				DatagramSocket socket = new DatagramSocket(2562);
				InetAddress broadcastAdress = getAdresseBroadcast();
				//InetSocketAddress adress= new InetSocketAddress(1234);
				socket.setBroadcast(true);
				String sPort = String.valueOf(port);
				DatagramPacket packet = new DatagramPacket(sPort.getBytes(),
						sPort.length(), broadcastAdress, 2564);
				socket.send(packet);
				Log.i("Paquet envoyé", "Le paquet est envoyé sur le réseau");
				byte[] buf = new byte[1024];
				packet = new DatagramPacket(buf, buf.length);
				socket.setSoTimeout(1000);
				socket.receive(packet);
				if (!(new String(packet.getData()).equals(reponse))) {
					socket.receive(packet);
				}
				Log.i("Socket Fermer", "Fermeture des sockets");
			} catch (SocketException e) {
				// TODO Auto-generated catch block
				Log.e("SocketException", "Exception levé");
				e.printStackTrace();
				handler.postDelayed(broadcast, 1000);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				handler.postDelayed(broadcast, 1000);
			}
			lock.release();
			handler.postDelayed(broadcast, 1000);
		}
	};

}