package com.arkwilhow.advzombierun;

import java.util.ArrayList;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;

import android.location.Location;


public class GameMaster {
	private Location joueur;
	private MarqueursJoueurs zombies;
	private int density;
	private int speed;
	private int life;
	private int alert;
	
	public GameMaster(Location joueur) {
		super();
		this.joueur = joueur;
	}

	public Location getJoueur() {
		return joueur;
	}

	public void setJoueur(Location joueur) {
		this.joueur = joueur;
	}

	public MarqueursJoueurs getZombies() {
		return zombies;
	}

	public void setZombies(MarqueursJoueurs zombies) {
		this.zombies = zombies;
	}


	/**
	 * Creer un zombie (i.e. un OverlayItem) dans un cercle de prer metres
	 * autour de la location en parametre 
	 * Il faura sans doute modifier cette fonction de sorte que les zombis ne puissent pas apparaitre
	 * trop pres du joueur
	 * @param location
	 * @param prer
	 * @return 
	 */
	public OverlayItem creer_zombi(int prer){
		//Un degre = 111 300 metres
		//111 300/50 = 2226
		//1/2226 = 0.00044923629 degre
		//En microdegre -> ~= 449 microdegrees

		/*
		 * Pour générer un point dans un cercle autour de la location(x0,y0) du joueur
		 * Tout d'abord créer deux valeurs aléatoires u et v entre [0,1]
		 * Créer r en microdegrés avec le calcul précédent (pour 50m r = 449)
		 * Ensuite :
		 *w = r * sqrt(u)
		 *t = 2 * Pi * v
		 *x = w * cos(t) 
		 *y = w * sin(t)
		 *x' = x / cos(y0)
		 *Le point voulu est à (x'+x0, y+y0)
		 */

		float u = (float)(Math.random() * ((1 - 0) + 1));
		float v = (float)(Math.random() * ((1 - 0) + 1));
		int x0 =(int) joueur.getLatitude();
		int y0 =(int) joueur.getLongitude();
		double r = (1/(111300/prer))*1e6;
		double w = r * Math.sqrt(u);
		double t = 2 * Math.PI * v;
		double x = w * Math.cos(t);
		double y = w * Math.sin(t);
		double xx = x / Math.cos(x0);
		GeoPoint point = new GeoPoint((int)xx+x0, (int)y+y0);
		OverlayItem zomb = new OverlayItem(point, "Beuh", "Arf");
		return zomb;
	}

	//renvoi une liste de OverlayItem, pour marquer les zombis
	public ArrayList<OverlayItem> liste_zombis(){
		ArrayList<OverlayItem> zombis = new ArrayList<OverlayItem>();
		for(int i = 0 ;i < density;++i){
			//On suppose que les zombies apparaissent dons une zone de 100m autour du joueur
			zombis.add(creer_zombi(100));
		}
		return zombis;
	}

	/**
	 * 
	 * @param m
	 * @return la nouvelle liste des marqueurs (i.e. les zombis après déplacement)
	 */
	//Gere les déplacements des zombis
	public ArrayList<OverlayItem> deplacement(MarqueursJoueurs m){
		//On recupere la liste des zombis que contient un marqueur
		ArrayList<Joueur> zombis = m.getListeMarqueur();
		ArrayList<OverlayItem> new_zombis = new ArrayList<OverlayItem>();
		Location dest = new Location("");
		int d = speed; 
		//Si speed est exprime en m/s et qu'on a un refresh de la carte toutes les secondes
		//Alors la distane parcourue est égale a speed
		double angle,anglerad;
		double lat2,long2;
		double lat1,long1;
		double radiusTerreMetres = 6371010;
		double distRatio = d / radiusTerreMetres;
		GeoPoint g;
		OverlayItem ov;
		for(OverlayItem o : zombis){
			/*
			 *Deplacement du zombi en direction du joueur en fonction de l'attribut speed
			 *On suppose que speed est exprimé en m/s
			 *On commene par creer un autre objet Location afin d'obtenir l'angle entre les deux points
			 *Puis on applique la formule suivante :
			 *φ2 = asin( sin(φ1)*cos(d/R) + cos(φ1)*sin(d/R)*cos(θ) )
			 *λ2 = λ1 + atan2( sin(θ)*sin(d/R)*cos(φ1), cos(d/R)−sin(φ1)*sin(φ2) )
			 *avec φ la latitude, λ la longitude, 
			 *θ l'angle (en radians, dans le sens des aiguilles depuis le nord), 
			 *d la distance parcourue, R le radius de la Terre (d/R la distance angulaire, en radians)
			 */
			lat1 = o.getPoint().getLatitudeE6()/1E6F;
			long1 = o.getPoint().getLongitudeE6()/1E6F;
			dest.setLatitude(lat1);
			dest.setLongitude(long1);
			angle = joueur.bearingTo(dest);//Obtention de l'angle en degrees entre les deux points
			anglerad = DegreesToRadians(angle);
			lat2 = Math.asin(Math.sin(lat1)*Math.cos(distRatio) 
					+ Math.cos(lat1) * Math.sin(distRatio)*Math.cos(anglerad));
			long2 = long1 + Math.atan2(Math.sin(anglerad)*Math.sin(distRatio)*Math.cos(lat1),
					Math.cos(distRatio)-Math.sin(lat1)*Math.sin(lat2));
			g = new GeoPoint((int)(lat2*1E6),(int)(long2*1E6));
			Location l = new Location("");
			l.setLatitude(o.getPoint().getLatitudeE6()/1E6F);
			l.setLongitude(o.getPoint().getLongitudeE6()/1E6F);
			if(joueur.distanceTo(l) <= d)
				joueur_touche();
			ov = new OverlayItem(g, "Zombie", "Beuh");
			new_zombis.add(ov);
		}
		return new_zombis;
	}

	public static double DegreesToRadians(double degrees)
	{
		double degToRadFactor = Math.PI / 180;
		return degrees * degToRadFactor;
	}

 /**
  * Modifie la liste en parametre en recreant des zombis près du joueur lorsque ceux-ci sont trop loin
  * @param zombis
  * @return
  */
	public void verifie_zombis(ArrayList<OverlayItem> zombis){
		Location l = new Location("");
		double lat1,long1;
		int r = 100; //La distance maximale à laquelle un zombie peut se trouver
		//Pour l'instant codé en "dur" il faudra sans doute la calculer en fonction de la difficulté
		for(OverlayItem o : zombis){
			lat1 = o.getPoint().getLatitudeE6()/1E6F;
			long1 = o.getPoint().getLongitudeE6()/1E6F;
			l.setLatitude(lat1);
			l.setLongitude(long1);
			if(joueur.distanceTo(l) > r){
				o = creer_zombi(r);
			}
		}
	}
	
	/**
	 * Déclenche les evenements après qu'un zombi ait rattrapé le joueur
	 */
	public void joueur_touche(){
		if(life == 1){
			//On stoppe le jeu
		}
		else {
			//On diminue la vie du joueur
			life--;
			//Puis on envoi un message
			switch(alert){
			//Ici on envoi le message selon le choix du joueur
			}
		}
	}
}
