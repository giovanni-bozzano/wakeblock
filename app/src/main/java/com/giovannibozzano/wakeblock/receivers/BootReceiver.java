package com.giovannibozzano.wakeblock.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.giovannibozzano.wakeblock.utils.Utils;

public class BootReceiver extends BroadcastReceiver
{
	@Override
	public void onReceive(Context context, Intent intent)
	{
		if (intent.getAction() == null || !intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
			return;
		}
		Utils.bindService(context);
	}
}
