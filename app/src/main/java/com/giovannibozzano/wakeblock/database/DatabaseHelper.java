package com.giovannibozzano.wakeblock.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.giovannibozzano.wakeblock.database.WakeLocksTable.WakeLockEntry;

public class DatabaseHelper extends SQLiteOpenHelper
{
	private static final int DATABASE_VERSION = 4;
	private static final String DATABASE_NAME = "WakeLocks.db";
	private static final String SQL_CREATE_ENTRIES = "CREATE TABLE " + WakeLockEntry.TABLE_NAME + " (" + WakeLockEntry._ID + " INTEGER PRIMARY KEY, " + WakeLockEntry.COLUMN_TAG + " TEXT NOT NULL, " + WakeLockEntry.COLUMN_OCCURRENCES + " INTEGER NOT NULL DEFAULT 0, " + WakeLockEntry.COLUMN_TIMES_BLOCKED + " INTEGER NOT NULL DEFAULT 0, " + WakeLockEntry.COLUMN_RUN_TIME + " INTEGER NOT NULL DEFAULT 0, " + WakeLockEntry.COLUMN_LAST_ACQUISITION + " INTEGER DEFAULT NULL, " + WakeLockEntry.COLUMN_ACQUIRING_PACKAGES + " TEXT NOT NULL, " + WakeLockEntry.COLUMN_IS_BLOCKED + " BOOLEAN NOT NULL, " + WakeLockEntry.COLUMN_BLOCK_TIME + " INTEGER NOT NULL )";
	private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + WakeLockEntry.TABLE_NAME;

	public DatabaseHelper(Context context)
	{
		super(context, DatabaseHelper.DATABASE_NAME, null, DatabaseHelper.DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database)
	{
		database.execSQL(DatabaseHelper.SQL_CREATE_ENTRIES);
	}

	@Override
	public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion)
	{
		database.execSQL(DatabaseHelper.SQL_DELETE_ENTRIES);
		this.onCreate(database);
	}

	@Override
	public void onDowngrade(SQLiteDatabase database, int oldVersion, int newVersion)
	{
		this.onUpgrade(database, oldVersion, newVersion);
	}
}
