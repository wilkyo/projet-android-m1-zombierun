package com.arkwilhow.advzombierun;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

public class Marqueur extends ItemizedOverlay {

	private ArrayList<OverlayItem> listeMarqueur = new ArrayList<OverlayItem>();
	private Context mContext;
	public Marqueur(Drawable arg0) {
		super(boundCenterBottom(arg0));
		// TODO Auto-generated constructor stub
	}
	
	

	public ArrayList<OverlayItem> getListeMarqueur() {
		return listeMarqueur;
	}



	public void setListeMarqueur(ArrayList<OverlayItem> listeMarqueur) {
		this.listeMarqueur = listeMarqueur;
	}



	public Marqueur(Drawable defaultMarker, Context context) {
		  super(boundCenterBottom(defaultMarker));
		  mContext = context;
	}
	@Override
	protected OverlayItem createItem(int arg0) {
		return listeMarqueur.get(arg0);
	}

	@Override
	public int size() {
		return listeMarqueur.size();
	}
	
	public void addMarqueur(OverlayItem item)
	{
		listeMarqueur.add(item);
		populate();
	}

	@Override
	protected boolean onTap(int arg0) {
		// TODO Auto-generated method stub
		OverlayItem item = listeMarqueur.get(arg0);
		AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
		dialog.setTitle(item.getTitle());
		dialog.setMessage(item.getSnippet());
		dialog.show();
		return true;
	}
}
