package com.giovannibozzano.wakeblock.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.giovannibozzano.wakeblock.utils.Utils;

public class UpdateReceiver extends BroadcastReceiver
{
	@Override
	public void onReceive(Context context, Intent intent)
	{
		if (intent.getAction() == null || !intent.getAction().equals(Intent.ACTION_MY_PACKAGE_REPLACED)) {
			return;
		}
		Utils.bindService(context);
	}
}
