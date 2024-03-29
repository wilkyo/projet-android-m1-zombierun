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
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

/**
 * Activité pour l'affichage de la map
 * 
 */
public class GameMapActivity extends MapActivity {
	private MapView map;
	private MapController mc;
	private LocationManager locManager;
	private List<Overlay> mapOverlays;
	private GameMaster master = null;
	private Context mContext;
	private ArrayList<Location> positionsRecuperees;
	private Handler handler = new Handler();
	private final static int REFRESH_TIME = 500;
	private static boolean finished = false;

	private final LocationListener listener = new LocationListener() {

		public void onStatusChanged(String provider, int status, Bundle extras) {
		}

		public void onProviderEnabled(String provider) {
		}

		public void onProviderDisabled(String provider) {
			checkGPS();
		}

		/**
		 * La position a changée
		 */
		public void onLocationChanged(Location location) {
			positionsRecuperees.set(0, location);
		}
	};

	// cree l'application et paramètre l'apparences de la map
	@Override
	public void onCreate(Bundle savedInstanceState) {
		try {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.activity_game_map);
			mContext = this;
			map = (MapView) findViewById(R.id.mapView);
			map.setBuiltInZoomControls(true);
			mc = map.getController();
			mc.setZoom(17);

			mapOverlays = map.getOverlays();

			positionsRecuperees = new ArrayList<Location>();
			for (int i = 0; i < PreferencesActivity.getNbJoueurs(); i++) {
				positionsRecuperees.add(i, null);
			}

			locManager = (LocationManager) getSystemService(LOCATION_SERVICE);
			Log.v("Map.onCreate", "onCreate vaincu");
		} catch (Exception e) {
			Toast.makeText(
					mContext,
					"onCreate: " + e.getMessage() + " "
							+ e.getLocalizedMessage(), Toast.LENGTH_LONG)
					.show();
		}
	}

	// on se délie du listener
	@Override
	protected void onPause() {
		super.onPause();
		handler.removeCallbacks(timedTask);
		locManager.removeUpdates(listener);
	}

	@Override
	protected void onResume() {
		super.onResume();
		handler.post(timedTask);
	}

	// appeler au demarrage de l'application
	@Override
	protected void onStart() {
		super.onStart();
		checkGPS();
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

	/**
	 * Vérifie que le GPS est bien activé
	 */
	public void checkGPS() {
		final boolean gpsEnabled = locManager
				.isProviderEnabled(LocationManager.GPS_PROVIDER);
		if (!gpsEnabled) {
			AlertDialog.Builder dialog = new AlertDialog.Builder(this);
			dialog.setCancelable(false);
			dialog.setTitle(getText(R.string.diag_gps_title));
			dialog.setMessage(getText(R.string.diag_gps_text));
			dialog.setPositiveButton(getText(R.string.diag_ok),
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							enableLocationSettings();
						}
					});
			dialog.show();
		} else {
			locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
					REFRESH_TIME, 5, listener);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				|| keyCode == KeyEvent.KEYCODE_HOME) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(R.string.diag_quit_title)
					.setMessage(R.string.diag_quit_text)
					.setCancelable(true)
					.setPositiveButton(R.string.diag_yes,
							new DialogInterface.OnClickListener() {

								public void onClick(DialogInterface dialog,
										int which) {
									finish();
								}
							})
					.setNegativeButton(R.string.diag_no,
							new DialogInterface.OnClickListener() {

								public void onClick(DialogInterface dialog,
										int which) {
								}
							});
			AlertDialog dia = builder.create();
			dia.show();
			return false;
		} else
			return super.onKeyDown(keyCode, event);
	}

	/**
	 * Range la liste des positions dans un tableau
	 * 
	 * @return
	 */
	private Location[] getPositionsJoueurs() {
		return positionsRecuperees.toArray(new Location[0]);
	}

	/**
	 * Routine exécutée tous les x millisecondes. Effectue le déplacement des
	 * zombis.
	 */
	private Runnable timedTask = new Runnable() {

		public void run() {
			if (!finished) {
				try {
					// Déplacement des zombis et des joueurs
					final Location location = positionsRecuperees.get(0);
					if (location != null) {
						if (master == null) {
							GeoPoint point = new GeoPoint(
									(int) (location.getLatitude() * 1e6),
									(int) (location.getLongitude() * 1e6));
							mc.setCenter(point);

							SharedPreferences pref = PreferenceManager
									.getDefaultSharedPreferences(mContext);

							MarqueursJoueurs joueurs = new MarqueursJoueurs(
									getResources().getDrawable(
											R.drawable.marqueurjoueur),
									mContext);
							joueurs.addMarqueur(new Joueur(point, "joueur",
									"Je suis le joueur"));

							MarqueursZombies zombies = new MarqueursZombies(
									getResources().getDrawable(
											R.drawable.marqueurzombi0),
									mContext);

							master = new GameMaster(joueurs, zombies,
									pref.getInt("density", 0), pref.getInt(
											"speed", 0),
									pref.getInt("timer", 0), pref.getInt(
											"life", 0), pref.getInt("alert",
											R.id.alertChoice1), mContext,
									REFRESH_TIME);
							master.creerListeZombis();

							Log.v("Map.onLocationChanged",
									"création master passée");
						} else {
							// Vérifications de victoire ou échec
							if (master.getVictoire()) {
								finished = true;
								Intent i = new Intent();
								i.setClass(mContext, GameEndActivity.class);
								i.putExtra("lost", false);
								mContext.startActivity(i);
							} else if (master.getEchec()) {
								finished = true;
								Intent i = new Intent();
								i.setClass(mContext, GameEndActivity.class);
								i.putExtra("lost", true);
								i.putExtra("etat", master.getEtat());
								mContext.startActivity(i);
							}
							master.deplacement(getPositionsJoueurs());
						}
						mapOverlays.clear();
						if (master.getMarqueurDestination() != null)
							mapOverlays.add(master.getMarqueurDestination());
						mapOverlays.add(master.getMarqueursJoueurs());
						if (master.zombisVisibles()) {
							mapOverlays.add(master.getMarqueursZombies());
							mapOverlays.add(master.getMarqueursZombiesAware());
						}
						map.invalidate();
					}
				} catch (Exception e) {
					Toast.makeText(mContext, "run: " + e.toString(),
							Toast.LENGTH_LONG).show();
				}
				handler.postDelayed(timedTask, REFRESH_TIME);
			} else {
				master.setEtat(0);
				finish();
			}
		}
	};

	public static boolean getFinished() {
		return finished;
	}

	public static void setFinished(boolean b) {
		finished = b;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.menu_centerMap:
			centerMap();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void centerMap() {
		final Location location = positionsRecuperees.get(0);
		if (location == null)
			return;
		else {
			GeoPoint point = new GeoPoint((int) (location.getLatitude() * 1e6),
					(int) (location.getLongitude() * 1e6));
			mc.setCenter(point);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_game_map, menu);
		return true;
	}

}
