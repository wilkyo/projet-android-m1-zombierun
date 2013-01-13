package com.arkwilhow.advzombierun;

import java.util.ArrayList;
import com.arkwilhow.metiers.Joueur;
import com.arkwilhow.metiers.MarqueurDestination;
import com.arkwilhow.metiers.MarqueursJoueurs;
import com.arkwilhow.metiers.MarqueursZombies;
import com.arkwilhow.metiers.MarqueursZombiesAware;
import com.arkwilhow.metiers.Zombie;
import com.google.android.maps.GeoPoint;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Vibrator;
import android.util.Log;
import android.widget.Toast;

public class GameMaster {
	private MarqueursJoueurs marqueursJoueurs;
	private MarqueursZombies marqueursZombies;
	private MarqueursZombiesAware marqueursZombiesAware;
	private final static int[] density_array = new int[] { 10, 20, 30, 40 };
	private int density;
	private final static int[] speed_array = new int[] { 1, 2, 3, 4 };
	private int speed;
	private final static int[] life_array = new int[] { 1, 3, 10, 42, 100, 9001 };
	private int life;
	private int alert;
	private Context mContext;
	private MarqueurDestination marqueurDestination;
	private int refresh_time;
	private String TAG = "GameMaster";

	public GameMaster(MarqueursJoueurs marqueursJoueurs,
			MarqueursZombies marqueursZombies, int density, int speed,
			int life, int alert, Context context, int refreshTime) {
		this.marqueursJoueurs = marqueursJoueurs;
		this.marqueursZombies = marqueursZombies;
		this.density = density;
		this.speed = speed;
		this.life = life;
		this.alert = alert;
		this.mContext = context;
		this.marqueurDestination = new MarqueurDestination(mContext
				.getResources().getDrawable(R.drawable.marqueur_destination));
		this.marqueursZombiesAware = new MarqueursZombiesAware(mContext
				.getResources().getDrawable(R.drawable.marqueurzombi1),
				mContext);
		this.refresh_time = refreshTime;
	}

	public MarqueursJoueurs getMarqueursJoueurs() {
		return marqueursJoueurs;
	}

	/*
	 * public void setMarqueursJoueurs(MarqueursJoueurs joueurs) {
	 * this.marqueursJoueurs = joueurs; }
	 */

	public MarqueursZombies getMarqueursZombies() {
		return marqueursZombies;
	}

	/*
	 * public void setMarqueursZombies(MarqueursZombies zombies) {
	 * this.marqueursZombies = zombies; }
	 */

