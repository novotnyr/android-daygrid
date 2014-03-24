package sk.upjs.ics.novotnyr.daygrid;

import static android.content.ContentResolver.CURSOR_ITEM_BASE_TYPE;
import static android.content.ContentResolver.SCHEME_CONTENT;
import static sk.upjs.ics.android.util.Defaults.ALL_COLUMNS;
import static sk.upjs.ics.android.util.Defaults.NO_CONTENT_OBSERVER;
import static sk.upjs.ics.android.util.Defaults.NO_GROUP_BY;
import static sk.upjs.ics.android.util.Defaults.NO_HAVING;
import static sk.upjs.ics.android.util.Defaults.NO_NULL_COLUMN_HACK;
import static sk.upjs.ics.android.util.Defaults.NO_SELECTION_ARGS;
import static sk.upjs.ics.android.util.Defaults.NO_SORT_ORDER;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class DayColorContentProvider extends ContentProvider {
	private static final String AUTHORITY = "sk.upjs.ics.novotnyr.daygrid.provider";

	public static final Uri YEAR_MONTH_DAY_COLOR_CONTENT_URI = new Uri.Builder().scheme(SCHEME_CONTENT).authority(AUTHORITY).appendPath(Database.DayColor.TABLE_NAME).build();

	private static final String MIME_TYPE_DAYCOLOR_SINGLE_ROW = CURSOR_ITEM_BASE_TYPE + "/vnd." + AUTHORITY + "." + Database.DayColor.TABLE_NAME;
	
	private static final int YEAR_MONTH_DAY_COLOR_CODE = 0;
	
	private UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

	private DayGridDatabaseOpenHelper dbOpenHelper;
	
	@Override
	public boolean onCreate() {
		dbOpenHelper = new DayGridDatabaseOpenHelper(this.getContext());

		uriMatcher.addURI(AUTHORITY, Database.DayColor.TABLE_NAME + "/*/*/*", YEAR_MONTH_DAY_COLOR_CODE);
		return true;
	}
	
	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		switch(uriMatcher.match(uri)) {
		case YEAR_MONTH_DAY_COLOR_CODE:
			return queryDayColorForYearAndMonth(uri);
		default:
			return null;
		}
	}
	
	private Cursor queryDayColorForYearAndMonth(Uri uri) {
		long timestamp = getTimestamp(uri);

		Cursor sqlCursor = getDatabase().query(Database.DayColor.TABLE_NAME, 
				ALL_COLUMNS, 
				Database.DayColor.TIMESTAMP + "=" + timestamp, 
				NO_SELECTION_ARGS,
				NO_GROUP_BY, 
				NO_HAVING, 
				NO_SORT_ORDER);
		return sqlCursor;
	}

	private long getTimestamp(Uri uri) {
		List<String> pathSegments = uri.getPathSegments();
		// "daycolor" = pathSegments.get(0)
		int year = Integer.parseInt(pathSegments.get(1));
		int month = Integer.parseInt(pathSegments.get(2));
		int dayOfMonth = Integer.parseInt(pathSegments.get(3));
		Calendar date = new GregorianCalendar(year, month - 1, dayOfMonth);
		
		return date.getTimeInMillis() / 1000;
	}

	private SQLiteDatabase getDatabase() {
		return dbOpenHelper.getWritableDatabase();
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		switch(uriMatcher.match(uri)) {
		case YEAR_MONTH_DAY_COLOR_CODE:
			long timestamp = getTimestamp(uri);
			int deletedRows = getDatabase().delete(Database.DayColor.TABLE_NAME, Database.DayColor.TIMESTAMP + "=" + timestamp, NO_SELECTION_ARGS);
			getContext().getContentResolver().notifyChange(uri, NO_CONTENT_OBSERVER);
			return deletedRows;
		default:
			return 0;
		}	
	}

	@Override
	public String getType(Uri uri) {
		switch(uriMatcher.match(uri)) {
		case YEAR_MONTH_DAY_COLOR_CODE:
			return MIME_TYPE_DAYCOLOR_SINGLE_ROW;
		default:
			return null;
		}		
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		switch(uriMatcher.match(uri)) {
		case YEAR_MONTH_DAY_COLOR_CODE:
			setColorForYearMonthAndDay(uri, values);
			return uri;
		default:
			return null;
		}
	}

	private void setColorForYearMonthAndDay(Uri uri, ContentValues values) {
		long timestamp = getTimestamp(uri);
		
		ContentValues cv = new ContentValues();
		cv.put(Database.DayColor.TIMESTAMP, timestamp);
		cv.put(Database.DayColor.COLOR, values.getAsInteger(Database.DayColor.COLOR));
		
		getDatabase().insert(Database.DayColor.TABLE_NAME, NO_NULL_COLUMN_HACK, cv);

		getContext().getContentResolver().notifyChange(uri, NO_CONTENT_OBSERVER);
	}


	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		throw new UnsupportedOperationException("Not yet implemented");
	}
}
