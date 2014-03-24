package sk.upjs.ics.novotnyr.daygrid;

import android.provider.BaseColumns;

public class Database {
    public static final String NAME = "daygrid";
	
    public static final int VERSION = 1;
    
	public interface DayColor extends BaseColumns {
		public static final String TABLE_NAME = "daycolor";

		public static final String TIMESTAMP = "timestamp";
		public static final String COLOR = "color";
	}	
}
