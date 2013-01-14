package com.arkwilhow.advzombierun;

import com.arkwilhow.metiers.Joueur;
import com.arkwilhow.metiers.MarqueurDestination;
import com.arkwilhow.metiers.MarqueursJoueurs;
import com.arkwilhow.metiers.MarqueursZombies;
import com.arkwilhow.metiers.MarqueursZombiesAware;
import com.arkwilhow.metiers.Zombie;
import com.google.android.maps.GeoPoint;
import android.content.Context;
import android.location.Location;
import android.os.Vibrator;
import android.widget.Toast;

public class GameMaster {

	/**
	 * Référence vers le contexte de la Map
	 */
	private Context mContext;

	/**
	 * Couche des Joueurs
	 */
	private MarqueursJoueurs marqueursJoueurs;
	/**
	 * Couche de la destination
	 */
	private MarqueurDestination marqueurDestination;
	/**
	 * Couche des Zombis non alertés
	 */
	private MarqueursZombies marqueursZombies;
	/**
	 * Couche des Zombis alertés
	 */
	private MarqueursZombiesAware marqueursZombiesAware;

	/**
	 * Tableau des densités en fonction du choix du Spinner
	 */
	private final static int[] density_array = new int[] { 10, 20, 30, 40 };
	/**
	 * Tableau des vitesses en fonction du choix du Spinner
	 */
	private final static int[] speed_array = new int[] { 1, 2, 3, 4 };
	/**
	 * Tableau des timers en fonction du choix du Spinner
	 */
	private final static int[] timer_array = new int[] { 24 * 3600 * 1000,
			60 * 1000, 120 * 1000, 300 * 1000, 600 * 1000, 900 * 1000,
			1800 * 1000 };
	/**
	 * Tableau des points de vie en fonction du choix du Spinner
	 */
	private final static int[] life_array = new int[] { 1, 3, 10, 42, 100, 9001 };

	/**
	 * Indice de la densité choisie
	 */
	private int density;
	/**
	 * Indice de la vitesse choisie
	 */
	private int speed;
	/**
	 * Indice du timer choisie
	 */
	private int timer;
	/**
	 * Indice des vies choisies
	 */
	private int life;
	/**
	 * Indice de l'alerte choisie
	 */
	private int alert;

	/**
	 * Temps écoulé
	 */
	private int elapsedTime = 0;
	/**
	 * Vie restante
	 */
	private int vieRestante;

	/**
	 * Etat de la partie; 1 => Victoire; 2 ou plus => Défaite
	 */
	private int etat = 0;
	/**
	 * Temps de rafraishissement de la map
	 */
	private int refresh_time;

	/**
	 * La distance maximale en mètres à laquelle un zombie peut se créer
	 */
	private final static int RAYON_CREATION = 100;
	/**
	 * La distance maximale en mètres à laquelle un zombie peut se trouver. Au
	 * delà, il est supprimé
	 */
	private final static int RAYON_DANGER = 200;
	/**
	 * Distance en mètres à laquelle un zombi peut nous détecter
	 */
	private float RAYON_DETECTION = 40;

	public GameMaster(MarqueursJoueurs marqueursJoueurs,
			MarqueursZombies marqueursZombies, int density, int speed,
			int timer, int life, int alert, Context context, int refreshTime) {
		this.mContext = context;
		this.marqueursJoueurs = marqueursJoueurs;
		this.marqueurDestination = new MarqueurDestination(mContext
				.getResources().getDrawable(R.drawable.marqueur_destination));
		this.marqueursZombies = marqueursZombies;
		this.marqueursZombiesAware = new MarqueursZombiesAware(mContext
				.getResources().getDrawable(R.drawable.marqueurzombi1),
				mContext);
		this.density = density;
		this.speed = speed;
		this.timer = timer;
		this.life = life;
		this.alert = alert;
		this.vieRestante = life_array[life];
		this.refresh_time = refreshTime;
	}

	public boolean getVictoire() {
		return etat == 1;
	}

