package com.sounaks.desktime;

import java.awt.Component;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

class TimezoneCellRenderer extends DefaultListCellRenderer
{

	TimezoneCellRenderer()
	{
	}

	@Override
	public Component getListCellRendererComponent(JList<?> jlist, Object obj, int i, boolean flag,	boolean	flag1)
	{
		super.getListCellRendererComponent(jlist, obj, i, flag,	flag1);
		TimeZone curTimeZone = TimeZone.getTimeZone(obj.toString());
		String offset = calculateOffset(curTimeZone.getRawOffset());
		setText(String.format("(%s%s)  %s", "GMT", offset, obj.toString()));
		return this;
	}

	private String calculateOffset(int rawOffset)
	{
		if (rawOffset == 0) {
			return "+00:00";
		}
		long hours = TimeUnit.MILLISECONDS.toHours(rawOffset);
		long minutes = TimeUnit.MILLISECONDS.toMinutes(rawOffset);
		minutes = Math.abs(minutes - TimeUnit.HOURS.toMinutes(hours));

		return String.format("%+03d:%02d", hours, Math.abs(minutes));
	}
}