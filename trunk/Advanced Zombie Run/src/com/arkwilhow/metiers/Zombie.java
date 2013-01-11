package com.arkwilhow.metiers;

import android.content.Context;
import com.arkwilhow.advzombierun.R;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;

/**
 * Classe permettant la gestion d'un zombie
 * 
 * @author ”Jean-Baptiste Perrin”
 * 
 */
public class Zombie extends OverlayItem {

	/**
	 * état du zombie, vrai en alerte, faux sinon
	 */
	private boolean enAlerte;

	/**
	 * Crée un nouveau zombie
	 * 
	 * @param point
	 *            la position du zombie
	 * @param arg1
	 *            le titre du marque
	 * @param arg2
	 *            le messagedu marqueur
	 */
	public Zombie(GeoPoint point, String arg1, String arg2) {
		super(point, arg1, arg2);
		enAlerte = false;
	}

	/**
	 * permet d'obtenir si le zombie est en alerte
	 * 
	 * @return l'état du zombie
	 */
	public boolean isEnAlerte() {
		return enAlerte;
	}

	/**
	 * permet de modifier l'état du zombie
	 * 
	 * @param enAlerte
	 *            le nouvel éétat du zombie
	 */
	public void setEnAlerte(boolean enAlerte, Context mContext) {
		this.enAlerte = enAlerte;
		/*if (enAlerte) {
			setMarker(mContext.getResources().getDrawable(
					R.drawable.marqueurzombi1));
		}*/
	}

}
