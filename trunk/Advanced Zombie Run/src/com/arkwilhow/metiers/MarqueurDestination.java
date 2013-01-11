package com.arkwilhow.metiers;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

@SuppressWarnings("rawtypes")
public class MarqueurDestination extends ItemizedOverlay {
	public MarqueurDestination(Drawable defaultMarker) {
		super(boundCenterBottom(defaultMarker));
	}

	private ArrayList<OverlayItem> listeMarqueur = new ArrayList<OverlayItem>();

	@Override
	public int size() {
		return listeMarqueur.size();
	}

	@Override
	protected OverlayItem createItem(int arg0) {
		return listeMarqueur.get(arg0);
	}

	public void addMarqueur(OverlayItem item) {
		listeMarqueur.add(item);
	}

	protected boolean onTap(int arg0) {
		return false;
	}

	public boolean onTouchEvent(android.view.MotionEvent arg0,
			com.google.android.maps.MapView arg1) {
		return false;
	}

}
