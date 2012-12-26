package com.arkwilhow.advzombierun;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;


public class Zombie extends OverlayItem {

	private boolean en_alerte;


	public Zombie(GeoPoint arg0, String arg1, String arg2) {
		super(arg0, arg1, arg2);
		en_alerte = false;
	}

	public boolean isEn_alerte() {
		return en_alerte;
	}

	public void setEn_alerte(boolean en_alerte) {
		this.en_alerte = en_alerte;
	}

}
