package com.giovannibozzano.wakeblock.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.giovannibozzano.wakeblock.WakeBlockService;

public class ShutdownReceiver extends BroadcastReceiver
{
	@Override
	public void onReceive(Context context, Intent intent)
	{
		if (intent.getAction() == null || !intent.getAction().equals(Intent.ACTION_SHUTDOWN)) {
			return;
		}
		Intent serviceIntent = new Intent(context, WakeBlockService.class);
		serviceIntent.putExtra("shutdown", true);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			context.startForegroundService(serviceIntent);
		} else {
			context.startService(serviceIntent);
		}
	}
}
