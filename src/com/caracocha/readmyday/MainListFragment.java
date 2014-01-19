package com.caracocha.readmyday;

/**
 * Created by Xabi on 10/11/13.
 */

import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Fragment;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fima.cardsui.views.CardUI;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainListFragment extends Fragment {

	private final static String[] sColumn_sel = new String[] {
			CalendarContract.Events._ID, CalendarContract.Events.DTSTART,
			CalendarContract.Events.DTEND, CalendarContract.Events.TITLE,
			CalendarContract.Events.ALL_DAY,
			CalendarContract.Events.CALENDAR_COLOR,
			CalendarContract.Events.ACCOUNT_NAME, CalendarContract.Events.RRULE };

	private long lDateEnd, lDateStart, lOffsetInMillis;

	private static String debug_TAG = "MAIN_LIST_FRAGMENT";

	private int iColTitle, iColDtStart, iColDtEnd, iColCalOwner, iColAllDay,
			iColCalendarColor;
	// True when the column index are fulfilled (the first time
	// cardObtainEventData() is entered)
	private boolean bColsRead = false;
	// In order to show the No Event card
	private boolean bNoCards = true;

	public MainListFragment() {

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.list_view, container, false);

		// code starts here

		Time t = new Time();
		t.setToNow();
		Log.d(debug_TAG, "Month day: " + t.monthDay);
		// Time when the search end
		t.set(59, 59, 23, t.monthDay, t.month, t.year);
		lDateEnd = t.toMillis(false);
		// Time when the search begins (one day before)
		lDateStart = lDateEnd - 86400000;

		// Offset in millis due to the events are saved in UTC time
		// TimeZone tz = TimeZone.getDefault();
		lOffsetInMillis = t.gmtoff;
		lDateStart += lOffsetInMillis;
		String sDateStart = Long.toString(lDateStart);
		lDateEnd += lOffsetInMillis;
		String sDateEnd = Long.toString(lDateEnd);
		// Search arguments
		String[] sSelectionArgs = new String[] { sDateStart, sDateEnd };

		Log.d(debug_TAG, "Offset: " + t.gmtoff);
		Log.d(debug_TAG, "Start: " + sDateStart);
		Log.d(debug_TAG, "End: " + sDateEnd);

		// Get all day events and recurring events (OPTIMIZE)
		Uri.Builder eventsUriBuilder = CalendarContract.Instances.CONTENT_URI
				.buildUpon();
		ContentUris.appendId(eventsUriBuilder, lDateStart);
		ContentUris.appendId(eventsUriBuilder, lDateEnd);
		Uri eventsUri = eventsUriBuilder.build();

		Cursor cursor = inflater
				.getContext()
				.getContentResolver()
				.query(eventsUri, sColumn_sel, null, null,
						CalendarContract.Instances.DTSTART + " ASC");

		CardUI mCardView = (CardUI) rootView.findViewById(R.id.cardsview);

		// Analyze results
		if (cursor.moveToFirst()) {
			Date date = new Date();
			SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
			MyPlayCard card;
			do {
				card = cardObtainEventData(cursor, date, formatter);
				if (card != null){
					mCardView.addCard(card);
					bNoCards = false;
				}
			} while (cursor.moveToNext());

		}

		// If no event is found...
		String sTitle1 = getActivity().getResources().getString(
				R.string.no_events);
		String sTime1 = getActivity().getResources()
				.getString(R.string.all_day);
		// String sOwner =
		// getActivity().getResources().getString(R.string.all_day);

		if (bNoCards)
			mCardView.addCard(new MyPlayCard(sTitle1, sTime1, "", "#"
					+ this.getResources().getColor(R.color.card_light_text),
					false, false, false));

		cursor.close();

		mCardView.refresh();

		return rootView;
	}

	/*
	 * Returns null if the event is an all_day event of the previous day. This
	 * is because all_day events are not saved in UTC time and cannot be
	 * filtered in the first query
	 */
	private MyPlayCard cardObtainEventData(Cursor cursor, Date date,
			SimpleDateFormat formatter) {

		// Get Column Index only one time
		if (!bColsRead) {
			iColTitle = cursor.getColumnIndex(CalendarContract.Events.TITLE);
			iColDtStart = cursor
					.getColumnIndex(CalendarContract.Events.DTSTART);
			iColDtEnd = cursor.getColumnIndex(CalendarContract.Events.DTEND);
			iColCalOwner = cursor
					.getColumnIndex(CalendarContract.Events.ACCOUNT_NAME);
			iColAllDay = cursor.getColumnIndex(CalendarContract.Events.ALL_DAY);
			iColCalendarColor = cursor
					.getColumnIndex(CalendarContract.Events.CALENDAR_COLOR);
			bColsRead = true;
		}

		// Read Data
		String sTitle = cursor.getString(iColTitle);
		long lStart = cursor.getLong(iColDtStart);
		long lEnd = cursor.getLong(iColDtEnd);
		String sOwner = cursor.getString(iColCalOwner);
		int sColor = cursor.getInt(iColCalendarColor);
		String sTime = null;

		Log.d(debug_TAG,
				sTitle + " " + lStart + " " + lEnd + " "
						+ Integer.toHexString(sColor));

		if ((cursor.getInt(iColAllDay)) == 1) {
			sTime = getActivity().getResources().getString(R.string.all_day); 
			if (lEnd < lDateEnd)
				return null; //Don't show all day events of the previous day
		} else {
			if (lStart < lDateStart)
				return null; //Don`t show events which end at 00.00 (from the previous day)
			date.setTime(lStart + lOffsetInMillis);
			sTime = formatter.format(date) + " - ";
			date.setTime(lEnd + lOffsetInMillis);
			sTime += formatter.format(date);

			Log.d(debug_TAG, "Time: " + sTime);
		}

		// Create card
		//If all day event...
		if ((cursor.getInt(iColAllDay)) == 1) {
			return new MyPlayCard(sTitle, sTime, sOwner, "#"
					+ Integer.toHexString(sColor), false, false, false);
		} 
		//...if past event...
		else if (lEnd < System.currentTimeMillis()) {
			return new MyPlayCard(sTitle, sTime, sOwner, "#"
					+ Integer.toHexString(sColor), true, false, false);
		} 
		//...if future event...
		else {
			return new MyPlayCard(sTitle, sTime, sOwner, "#"
					+ Integer.toHexString(sColor), false, false, false);
		}
	}
}