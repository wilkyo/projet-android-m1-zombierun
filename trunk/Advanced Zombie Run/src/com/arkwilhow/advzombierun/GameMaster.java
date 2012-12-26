package com.arkwilhow.advzombierun;

import java.util.ArrayList;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Vibrator;


public class GameMaster {
	private MarqueursJoueurs joueurs;
	private MarqueursZombies zombies;
	//private final static int [] density_array = new int[] {};
	private int density;
	private int speed;
	private int life;
	private int alert;
	private Context context;



	public GameMaster(MarqueursJoueurs joueurs, MarqueursZombies zombies,
			int density, int speed, int life, int alert, Context context) {
		super();
		this.joueurs = joueurs;
		this.zombies = zombies;
		this.density = density;
		this.speed = speed;
		this.life = life;
		this.alert = alert;
		this.context = context;
	}



	public MarqueursJoueurs getJoueurs() {
		return joueurs;
	}

	public void setJoueurs(MarqueursJoueurs joueurs) {
		this.joueurs = joueurs;
	}

	public MarqueursZombies getZombies() {
		return zombies;
	}

	public void setZombies(MarqueursZombies zombies) {
		this.zombies = zombies;
	}

	public int getDensity() {
		return density;
	}

	public void setDensity(int density) {
		this.density = density;
	}

	public int getSpeed() {
		return speed;
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}

	public int getLife() {
		return life;
	}

	public void setLife(int life) {
		this.life = life;
	}

	public int getAlert() {
		return alert;
	}

	public void setAlert(int alert) {
		this.alert = alert;
	}

	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
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
	public Zombie creer_zombi(int prer,Joueur joueur){
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
		int x0 =(int) joueur.getPoint().getLatitudeE6();
		int y0 =(int) joueur.getPoint().getLongitudeE6();
		double r = (1/(111300/prer))*1e6;
		double w = r * Math.sqrt(u);
		double t = 2 * Math.PI * v;
		double x = w * Math.cos(t);
		double y = w * Math.sin(t);
		double xx = x / Math.cos(x0);
		GeoPoint point = new GeoPoint((int)xx+x0, (int)y+y0);
		//OverlayItem zomb = new OverlayItem(point, "Beuh", "Arf");
		Zombie zomb = new Zombie(point, "Beuh", "Arf");
		return zomb;
	}

	//renvoi une liste de OverlayItem, pour marquer les zombis
	public void liste_zombis(){
		for(int i = 0 ;i < density;++i){
			//On supposeindex que les zombies apparaissent dons une zone de 100m autour du joueur
			zombies.addMarqueur(creer_zombi(100,joueurs.getListeMarqueur().get(0)));
		}
	}

	/**
	 * 
	 * @param m
	 * @return la nouvelle liste des marqueurs (i.e. les zombis après déplacement)
	 */
	//Gere les déplacements des zombis
	public void deplacement(){
		//On recupere la liste des zombis que contient un marqueur
		ArrayList<Zombie> zombis = zombies.getListeMarqueur();
		ArrayList<Zombie> new_zombis = new ArrayList<Zombie>();
		Location dest = new Location("");
		Location lo = new Location("");
		Location joueur = new Location("");
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
		for(Zombie z : zombis){
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
			lat1 = z.getPoint().getLatitudeE6()/1E6F;
			long1 = z.getPoint().getLongitudeE6()/1E6F;
			dest.setLatitude(lat1);
			dest.setLongitude(long1);

			//On récupére le joueur le plus proche du zombi en cours
			lo.setLatitude(lat1);
			lo.setLongitude(long1);
			Joueur j = plusProcheJoueur(lo);
			joueur.setLatitude(j.getPoint().getLatitudeE6());
			joueur.setLongitude(j.getPoint().getLongitudeE6());

			angle = joueur.bearingTo(dest);//Obtention de l'angle en degrees entre les deux points
			anglerad = DegreesToRadians(angle);
			lat2 = Math.asin(Math.sin(lat1)*Math.cos(distRatio) 
					+ Math.cos(lat1) * Math.sin(distRatio)*Math.cos(anglerad));
			long2 = long1 + Math.atan2(Math.sin(anglerad)*Math.sin(distRatio)*Math.cos(lat1),
					Math.cos(distRatio)-Math.sin(lat1)*Math.sin(lat2));
			g = new GeoPoint((int)(lat2*1E6),(int)(long2*1E6));
			Location l = new Location("");
			l.setLatitude(z.getPoint().getLatitudeE6()/1E6F);
			l.setLongitude(z.getPoint().getLongitudeE6()/1E6F);
			if(joueur.distanceTo(l) <= d)
				joueur_touche();
			Zombie zo = new Zombie(g, "Zombie", "Beuh");
			zo.setEn_alerte(z.isEn_alerte());
			new_zombis.add(zo);
		}
		zombies.setListeMarqueur(new_zombis);
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
	public void verifie_zombis(){
		ArrayList<Zombie> zombis = zombies.getListeMarqueur();
		Location l = new Location("");
		double lat1,long1;
		int r = 100; //La distance maximale à laquelle un zombie peut se trouver
		//Pour l'instant codée en "dur" il faudra sans doute la calculer en fonction de la difficulté
		boolean trop_loin = false;
		for(Zombie z : zombis){
			lat1 = z.getPoint().getLatitudeE6()/1E6F;
			long1 = z.getPoint().getLongitudeE6()/1E6F;
			l.setLatitude(lat1);
			l.setLongitude(long1);
			for(Joueur j : joueurs.getListeMarqueur()){
				Location joueur = new Location("");
				joueur.setLatitude(j.getPoint().getLatitudeE6());
				joueur.setLongitude(j.getPoint().getLongitudeE6());
				if(joueur.distanceTo(l) > r){
					trop_loin = true;
				}
			}
			if(trop_loin){
				zombies.addMarqueur(creer_zombi(100,joueurs.getListeMarqueur().get(0)));
				//Il faut aussi supprimer le zombie
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
	        Vibrator vib=(Vibrator)context.getSystemService(Context.VIBRATOR_SERVICE);
	        vib.vibrate(1000);
		}
	}

	//Renvoi le joueur le plus proche de la Location en paramètres
	public Joueur plusProcheJoueur(Location loc){
		Joueur j = joueurs.getListeMarqueur().get(0);
		Location l = new Location("");
		Location lo = new Location("");
		
		l.setLatitude(j.getPoint().getLatitudeE6());
		l.setLongitude(j.getPoint().getLongitudeE6());
		
		for(Joueur jo : joueurs.getListeMarqueur()){	
			lo.setLatitude(jo.getPoint().getLatitudeE6());
			lo.setLongitude(jo.getPoint().getLongitudeE6());
			if(lo.distanceTo(loc) < l.distanceTo(loc)){
				l.setLatitude(jo.getPoint().getLatitudeE6());
				l.setLongitude(jo.getPoint().getLongitudeE6());
			}
		}
		
		return j;
	}
}