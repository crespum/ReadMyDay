package com.caracocha.readmyday;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.TextView;

public class NotificationAlarm extends BroadcastReceiver {
	private static String debug_TAG = "NOTIFICATION_ALARM";

	@Override
	public void onReceive(Context context, Intent intent) {
		PowerManager pm = (PowerManager) context
				.getSystemService(Context.POWER_SERVICE);
		PowerManager.WakeLock wl = pm.newWakeLock(
				PowerManager.PARTIAL_WAKE_LOCK, "");
		wl.acquire();

		// Creamos notificaciÛn...
		vNotificate(context);
		// ...cancelamos la repetici√≥n de la alarma
		// CancelAlarm(context, e);

		wl.release();
	}

	/**
	 * MÈtodo para programar una "alarma", es decir, una acciÛn dentro de x
	 * segundos.
	 * 
	 * @param context
	 * @param event
	 * @return true si se a√±adi√≥ el recordatorio y false en caso contrario (si
	 *         la fecha ya hab√≠a pasado)
	 */

	public boolean SetAlarm(Context context) {

		SharedPreferences settings = context.getSharedPreferences(
				Preferences.SHARED_PREF_FILE, 0);
		int iHour = settings.getInt(Preferences.SHARED_PREF_HOUR, 12);
		int iMin = settings.getInt(Preferences.SHARED_PREF_MIN, 00);

		Log.d(debug_TAG, "Configurada nueva notificacÛn");
		// Buscamos fecha de inicio de las fiestas en milisegundos
		Time tNow = new Time();
		tNow.setToNow();
		int iAlarmDay;

		Time tAlarm = new Time();
		tAlarm.set(00, iMin, iHour, tNow.monthDay, tNow.month, tNow.year);
		long lAlarm = tAlarm.toMillis(false);

		// If an alarm is needed for the next day...
		if (tAlarm.before(tNow) || (lAlarm < System.currentTimeMillis())) {
			iAlarmDay = iAlarmDay(context, tNow.weekDay + 1);
			if (iAlarmDay == -1)
				return true;
			// plus (iAlarmDay days plus 1 day)
			lAlarm += (iAlarmDay + 1) * 86400000;
		}
		// ... if an alarm could be needed for today...
		else {
			iAlarmDay = iAlarmDay(context, tNow.weekDay);
			if (iAlarmDay == -1)
				return true;
			// plus (iAlarmDay days plus 1 day)
			lAlarm += iAlarmDay * 86400000;
		}

		Log.d(debug_TAG, "TimeLeft: " + (lAlarm - System.currentTimeMillis())
				+ "\nNotificationTime: " + lAlarm);

		AlarmManager am = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);

		Intent intent = new Intent().setClass(context, NotificationAlarm.class);

		PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);
		am.set(AlarmManager.RTC_WAKEUP, lAlarm, pi); // Millisec * Second
														// * Minute
		return true;

	}

	private void vNotificate(Context context) {
		// Prepare intent which is triggered if the
		// notification is selected
		Log.d(debug_TAG, "Launching notification");

		Intent intent = new Intent(context, MainListActivity.class);
		PendingIntent pIntent = PendingIntent
				.getActivity(context, 0, intent, 0);

		// Build notification
		Notification noti = new NotificationCompat.Builder(context)
				.setContentTitle(
						context.getResources().getString(R.string.notif_title))
				.setContentText(
						context.getResources().getString(R.string.notif_text))
				.setContentIntent(pIntent)
				.setSmallIcon(R.drawable.ic_launcher)
				.setLargeIcon(
						((BitmapDrawable) context.getResources().getDrawable(
								R.drawable.ic_launcher)).getBitmap()).build();

		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);

		// Hide the notification after its selected
		noti.flags |= Notification.FLAG_AUTO_CANCEL;

		// Configure vibration, lights and sound
		// noti.defaults = Notification.DEFAULT_ALL;

		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		boolean bVibrate = prefs.getBoolean(Preferences.KEY_PREF_NOTIF_VIBRATE,
				true);
		boolean bSound = prefs.getBoolean(Preferences.KEY_PREF_NOTIF_SOUND,
				true);

		if (bVibrate)
			noti.defaults |= Notification.DEFAULT_VIBRATE;
		if (bSound)
			noti.defaults |= Notification.DEFAULT_SOUND;

		notificationManager.notify(0, noti);

		// Configure the next alarm
		this.SetAlarm(context);
	}

	/**
	 * MÈtodo para cancelar la repeticiÛn de la alarma creada con SetAlarm()
	 * 
	 * @param context
	 */
	public void CancelAlarm(Context context) {
		Log.d(debug_TAG, "Disabling notification");

		Intent intent = new Intent(context, NotificationAlarm.class);
		PendingIntent sender = PendingIntent
				.getBroadcast(context, 0, intent, 0);
		AlarmManager alarmManager = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		alarmManager.cancel(sender);
	}

	/**
	 * Returns the next day the alarm has to be programmed
	 * 
	 * @param context
	 * @param iToday
	 *            (0 for sunday)
	 * @return int indicating days left for the next alarm or -1 if any day is
	 *         selected
	 */
	private int iAlarmDay(Context context, int iToday) {
		boolean bMonday, bTuesday, bWednesday, bThursday, bFriday, bSaturday, bSunday;
		boolean bDefault = true;

		SharedPreferences settings = context.getSharedPreferences(
				Preferences.SHARED_PREF_FILE, 0);
		bMonday = settings.getBoolean(Preferences.SHARED_PREF_MONDAY, bDefault);
		bTuesday = settings.getBoolean(Preferences.SHARED_PREF_TUESDAY,
				bDefault);
		bWednesday = settings.getBoolean(Preferences.SHARED_PREF_WEDNESDAY,
				bDefault);
		bThursday = settings.getBoolean(Preferences.SHARED_PREF_THURSDAY,
				bDefault);
		bFriday = settings.getBoolean(Preferences.SHARED_PREF_FRIDAY, bDefault);
		bSaturday = settings.getBoolean(Preferences.SHARED_PREF_SATURDAY,
				bDefault);
		bSunday = settings.getBoolean(Preferences.SHARED_PREF_SUNDAY, bDefault);

		boolean[] bAlarmDay = { bSunday, bMonday, bTuesday, bWednesday,
				bThursday, bFriday, bSaturday };

		int iNextAlarmDay = -1;
		for (int i = 0; i < 7; i++) {
			if (bAlarmDay[(iToday + i) % 7]) {
				iNextAlarmDay = i;
				break;
			}
		}

		Log.d(debug_TAG, "Alarm for " + iNextAlarmDay + " days from today");
		return iNextAlarmDay;
	}
	
//	private Bitmap createClusterBitmap(Context context, int clusterSize) {
//	    View cluster = LayoutInflater.from(context).inflate(R.layout.map_cluster,
//	            null);
//
//	    TextView clusterSizeText = (TextView) cluster.findViewById(R.map.cluster);
//	    clusterSizeText.setText(String.valueOf(clusterSize));
//
//	    cluster.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
//	            MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
//	    cluster.layout(0, 0, cluster.getMeasuredWidth(),cluster.getMeasuredHeight());
//
//	    final Bitmap clusterBitmap = Bitmap.createBitmap(cluster.getMeasuredWidth(),
//	            cluster.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
//
//	    Canvas canvas = new Canvas(clusterBitmap);
//	    cluster.draw(canvas);
//
//	    return clusterBitmap;
//	}
}