	public boolean getEchec() {
		return etat >= 2;
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
			marqueursZombies.addMarqueur(creerZombi(RAYON_CREATION,
					marqueursJoueurs.getListeMarqueur().get(0)));
		}
	}

	/**
	 * 
	 * @param m
	 * @return la nouvelle liste des marqueurs (i.e. les zombis après
	 *         déplacement)
	 */
	// Gere les déplacements des zombis
	public void deplacement(Location[] positionJoueurs) {
		updatePositionJoueurs(positionJoueurs);

		// On ne déplace pas les zombis tant que la destination n'a pas été
		// placée
		if (marqueursJoueurs.getDestination() == null)
			return;
		if (etat != 0)
			return;

		elapsedTime += refresh_time;
		if (timer_array[timer] - elapsedTime <= 0) {
			etat = 3;
			return;
		}

		// On recupere la liste des zombis que contient un marqueur
		Zombie[] zombisArray = marqueursZombies.getListeMarqueur().toArray(
				new Zombie[0]);
		Zombie[] zombiesAwareArray = marqueursZombiesAware.getListeMarqueur()
				.toArray(new Zombie[0]);
		marqueursZombies.clear();
		marqueursZombiesAware.clear();

		// Distance à parcourir en mètres
		double d = (((double) speed_array[speed]) * ((double) refresh_time) / 1000);
		// d = v * t
		double lat1, long1, lat2, long2;

		// Boucle des zombis alertés
		for (Zombie z : zombiesAwareArray) {
			// Deplacement du zombi en direction du joueur
			lat1 = z.getPoint().getLatitudeE6() / 1e6;
			long1 = z.getPoint().getLongitudeE6() / 1e6;
			Location positionPrecedente = new Location("");
			positionPrecedente.setLatitude(lat1);
			positionPrecedente.setLongitude(long1);

			// On récupére le joueur le plus proche du zombi en cours
			Location positionJoueur = plusProcheJoueur(positionPrecedente,
					positionJoueurs);
			// Les zombies cherchent un joueur
			// Location positionJoueur = new Location("");
			// positionJoueur.setLatitude(((double)
			// j.getPoint().getLatitudeE6()) / 1e6);
			// positionJoueur.setLongitude(((double)
			// j.getPoint().getLongitudeE6()) / 1e6);

			float distance = positionJoueur.distanceTo(positionPrecedente);
			lat2 = lat1 + d * (positionJoueur.getLatitude() - lat1) / distance;
			long2 = long1 + d * (positionJoueur.getLongitude() - long1)
					/ distance;

			GeoPoint g = new GeoPoint((int) (lat2 * 1e6), (int) (long2 * 1e6));

			if (distance <= d) {
				contactJoueur();
			} else {
				Zombie nouv = new Zombie(g, z.getTitle(), z.getSnippet());
				nouv.setEnAlerte(z.isEnAlerte(), mContext);
				marqueursZombiesAware.addMarqueur(nouv);
			}
		}

		// Les zombis non alertés se déplacent moins vite
		d /= 2;

		// Boucle des zombis non alertés
		for (Zombie z : zombisArray) {
			Location positionZombi = new Location("");
			positionZombi.setLatitude(z.getPoint().getLatitudeE6() / 1e6);
			positionZombi.setLongitude(z.getPoint().getLongitudeE6() / 1e6);
			for (int i = 0; i < positionJoueurs.length; i++) {
				if (positionJoueurs[i].distanceTo(positionZombi) < RAYON_DETECTION) {
					z.setEnAlerte(true, mContext);
					Zombie nouv = new Zombie(z.getPoint(), z.getTitle(),
							z.getSnippet());
					nouv.setEnAlerte(true, mContext);
					marqueursZombiesAware.addMarqueur(nouv);
				} else {
					// Déplacement aléatoire d'un zombi non alerté
					lat1 = z.getPoint().getLatitudeE6() / 1e6F;
					long1 = z.getPoint().getLongitudeE6() / 1e6F;
					positionZombi.setLatitude(lat1);
					positionZombi.setLongitude(long1);

					// On récupére un point autour aléatoirement
					Location positionDistante = new Location("");
					positionDistante
							.setLatitude(lat1 + (Math.random() * 2 - 1));
					positionDistante.setLongitude(long1
							+ (Math.random() * 2 - 1));

					float distance = positionDistante.distanceTo(positionZombi);
					lat2 = lat1 + d * (positionDistante.getLatitude() - lat1)
							/ distance;
					long2 = long1 + d
							* (positionDistante.getLongitude() - long1)
							/ distance;

					GeoPoint g = new GeoPoint((int) (lat2 * 1e6),
							(int) (long2 * 1e6));

					marqueursZombies.addMarqueur(new Zombie(g, z.getTitle(), z
							.getSnippet()));
				}
			}
		}

		verifieZombis(positionJoueurs);
	}

	/**
	 * Met à jour la position des joueurs sur leur couche.
	 * 
	 * @param positionJoueurs
	 *            Tableau des positions
	 */
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

	/**
	 * Modifie la liste en parametre en recreant des zombis près du joueur
	 * lorsque ceux-ci sont trop loin
	 * 
	 * @param positionJoueurs
	 *            Tableau des positions des Joueurs
	 */
	public void verifieZombis(Location[] positionJoueurs) {
		Zombie[] zombis = marqueursZombies.getListeMarqueur().toArray(
				new Zombie[0]);
		Zombie[] zombisAware = marqueursZombiesAware.getListeMarqueur()
				.toArray(new Zombie[0]);
		marqueursZombies.clear();
		marqueursZombiesAware.clear();

		Location positionZombi = new Location("");

		// Pour l'instant codée en "dur" il faudra sans doute la calculer en
		// fonction de la difficulté
		for (Zombie z : zombis) {
			boolean trop_loin = false;
			positionZombi.setLatitude(z.getPoint().getLatitudeE6() / 1e6F);
			positionZombi.setLongitude(z.getPoint().getLongitudeE6() / 1e6F);
			for (Location positionJoueur : positionJoueurs) {
				if (positionJoueur.distanceTo(positionZombi) > RAYON_DANGER) {
					trop_loin = true;
					break;
				}
			}
			if (!trop_loin) {
				marqueursZombies.addMarqueur(z);
			}
		}
		for (Zombie z : zombisAware) {
			boolean trop_loin = false;
			positionZombi.setLatitude(z.getPoint().getLatitudeE6() / 1e6F);
			positionZombi.setLongitude(z.getPoint().getLongitudeE6() / 1e6F);
			for (Location positionJoueur : positionJoueurs) {
				if (positionJoueur.distanceTo(positionZombi) > RAYON_DANGER) {
					trop_loin = true;
					break;
				}
			}
			if (!trop_loin) {
				marqueursZombiesAware.addMarqueur(z);
			}
		}
		// Recréation des zombis manquants
		while (marqueursZombies.size() + marqueursZombiesAware.size() < density_array[density]) {
			marqueursZombies.addMarqueur(creerZombi(RAYON_CREATION,
					marqueursJoueurs.getListeMarqueur().get(0)));
		}
	}

	// Renvoi le joueur le plus proche de la Location en paramètres
	public Location plusProcheJoueur(Location positionEntite,
			Location[] positionJoueurs) {
		Location positionPlusProcheJoueur = new Location("");
		positionPlusProcheJoueur.setLatitude(positionJoueurs[0].getLatitude());
		positionPlusProcheJoueur
				.setLongitude(positionJoueurs[0].getLongitude());

		for (int i = 1; i < positionJoueurs.length; i++) {
			if (positionJoueurs[i].distanceTo(positionEntite) < positionPlusProcheJoueur
					.distanceTo(positionEntite)) {
				positionPlusProcheJoueur.setLatitude(positionJoueurs[i]
						.getLatitude());
				positionPlusProcheJoueur.setLongitude(positionJoueurs[i]
						.getLongitude());
			}
		}

		return positionPlusProcheJoueur;
	}

	public void gagner() {
		etat = 1;
	}

	/**
	 * Déclenche les evenements après qu'un zombi ait rattrapé un joueur
	 */
	public void contactJoueur() {
		if (vieRestante > 0) {
			// On diminue la vie du joueur
			vieRestante--;
			// Puis on envoi un message
			switch (alert) {
			// Ici on envoi le message selon le choix du joueur
			case (0):
			default:
				Vibrator vib = (Vibrator) mContext
						.getSystemService(Context.VIBRATOR_SERVICE);
				vib.vibrate(1000);
			}
			if (vieRestante == 0) {
				etat = 2;
			}
		}
	}

	public Context getMContext() {
		return mContext;
	}

	public void setMContext(Context context) {
		this.mContext = context;
	}

	public MarqueursJoueurs getMarqueursJoueurs() {
		return marqueursJoueurs;
	}

	public MarqueursZombies getMarqueursZombies() {
		return marqueursZombies;
	}

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

	public int getEtat() {
		return etat;
	}

	public void setEtat(int i) {
		etat = i;
	}
}
