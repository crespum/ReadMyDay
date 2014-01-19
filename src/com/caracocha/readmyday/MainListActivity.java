package com.caracocha.readmyday;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class MainListActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_fragment);

		// Volver al fragment inicial (Cards)
		if (getFragmentManager().getBackStackEntryCount() > 0)
			getFragmentManager().popBackStack();

//		SharedPreferences prefs = getSharedPreferences(
//				Preferences.SHARED_PREF_FILE, Context.MODE_PRIVATE);
//		boolean bFirstTime = prefs.getBoolean(
//				Preferences.SHARED_PREF_FIRST_TIME, true);

		if (savedInstanceState == null) {
			FragmentTransaction transaction = getFragmentManager()
					.beginTransaction();
			transaction.add(R.id.list_fragment_fragment,
					new MainListFragment(), "main");

//			if (bFirstTime) {
//				SharedPreferences.Editor editor = prefs.edit();
//				editor.putBoolean(Preferences.SHARED_PREF_FIRST_TIME, false);
//				editor.commit();
//
//				transaction.replace(R.id.list_fragment_fragment,
//						new Preferences(), "prefs");
//				transaction.addToBackStack(null);
//			}
			transaction.commit();
		}

		AdView adView = (AdView) findViewById(R.id.adView);
		AdRequest adRequest = new AdRequest.Builder().build();
		adView.loadAd(adRequest);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_settings:
			Fragment pref_tablet = getFragmentManager().findFragmentById(
					R.id.list_fragment_preferences);
			Fragment pref_phone = getFragmentManager().findFragmentById(
					R.id.list_fragment_fragment);

			// Si son visibles las preferencias no hace nada al tocar al botón
			if ((pref_tablet != null && pref_tablet.isAdded())
					|| (pref_phone != null && pref_phone.getTag().equals(
							"prefs")))
				return true;

			// For tablets...
			if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE
					&& getResources().getConfiguration().isLayoutSizeAtLeast(
							Configuration.SCREENLAYOUT_SIZE_LARGE)) {
				FragmentTransaction transaction = getFragmentManager()
						.beginTransaction();
				transaction.replace(R.id.list_fragment_preferences,
						new Preferences());
				transaction.addToBackStack(null);
				transaction.commit();
			}
			// ...for phones...
			else {
				FragmentTransaction transaction = getFragmentManager()
						.beginTransaction();
				transaction.replace(R.id.list_fragment_fragment,
						new Preferences(), "prefs");
				transaction.addToBackStack(null);
				transaction.commit();
			}

			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
