package com.arkwilhow.advzombierun;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

public class MultiPlayerActivity extends Activity {

	private int[] gamesIds;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_multi_player);

		ListView listView = (ListView) findViewById(R.id.hostedGamesList);

		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				loadHostedGame(gamesIds[position]);
			}
		});
	}

	private void refreshListView(ListView listView) {
		// Bouchon TODO
		gamesIds = new int[] { 85, 123435 };
		String[] hostedGames = new String[] { "wilkyo", "HowiePowie" };
		for (int i = 0; i < hostedGames.length; i++) {
			Log.d("refreshListView", hostedGames[i]);
		}
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, android.R.id.text1,
				hostedGames) {
			public View getView(int position, View convertView, ViewGroup parent) {
				View view = super.getView(position, convertView, parent);

				/* Change le style du textView ayant pour id text1 */
				TextView textView = (TextView) view
						.findViewById(android.R.id.text1);

				textView.setTextColor(Color.parseColor("#8A0808"));
				textView.setBackgroundColor(Color.parseColor("#424242"));
				textView.setTextSize(20);

				return view;
			}
		};

		// Assign adapter to ListView
		listView.setAdapter(adapter);
	}

	protected void loadHostedGame(int id) {
		Log.d("loadHostedGame", String.valueOf(id));
		Intent intent = new Intent(this, Map.class); // Pas directement la Map,
														// il faudra peut être
														// une activité
														// d'attente
		if (intent != null) {
			// Affectations à faire ici si nécessaire TODO
			this.startActivity(intent);
		}
	}

	public void newHostedGame(View v) {
		Intent intent = new Intent(this, PreferencesActivity.class);
		if (intent != null) {
			// Affectations à faire ici si n�cessaire
			// Définir qu'on est en multi TODO
			this.startActivity(intent);
		}
	}

	public void refresh(View v) {
		refreshListView((ListView) findViewById(R.id.hostedGamesList));
	}

	public void onResume() {
		refreshListView((ListView) findViewById(R.id.hostedGamesList));
		super.onResume();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_multi_player, menu);
		return true;
	}

	public void previous(View v) {
		finish();
	}

}
