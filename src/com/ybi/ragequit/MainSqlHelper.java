package com.ybi.ragequit;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * 
 *
 */

public class MainSqlHelper extends SQLiteOpenHelper {

	public static final String TABLE_PICS = "pics";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_TITLE = "title";
	public static final String COLUMN_DESC = "desc";
	public static final String COLUMN_LINK = "link";
	public static final String COLUMN_DATE = "date";
	public static final String COLUMN_THUMBNAIL = "thumbnail";
	public static final String COLUMN_CONTENT = "content";
	public static final String COLUMN_CHECKSUM = "checksum";
	public static final String COLUMN_LOCATION = "location";

	public static final String[] ALL_COLUMNS = { COLUMN_ID, COLUMN_TITLE, COLUMN_DESC, COLUMN_LINK, COLUMN_DATE, COLUMN_THUMBNAIL,
		COLUMN_CONTENT, COLUMN_CHECKSUM, COLUMN_LOCATION };

	private static final String DATABASE_NAME = "pics.db";
	private static final int DATABASE_VERSION = 3;

	// Database creation sql statement
	private static final String DATABASE_CREATE = "create table " + TABLE_PICS + "(" + COLUMN_ID
			+ " integer primary key autoincrement," + COLUMN_TITLE + " text not null," + COLUMN_DESC + " text not null," + COLUMN_LINK
			+ " text not null," + COLUMN_DATE + " date not null," + COLUMN_THUMBNAIL + " text not null," + COLUMN_CONTENT
			+ " text not null," + COLUMN_CHECKSUM + " text not null," + COLUMN_LOCATION + " text not null );";

	public MainSqlHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(MainSqlHelper.class.getName(), "Upgrading database from version " + oldVersion + " to " + newVersion
				+ ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_PICS);
		onCreate(db);
	}

}
