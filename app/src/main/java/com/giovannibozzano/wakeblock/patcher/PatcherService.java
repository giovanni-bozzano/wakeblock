package com.giovannibozzano.wakeblock.patcher;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;

import androidx.annotation.Nullable;

import com.giovannibozzano.wakeblock.R;
import com.giovannibozzano.wakeblock.enums.InstallMode;
import com.giovannibozzano.wakeblock.utils.Utils;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;

public class PatcherService extends Service
{
	private boolean isProcessing = false;
	private final int notificationId = (int) (System.currentTimeMillis() % 10000);
	private final IBinder binder = new LocalBinder();
	private ObservableEmitter<PatcherStatus> statusObserver;
	private Observable<PatcherStatus> statusObservable;
	private Notification.Builder notificationBuilder;
	private WakeLock wakeLock;

	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		return START_STICKY;
	}

	@Nullable
	@Override
	public IBinder onBind(Intent intent)
	{
		return this.binder;
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();
		if (this.wakeLock != null && this.wakeLock.isHeld()) {
			this.wakeLock.release();
		}
	}

	public Observable<PatcherStatus> observeStatus()
	{
		if (this.statusObservable == null) {
			this.statusObservable = Observable.create(emitter -> this.statusObserver = emitter);
			this.statusObservable = this.statusObservable.share();
		}
		return this.statusObservable;
	}

	public void execute(String coreModPath, InstallMode installMode)
	{
		this.isProcessing = true;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			this.notificationBuilder = new Notification.Builder(this, Utils.createNotificationChannel(this, this.getPackageName(), "Patcher", NotificationManager.IMPORTANCE_LOW, Notification.VISIBILITY_PUBLIC, true));
		} else {
			this.notificationBuilder = new Notification.Builder(this);
			this.notificationBuilder.setPriority(Notification.PRIORITY_LOW);
		}
		this.startForeground(this.notificationId, this.buildNotification("Patching process is starting..."));
		PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
		if (powerManager == null) {
			return;
		}
		this.wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "wakeblock:wakeblock-patcher");
		this.wakeLock.acquire();
		Thread thread = new Thread(() -> {
			new PatcherProcess(this, this.statusObserver, coreModPath, installMode).run();
			this.wakeLock.release();
			this.stopForeground(true);
			this.stopSelf();
			this.isProcessing = false;
		});
		thread.setPriority(Thread.MAX_PRIORITY);
		thread.start();
	}

	private Notification buildNotification(String text)
	{
		Log.i(this.getPackageName(), text);
		return this.notificationBuilder.setSmallIcon(R.drawable.ic_service_unbound).setContentTitle("WakeBlock Patcher").setContentText(text).build();
	}

	public void sendNotification(String text)
	{
		NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
		if (notificationManager == null) {
			return;
		}
		notificationManager.notify(this.notificationId, this.buildNotification(text));
	}

	public boolean isProcessing()
	{
		return this.isProcessing;
	}

	public class LocalBinder extends Binder
	{
		public PatcherService getService()
		{
			return PatcherService.this;
		}
	}
}
