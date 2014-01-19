package com.caracocha.readmyday;

import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fima.cardsui.objects.RecyclableCard;

public class MyPlayCard extends RecyclableCard {

	private String location, time;
	private boolean bPastEvent;

	public MyPlayCard(String titlePlay, String time, String location,
			String color, Boolean bPastEvent, Boolean hasOverflow, Boolean isClickable) {

		super(titlePlay, time, color, color, hasOverflow, isClickable);

		this.location = location;
		this.time = time;
		this.bPastEvent = bPastEvent;
	}

	@Override
	protected int getCardLayoutId() {
		return R.layout.card_play;
	}

	@Override
	protected void applyTo(View convertView) {
		((TextView) convertView.findViewById(R.id.play_card_title))
				.setText(titlePlay);
		((TextView) convertView.findViewById(R.id.play_card_title))
				.setTextColor(Color.parseColor(color));
		((TextView) convertView.findViewById(R.id.play_card_time))
				.setText(time);
		((TextView) convertView.findViewById(R.id.play_card_cal_owner))
				.setText(location);
		((ImageView) convertView.findViewById(R.id.play_card_stripe))
				.setBackgroundColor(Color.parseColor(color));
		if (bPastEvent)
			((LinearLayout) convertView.findViewById(R.id.play_card_background))
					.setBackgroundColor(Color.parseColor("#ededed"));

		if (isClickable == true)
			((LinearLayout) convertView
					.findViewById(R.id.play_card_contentLayout))
					.setBackgroundResource(R.drawable.selectable_background_cardbank);

		if (hasOverflow == true)
			((ImageView) convertView.findViewById(R.id.play_card_overflow))
					.setVisibility(View.VISIBLE);
		else
			((ImageView) convertView.findViewById(R.id.play_card_overflow))
					.setVisibility(View.GONE);
	}
}
