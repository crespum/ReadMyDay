package com.caracocha.readmyday;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;
import android.util.Log;

public class Preferences extends PreferenceFragment implements
		OnPreferenceChangeListener, OnPreferenceClickListener {
	private static String debug_TAG = "PREFERENCES";
	public static final String KEY_PREF_NOTIF_EN = "pref_key_notifications_enable";
	public static final String KEY_PREF_NOTIF_TIME = "pref_key_notifications_time";
	public static final String KEY_PREF_NOTIF_SOUND = "pref_key_notifications_sound";
	public static final String KEY_PREF_NOTIF_VIBRATE = "pref_key_notifications_vibrate";
	public static final String KEY_PREF_VERSION = "pref_key_version";
	public static final String KEY_PREF_SHARE = "pref_key_share";

	public static final String SHARED_PREF_FILE = "SHARED_PREF_FILE";
	public static final String SHARED_PREF_HOUR = "SHARED_PREF_HOUR";
	public static final String SHARED_PREF_MIN = "SHARED_PREF_MIN";
	public static final String SHARED_PREF_FIRST_TIME = "SHARED_PREF_FIRST_TIME";
	public static final String SHARED_PREF_MONDAY = "SHARED_PREF_MONDAY";
	public static final String SHARED_PREF_TUESDAY = "SHARED_PREF_TUESDAY";
	public static final String SHARED_PREF_WEDNESDAY = "SHARED_PREF_WEDNESDAY";
	public static final String SHARED_PREF_THURSDAY = "SHARED_PREF_THURSDAY";
	public static final String SHARED_PREF_FRIDAY = "SHARED_PREF_FRIDAY";
	public static final String SHARED_PREF_SATURDAY = "SHARED_PREF_SATURDAY";
	public static final String SHARED_PREF_SUNDAY = "SHARED_PREF_SUNDAY";



	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Load the preferences from an XML resource
		addPreferencesFromResource(R.xml.preferences);
		this.findPreference(Preferences.KEY_PREF_NOTIF_EN)
				.setOnPreferenceChangeListener(this);
		this.findPreference(Preferences.KEY_PREF_NOTIF_TIME)
				.setOnPreferenceChangeListener(this);
		this.findPreference(Preferences.KEY_PREF_NOTIF_VIBRATE)
				.setOnPreferenceChangeListener(this);
		this.findPreference(Preferences.KEY_PREF_NOTIF_SOUND)
				.setOnPreferenceChangeListener(this);
		this.findPreference(Preferences.KEY_PREF_SHARE)
				.setOnPreferenceClickListener(this);
		
		try {
			String versionName = getActivity().getPackageManager()
					.getPackageInfo(getActivity().getPackageName(), 0).versionName;
			this.findPreference(Preferences.KEY_PREF_VERSION).setSummary(
					versionName);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		SharedPreferences settings = getActivity().getSharedPreferences(
				Preferences.SHARED_PREF_FILE, 0);
		int iHour = settings.getInt(Preferences.SHARED_PREF_HOUR, 12);
		int iMin = settings.getInt(Preferences.SHARED_PREF_MIN, 00);
		String sTime = String.format("%02d", iHour) + ":"
				+ String.format("%02d", iMin);
		this.findPreference(Preferences.KEY_PREF_NOTIF_TIME).setSummary(sTime);

	}

	@Override
	public boolean onPreferenceChange(Preference pref, Object newVal) {
		if (pref.getKey().equals(KEY_PREF_NOTIF_EN)) {
			if ((Boolean) newVal == true) {
				new NotificationAlarm().SetAlarm(getActivity());
			} else {
				new NotificationAlarm().CancelAlarm(getActivity());
			}
			return true;
		}
		if (pref.getKey().equals(KEY_PREF_NOTIF_TIME)) {
			String s = (String) newVal;
			String[] pieces = s.split(":");
			SharedPreferences.Editor settings = getActivity()
					.getSharedPreferences(SHARED_PREF_FILE, 0).edit();
			int iHour = Integer.parseInt(pieces[0]);
			int iMin = Integer.parseInt(pieces[1]);
			settings.putInt(SHARED_PREF_HOUR, iHour);
			settings.putInt(SHARED_PREF_MIN, iMin);
			settings.apply();
			NotificationAlarm n = new NotificationAlarm();
			n.CancelAlarm(getActivity());
			n.SetAlarm(getActivity());

			String sTime = String.format("%02d", iHour) + ":"
					+ String.format("%02d", iMin);
			pref.setSummary(sTime);

			Log.d(debug_TAG, "New notification at " + s);
			return true;
		}
		if (pref.getKey().equals(KEY_PREF_NOTIF_SOUND)
				|| pref.getKey().equals(KEY_PREF_NOTIF_VIBRATE)) {
			NotificationAlarm n = new NotificationAlarm();
			n.CancelAlarm(getActivity());
			n.SetAlarm(getActivity());
			return true;
		}
		return false;
	}

	@Override
	public boolean onPreferenceClick(Preference pref) {
		if (pref.getKey().equals(KEY_PREF_SHARE)) {
			Intent shareIntent = new Intent();
			shareIntent.setAction(Intent.ACTION_SEND);
			shareIntent.setType("text/plain");
			shareIntent.putExtra(Intent.EXTRA_TEXT,
					getString(R.string.pref_share_content));
			startActivity(Intent.createChooser(shareIntent,
					getString(R.string.pref_title_share)));
			return true;
		}
		return false;
	}

}
