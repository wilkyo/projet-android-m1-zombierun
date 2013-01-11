package com.arkwilhow.metiers;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import com.arkwilhow.advzombierun.R;
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
	private GeoPoint destination = null;
	private OverlayItem MarqueurDest = null;

	public MarqueursJoueurs(Drawable defaultMarker) {
		super(boundCenterBottom(defaultMarker));
	}

	public MarqueursJoueurs(Drawable defaultMarker, Context context) {
		super(boundCenterBottom(defaultMarker));
		mContext = context;
	}

	public ArrayList<Joueur> getListeMarqueur() {
		return listeMarqueur;
	}

	public void setListeMarqueur(ArrayList<Joueur> listeMarqueur) {
		this.listeMarqueur = listeMarqueur;
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
	public boolean onTouchEvent(MotionEvent event, MapView map) {
		// super.onTouchEvent(arg0, arg1);
		return false;
	}

	@Override
	public boolean onTap(GeoPoint point, MapView map) {
		if (destination == null) {
			final GeoPoint tmp = point;
			AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
			dialog.setTitle(mContext.getText(R.string.diag_destination_title));
			dialog.setMessage(mContext.getText(R.string.diag_destination_text));
			dialog.setCancelable(true);
			dialog.setPositiveButton(R.string.diag_yes,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							destination = tmp;
							MarqueurDest = new OverlayItem(destination,
									(String) mContext
											.getText(R.string.marquerdesttit),
									(String) mContext
											.getText(R.string.marqueurdestmsg));
						}
					});
			dialog.setNegativeButton(R.string.diag_no,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) { // Do
							// Nothing
						}
					});
			dialog.show();
			return true;
		}
		return false;
	}

	public GeoPoint getDestination() {
		return destination;
	}

	@Override
	protected boolean onTap(int index) {
		OverlayItem item = listeMarqueur.get(index);
		AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
		dialog.setTitle(item.getTitle());
		dialog.setMessage(item.getSnippet());
		dialog.show();
		return true;
	}

	public void clear() {
		listeMarqueur.clear();
	}

	public OverlayItem getMarkerDestination() {
		return MarqueurDest;
	}
}
