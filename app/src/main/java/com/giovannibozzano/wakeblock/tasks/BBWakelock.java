package com.giovannibozzano.wakeblock.tasks;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.giovannibozzano.wakeblock.R;
import com.giovannibozzano.wakeblock.database.DatabaseHelper;
import com.giovannibozzano.wakeblock.database.WakeLocksTable;
import com.giovannibozzano.wakeblock.utils.WakeLockJSONData;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;

public class BBWakelock extends AsyncTask<Void, Void, Void>
{
	private final WeakReference<Activity> activityWeakReference;

	public BBWakelock(Activity activity)
	{
		this.activityWeakReference = new WeakReference<>(activity);
	}

	@Override
	protected Void doInBackground(Void... voids)
	{
		File outputFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/WakeBlock/bwlb.json");
		File outputDirectory = outputFile.getParentFile();
		if (outputDirectory != null && (!outputDirectory.exists() || !outputDirectory.isDirectory()) && !outputDirectory.mkdirs()) {
			Log.e(activityWeakReference.get().getPackageName(), String.valueOf(R.string.json_error));
			this.activityWeakReference.get().runOnUiThread(() -> Toast.makeText(activityWeakReference.get(), String.valueOf(R.string.backup_error), Toast.LENGTH_LONG).show());
		}
		if (outputFile.exists() && !outputFile.delete()) {
			Log.e(activityWeakReference.get().getPackageName(), String.valueOf(R.string.json_error));
			this.activityWeakReference.get().runOnUiThread(() -> Toast.makeText(activityWeakReference.get(), String.valueOf(R.string.backup_error), Toast.LENGTH_LONG).show());
		}
		SQLiteDatabase database = new DatabaseHelper(this.activityWeakReference.get()).getReadableDatabase();
		HashMap<String, WakeLockJSONData> hashMap = new HashMap<>();
		String[] projection = { WakeLocksTable.WakeLockEntry.COLUMN_TAG, WakeLocksTable.WakeLockEntry.COLUMN_ACQUIRING_PACKAGES, WakeLocksTable.WakeLockEntry.COLUMN_BLOCK_TIME, WakeLocksTable.WakeLockEntry.COLUMN_IS_BLOCKED };
		Cursor cursor = database.query(WakeLocksTable.WakeLockEntry.TABLE_NAME, projection, null, null, null, null, null);
		cursor.moveToFirst();
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		Type type = new TypeToken<List<String>>()
		{
		}.getType();
		for (int i = 0; i < cursor.getCount(); i++) {
			if (cursor.getInt(3) == 1) {
				hashMap.put(cursor.getString(0), new WakeLockJSONData(gson.fromJson(cursor.getString(1), type), cursor.getLong(2), true));
			}
			cursor.moveToNext();
		}
		cursor.close();
		database.close();
		try (Writer writer = new FileWriter(outputFile.getAbsolutePath())) {
			gson.toJson(hashMap, writer);
		} catch (IOException e) {
			e.printStackTrace();
			Log.e(activityWeakReference.get().getPackageName(), String.valueOf(R.string.json_error));
			this.activityWeakReference.get().runOnUiThread(() -> Toast.makeText(activityWeakReference.get(), "Backup Error!", Toast.LENGTH_LONG).show());
		}
		return null;
	}

	@Override
	protected void onPostExecute(Void result)
	{
		Toast.makeText(activityWeakReference.get(), "Backup Done! You can find it in /sdcard/WakeBlock", Toast.LENGTH_LONG).show();
	}
}
