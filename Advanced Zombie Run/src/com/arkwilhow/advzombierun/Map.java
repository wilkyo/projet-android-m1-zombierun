package com.arkwilhow.advzombierun;

import java.util.ArrayList;
import java.util.List;

import com.arkwilhow.metiers.Joueur;
import com.arkwilhow.metiers.MarqueursJoueurs;
import com.arkwilhow.metiers.MarqueursZombies;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;

/**
 * Activité pour l'affichage de la map
 * 
 * @author ”Jean-Baptiste Perrin”
 * 
 */
public class Map extends MapActivity {
	private MapView map;
	private MapController mc;
	private LocationManager locManager;
	private MarqueursJoueurs itemizedoverlay;
	private List<Overlay> mapOverlays;
	private GameMaster master = null;

	private ArrayList<Location> positionsRecuperees;

	// private final static String TAG = "Map";
	private final LocationListener listener = new LocationListener() {

		public void onStatusChanged(String provider, int status, Bundle extras) {
		}

		public void onProviderEnabled(String provider) {
		}

		public void onProviderDisabled(String provider) {
			checkGPS();
		}

		private Location[] getPositionsJoueurs() {
			Location[] positions = new Location[itemizedoverlay.size()];
			for (int i = 0; i < positions.length; i++) {
				positions[i] = positionsRecuperees.get(i);
			}
			return positions;
		}

		/**
		 * La position a changée
		 */
		public void onLocationChanged(Location location) {
			GeoPoint point = new GeoPoint((int) (location.getLatitude() * 1e6),
					(int) (location.getLongitude() * 1e6));
			mc.setCenter(point);
			if (master == null) {
				SharedPreferences pref = getPreferences(MODE_PRIVATE);
				MarqueursJoueurs joue = new MarqueursJoueurs(getResources()
						.getDrawable(R.drawable.androidmarker));
				joue.addMarqueur(new Joueur(point, "joueur",
						"Je suis le joueur"));
				master = new GameMaster(joue, new MarqueursZombies(
						getResources().getDrawable(R.drawable.androidmarker)),
						pref.getInt("density", 0), pref.getInt("speed", 0),
						pref.getInt("life", 0), pref.getInt("alert",
								R.id.alertChoice1), null);
				master.liste_zombis();
			} else {
				positionsRecuperees.set(0, location);
				master.deplacement(getPositionsJoueurs());
			}
			mapOverlays.clear();
			mapOverlays.add(master.getJoueurs());
			mapOverlays.add(master.getZombies());
		}
	};

	// cree l'application et paramêtre l'apparences de la map
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map_layout);

		map = (MapView) findViewById(R.id.mapView);
		map.setBuiltInZoomControls(true);
		mc = map.getController();
		mc.setZoom(17);

		GeoPoint point = new GeoPoint((int) (47.843248 * 1e6),
				(int) (1.934205 * 1e6));
		mc.setCenter(point);

		mapOverlays = map.getOverlays();

		Drawable drawable = this.getResources().getDrawable(
				R.drawable.androidmarker);
		itemizedoverlay = new MarqueursJoueurs(drawable, this);
		positionsRecuperees = new ArrayList<Location>();
		for (int i = 0; i < itemizedoverlay.size(); i++) {
			positionsRecuperees.add(i, null);
		}
		locManager = (LocationManager) getSystemService(LOCATION_SERVICE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.android.maps.MapActivity#isRouteDisplayed()
	 */
	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	/**
	 * Appel au gestionnaire de localisation
	 */
	private void enableLocationSettings() {
		Intent settingsIntent = new Intent(
				Settings.ACTION_LOCATION_SOURCE_SETTINGS);
		startActivity(settingsIntent);
	}

	// on se délie du listener
	@Override
	protected void onPause() {
		super.onPause();
		locManager.removeUpdates(listener);
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	// appeler au demarrage de l'application
	@Override
	protected void onStart() {
		super.onStart();
		checkGPS();
		locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000,
				10, listener);
	}

	/**
	 * Vérifie que le GPS est bien activé
	 */
	public void checkGPS() {
		final boolean gpsEnabled = locManager
				.isProviderEnabled(LocationManager.GPS_PROVIDER);
		if (!gpsEnabled) {
			AlertDialog.Builder dialog = new AlertDialog.Builder(this);
			dialog.setTitle("GPS non activé");
			dialog.setMessage("Veuillez activez le GPS");
			dialog.setPositiveButton("OK",
					new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {
							enableLocationSettings();
						}
					});
			dialog.show();
		}
		if (master != null)
			master.setContext(this);
	}
}
