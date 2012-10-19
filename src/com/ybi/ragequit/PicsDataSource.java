package com.ybi.ragequit;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * 
 *
 */

public class PicsDataSource {

	// Database fields
	private SQLiteDatabase database;
	private final MainSqlHelper dbHelper;

	public PicsDataSource(Context context) {
		dbHelper = new MainSqlHelper(context);
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	public Message createMessage(Message message) {

		Log.d("RageQuit", "Creating message " + message.getTitle());
		ContentValues values = new ContentValues();
		values.put(MainSqlHelper.COLUMN_TITLE, message.getTitle());
		values.put(MainSqlHelper.COLUMN_DESC, message.getDescription());
		values.put(MainSqlHelper.COLUMN_LINK, message.getLink().toString());
		values.put(MainSqlHelper.COLUMN_DATE, message.getDate());
		values.put(MainSqlHelper.COLUMN_CONTENT, message.getMediaContent());
		values.put(MainSqlHelper.COLUMN_THUMBNAIL, message.getMediaThumbnail());
		values.put(MainSqlHelper.COLUMN_CHECKSUM, message.getChecksum());

		long insertId = database.insert(MainSqlHelper.TABLE_PICS, null, values);
		Cursor cursor =
				database.query(
						MainSqlHelper.TABLE_PICS,
						MainSqlHelper.ALL_COLUMNS,
						MainSqlHelper.COLUMN_ID + " = " + insertId,
						null,
						null,
						null,
						null);
		cursor.moveToFirst();
		Message newm = cursorToMessage(cursor);
		cursor.close();
		return newm;
	}

	public void deleteMessage(Message message) {
		long id = message.getId();
		database.delete(MainSqlHelper.TABLE_PICS, MainSqlHelper.COLUMN_ID + " = " + id, null);
	}

	public List<Message> getAllMessages() {
		List<Message> messages = new ArrayList<Message>();

		Cursor cursor = database.query(MainSqlHelper.TABLE_PICS, MainSqlHelper.ALL_COLUMNS, null, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Message me = cursorToMessage(cursor);
			messages.add(me);
			cursor.moveToNext();
		}
		// Make sure to close the cursor
		cursor.close();
		return messages;
	}

	private Message cursorToMessage(Cursor cursor) {
		//	public static final String[] ALL_COLUMNS = { COLUMN_ID, COLUMN_TITLE, COLUMN_DESC, COLUMN_LINK, COLUMN_DATE, COLUMN_THUMBNAIL, COLUMN_CONTENT };
		Message message = new Message();
		message.setId(cursor.getLong(0));
		message.setTitle(cursor.getString(1));
		message.setDescription(cursor.getString(2));
		message.setLink(cursor.getString(3));
		message.setDate(cursor.getString(4));
		message.setMediaThumbnail(cursor.getString(5));
		message.setMediaContent(cursor.getString(6));
		return message;
	}

	public boolean isMessage(String string) {
		Log.d("RageQuit", "Cehcking message " + string);
		boolean resutl = false;
		String[] id = { MainSqlHelper.COLUMN_ID };
		Cursor cursor =
				database.query(MainSqlHelper.TABLE_PICS, id, MainSqlHelper.COLUMN_LINK + "='" + string + "'", null, null, null, null);

		if (cursor.getCount() > 0) {
			return resutl;
		}
		return resutl;
	}
}
