package com.caracocha.readmyday;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ToggleButton;

public class WeekDay extends Preference implements OnClickListener {

	private boolean bMonday, bTuesday, bWednesday, bThursday, bFriday,
			bSaturday, bSunday;
	private boolean bDefault = true;
	private ToggleButton tbMonday, tbTuesday, tbWednesday, tbThursday,
			tbFriday, tbSaturday, tbSunday;
	private Context context;
	public WeekDay(Context context, AttributeSet attrs) {
		super(context, attrs);

		this.context = context;
		setLayoutResource(R.layout.prefs_week_day);
		vReadPreferences();

	}

	@Override
	protected void onBindView(View view) {
		super.onBindView(view);
		tbMonday = (ToggleButton) view.findViewById(R.id.prefs_monday);
		tbMonday.setOnClickListener(this);
		tbMonday.setChecked(bMonday);

		tbTuesday = (ToggleButton) view.findViewById(R.id.prefs_tuesday);
		tbTuesday.setOnClickListener(this);
		tbTuesday.setChecked(bTuesday);

		tbWednesday = (ToggleButton) view.findViewById(R.id.prefs_wednesday);
		tbWednesday.setOnClickListener(this);
		tbWednesday.setChecked(bWednesday);

		tbThursday = (ToggleButton) view.findViewById(R.id.prefs_thursday);
		tbThursday.setOnClickListener(this);
		tbThursday.setChecked(bThursday);

		tbFriday = (ToggleButton) view.findViewById(R.id.prefs_friday);
		tbFriday.setOnClickListener(this);
		tbFriday.setChecked(bFriday);

		tbSaturday = (ToggleButton) view.findViewById(R.id.prefs_saturday);
		tbSaturday.setOnClickListener(this);
		tbSaturday.setChecked(bSaturday);

		tbSunday = (ToggleButton) view.findViewById(R.id.prefs_sunday);
		tbSunday.setOnClickListener(this);
		tbSunday.setChecked(bSunday);

	}

	@Override
	protected void onSetInitialValue(boolean restorePersistedValue,
			Object defaultValue) {
		super.onSetInitialValue(restorePersistedValue, defaultValue);
		//vReadPreferences();

		tbMonday.setChecked(bMonday);
		tbTuesday.setChecked(bTuesday);
		tbWednesday.setChecked(bWednesday);
		tbThursday.setChecked(bThursday);
		tbFriday.setChecked(bFriday);
		tbSaturday.setChecked(bSaturday);
		tbSunday.setChecked(bSunday);

	}

	@Override
	public void onClick(View v) {
		SharedPreferences.Editor settings = getContext().getSharedPreferences(
				Preferences.SHARED_PREF_FILE, 0).edit();

		switch (v.getId()) {
		case R.id.prefs_monday:
			settings.putBoolean(Preferences.SHARED_PREF_MONDAY,
					((ToggleButton) v).isChecked());
			bMonday = ((ToggleButton) v).isChecked();
			break;
		case R.id.prefs_tuesday:
			settings.putBoolean(Preferences.SHARED_PREF_TUESDAY,
					((ToggleButton) v).isChecked());
			bTuesday = ((ToggleButton) v).isChecked();
			break;
		case R.id.prefs_wednesday:
			settings.putBoolean(Preferences.SHARED_PREF_WEDNESDAY,
					((ToggleButton) v).isChecked());
			 bWednesday = ((ToggleButton) v).isChecked();
			break;
		case R.id.prefs_thursday:
			settings.putBoolean(Preferences.SHARED_PREF_THURSDAY,
					((ToggleButton) v).isChecked());
			bThursday = ((ToggleButton) v).isChecked();
			break;
		case R.id.prefs_friday:
			settings.putBoolean(Preferences.SHARED_PREF_FRIDAY,
					((ToggleButton) v).isChecked());
			bFriday = ((ToggleButton) v).isChecked();
			break;
		case R.id.prefs_saturday:
			settings.putBoolean(Preferences.SHARED_PREF_SATURDAY,
					((ToggleButton) v).isChecked());
			bSaturday = ((ToggleButton) v).isChecked();
			break;
		case R.id.prefs_sunday:
			settings.putBoolean(Preferences.SHARED_PREF_SUNDAY,
					((ToggleButton) v).isChecked());
			bSunday = ((ToggleButton) v).isChecked();
			break;

		default:
			break;
		}
		
		settings.commit();		

		NotificationAlarm n = new NotificationAlarm();
		n.CancelAlarm(context);
		n.SetAlarm(context);

	}

	private void vReadPreferences() {
		SharedPreferences settings = context
				.getSharedPreferences(Preferences.SHARED_PREF_FILE, 0);

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
	}
}