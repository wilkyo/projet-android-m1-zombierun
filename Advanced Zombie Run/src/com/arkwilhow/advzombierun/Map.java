package com.arkwilhow.advzombierun;

import java.util.List;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;

public class Map extends MapActivity {
	private MapView map;
	private MapController mc;
	private LocationManager locManager;
	private Marqueur itemizedoverlay;
	private List<Overlay> mapOverlays;
    private final LocationListener listener = new LocationListener() {
		
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub
			
		}
		
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub
			
		}
		
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub
			
		}
		
		public void onLocationChanged(Location location) {
			GeoPoint point = new GeoPoint((int)(location.getLatitude() * 1e6), (int)(location.getLongitude() * 1e6));
			OverlayItem overlayitem = new OverlayItem(point, "ma localistion", "Vous êtes la");
			mc.setCenter(point);
			itemizedoverlay.addMarqueur(overlayitem);
			mapOverlays.add(itemizedoverlay);
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.map_layout);

	    locManager = (LocationManager) getSystemService(LOCATION_SERVICE);
	    final boolean gpsEnabled = locManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
	    if(!gpsEnabled)
	    {
			AlertDialog.Builder dialog = new AlertDialog.Builder(this);
			dialog.setTitle("GPS non activé");
			dialog.setMessage("Veuillez activez le GPS");
			dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
					enableLocationSettings();
				}
			});
			dialog.show();
			
	    }
		
	    map = (MapView)findViewById(R.id.mapView);
	    map.setBuiltInZoomControls(true);
	    mc = map.getController();
	    mc.setZoom(17);
	    
	    GeoPoint point = new GeoPoint((int)(47.843248 * 1e6) ,(int)(1.934205 * 1e6));	    
	    mc.setCenter(point);

	    mapOverlays = map.getOverlays();
	    
	    Drawable drawable = this.getResources().getDrawable(R.drawable.androidmarker);
	    itemizedoverlay = new Marqueur(drawable, this);

//	    mapOverlays.add(itemizedoverlay);
		locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 10, listener);
	    
	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}
	
	private void enableLocationSettings() {
	    Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
	    startActivity(settingsIntent);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		locManager.removeUpdates(listener);
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 10, listener);
	}
}
