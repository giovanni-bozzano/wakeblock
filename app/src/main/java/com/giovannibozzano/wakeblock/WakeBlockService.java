package com.giovannibozzano.wakeblock;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.SparseArray;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.giovannibozzano.wakeblock.database.DatabaseHelper;
import com.giovannibozzano.wakeblock.database.WakeLocksTable;
import com.giovannibozzano.wakeblock.enums.WakeLockStatus;
import com.giovannibozzano.wakeblock.utils.Utils;
import com.giovannibozzano.wakeblock.utils.WakeLockData;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;

public class WakeBlockService extends Service
{
	private static final Object LOCK = new Object();
	private static final SparseArray<IMessageHandler> MESSAGE_TYPES = new SparseArray<>();

	static {
		WakeBlockService.MESSAGE_TYPES.put(MessageType.WAKELOCK_ACQUIRE, WakeBlockService::handleAcquireWakeLock);
		WakeBlockService.MESSAGE_TYPES.put(MessageType.WAKELOCK_RELEASE, WakeBlockService::handleReleaseWakeLock);
		// WakeBlockService.MESSAGE_TYPES.put(MessageType.WAKELOCK_UPDATE, WakeBlockService::handleUpdateWakeLock);
		WakeBlockService.MESSAGE_TYPES.put(MessageType.WAKEBLOCK_VERSION, (wakeBlockService, message) -> {
			if (WakeBlock.isCoreModVersionChecked()) {
				return;
			}
			WakeBlock.setCoreModVersion(message.getData().getShort("version"));
			WakeBlock.setCoreModVersionChecked(true);
			if (WakeBlock.getCoreModVersion() != Utils.CORE_MOD_VERSION) {
				NotificationCompat.Builder notificationBuilder;
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
					notificationBuilder = new NotificationCompat.Builder(wakeBlockService, Utils.createNotificationChannel(wakeBlockService, wakeBlockService.getPackageName(), "Core Mod", NotificationManager.IMPORTANCE_DEFAULT, Notification.VISIBILITY_PUBLIC, true));
				} else {
					notificationBuilder = new NotificationCompat.Builder(wakeBlockService, wakeBlockService.getPackageName());
					notificationBuilder.setPriority(Notification.PRIORITY_DEFAULT);
				}
				NotificationManager notificationManager = (NotificationManager) wakeBlockService.getSystemService(Context.NOTIFICATION_SERVICE);
				if (notificationManager == null) {
					return;
				}
				notificationManager.notify((int) (System.currentTimeMillis() % 10000), notificationBuilder.setSmallIcon(R.drawable.ic_service_unbound).setContentTitle("Core Mod Outdated").setStyle(new NotificationCompat.BigTextStyle().bigText("The WakeBlock core mod is outdated. Please uninstall and reinstall the mod to update it.\nNote that until the core mod is updated WakeBlock will not block or register any wakelock.")).build());
			}
			if (wakeBlockService.coreModOutdatedObservable == null) {
				wakeBlockService.coreModOutdatedObservable = Observable.create(emitter -> {
					wakeBlockService.coreModOutdatedObserver = emitter;
					wakeBlockService.coreModOutdatedObserver.onNext(WakeBlock.getCoreModVersion() != Utils.CORE_MOD_VERSION);
				});
				wakeBlockService.coreModOutdatedObservable = wakeBlockService.coreModOutdatedObservable.share();
			} else {
				wakeBlockService.coreModOutdatedObserver.onNext(WakeBlock.getCoreModVersion() != Utils.CORE_MOD_VERSION);
			}
		});
	}

	private final Map<String, WakeLockData> loadedWakeLocks = Collections.synchronizedMap(new HashMap<>());
	private SharedPreferences sharedPreferences;
	private final IBinder binder = new LocalBinder();
	private final DatabaseHelper databaseHelper = new DatabaseHelper(this);
	private Messenger systemMessenger;
	private boolean bound = false;
	private ObservableEmitter<Boolean> bindingObserver;
	private Observable<Boolean> bindingObservable;
	private ObservableEmitter<Boolean> coreModOutdatedObserver;
	private Observable<Boolean> coreModOutdatedObservable;
	private Integer foregroundId;

	@Override
	public void onCreate()
	{
		this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		if (intent != null && intent.getBooleanExtra("shutdown", false)) {
			Log.i(Utils.TAG, "+++++++++++++++++++++++++ WAKEBLOCK SERVICE IS BEING STARTED FOR SYSTEM SHUTDOWN +++++++++++++++++++++++++");
			synchronized (WakeBlockService.LOCK) {
				if (this.sharedPreferences.getBoolean("debug_log", false)) {
					Log.i(Utils.TAG, "########################################");
					Log.i(Utils.TAG, "# START SHUTDOWN SAVING");
				}
				this.saveToDatabase();
				if (this.sharedPreferences.getBoolean("debug_log", false)) {
					Log.i(Utils.TAG, "# END SHUTDOWN SAVING");
					Log.i(Utils.TAG, "########################################");
				}
			}
			this.stopSelf();
			return Service.START_NOT_STICKY;
		}
		if (!this.sharedPreferences.getBoolean("foreground_service", false)) {
			return Service.START_STICKY;
		}
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			String channelId = Utils.createNotificationChannel(this, this.getPackageName(), "Service", NotificationManager.IMPORTANCE_MIN, Notification.VISIBILITY_SECRET, false);
			Notification notification = new NotificationCompat.Builder(this, channelId).build();
			if (this.foregroundId == null) {
				this.foregroundId = (int) (System.currentTimeMillis() % 10000);
			}
			this.startForeground(this.foregroundId, notification);
		}
		Log.i(Utils.TAG, "+++++++++++++++++++++++++ WAKEBLOCK SERVICE IS SENDING THE WAKELOCK TO THE SYSTEM +++++++++++++++++++++++++");
		PowerManager powerManager = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
		if (powerManager != null) {
			WakeLock wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "service_bind");
			wakeLock.acquire(0L);
			wakeLock.release();
			Log.i(Utils.TAG, "+++++++++++++++++++++++++ WAKEBLOCK SERVICE SENT THE WAKELOCK TO THE SYSTEM +++++++++++++++++++++++++");
		} else {
			Log.i(Utils.TAG, "+++++++++++++++++++++++++ WAKEBLOCK SERVICE FAILED TO SEND THE WAKELOCK TO THE SYSTEM +++++++++++++++++++++++++");
		}
		return Service.START_STICKY;
	}

	@Override
	public void onDestroy()
	{
		Log.i(Utils.TAG, "+++++++++++++++++++++++++ WAKEBLOCK SERVICE IS BEING DESTROYED +++++++++++++++++++++++++");
		if (this.systemMessenger != null) {
			try {
				this.systemMessenger.send(Message.obtain(null, 0));
			} catch (RemoteException exception) {
				exception.printStackTrace();
			}
		}
		this.databaseHelper.close();
		super.onDestroy();
	}

	public IBinder onBind(Intent intent)
	{
		if (intent.getBooleanExtra("system", false)) {
			Log.i(Utils.TAG, "+++++++++++++++++++++++++ WAKEBLOCK SERVICE IS NOW BOUND TO THE SYSTEM +++++++++++++++++++++++++");
			this.bound = true;
			HandlerThread handlerThread = new HandlerThread("wakeblock_server");
			handlerThread.start();
			Handler handler = new Handler(handlerThread.getLooper())
			{
				@Override
				public void handleMessage(@NonNull Message message)
				{
					// TODO: remove this in future versions (the core mod automatically sends its version upon binding).
					if (!WakeBlock.isCoreModVersionChecked()) {
						try {
							WakeBlockService.this.systemMessenger.send(Message.obtain(null, 2));
						} catch (RemoteException exception) {
							exception.printStackTrace();
						}
					}
					// TODO END
					IMessageHandler messageHandler = WakeBlockService.MESSAGE_TYPES.get(message.what);
					if (messageHandler != null) {
						messageHandler.run(WakeBlockService.this, message);
					} else {
						super.handleMessage(message);
					}
				}
			};
			SQLiteDatabase database = this.databaseHelper.getReadableDatabase();
			String[] projection = { WakeLocksTable.WakeLockEntry.COLUMN_TAG, WakeLocksTable.WakeLockEntry.COLUMN_OCCURRENCES, WakeLocksTable.WakeLockEntry.COLUMN_TIMES_BLOCKED, WakeLocksTable.WakeLockEntry.COLUMN_RUN_TIME, WakeLocksTable.WakeLockEntry.COLUMN_LAST_ACQUISITION, WakeLocksTable.WakeLockEntry.COLUMN_ACQUIRING_PACKAGES, WakeLocksTable.WakeLockEntry.COLUMN_IS_BLOCKED, WakeLocksTable.WakeLockEntry.COLUMN_BLOCK_TIME };
			Cursor cursor = database.query(WakeLocksTable.WakeLockEntry.TABLE_NAME, projection, null, null, null, null, null);
			cursor.moveToFirst();
			if (cursor.getCount() > 0) {
				do {
					List<String> acquiringPackages = new Gson().fromJson(cursor.getString(5), new TypeToken<List<String>>()
					{
					}.getType());
					this.loadedWakeLocks.put(cursor.getString(0), new WakeLockData(cursor.getInt(1), cursor.getInt(2), cursor.getLong(3), cursor.getLong(4), acquiringPackages, cursor.getInt(6) == 1, cursor.getLong(7), WakeLockStatus.NOT_PENDING));
				} while (cursor.moveToNext());
			}
			cursor.close();
			database.close();
			Timer timer = new Timer();
			timer.schedule(new TimerTask()
			{
				@Override
				public void run()
				{
					synchronized (WakeBlockService.LOCK) {
						if (WakeBlockService.this.sharedPreferences.getBoolean("debug_log", false)) {
							Log.i(Utils.TAG, "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
							Log.i(Utils.TAG, "@ START THREAD SAVING");
						}
						WakeBlockService.this.saveToDatabase();
						if (WakeBlockService.this.sharedPreferences.getBoolean("debug_log", false)) {
							Log.i(Utils.TAG, "@ END THREAD SAVING");
							List<Map.Entry<String, WakeLockData>> wakeLockData = new ArrayList<>(WakeBlockService.this.loadedWakeLocks.entrySet());
							for (Map.Entry<String, WakeLockData> wakeLock : wakeLockData) {
								WakeLockData wakeLockSingleData = wakeLock.getValue();
								if (!wakeLockSingleData.getLocks().isEmpty()) {
									Log.i(Utils.TAG, "@ RUNNING: " + wakeLock.getKey());
								}
							}
							Log.i(Utils.TAG, "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
						}
					}
				}
			}, (long) (60 * 60 * 10), (long) (60 * 60 * 10));
			this.systemMessenger = new Messenger(intent.getBundleExtra("bundle").getBinder("messenger"));
			if (this.bindingObservable == null) {
				this.bindingObservable = Observable.create(emitter -> {
					this.bindingObserver = emitter;
					this.bindingObserver.onNext(true);
				});
				this.bindingObservable = this.bindingObservable.share();
			} else {
				this.bindingObserver.onNext(true);
			}
			return new Messenger(handler).getBinder();
		}
		Log.i(Utils.TAG, "+++++++++++++++++++++++++ WAKEBLOCK SERVICE IS NOW BOUND TO THE APP +++++++++++++++++++++++++");
		return this.binder;
	}

	@Override
	public boolean onUnbind(Intent intent)
	{
		Log.i(Utils.TAG, "+++++++++++++++++++++++++ WAKEBLOCK SERVICE IS NOW UNBOUND FROM " + intent.getPackage() + " +++++++++++++++++++++++++");
		return false;
	}

	private void handleAcquireWakeLock(Message message)
	{
		if (!WakeBlock.isCoreModVersionChecked()) {
			try {
				this.systemMessenger.send(Message.obtain(null, 0));
				if (this.sharedPreferences.getBoolean("debug_log", false)) {
					Log.i(Utils.TAG, "- ALLOW");
				}
			} catch (RemoteException exception) {
				exception.printStackTrace();
			}
			return;
		}
		IBinder lock = message.getData().getBinder("lock");
		String tag = message.getData().getString("tag");
		String acquiringPackage = message.getData().getString("package_name");
		if (lock == null || tag == null || acquiringPackage == null) {
			return;
		}
		if (this.sharedPreferences.getBoolean("debug_log", false)) {
			Log.i(Utils.TAG, "----------------------------------------");
			Log.i(Utils.TAG, "- ACQUIRING: " + tag);
			Log.i(Utils.TAG, "- PACKAGE: " + acquiringPackage);
		}
		WakeLockData wakeLockData = WakeBlockService.this.loadedWakeLocks.get(tag);
		long now = System.currentTimeMillis();
		// Check if this WakeLock is inside the map
		if (wakeLockData == null) {
			// If it is not, create a new entry which is running
			List<String> acquiringPackages = new ArrayList<>();
			acquiringPackages.add(acquiringPackage);
			WakeLockData newWakeLockData = new WakeLockData(1, 0, 0, now, acquiringPackages, false, 0, WakeLockStatus.PENDING_CREATION);
			newWakeLockData.addToLocks(lock);
			newWakeLockData.setOccurrencesBoot(1);
			WakeBlockService.this.loadedWakeLocks.put(tag, newWakeLockData);
			try {
				this.systemMessenger.send(Message.obtain(null, 0));
				if (this.sharedPreferences.getBoolean("debug_log", false)) {
					Log.i(Utils.TAG, "- NEW ALLOW");
				}
			} catch (RemoteException exception) {
				exception.printStackTrace();
			}
		} else {
			// If it is, check if it is blocked and how much time has passed since last occurrence
			if (wakeLockData.getIsBlocked() && ((wakeLockData.getBlockTime() > now - wakeLockData.getLastAcquisition()) || !wakeLockData.getLocks().isEmpty() || wakeLockData.getBlockTime() == 0)) {
				wakeLockData.setTimesBlockedTotal(wakeLockData.getTimesBlockedTotal() + 1);
				wakeLockData.setTimesBlockedBoot(wakeLockData.getTimesBlockedBoot() + 1);
				try {
					this.systemMessenger.send(Message.obtain(null, 1));
					if (this.sharedPreferences.getBoolean("debug_log", false)) {
						Log.i(Utils.TAG, "- BLOCK");
					}
				} catch (RemoteException exception) {
					exception.printStackTrace();
				}
			} else {
				// Last occurrence was right now, and it's now running with one more instance than before
				wakeLockData.getLocks().add(lock);
				wakeLockData.setOccurrencesTotal(wakeLockData.getOccurrencesTotal() + 1);
				wakeLockData.setOccurrencesBoot(wakeLockData.getOccurrencesBoot() + 1);
				if (!wakeLockData.getAcquiringPackages().contains(acquiringPackage)) {
					wakeLockData.addToAcquiringPackages(acquiringPackage);
				}
				if (wakeLockData.getLocks().size() > 1 && now > wakeLockData.getLastRunTimeRefresh()) {
					wakeLockData.setRunTimeTotal(wakeLockData.getRunTimeTotal() + (now - wakeLockData.getLastRunTimeRefresh()));
					wakeLockData.setRunTimeBoot(wakeLockData.getRunTimeBoot() + (now - wakeLockData.getLastRunTimeRefresh()));
				}
				wakeLockData.setLastAcquisition(now);
				wakeLockData.setLastRunTimeRefresh(now);
				// If it's the first time the WakeLock entry is updated, set it so that it will be updated in the database
				if (wakeLockData.getStatus() == WakeLockStatus.NOT_PENDING) {
					wakeLockData.setStatus(WakeLockStatus.PENDING_UPDATE);
				}
				try {
					this.systemMessenger.send(Message.obtain(null, 0));
					if (this.sharedPreferences.getBoolean("debug_log", false)) {
						Log.i(Utils.TAG, "- ALLOW");
					}
				} catch (RemoteException exception) {
					exception.printStackTrace();
				}
			}
		}
		if (this.sharedPreferences.getBoolean("debug_log", false)) {
			Log.i(Utils.TAG, "----------------------------------------");
		}
	}

	private void handleReleaseWakeLock(Message message)
	{
		if (!WakeBlock.isCoreModVersionChecked()) {
			try {
				this.systemMessenger.send(Message.obtain(null, 0));
			} catch (RemoteException exception) {
				exception.printStackTrace();
			}
			return;
		}
		IBinder lock = message.getData().getBinder("lock");
		String tag = message.getData().getString("tag");
		if (lock == null || tag == null) {
			return;
		}
		if (this.sharedPreferences.getBoolean("debug_log", false)) {
			Log.i(Utils.TAG, "****************************************");
			Log.i(Utils.TAG, "* RELEASING: " + tag);
		}
		WakeLockData wakeLockData = WakeBlockService.this.loadedWakeLocks.get(tag);
		// Check if this WakeLock is inside the map and if it is set as running
		if (wakeLockData != null && wakeLockData.getLocks().contains(lock)) {
			wakeLockData.getLocks().remove(lock);
			long now = System.currentTimeMillis();
			// Check system time to avoid boot time-set glitch
			if (now > wakeLockData.getLastRunTimeRefresh()) {
				wakeLockData.setRunTimeTotal(wakeLockData.getRunTimeTotal() + (now - wakeLockData.getLastRunTimeRefresh()));
				wakeLockData.setRunTimeBoot(wakeLockData.getRunTimeBoot() + (now - wakeLockData.getLastRunTimeRefresh()));
				if (this.sharedPreferences.getBoolean("debug_log", false)) {
					Log.i(Utils.TAG, "* LAST RUNTIME: " + (now - wakeLockData.getLastRunTimeRefresh()));
					Log.i(Utils.TAG, "* TOTAL RUNTIME: " + wakeLockData.getRunTimeTotal());
				}
			}
			wakeLockData.setLastRunTimeRefresh(now);
			if (wakeLockData.getStatus() == WakeLockStatus.NOT_PENDING) {
				wakeLockData.setStatus(WakeLockStatus.PENDING_UPDATE);
			}
			// Last occurrence was right now
			if (this.sharedPreferences.getBoolean("debug_log", false)) {
				Log.i(Utils.TAG, "* INSTANCES LEFT: " + wakeLockData.getLocks().size());
			}
		} else if (this.sharedPreferences.getBoolean("debug_log", false)) {
			// This WakeLock was never registered by the app
			Log.i(Utils.TAG, "* ERROR: WAKELOCK NEVER ACQUIRED OR ALREADY RELEASED");
		}
		try {
			this.systemMessenger.send(Message.obtain(null, 0));
		} catch (RemoteException exception) {
			exception.printStackTrace();
		}
		if (this.sharedPreferences.getBoolean("debug_log", false)) {
			Log.i(Utils.TAG, "****************************************");
		}
	}

	/*
	private void handleUpdateWakeLock(Message message)
	{
		if (!WakeBlock.isCoreModVersionChecked()) {
			try {
				this.systemMessenger.send(Message.obtain(null, 0));
			} catch (RemoteException exception) {
				exception.printStackTrace();
			}
			return;
		}
		IBinder lock = message.getData().getBinder("lock");
		String oldTag = message.getData().getString("old_tag");
		String newTag = message.getData().getString("new_tag");
		if (lock == null | oldTag == null || newTag == null) {
			return;
		}
		long now = System.currentTimeMillis();
		WakeLockData oldWakeLockData = WakeBlockService.this.loadedWakeLocks.get(oldTag);
		if (oldWakeLockData != null && oldWakeLockData.getLocks().contains(lock)) {
			oldWakeLockData.getLocks().remove(lock);
			// Check system time to avoid boot time-set glitch
			if (now > oldWakeLockData.getLastRunTimeRefresh()) {
				oldWakeLockData.setRunTimeTotal(oldWakeLockData.getRunTimeTotal() + (now - oldWakeLockData.getLastRunTimeRefresh()));
				oldWakeLockData.setRunTimeBoot(oldWakeLockData.getRunTimeBoot() + (now - oldWakeLockData.getLastRunTimeRefresh()));
			}
			oldWakeLockData.setLastRunTimeRefresh(now);
			if (oldWakeLockData.getStatus() == WakeLockStatus.NOT_PENDING) {
				oldWakeLockData.setStatus(WakeLockStatus.PENDING_UPDATE);
			}
			WakeLockData newWakeLockData = WakeBlockService.this.loadedWakeLocks.get(newTag);
			if (newWakeLockData != null) {
				newWakeLockData.getLocks().add(lock);
				newWakeLockData.setOccurrencesTotal(newWakeLockData.getOccurrencesTotal() + 1);
				newWakeLockData.setOccurrencesBoot(newWakeLockData.getOccurrencesBoot() + 1);
				if (oldWakeLockData.getAcquiringPackages().size() > 1 && !newWakeLockData.getAcquiringPackages().contains(oldWakeLockData.getAcquiringPackages().get(0))) {
					newWakeLockData.getAcquiringPackages().add(oldWakeLockData.getAcquiringPackages().get(0));
				}
				if (!newWakeLockData.getLocks().isEmpty() && now > newWakeLockData.getLastRunTimeRefresh()) {
					newWakeLockData.setRunTimeTotal(newWakeLockData.getRunTimeTotal() + (now - newWakeLockData.getLastRunTimeRefresh()));
					newWakeLockData.setRunTimeBoot(newWakeLockData.getRunTimeBoot() + (now - newWakeLockData.getLastRunTimeRefresh()));
				}
				newWakeLockData.setLastAcquisition(now);
				newWakeLockData.setLastRunTimeRefresh(now);
				if (newWakeLockData.getStatus() == WakeLockStatus.NOT_PENDING) {
					newWakeLockData.setStatus(WakeLockStatus.PENDING_UPDATE);
				}
			} else {
				List<String> acquiringPackages = new ArrayList<>();
				if (!oldWakeLockData.getAcquiringPackages().isEmpty()) {
					acquiringPackages.add(oldWakeLockData.getAcquiringPackages().get(0));
				}
				WakeBlockService.this.loadedWakeLocks.put(newTag, new WakeLockData(1, 0, 0, now, acquiringPackages, false, 0, WakeLockStatus.PENDING_CREATION));
				WakeBlockService.this.loadedWakeLocks.get(newTag).addToLocks(lock);
				WakeBlockService.this.loadedWakeLocks.get(newTag).setOccurrencesBoot(1);
			}
		}
		try {
			this.systemMessenger.send(Message.obtain(null, 0));
		} catch (RemoteException exception) {
			exception.printStackTrace();
		}
	}
	*/

	private void saveToDatabase()
	{
		SQLiteDatabase database = this.databaseHelper.getWritableDatabase();
		long now = System.currentTimeMillis();
		List<Map.Entry<String, WakeLockData>> wakeLockData = new ArrayList<>(WakeBlockService.this.loadedWakeLocks.entrySet());
		for (Map.Entry<String, WakeLockData> wakeLock : wakeLockData) {
			WakeLockData wakeLockSingleData = wakeLock.getValue();
			ContentValues values = new ContentValues();
			if (wakeLockSingleData.getStatus() == WakeLockStatus.PENDING_CREATION) {
				wakeLockSingleData.setStatus(WakeLockStatus.NOT_PENDING);
				if (this.sharedPreferences.getBoolean("debug_log", false)) {
					Log.i(Utils.TAG, "@ SAVING: " + wakeLock.getKey());
				}
				values.put(WakeLocksTable.WakeLockEntry.COLUMN_TAG, wakeLock.getKey());
				values.put(WakeLocksTable.WakeLockEntry.COLUMN_OCCURRENCES, wakeLockSingleData.getOccurrencesTotal());
				values.put(WakeLocksTable.WakeLockEntry.COLUMN_TIMES_BLOCKED, wakeLockSingleData.getTimesBlockedTotal());
				if (!wakeLockSingleData.getLocks().isEmpty() && now > wakeLockSingleData.getLastRunTimeRefresh()) {
					wakeLockSingleData.setRunTimeTotal(wakeLockSingleData.getRunTimeTotal() + (now - wakeLockSingleData.getLastRunTimeRefresh()));
					wakeLockSingleData.setRunTimeBoot(wakeLockSingleData.getRunTimeBoot() + (now - wakeLockSingleData.getLastRunTimeRefresh()));
					wakeLockSingleData.setLastRunTimeRefresh(now);
				}
				values.put(WakeLocksTable.WakeLockEntry.COLUMN_RUN_TIME, wakeLockSingleData.getRunTimeTotal());
				values.put(WakeLocksTable.WakeLockEntry.COLUMN_LAST_ACQUISITION, wakeLockSingleData.getLastAcquisition());
				values.put(WakeLocksTable.WakeLockEntry.COLUMN_ACQUIRING_PACKAGES, new Gson().toJson(wakeLockSingleData.getAcquiringPackages()));
				values.put(WakeLocksTable.WakeLockEntry.COLUMN_IS_BLOCKED, wakeLockSingleData.getIsBlocked() ? 1 : 0);
				values.put(WakeLocksTable.WakeLockEntry.COLUMN_BLOCK_TIME, wakeLockSingleData.getBlockTime());
				database.insert(WakeLocksTable.WakeLockEntry.TABLE_NAME, null, values);
			} else if (wakeLockSingleData.getStatus() == WakeLockStatus.PENDING_UPDATE) {
				wakeLockSingleData.setStatus(WakeLockStatus.NOT_PENDING);
				if (this.sharedPreferences.getBoolean("debug_log", false)) {
					Log.i(Utils.TAG, "@ UPDATING: " + wakeLock.getKey());
				}
				values.put(WakeLocksTable.WakeLockEntry.COLUMN_OCCURRENCES, wakeLockSingleData.getOccurrencesTotal());
				values.put(WakeLocksTable.WakeLockEntry.COLUMN_TIMES_BLOCKED, wakeLockSingleData.getTimesBlockedTotal());
				if (!wakeLockSingleData.getLocks().isEmpty() && now > wakeLockSingleData.getLastRunTimeRefresh()) {
					wakeLockSingleData.setRunTimeTotal(wakeLockSingleData.getRunTimeTotal() + (now - wakeLockSingleData.getLastRunTimeRefresh()));
					wakeLockSingleData.setRunTimeBoot(wakeLockSingleData.getRunTimeBoot() + (now - wakeLockSingleData.getLastRunTimeRefresh()));
					wakeLockSingleData.setLastRunTimeRefresh(now);
				}
				values.put(WakeLocksTable.WakeLockEntry.COLUMN_RUN_TIME, wakeLockSingleData.getRunTimeTotal());
				values.put(WakeLocksTable.WakeLockEntry.COLUMN_LAST_ACQUISITION, wakeLockSingleData.getLastAcquisition());
				values.put(WakeLocksTable.WakeLockEntry.COLUMN_ACQUIRING_PACKAGES, new Gson().toJson(wakeLockSingleData.getAcquiringPackages()));
				values.put(WakeLocksTable.WakeLockEntry.COLUMN_IS_BLOCKED, wakeLockSingleData.getIsBlocked() ? 1 : 0);
				values.put(WakeLocksTable.WakeLockEntry.COLUMN_BLOCK_TIME, wakeLockSingleData.getBlockTime());
				String selection = WakeLocksTable.WakeLockEntry.COLUMN_TAG + " LIKE ?";
				String[] selectionArguments = { wakeLock.getKey() };
				database.update(WakeLocksTable.WakeLockEntry.TABLE_NAME, values, selection, selectionArguments);
			}
		}
		database.close();
	}

	public boolean fetchBinding()
	{
		return this.bound;
	}

	public Observable<Boolean> observeBinding()
	{
		if (this.bindingObservable == null) {
			this.bindingObservable = Observable.create(emitter -> this.bindingObserver = emitter);
			this.bindingObservable = this.bindingObservable.share();
		}
		return this.bindingObservable;
	}

	public Observable<Boolean> observeOutdatedCoreMod()
	{
		if (this.coreModOutdatedObservable == null) {
			this.coreModOutdatedObservable = Observable.create(emitter -> this.coreModOutdatedObserver = emitter);
			this.coreModOutdatedObservable = this.coreModOutdatedObservable.share();
		}
		return this.bindingObservable;
	}

	public Map<String, WakeLockData> getLoadedWakeLocks()
	{
		return this.loadedWakeLocks;
	}

	class LocalBinder extends Binder
	{
		WakeBlockService getService()
		{
			return WakeBlockService.this;
		}
	}
}
