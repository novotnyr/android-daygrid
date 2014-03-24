package sk.upjs.ics.novotnyr.daygrid;

import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.YEAR;
import static sk.upjs.ics.android.util.Defaults.*;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.MonthDisplayHelper;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class MainActivity extends Activity {

	private static final String BUNDLE_KEY_CURRENT_CALENDAR = "currentCalendar";
	private static final int WEEKDAY_COUNT = 7;
	private static final int WEEK_COUNT = 6;
	
	private Calendar currentCalendar = Calendar.getInstance();
	
	private DateFormat dateFormat = new SimpleDateFormat("yyyy/m");
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if(savedInstanceState != null) {
			currentCalendar = (Calendar) savedInstanceState.get(BUNDLE_KEY_CURRENT_CALENDAR);
		}
		
		prepareLayout();
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putSerializable(BUNDLE_KEY_CURRENT_CALENDAR, currentCalendar);
		super.onSaveInstanceState(outState);
	}

	private void prepareLayout() {
		MonthDisplayHelper monthDisplay = getMonthDisplayHelper(currentCalendar);
		
		TableLayout layout = new TableLayout(this);
		layout.setStretchAllColumns(true);
		
		for(int weekIndex = 0; weekIndex < WEEK_COUNT; weekIndex++) {
			TableRow row = new TableRow(this);
			
			for (int dayIndex = 0; dayIndex < WEEKDAY_COUNT; dayIndex++) {
				final TextView textView = new TextView(this);
				
				int padding = (int) getResources().getDimension(R.dimen.day_padding);
				textView.setPadding(padding, padding, padding, padding);
				textView.setGravity(Gravity.CENTER);
				textView.setTextSize(getResources().getDimension(R.dimen.text_size));
				
				int dayNumber = monthDisplay.getDayAt(weekIndex, dayIndex);
				textView.setText(Integer.toString(dayNumber));
				Calendar day = getCalendarForDay(currentCalendar, dayNumber);
				textView.setTag(day);
				textView.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						onDayClick(textView);
					}
				});
				if(!monthDisplay.isWithinCurrentMonth(weekIndex, dayIndex)) {
					textView.setEnabled(false);
				} else {
					textView.setBackgroundColor(getColor(day).getColor());					
				}
				
				row.addView(textView);
			}
			layout.addView(row);
		}
		
		setContentView(layout);
		
		setTitle(dateFormat.format(currentCalendar.getTime()));
	}
	
	private Calendar getCalendarForDay(Calendar month, int day) {
		Calendar dayCalendar = (Calendar) month.clone();
		dayCalendar.set(DAY_OF_MONTH, day);
		return dayCalendar;
	}

	private MonthDisplayHelper getMonthDisplayHelper(Calendar calendar) {
		return new MonthDisplayHelper(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH), Calendar.MONDAY);
	}

	public void onDayClick(TextView dayTextView) {
		Calendar selectedDay = (Calendar) dayTextView.getTag();
		if(isColored(selectedDay)) {
			clearColors(selectedDay);
		} else {
			pickColorForDate(selectedDay);
		}
	}
	
	private boolean isColored(Calendar day) {
		return getColor(day) != DayColor.TRANSPARENT;
	}

	private void clearColors(final Calendar day) {
		Uri uri = getDayColorUri(day);

		getContentResolver().delete(uri, NO_SELECTION, NO_SELECTION_ARGS);
		prepareLayout();
	}

	private void pickColorForDate(final Calendar day) {
			new AlertDialog.Builder(this)
				.setTitle("Vyberte farbu")
				.setItems(R.array.colors_array,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								colorize(day, DayColor.values()[which]);
								prepareLayout();
							}

						})
				.show();
	}
	
	private void colorize(Calendar day, DayColor color) {
		Uri uri = getDayColorUri(day);
		
		ContentValues contentValues = new ContentValues();
		contentValues.put(Database.DayColor.COLOR, color.getColor());
		getContentResolver().insert(uri, contentValues);
	}

	private Uri getDayColorUri(Calendar day) {
		Uri uri = Uri.withAppendedPath(DayColorContentProvider.YEAR_MONTH_DAY_COLOR_CONTENT_URI, "" + day.get(YEAR));
		uri = Uri.withAppendedPath(uri, "" + (day.get(MONTH) + 1));
		uri = Uri.withAppendedPath(uri, "" + (day.get(DAY_OF_MONTH) + 1));
		return uri;		
	}

	private DayColor getColor(Calendar day) {
		Uri uri = getDayColorUri(day);
		
		Cursor cursor = getContentResolver().query(uri, NO_PROJECTION, NO_SELECTION, NO_SELECTION_ARGS, NO_SORT_ORDER);
		DayColor color = DayColor.TRANSPARENT;
		if(cursor.moveToNext()) {
			int c = cursor.getInt(cursor.getColumnIndex(Database.DayColor.COLOR));
			color = DayColor.fromColor(c);
		}
		cursor.close();
		return color;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case R.id.action_previous_month:
			currentCalendar.add(MONTH, -1);
			prepareLayout();	
			break;
		case R.id.action_next_month:
			currentCalendar.add(MONTH, +1);
			prepareLayout();
			break;
		}
		return super.onOptionsItemSelected(item);		
	}

}
