package com.giovannibozzano.wakeblock;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Slog;

public class WakeBlockService
{
	private static final short VERSION = 1;
	private static final WakeBlockService INSTANCE = new WakeBlockService();
	private static final String TAG = "WakeBlockService";
	private Messenger client;
	private Messenger server;
	private boolean serviceBound = false;
	private static volatile boolean bindNext = false;
	private static volatile boolean acquire = true;
	private static final Object lock = new Object();
	private final Intent serviceIntent = new Intent("com.giovannibozzano.wakeblock.Service");
	private final ServiceConnection serviceConnection = new ServiceConnection()
	{
		@Override
		public void onServiceConnected(ComponentName className, IBinder service)
		{
			WakeBlockService.this.server = new Messenger(service);
			WakeBlockService.this.serviceBound = true;
			try {
				Message newMessage = Message.obtain(null, 3);
				Bundle bundle = new Bundle();
				bundle.putShort("version", WakeBlockService.VERSION);
				newMessage.setData(bundle);
				WakeBlockService.this.server.send(newMessage);
			} catch (RemoteException exception) {
				WakeBlockService.this.server = null;
				WakeBlockService.this.serviceBound = false;
			}
		}

		@Override
		public void onServiceDisconnected(ComponentName className)
		{
			WakeBlockService.this.server = null;
			WakeBlockService.this.serviceBound = false;
			synchronized (WakeBlockService.lock) {
				WakeBlockService.lock.notifyAll();
			}
		}
	};

	private WakeBlockService()
	{
		HandlerThread handlerThread = new HandlerThread("wakeblock_client");
		handlerThread.start();
		Handler handler = new Handler(handlerThread.getLooper())
		{
			@Override
			public void handleMessage(Message message)
			{
				switch (message.what) {
					case 0:
						synchronized (WakeBlockService.lock) {
							WakeBlockService.lock.notify();
						}
						break;
					case 1:
						synchronized (WakeBlockService.lock) {
							WakeBlockService.acquire = false;
							WakeBlockService.lock.notify();
						}
						break;
					case 2:
						try {
							Message newMessage = Message.obtain(null, 3);
							Bundle bundle = new Bundle();
							bundle.putShort("version", WakeBlockService.VERSION);
							newMessage.setData(bundle);
							WakeBlockService.this.server.send(newMessage);
						} catch (RemoteException exception) {
							WakeBlockService.this.server = null;
							WakeBlockService.this.serviceBound = false;
						}
						break;
					default:
						super.handleMessage(message);
				}
			}
		};
		this.client = new Messenger(handler);
		this.serviceIntent.putExtra("system", true);
		Bundle bundle = new Bundle();
		bundle.putBinder("messenger", this.client.getBinder());
		this.serviceIntent.putExtra("bundle", bundle);
		this.serviceIntent.setPackage("com.giovannibozzano.wakeblock");
	}

	public boolean acquireWakeLockInternal(final Context context, IBinder lock, String tag, String packageName)
	{
		// If the wakelock is the binding signal, bind the service and discard the wakelock
		if (packageName.equals("com.giovannibozzano.wakeblock") && tag.equals("service_bind")) {
			if (!this.serviceBound) {
				new Thread(new Runnable()
				{
					@Override
					public void run()
					{
						context.bindService(WakeBlockService.this.serviceIntent, WakeBlockService.this.serviceConnection, Context.BIND_AUTO_CREATE);
					}
				}).start();
			}
			return false;
		}
		// If the service is not bound, accept the wakelock
		if (!this.serviceBound) {
			return true;
		}

		synchronized (WakeBlockService.lock) {
			WakeBlockService.acquire = true;
			try {
				Message message = Message.obtain(null, 0);
				Bundle bundle = new Bundle();
				bundle.putBinder("lock", lock);
				bundle.putString("tag", tag);
				bundle.putString("package_name", packageName);
				message.setData(bundle);
				this.server.send(message);
			} catch (RemoteException exception) {
				this.server = null;
				this.serviceBound = false;
				WakeBlockService.acquire = true;
				return true;
			}
			try {
				WakeBlockService.lock.wait();
			} catch (InterruptedException exception) {
				Slog.e(WakeBlockService.TAG, exception.getMessage());
			}
			return WakeBlockService.acquire;
		}
	}

	public void removeWakeLockLocked(IBinder lock, String mTag)
	{
		if (!this.serviceBound) {
			return;
		}
		synchronized (WakeBlockService.lock) {
			try {
				Message message = Message.obtain(null, 1);
				Bundle bundle = new Bundle();
				bundle.putBinder("lock", lock);
				bundle.putString("tag", mTag);
				message.setData(bundle);
				this.server.send(message);
			} catch (RemoteException exception) {
				this.server = null;
				this.serviceBound = false;
				return;
			}
			try {
				WakeBlockService.lock.wait();
			} catch (InterruptedException exception) {
				Slog.e(WakeBlockService.TAG, exception.getMessage());
			}
		}
	}

	public static WakeBlockService getInstance()
	{
		return WakeBlockService.INSTANCE;
	}
}
