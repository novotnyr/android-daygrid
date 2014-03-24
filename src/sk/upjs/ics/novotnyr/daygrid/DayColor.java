package sk.upjs.ics.novotnyr.daygrid;

import android.graphics.Color;

public enum DayColor {
	RED(Color.parseColor("#FF4444")),
	BLUE(Color.parseColor("#33B5E5")),
	GREEN(Color.parseColor("#99CC00")),
	VIOLET(Color.parseColor("#AA66CC")),
	ORANGE(Color.parseColor("#FFBB33")),
	WHITE(Color.WHITE),
	BLACK(Color.BLACK),
	TRANSPARENT(Color.TRANSPARENT);
	
	private int color;

	private DayColor(int color) {
		this.color = color;
	}
	
	public int getColor() {
		return color;
	}
	
	public static DayColor fromColor(int color) {
		for (DayColor c : DayColor.values()) {
			if(c.getColor() == color) {
				return c;
			}
		}
		throw new IllegalArgumentException("No color found for Android color " + color);
	}
}
