package com.arkwilhow.advzombierun;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;

/**
 * Classe représentant la position d'un joueur joueur
 * 
 */
public class Joueur extends OverlayItem {

	/**
	 * Crée un marqueur joueur
	 * 
	 * @param point
	 *            la position du joueur
	 * @param title
	 *            le titre
	 * @param snippet
	 *            le texte du marqueur
	 */
	public Joueur(GeoPoint point, String title, String snippet) {
		super(point, title, snippet);
	}

}
