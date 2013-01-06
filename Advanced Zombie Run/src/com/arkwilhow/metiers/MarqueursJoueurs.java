package com.arkwilhow.metiers;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

@SuppressWarnings("rawtypes")
/**
 * Classe gérant la liste des joueurs
 * @author ”Jean-Baptiste Perrin”
 *
 */
public class MarqueursJoueurs extends ItemizedOverlay {

	private ArrayList<Joueur> listeMarqueur = new ArrayList<Joueur>();
	private Context mContext;

	public MarqueursJoueurs(Drawable arg0) {
		super(boundCenterBottom(arg0));
		// TODO Auto-generated constructor stub
	}

	public ArrayList<Joueur> getListeMarqueur() {
		return listeMarqueur;
	}

	public void setListeMarqueur(ArrayList<Joueur> listeMarqueur) {
		this.listeMarqueur = listeMarqueur;
	}

	public MarqueursJoueurs(Drawable defaultMarker, Context context) {
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

	public void addMarqueur(Joueur item) {
		listeMarqueur.add(item);
		populate();
	}

	@Override
	public boolean onTouchEvent(MotionEvent arg0, MapView arg1) {
		// super.onTouchEvent(arg0, arg1);
		return false;
	}

	@Override
	public boolean onTap(GeoPoint arg0, MapView arg1) {
		// super.onTap(arg0, arg1);
		return false;
	}

	@Override
	protected boolean onTap(int arg0) {
		OverlayItem item = listeMarqueur.get(arg0);
		AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
		dialog.setTitle(item.getTitle());
		dialog.setMessage(item.getSnippet());
		dialog.show();
		return true;
	}

	public void clear() {
		listeMarqueur.clear();
	}
}
