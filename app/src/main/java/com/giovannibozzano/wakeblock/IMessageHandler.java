package com.giovannibozzano.wakeblock;

import android.os.Message;

@FunctionalInterface interface IMessageHandler
{
	void run(WakeBlockService wakeBlockService, Message message);
}
