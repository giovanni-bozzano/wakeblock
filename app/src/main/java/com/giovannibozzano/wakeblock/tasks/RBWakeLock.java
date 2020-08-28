package com.giovannibozzano.wakeblock.tasks;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.giovannibozzano.wakeblock.WakeBlock;
import com.giovannibozzano.wakeblock.database.DatabaseHelper;
import com.giovannibozzano.wakeblock.database.WakeLocksTable.WakeLockEntry;
import com.giovannibozzano.wakeblock.enums.WakeLockStatus;
import com.giovannibozzano.wakeblock.utils.WakeLockData;
import com.giovannibozzano.wakeblock.utils.WakeLockJSONData;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class RBWakeLock extends AsyncTask<Void, Void, Void>
{
	private final WeakReference<Activity> activityWeakReference;

	public RBWakeLock(Activity activity)
	{
		this.activityWeakReference = new WeakReference<>(activity);
	}

	@Override
	protected Void doInBackground(Void... voids)
	{
		FileReader fileReader;
		try {
			fileReader = new FileReader(new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/WakeBlock/bwlb.json"));
		} catch (FileNotFoundException e) {
			this.activityWeakReference.get().runOnUiThread(() -> Toast.makeText(this.activityWeakReference.get(), "Restore failed! Does the file even exists?", Toast.LENGTH_LONG).show());
			return null;
		}
		SQLiteDatabase database = new DatabaseHelper(this.activityWeakReference.get()).getWritableDatabase();
		Gson gson = new Gson();
		Map<String, WakeLockJSONData> hashMap = gson.fromJson(fileReader, new TypeToken<HashMap<String, WakeLockJSONData>>()
		{
		}.getType());
		for (Entry<String, WakeLockJSONData> wakeLockData : hashMap.entrySet()) {
			String query = "INSERT OR REPLACE INTO " + WakeLockEntry.TABLE_NAME + " (" + WakeLockEntry._ID + ", " + WakeLockEntry.COLUMN_TAG + ", " + WakeLockEntry.COLUMN_OCCURRENCES + ", " + WakeLockEntry.COLUMN_TIMES_BLOCKED + ", " + WakeLockEntry.COLUMN_RUN_TIME + ", " + WakeLockEntry.COLUMN_LAST_ACQUISITION + ", " + WakeLockEntry.COLUMN_ACQUIRING_PACKAGES + ", " + WakeLockEntry.COLUMN_IS_BLOCKED + ", " + WakeLockEntry.COLUMN_BLOCK_TIME + ") VALUES (" + "(SELECT " + WakeLockEntry._ID + " FROM " + WakeLockEntry.TABLE_NAME + " WHERE " + WakeLockEntry.COLUMN_TAG + " = '" + wakeLockData.getKey() + "'), '" + wakeLockData.getKey() + "', " + "COALESCE((SELECT " + WakeLockEntry.COLUMN_OCCURRENCES + " FROM " + WakeLockEntry.TABLE_NAME + " WHERE " + WakeLockEntry.COLUMN_TAG + " = '" + wakeLockData.getKey() + "'), 0), " + "COALESCE((SELECT " + WakeLockEntry.COLUMN_TIMES_BLOCKED + " FROM " + WakeLockEntry.TABLE_NAME + " WHERE " + WakeLockEntry.COLUMN_TAG + " = '" + wakeLockData.getKey() + "'), 0), " + "COALESCE((SELECT " + WakeLockEntry.COLUMN_RUN_TIME + " FROM " + WakeLockEntry.TABLE_NAME + " WHERE " + WakeLockEntry.COLUMN_TAG + " = '" + wakeLockData.getKey() + "'), 0), " + "COALESCE((SELECT " + WakeLockEntry.COLUMN_LAST_ACQUISITION + " FROM " + WakeLockEntry.TABLE_NAME + " WHERE " + WakeLockEntry.COLUMN_TAG + " = '" + wakeLockData.getKey() + "'), NULL), '" + gson.toJson(wakeLockData.getValue().getAcquiringPackages()) + "', " + "1, " + wakeLockData.getValue().getBlockTime() + ")";
			Log.i(this.activityWeakReference.get().getPackageName(), query);
			database.execSQL(query);
		}
		if (WakeBlock.getWakeBlockService().fetchBinding()) {
			for (Entry<String, WakeLockJSONData> wakeLockData : hashMap.entrySet()) {
				if (WakeBlock.getWakeBlockService().getLoadedWakeLocks().containsKey(wakeLockData.getKey())) {
					WakeLockData serviceWakeLockData = WakeBlock.getWakeBlockService().getLoadedWakeLocks().get(wakeLockData.getKey());
					serviceWakeLockData.getAcquiringPackages().clear();
					for (String acquiringPackages : wakeLockData.getValue().getAcquiringPackages()) {
						serviceWakeLockData.addToAcquiringPackages(acquiringPackages);
					}
					serviceWakeLockData.setIsBlocked(true);
					serviceWakeLockData.setBlockTime(wakeLockData.getValue().getBlockTime());
				} else {
					WakeBlock.getWakeBlockService().getLoadedWakeLocks().put(wakeLockData.getKey(), new WakeLockData(0, 0, 0, null, wakeLockData.getValue().getAcquiringPackages(), true, wakeLockData.getValue().getBlockTime(), WakeLockStatus.NOT_PENDING));
				}
			}
		}
		database.close();
		return null;
	}

	@Override
	protected void onPostExecute(Void result)
	{
		Toast.makeText(this.activityWeakReference.get(), "Restore Done!", Toast.LENGTH_LONG).show();
	}
}
