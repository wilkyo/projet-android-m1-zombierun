package com.arkwilhow.advzombierun;

import android.os.Bundle;
import android.app.Activity;
import android.graphics.Typeface;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

public class GameEndActivity extends Activity {

	Boolean lose;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game_end);

		TextView textViewTron = (TextView) findViewById(R.id.WinOrLose);
		Typeface fontTron = Typeface.createFromAsset(getAssets(),
				"coldnightforalligators.ttf");
		textViewTron.setTypeface(fontTron);
		Bundle extra = getIntent().getExtras();
		lose = extra.getBoolean("lose");
		if (lose) {
			textViewTron.setText(R.string.lose_game);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_home, menu);
		return true;
	}

	public void retourMenu(View v) {
		finish();
	}
}
