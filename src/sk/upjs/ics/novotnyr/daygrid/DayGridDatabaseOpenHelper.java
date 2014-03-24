package sk.upjs.ics.novotnyr.daygrid;

import static sk.upjs.ics.android.util.Defaults.DEFAULT_CURSOR_FACTORY;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DayGridDatabaseOpenHelper extends SQLiteOpenHelper {
	
	public DayGridDatabaseOpenHelper(Context context) {
		super(context, Database.NAME, DEFAULT_CURSOR_FACTORY, Database.VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String sql = "CREATE TABLE %s (" +
				"%s INTEGER PRIMARY KEY AUTOINCREMENT, " +
				"%s DATE NOT NULL, " +
				"%s INTEGER NOT NULL" +
				")";
		sql = String.format(sql, 
				Database.DayColor.TABLE_NAME, 
				Database.DayColor._ID,
				Database.DayColor.TIMESTAMP,
				Database.DayColor.COLOR);
		db.execSQL(sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// not supported		
	}

}
