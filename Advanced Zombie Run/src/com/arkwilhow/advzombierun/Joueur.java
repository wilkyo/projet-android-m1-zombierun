package com.arkwilhow.advzombierun;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;

/**
 * Classe représentant la position d'un joueur joueur
 * @author ”Jean-Baptiste Perrin”
 *
 */
public class Joueur extends OverlayItem {

	/**
	 * Crée un marqueur joueur
	 * @param arg0 la position du joueur
	 * @param arg1 le titre
	 * @param arg2 le texte du marqueur
	 */
	public Joueur(GeoPoint arg0, String arg1, String arg2) {
		super(arg0, arg1, arg2);
		// TODO Auto-generated constructor stub
	}
	
	
	
}
