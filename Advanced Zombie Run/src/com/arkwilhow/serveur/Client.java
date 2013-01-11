package com.arkwilhow.serveur;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;

public class Client {
	Context mContext;
	Integer mPort;

	public Client(Context c, Integer p) {
		mContext = c;
		mPort = p;
	}

	protected InetAddress getAdresseBroadcast() throws UnknownHostException {
		WifiManager wifiManager = (WifiManager) mContext
				.getSystemService(Context.WIFI_SERVICE);
		DhcpInfo dhcp = wifiManager.getDhcpInfo();

		int broadcast = (dhcp.ipAddress & dhcp.netmask) | ~dhcp.netmask;
		byte[] quads = new byte[4];
		for (int k = 0; k < 4; k++)
			quads[k] = (byte) ((broadcast >> k * 8) & 0xFF);
		return InetAddress.getByAddress(quads);
	}

	public void envoyerTrameUDP(String requete, int port, String reponse)
			throws Exception {
		DatagramSocket socket = new DatagramSocket(mPort);
		socket.setBroadcast(true);
		InetAddress broadcastAdress = getAdresseBroadcast();
		DatagramPacket packet = new DatagramPacket(requete.getBytes(),
				requete.length(), broadcastAdress, port);
		socket.send(packet);

		byte[] buf = new byte[1024];
		packet = new DatagramPacket(buf, buf.length);

		socket.receive(packet);
		while (!(new String(packet.getData()).equals(reponse))) {
			socket.receive(packet);
		}
		socket.close();
	}

	public void ReceptionTrameUDP(String requete, int port, String reponse)
			throws Exception {
		DatagramSocket socket = new DatagramSocket(mPort);
		socket.setBroadcast(true);
		InetAddress broadcastAdress = getAdresseBroadcast();
		DatagramPacket packet = new DatagramPacket(requete.getBytes(),
				requete.length(), broadcastAdress, port);
		socket.send(packet);

		byte[] buf = new byte[1024];
		packet = new DatagramPacket(buf, buf.length);

		socket.receive(packet);
		while (!(new String(packet.getData()).equals(reponse))) {
			socket.receive(packet);
		}
		socket.close();
	}

}