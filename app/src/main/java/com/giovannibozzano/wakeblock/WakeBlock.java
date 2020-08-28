package com.giovannibozzano.wakeblock;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.topjohnwu.superuser.BusyBox;
import com.topjohnwu.superuser.Shell;

public class WakeBlock extends Shell.ContainerApp
{
	private static boolean initialized = false;
	private static boolean coreModVersionChecked = false;
	private static Short coreModVersion = null;
	private static WakeBlockService wakeBlockService;
	private static ServiceConnection serviceConnection;

	@Override
	public void onCreate()
	{
		super.onCreate();
		Shell.setFlags(Shell.FLAG_REDIRECT_STDERR);
		Shell.verboseLogging(BuildConfig.DEBUG);
		BusyBox.setup(this);
		WakeBlock.serviceConnection = new ServiceConnection()
		{
			@Override
			public void onServiceConnected(ComponentName componentName, IBinder binder)
			{
				WakeBlock.wakeBlockService = ((WakeBlockService.LocalBinder) binder).getService();
			}

			@Override
			public void onServiceDisconnected(ComponentName componentName)
			{
				WakeBlock.wakeBlockService = null;
			}
		};
		this.bindService(new Intent(this, WakeBlockService.class), WakeBlock.serviceConnection, Context.BIND_AUTO_CREATE);
	}

	@Override
	public void onTerminate()
	{
		super.onTerminate();
		if (WakeBlock.serviceConnection != null) {
			this.unbindService(WakeBlock.serviceConnection);
		}
	}

	public static boolean isInitialized()
	{
		return WakeBlock.initialized;
	}

	public static void setInitialized(boolean initialized)
	{
		WakeBlock.initialized = initialized;
	}

	public static boolean isCoreModVersionChecked()
	{
		return WakeBlock.coreModVersionChecked;
	}

	public static void setCoreModVersionChecked(boolean coreModVersionChecked)
	{
		WakeBlock.coreModVersionChecked = coreModVersionChecked;
	}

	public static Short getCoreModVersion()
	{
		return WakeBlock.coreModVersion;
	}

	public static void setCoreModVersion(Short coreModVersion)
	{
		WakeBlock.coreModVersion = coreModVersion;
	}

	public static WakeBlockService getWakeBlockService()
	{
		return WakeBlock.wakeBlockService;
	}
}
