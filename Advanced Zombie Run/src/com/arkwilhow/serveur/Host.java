package com.arkwilhow.serveur;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.util.ArrayList;
import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.util.Log;
 
public class Host {
	private final WifiManager mWifiManager;
 
	public Host(Context context) {
		mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
	}
	
	/**
     * Start AccessPoint mode with the specified
     * configuration. If the radio is already running in
     * AP mode, update the new configuration
     * Note that starting in access point mode disables station
     * mode operation
     * @param wifiConfig SSID, security and channel details as part of WifiConfiguration
     * @return {@code true} if the operation succeeds, {@code false} otherwise
     */
	public boolean setWifiApEnabled(WifiConfiguration wifiConfig, boolean enabled) {
		try {
			if (enabled) { // disable WiFi in any case
				mWifiManager.setWifiEnabled(false);
			}

			Method method = mWifiManager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
			return (Boolean) method.invoke(mWifiManager, wifiConfig, enabled);
		} catch (Exception e) {
			Log.e(this.getClass().toString(), "", e);
			return false;
		}
	}
    
    /**
     * Gets the Wi-Fi AP Configuration.
     * @return AP details in {@link WifiConfiguration}
     */
    public WifiConfiguration getWifiApConfiguration() {
		try {
			Method method = mWifiManager.getClass().getMethod("getWifiApConfiguration");
			return (WifiConfiguration) method.invoke(mWifiManager);
		} catch (Exception e) {
			Log.e(this.getClass().toString(), "", e);
			return null;
		}
    }
    
    /**
     * Sets the Wi-Fi AP Configuration.
     * @return {@code true} if the operation succeeded, {@code false} otherwise
     */
    public boolean setWifiApConfiguration(WifiConfiguration wifiConfig) {
    	try {
			Method method = mWifiManager.getClass().getMethod("setWifiApConfiguration", WifiConfiguration.class);
			return (Boolean) method.invoke(mWifiManager, wifiConfig);
		} catch (Exception e) {
			Log.e(this.getClass().toString(), "", e);
			return false;
		}
	}
    
	/**
     * Gets a list of the clients connected to the Hotspot, reachable timeout is 300
     * @param onlyReachables {@code false} if the list should contain unreachable (probably disconnected) clients, {@code true} otherwise
     * @return ArrayList of {@link ClientScanResult}
     */
    public ArrayList<ClientScanResult> getClientList(boolean onlyReachables) {
    	return getClientList(onlyReachables, 300);
    }
    
	/**
     * Gets a list of the clients connected to the Hotspot 
     * @param onlyReachables {@code false} if the list should contain unreachable (probably disconnected) clients, {@code true} otherwise
     * @param reachableTimeout Reachable Timout in miliseconds
     * @return ArrayList of {@link ClientScanResult}
     */
	public ArrayList<ClientScanResult> getClientList(boolean onlyReachables, int reachableTimeout) {
		BufferedReader br = null;
		ArrayList<ClientScanResult> result = null;

		try {
			result = new ArrayList<ClientScanResult>();
			br = new BufferedReader(new FileReader("/proc/net/arp"));
			String line;
			while ((line = br.readLine()) != null) {
				String[] splitted = line.split(" +");

				if ((splitted != null) && (splitted.length >= 4)) {
					// Basic sanity check
					String mac = splitted[3];

					if (mac.matches("..:..:..:..:..:..")) {
						boolean isReachable = InetAddress.getByName(splitted[0]).isReachable(reachableTimeout);

						if (!onlyReachables || isReachable) {
							result.add(new ClientScanResult(splitted[0], splitted[3], splitted[5], isReachable));
						}
					}
				}
			}
		} catch (Exception e) {
			Log.e(this.getClass().toString(), e.getMessage());
		} finally {
			try {
				br.close();
			} catch (IOException e) {
				Log.e(this.getClass().toString(), e.getMessage());
			}
		}

		return result;
	}
}