package com.buildmlearn.flashcard;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.util.Linkify;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class ScoreActivity extends ActionBarActivity {
	GlobalData gd;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_finish);
		gd = GlobalData.getInstance();
		TextView mCardQuizName=(TextView) findViewById(R.id.tv_lastcard);
		mCardQuizName.setText(gd.iQuizTitle);

		Button startAgainButton = (Button) findViewById(R.id.btn_restart);
		startAgainButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent myIntent = new Intent(ScoreActivity.this,
						StartActivity.class);
				startActivity(myIntent);
				finish();
			}
		});

		Button quitButton = (Button) findViewById(R.id.btn_exit);
		quitButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finish();
				// android.os.Process.killProcess(android.os.Process.myPid());
			}
		});
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		getMenuInflater().inflate(R.menu.main, menu);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.action_info) {

			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
					ScoreActivity.this);

			// set title
			alertDialogBuilder.setTitle("About Us");

			// set dialog message
			alertDialogBuilder
					.setMessage(getString(R.string.about_us))
					.setCancelable(false)
					.setPositiveButton("Ok",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									dialog.cancel();
									dialog.dismiss();
								}
							});

			// create alert dialog
			AlertDialog alertDialog = alertDialogBuilder.create();
			// show it
			alertDialog.show();
			TextView msg = (TextView) alertDialog
					.findViewById(android.R.id.message);
			Linkify.addLinks(msg, Linkify.WEB_URLS);

			return super.onOptionsItemSelected(item);
		}
		return true;
	}

}
