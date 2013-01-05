package com.arkwilhow.metiers;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;

/**
 * Classe permettant la gestion d'un zombie
 * @author ”Jean-Baptiste Perrin”
 *
 */
public class Zombie extends OverlayItem {

	/**
	 * état du zombie, vrai en alerte, faux sinon
	 */
	private boolean en_alerte;

	/**
	 * Crée un nouveau zombie
	 * @param arg0 la position du zombie
	 * @param arg1 le titre du marque
	 * @param arg2 le messagedu marqueur
	 */
	public Zombie(GeoPoint arg0, String arg1, String arg2) {
		super(arg0, arg1, arg2);
		en_alerte = false;
	}

	/**
	 * permet d'obtenir si le zombie est en alerte
	 * @return l'état du zombie
	 */
	public boolean isEn_alerte() {
		return en_alerte;
	}

	/**
	 * permet de modifier l'état du zombie
	 * @param en_alerte le nouvel éétat du zombie
	 */
	public void setEn_alerte(boolean en_alerte) {
		this.en_alerte = en_alerte;
	}

}
