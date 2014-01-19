package com.caracocha.readmyday;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * BroadcastReceiver que se ejecuta nada mas encender el móvil y que programa
 * las notificaciones pendientes.
 * 
 * @author xabi
 * 
 */
public class AutoStart extends BroadcastReceiver {
	NotificationAlarm n = new NotificationAlarm();
	private static String debug_TAG = "AUTO_START";

	@Override
	public void onReceive(Context context, Intent intent) {
		boolean bNotify = PreferenceManager
				.getDefaultSharedPreferences(context).getBoolean(
						Preferences.KEY_PREF_NOTIF_EN, false);

		if (bNotify
				&& (intent.getAction().equals(
						"android.intent.action.BOOT_COMPLETED") || intent
						.getAction().equals(
								"android.intent.action.AIRPLANE_MODE"))) {
			vConfigureAlarm(context);
		}
	}

	/*
	 * Método que lee la base de datos de notificaciones y genera una alarma
	 * para cada una de ellas
	 */
	private void vConfigureAlarm(Context context) {
		Log.d(debug_TAG, "Alarma añadida");
		n.SetAlarm(context);
	}
}