	public MarqueursZombiesAware getMarqueursZombiesAware() {
		return marqueursZombiesAware;
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

	public Context getMContext() {
		return mContext;
	}

	public void setMContext(Context context) {
		this.mContext = context;
	}

	public boolean zombisVisibles() {
		// Possibilité de rajouter des cas
		return marqueursJoueurs.getDestination() != null;
	}

	public MarqueurDestination getMarqueurDestination() {
		if (marqueursJoueurs.getMarkerDestination() == null) {
			return null;
		} else {
			if (marqueurDestination.size() == 0) {
				marqueurDestination.addMarqueur(marqueursJoueurs
						.getMarkerDestination());
			}
			return marqueurDestination;
		}
	}

	/**
	 * Creer un zombie (i.e. un OverlayItem) dans un cercle de prer metres
	 * autour de la location en parametre Il faura sans doute modifier cette
	 * fonction de sorte que les zombis ne puissent pas apparaitre trop pres du
	 * joueur
	 * 
	 * @param location
	 *            la position du joueur
	 * @param distanceAuJoueur
	 *            la distance
	 * @return le zombie ainsi crée
	 */
	public Zombie creerZombi(int distanceAuJoueur, Joueur joueur) {
		// Un degre = 111 300 metres
		// 111 300/50 = 2226
		// 1/2226 = 0.00044923629 degre
		// En microdegre -> ~= 449 microdegrees

		/*
		 * Pour générer un point dans un cercle autour de la location(x0,y0) du
		 * joueur Tout d'abord créer deux valeurs aléatoires u et v entre [0,1]
		 * Créer r en microdegrés avec le calcul précédent (pour 50m r = 449)
		 * Ensuite :w = r * sqrt(u)t = 2 * Pi * vx = w * cos(t)y = w * sin(t)x'
		 * = x / cos(y0)Le point voulu est à (x'+x0, y+y0)
		 */

		float u = (float) Math.random();
		float v = (float) Math.random();
		int x0 = (int) joueur.getPoint().getLatitudeE6();
		int y0 = (int) joueur.getPoint().getLongitudeE6();
		double r = (1 * 1e6 / (111300 / distanceAuJoueur));
		double w = r * Math.sqrt(u);
		double t = 2 * Math.PI * v;
		double x = w * Math.cos(t);
		double y = w * Math.sin(t);
		double xx = x / Math.cos(x0);
		GeoPoint point = new GeoPoint((int) xx + x0, (int) y + y0);
		// OverlayItem zomb = new OverlayItem(point, "Beuh", "Arf");
		Zombie zomb = new Zombie(point, "Beuh", "Arf");
		return zomb;
	}

	// renvoi une liste de OverlayItem, pour marquer les zombis
	public void creerListeZombis() {
		for (int i = 0; i < density_array[density]; ++i) {
			// On suppose que les zombies apparaissent dons une zone de
			// 100m autour du joueur
			marqueursZombies.addMarqueur(creerZombi(200, marqueursJoueurs
					.getListeMarqueur().get(0)));
		}
	}

	/**
	 * 
	 * @param m
	 * @return la nouvelle liste des marqueurs (i.e. les zombis après
	 *         déplacement)
	 */
	// Gere les déplacements des zombis
	public void deplacement(Location[] positions) {
		updatePositionJoueurs(positions);

		if (marqueursJoueurs.getDestination() == null)
			return;

		// On recupere la liste des zombis que contient un marqueur
		Zombie[] zombisArray = marqueursZombies.getListeMarqueur().toArray(
				new Zombie[0]);
		Zombie[] zombiesAwareArray = marqueursZombiesAware.getListeMarqueur()
				.toArray(new Zombie[0]);
		marqueursZombies.clear();
		marqueursZombiesAware.clear();
		// MarqueursZombies new_zombis = new
		// MarqueursZombies(mContext.getResources().getDrawable(R.drawable.marqueurzombi0),mContext);
		Location positionPrecedente = new Location("");
		Location locationDistante = new Location("");
		int d = speed_array[speed] * refresh_time;
		// d = v * t
		double angle, anglerad;
		double lat2, long2;
		double lat1, long1;
		double radiusTerreMetres = 6371010;
		double distRatio;

		// TODO Le calcul des déplacements a un problème
		// Boucle des zombis alertés
		for (Zombie z : zombiesAwareArray) {
			if (z.isEnAlerte()) {
				/*
				 * Deplacement du zombi en direction du joueur en fonction de
				 * l'attribut speed. On suppose que speed est exprimé en m/s.On
				 * commence par creer un autre objet Location afin d'obtenir
				 * l'angle entre les deux points.Puis on applique la formule
				 * suivante :φ2 = asin( sin(φ1)*cos(d/R) +
				 * cos(φ1)*sin(d/R)*cos(θ) )λ2 = λ1 + atan2(
				 * sin(θ)*sin(d/R)*cos(φ1), cos(d/R)−sin(φ1)*sin(φ2) )avec φ la
				 * latitude, λ la longitude,θ l'angle (en radians, dans le sens
				 * des aiguilles depuis le nord),d la distance parcourue, R le
				 * radius de la Terre (d/R la distance angulaire, en radians)
				 */
				lat1 = z.getPoint().getLatitudeE6() / 1e6;
				long1 = z.getPoint().getLongitudeE6() / 1e6;
				positionPrecedente.setLatitude(lat1);
				positionPrecedente.setLongitude(long1);

				// On récupére le joueur le plus proche du zombi en cours
				locationDistante.setLatitude(lat1);
				locationDistante.setLongitude(long1);
				Joueur j = plusProcheJoueur(locationDistante);

				Location joueur = new Location(""); // Les zombies cherchent un
													// joueur
				joueur.setLatitude(((double) j.getPoint().getLatitudeE6()) / 1e6);
				joueur.setLongitude(((double) j.getPoint().getLongitudeE6()) / 1e6);

				angle = joueur.bearingTo(positionPrecedente);
				// Obtention de l'angle en degrees
				// entre les deux points
				anglerad = DegreesToRadians(angle);
				distRatio = d / radiusTerreMetres;
				lat2 = Math.asin(Math.sin(lat1) * Math.cos(distRatio)
						+ Math.cos(lat1) * Math.sin(distRatio)
						* Math.cos(anglerad));
				long2 = long1
						+ Math.atan2(
								Math.sin(anglerad) * Math.sin(distRatio)
										* Math.cos(lat1),
								Math.cos(distRatio) - Math.sin(lat1)
										* Math.sin(lat2));
				GeoPoint g = new GeoPoint((int) (lat2 * 1e6),
						(int) (long2 * 1e6));

				Location positionZombi = new Location("");
				positionZombi.setLatitude(z.getPoint().getLatitudeE6() / 1E6F);
				positionZombi
						.setLongitude(z.getPoint().getLongitudeE6() / 1E6F);

				if (joueur.distanceTo(positionZombi) <= speed_array[speed]) {
					Toast.makeText(
							mContext,
							"Touched " + joueur.distanceTo(positionZombi)
									+ ", D = " + d, Toast.LENGTH_SHORT).show();
					joueurTouched();
				} else {
					Zombie zo = new Zombie(g, z.getTitle(), z.getSnippet());
					zo.setEnAlerte(z.isEnAlerte(), mContext);
					marqueursZombiesAware.addMarqueur(zo);
				}
			} else {
				Log.d(TAG, "aware pas en alerte");
				Toast.makeText(mContext, "Zombie Aware non alerté",
						Toast.LENGTH_SHORT).show();
			}
		}

		// Boucle des zombis non alertés
		for (Zombie z : zombisArray) {
			if (z.isEnAlerte()) {
				Log.d(TAG, "notaware, en alerte");
				Toast.makeText(mContext, "Zombie NonAware en alerte",
						Toast.LENGTH_SHORT).show();
			} else {
				for (int i = 0; i < positions.length; i++) {
					if (positions[i].distanceTo(locationDistante) < 40) {
						Toast.makeText(
								mContext,
								"detected: "
										+ positions[i]
												.distanceTo(locationDistante),
								Toast.LENGTH_SHORT).show();
						z.setEnAlerte(true, mContext);
						Zombie nouv = new Zombie(z.getPoint(), z.getTitle(),
								z.getSnippet());
						nouv.setEnAlerte(true, mContext);
						marqueursZombiesAware.addMarqueur(nouv);
					} else {
						// Déplacement aléatoire
						lat1 = z.getPoint().getLatitudeE6() / 1e6F;
						long1 = z.getPoint().getLongitudeE6() / 1e6F;
						positionPrecedente.setLatitude(lat1);
						positionPrecedente.setLongitude(long1);

						// On récupére un point autour aléatoirement
						locationDistante.setLatitude(lat1
								+ (int) ((Math.random() * 2 - 1)));
						locationDistante.setLongitude(long1
								+ (int) ((Math.random() * 2 - 1)));

						angle = locationDistante.bearingTo(positionPrecedente);
						// Obtention de l'angle en degrees
						// entre les deux points
						anglerad = DegreesToRadians(angle);
						distRatio = (d / 2) / radiusTerreMetres;
						lat2 = Math.asin(Math.sin(lat1) * Math.cos(distRatio)
								+ Math.cos(lat1) * Math.sin(distRatio)
								* Math.cos(anglerad));
						long2 = long1
								+ Math.atan2(
										Math.sin(anglerad)
												* Math.sin(distRatio)
												* Math.cos(lat1),
										Math.cos(distRatio) - Math.sin(lat1)
												* Math.sin(lat2));
						GeoPoint g = new GeoPoint((int) (lat2 * 1e6),
								(int) (long2 * 1e6));

						marqueursZombies.addMarqueur(new Zombie(g,
								z.getTitle(), z.getSnippet()));
					}
				}
			}
		}
	}

	private void updatePositionJoueurs(Location[] positionJoueurs) {
		try {
			Joueur[] joueurs = marqueursJoueurs.getListeMarqueur().toArray(
					new Joueur[0]);
			marqueursJoueurs.clear();

			for (int i = 0; i < joueurs.length; i++) {
				Joueur j = joueurs[i];
				GeoPoint nouveauPoint = new GeoPoint(
						(int) (positionJoueurs[i].getLatitude() * 1e6),
						(int) (positionJoueurs[i].getLongitude() * 1e6));
				marqueursJoueurs.addMarqueur(new Joueur(nouveauPoint, j
						.getTitle(), j.getSnippet()));
			}

			GeoPoint destination = getMarqueursJoueurs().getDestination();
			if (destination != null) {
				Location l = new Location("");
				l.setLatitude(destination.getLatitudeE6() / 1E6F);
				l.setLongitude(destination.getLongitudeE6() / 1E6F);
				if (positionJoueurs[0].distanceTo(l) < 5)
					gagner();
			}
		} catch (Exception e) {
			Toast.makeText(mContext, "updatePositionJoueurs: " + e.toString(),
					Toast.LENGTH_LONG).show();
		}
	}

	public void gagner() {
		Toast.makeText(mContext, "Victory", Toast.LENGTH_LONG).show();
		Intent i = new Intent(); 
		i.setClass(mContext, GameEndActivity.class);
		i.putExtra("loser", false);
		mContext.startActivity(i);
		/*
		 * TODO GameEnd.setVictory(true); Intent i = new Intent(mContext,
		 * GameEndActivity.class); i.start(); mContext.finish();
		 */
	}

	public static double DegreesToRadians(double degrees) {
		double degToRadFactor = Math.PI / 180;
		return degrees * degToRadFactor;
	}

	/**
	 * Modifie la liste en parametre en recreant des zombis près du joueur
	 * lorsque ceux-ci sont trop loin
	 * 
	 */
	public void verifieZombis() {
		ArrayList<Zombie> zombis = marqueursZombies.getListeMarqueur();
		Location l = new Location("");
		double lat1, long1;
		int r = 100; // La distance maximale à laquelle un zombie peut se
						// trouver
		// Pour l'instant codée en "dur" il faudra sans doute la calculer en
		// fonction de la difficulté
		Location joueur = new Location("");
		boolean trop_loin = false;
		for (Zombie z : zombis) {
			lat1 = z.getPoint().getLatitudeE6() / 1E6F;
			long1 = z.getPoint().getLongitudeE6() / 1E6F;
			l.setLatitude(lat1);
			l.setLongitude(long1);
			for (Joueur j : marqueursJoueurs.getListeMarqueur()) {
				joueur.setLatitude(j.getPoint().getLatitudeE6());
				joueur.setLongitude(j.getPoint().getLongitudeE6());
				if (joueur.distanceTo(l) > r) {
					trop_loin = true;
				}
			}
			if (trop_loin) {
				marqueursZombies.addMarqueur(creerZombi(100, marqueursJoueurs
						.getListeMarqueur().get(0)));
				// Il faut aussi supprimer le zombie
			}
		}
	}

	/**
	 * Déclenche les evenements après qu'un zombi ait rattrapé le joueur
	 */
	public void joueurTouched() {
		if (life_array[life] <= 1) {
			Toast.makeText(mContext, "Echec", Toast.LENGTH_SHORT).show();
			// On stoppe le jeu
			Intent i = new Intent(); 
			i.setClass(mContext, GameEndActivity.class);
			i.putExtra("loser", true);
			mContext.startActivity(i);
			
		} else {
			// On diminue la vie du joueur
			life--;
			if (life == 0){
				Intent i = new Intent(); 
				i.setClass(mContext, GameEndActivity.class);
				i.putExtra("loser", true);
				mContext.startActivity(i);
			}
			// Puis on envoi un message
			switch (alert) {
			// Ici on envoi le message selon le choix du joueur
			}
			Vibrator vib = (Vibrator) mContext
					.getSystemService(Context.VIBRATOR_SERVICE);
			vib.vibrate(1000);
		}
	}

	// Renvoi le joueur le plus proche de la Location en paramètres
	public Joueur plusProcheJoueur(Location loc) {
		Joueur j = marqueursJoueurs.getListeMarqueur().get(0);
		Location l = new Location("");
		Location lo = new Location("");

		l.setLatitude(j.getPoint().getLatitudeE6());
		l.setLongitude(j.getPoint().getLongitudeE6());

		for (Joueur jo : marqueursJoueurs.getListeMarqueur()) {
			lo.setLatitude(jo.getPoint().getLatitudeE6());
			lo.setLongitude(jo.getPoint().getLongitudeE6());
			if (lo.distanceTo(loc) < l.distanceTo(loc)) {
				l.setLatitude(jo.getPoint().getLatitudeE6());
				l.setLongitude(jo.getPoint().getLongitudeE6());
			}
		}

		return j;
	}
}
